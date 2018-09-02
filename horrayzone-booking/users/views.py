import json

from django.contrib.auth.models import User
from django.http import JsonResponse
from django.utils.decorators import method_decorator
from django.views.decorators.debug import sensitive_post_parameters
from google.auth.transport import requests
from google.oauth2 import id_token
from rest_framework.generics import GenericAPIView
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.views import APIView

from rest_framework import status

from users.models import Profile
from .serializers import  UserSerializer, PasswordResetSerializer, PasswordResetConfirmSerializer, \
    PasswordChangeSerializer
from rest_framework.response import Response
from django.shortcuts import render, HttpResponse
from django.contrib.auth.hashers import make_password
from oauth2.token_verify import verify_token
from django.core.exceptions import ObjectDoesNotExist
from django.contrib.sites.shortcuts import get_current_site
from django.utils.encoding import force_bytes, force_text
from django.utils.http import urlsafe_base64_encode, urlsafe_base64_decode
from django.template.loader import render_to_string
from oauth2.Activation_Token_Generator import account_activation_token
from django.core.mail import EmailMessage
sensitive_post_parameters_m = method_decorator(
    sensitive_post_parameters(
        'password', 'old_password', 'new_password1', 'new_password2'
    )
)

class Detail(APIView):
    def get(self, request, user_id):
        """
        To get the user information whose user_id specified in Url
        request type allowed : only GET
        
        :param request: 
        :param user_id: 
        :return: json object of either user info. or error message
        """
        data = request.GET.copy()
        data['signup'] = False
        r = verify_token(data)
        if r['message'] != 'valid':
            return Response({'message': r['message']})

        if user_id == r['user_id']:
            user = User.objects.get(id=user_id)
            serializer = UserSerializer(user)
            return Response(serializer.data)
        else:
            return Response({'message': 'Access token not for the given User'})
                

'''
def web_signin(request):
    return render(request, 'users/google_signin.html', dict())
'''
CLIENT_ID = '942594263615-usf7m8g8sph7nt9p0iarrvk1oo90lipj.apps.googleusercontent.com'
class Google_Token(APIView):
    permission_classes = [AllowAny]
    def post(self, request):
     data = request.data.copy()
     response=""
     try:
         # Specify the CLIENT_ID of the app that accesses the backend:
         idinfo = id_token.verify_oauth2_token(data['token'], requests.Request(), CLIENT_ID)

         # Or, if multiple clients access the backend server:
         # idinfo = id_token.verify_oauth2_token(token, requests.Request())
         # if idinfo['aud'] not in [CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]:
         #     raise ValueError('Could not verify audience.')

         if idinfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
             raise ValueError('Wrong issuer.')

         # If auth request is from a G Suite domain:
         # if idinfo['hd'] != GSUITE_DOMAIN_NAME:
         #     raise ValueError('Wrong hosted domain.')

         # ID token is valid. Get the user's Google Account ID from the decoded token.
         userid = idinfo['sub']
         print(idinfo)
         user = User()  # insert user info into database
         try:
             user = User.objects.get(email=idinfo['email'])

             serializer = UserSerializer(user)
             response=JsonResponse(json.dumps({"username":user.username,"email":user.email}),safe=False)
         except ObjectDoesNotExist:
             user.username=idinfo['email'][:-10]
             user.first_name = idinfo['given_name']
             user.last_name = idinfo['family_name']
             user.email = idinfo['email']

             user.set_unusable_password()
             # user.is_active = False
             user.save()
             response=JsonResponse(json.dumps({"username":idinfo['email'][:-10],"email":idinfo['email'],"name":idinfo['name'],"picture":idinfo['picture'],"given_name":idinfo['given_name'],"family_name":idinfo['family_name']}), safe=False)
     except ValueError:
         # Invalid token
         pass
     print(json.dumps({"email":idinfo['email'],"name":idinfo['name'],"picture":idinfo['picture'],"given_name":idinfo['given_name'],"family_name":idinfo['family_name']}))
     return response
class GoogleSignUp(APIView):
    permission_classes = [AllowAny]
    def post(self, request):
        """
        To sign up the user manually
        request type allowed : only POST

        :param request:
        :return: either success message or user info if user already signed up
        """
        data = request.data.copy()
        print(data)
        data['signup'] = True
        r = verify_token(data)
        if r['message'] != 'valid':
            return Response({'message': r['message']})
        else:
         print(r)
         email = data['email']
         print(email)
         user = User()
         user = User.objects.get(email=email)
        # insert user info into database

         user.username = data['username']


         password = data['password']

         user.password = make_password(password=password, salt=None, hasher='unsalted_md5')
            # user.is_active = False
         user.save()

            # To send confirmation email


         return JsonResponse(json.dumps({'message': 'registered'}),safe=False)
class SignUp(APIView):
    permission_classes = [AllowAny]
    def post(self, request):
        """
        To sign up the user manually
        request type allowed : only POST

        :param request:
        :return: either success message or user info if user already signed up
        """
        data = request.data.copy()
        print(data)
        data['signup'] = True
        r = verify_token(data)
        if r['message'] != 'valid':
            return Response({'message': r['message']})
        print(r)
        user_id = r.get('username')
        print(user_id)
        user = User()          # insert user info into database
        try:
            user = User.objects.get(username=user_id)

            serializer = UserSerializer(user)
            return Response(serializer.data)
        except ObjectDoesNotExist:
            user.username = data['username']
            user.first_name =data['firstName']
            user.last_name=data['lastName']
            user.email = data['email']

            password = data['password']

            user.password = make_password(password=password, salt=None, hasher='unsalted_md5')
            user.is_active = False
            user.save()

            # To send confirmation email

            subject = 'Activate your Entrygate account.'
            message = render_to_string('users/acc_active_email.html', {
                'user': user,'domain': 'entrygate-booking.herokuapp.com',
                'uid': urlsafe_base64_encode(force_bytes(user.pk)),
                'token': account_activation_token.make_token(user),
            })
            toemail = data['email']
            email = EmailMessage(subject, message, to=[toemail])
            email.send()
            return Response({'message': 'User successfully registered and Please confirm your Email'})
