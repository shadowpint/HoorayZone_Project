from django.core.exceptions import ObjectDoesNotExist
from django.utils import timezone
from oauth2_provider.models import (
    AccessToken,
    RefreshToken,
    clear_expired
)


def clear_expired_token():
    now = timezone.now()
    AccessToken.objects.filter(expires__lt=now).delete()


def verify_token(data):
    # CLIENT_ID_1 = '563936792008-qsd946svnd2ptu5sdsrqi2q63dn37fvd.apps.googleusercontent.com' # narendra
    # CLIENT_ID_2 = '731349432909-a4p2obbrvlvttcu563bj7qo5jln1ij29.apps.googleusercontent.com'   # pavan
    # client_secret = 'PkIP-kphbiRnXXwrgfmT9v-H'
    # APP_ACCESS_TOKEN = '434479586921685|1pR96adplJZY4ugzsqwfnryePnQ'

    client_id = data['client_id']
    # if client_id not in ['126DhNWthQyVmJ5bREA8KWJimTZuPQZdmbles2h1']:
    if client_id not in ['6J0cM1ksd9MEccKeH4iWH8zMmd7dSRNuDE2JOEPI','gNKyZdiBTgNs4UwRu3fRQJ4IbCHe3fQvpI5yyyH7','YUjs6N9o5D3mWBAb47Q5iCxJinc6b4VYflJF3lu4','PX05LgrzbPgJTBkGc3CxzCQwIJbH5fG8IBoDa7C0','5EVc7ZA0unotEqBx7gSzjXv3YDjos4IgDfFf1B0Q']:
        message = {'message': 'Invalid client id'}
        return message

    if data['signup']:
        user_id = ''
        r = {'message': 'valid', 'user_id': user_id}
        return r
    token = data['access_token']
    client_secret = data['client_secret']
    # clear_expired_token()
    try:
        access_token = AccessToken.objects.get(token=token)
    except ObjectDoesNotExist:
        r = {'message': 'Invalid Token'}
        return r

    if access_token:
        app = access_token.application
        if not access_token.is_expired() and client_id == app.client_id and client_secret == app.client_secret:
            user = access_token.user
            user_id = user.id
            r = {'message': 'valid', 'user_id': user_id}
            return r
        else:
            r = {'message': 'Token may expire or Invalid Client Id/secret'}
            return r

                