# class SignUp(APIView):
#     permission_classes = [AllowAny]
#
#     def post(self, request):
#         """
#         To sign up the user manually
#         request type allowed : only POST
#
#         :param request:
#         :return: either success message or user info if user already signed up
#         """
#         data = request.data.copy()
#         print data
#         data['signup'] = True
#         r = verify_token(data)
#         if r['message'] != 'valid':
#             return Response({'message': r['message']})
#         print r
#         user_id = r.get('username')
#         print user_id
#         user = User()  # insert user info into database
#         try:
#             user = User.objects.get(username=user_id)
#
#             serializer = UserSerializer(user)
#             return Response(serializer.data)
#         except ObjectDoesNotExist:
#             user.username = data['username']
#             user.first_name = data['first_name']
#             user.last_name = data['last_name']
#             user.email = data['email']
#
#             password = data['password']
#
#             user.password = make_password(password=password, salt=None, hasher='unsalted_md5')
#
#             user.save()
#             #
#             # # To send confirmation email
#             # current_site = get_current_site(request)
#             # subject = 'Activate your  account.'
#             # message = render_to_string('users/acc_active_email.html', {
#             #     'user': user, 'domain': current_site.domain,
#             #     'uid': urlsafe_base64_encode(force_bytes(user.pk)),
#             #     'token': account_activation_token.make_token(user),
#             # })
#             # toemail = data['email']
#             # email = EmailMessage(subject, message, to=[toemail])
#             # email.send()
#             return Response({'message': 'User successfully registered'})


class SignIn(APIView):

    def post(self, request):
        """
        To sign in a user.
        
        :param request: 
        :return: json object which contains user information
        """
        data = request.data.copy()
        data['signup'] = False
        r = verify_token(data)
        if r['message'] != 'valid':
            return Response({'message': r['message']})
        user_id = r['user_id']
        if user_id:
            user = User.objects.get(id=user_id)
            serializer = UserSerializer(user)
            return Response(serializer.data)


class ProfileUpdate(APIView):
    def post(self, request):
        """
        To update profile info. of the user.
        
        :param request: 
        :return: Success or Failure message
        """
        data = request.data.copy()
        data['signup'] = False
        r = verify_token(data)
        if r['message'] != 'valid':
            return Response({'message': r['message']})
        user_id = r['user_id']
        if user_id:
            user = User.objects.get(id=user_id)
            if 'first_name' in data:
                user.first_name = data['first_name']
            if 'last_name' in data:
                user.last_name = data['last_name']
            if 'gender' in data:
                user.gender = data['gender']
            if 'email' in data:
                if not user.email:
                    user.email = data['email']
            if 'contact' in data:
                user.contact = data['contact']
            if 'pic_url' in data:
                user.pic_url = data['pic_url']
            if 'height' in data:
                user.height = data['height']
            if 'weight' in data:
                user.weight = data['weight']
            if 'dob' in data:
                user.dob = data['dob']                
            user.save()
            return Response({'message': 'Profile successfully updated'})


def activate(request,uidb64,token):
    """
    To activate the user account after email confirmation
    
    :param request: 
    :param uidb64: 
    :param token: 
    :return: Success or Failure message
    """
    try:

        uid = force_text(urlsafe_base64_decode(uidb64))
        user = User.objects.get(pk=uid)
        print(user)
    except(TypeError, ValueError, OverflowError, User.DoesNotExist):
        user = None
       
    if user is not None and account_activation_token.check_token(user, token):
        if not user.is_active:
            user.is_active = True
            user.save()
            return HttpResponse('Thank you for your email confirmation. Now you can login your account.')
        else:
            return HttpResponse('Your email is already confirmed. Thank You!')      
    else:
        return HttpResponse('Activation link is invalid! :(')



class PasswordResetView(GenericAPIView):
    """
    Calls Django Auth PasswordResetForm save method.

    Accepts the following POST parameters: email
    Returns the success/fail message.
    """
    serializer_class = PasswordResetSerializer
    permission_classes = (AllowAny,)

    def post(self, request, *args, **kwargs):
        # Create a serializer with request.data
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        serializer.save()
        # Return the success message with OK HTTP status
        return Response(
            {"detail": "Password reset e-mail has been sent."},
            status=status.HTTP_200_OK
        )


class PasswordResetConfirmView(GenericAPIView):
    """
    Password reset e-mail link is confirmed, therefore
    this resets the user's password.

    Accepts the following POST parameters: token, uid,
        new_password1, new_password2
    Returns the success/fail message.
    """
    serializer_class = PasswordResetConfirmSerializer
    permission_classes = (AllowAny,)

    @sensitive_post_parameters_m
    def dispatch(self, *args, **kwargs):
        return super(PasswordResetConfirmView, self).dispatch(*args, **kwargs)

    def post(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(
            {"detail": "Password has been reset with the new password."}
        )


class PasswordChangeView(GenericAPIView):
    """
    Calls Django Auth SetPasswordForm save method.

    Accepts the following POST parameters: new_password1, new_password2
    Returns the success/fail message.
    """
    serializer_class = PasswordChangeSerializer
    permission_classes = (IsAuthenticated,)

    @sensitive_post_parameters_m
    def dispatch(self, *args, **kwargs):
        return super(PasswordChangeView, self).dispatch(*args, **kwargs)

    def post(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response({"detail":"New password has been saved."})