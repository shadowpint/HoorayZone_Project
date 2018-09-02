# -*- coding: utf-8 -*-
from __future__ import unicode_literals
import json

from django.contrib.staticfiles import views

from django.http import HttpResponse, JsonResponse
from rest_framework.permissions import IsAuthenticated

from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import generics, filters, status
import braintree

from cart.models import Address, Event, Price
from booking import settings
from payment.models import Ticket
from payment.serializers import TicketSerializer

braintree.Configuration.use_unsafe_ssl = True
if settings.BRAINTREE_PRODUCTION:
    braintree_env = braintree.Environment.Production
else:
    braintree_env = braintree.Environment.Sandbox
config = braintree.Configuration(

    environment=braintree_env,
    merchant_id="qk7fmr6jgvtfxscf",
    public_key="nnbsqv9j8nmfp4zc",
    private_key="ac2b190b0dd27c4df38f26c171db3b33",
)
gateway = braintree.BraintreeGateway(config)

braintree.Configuration.use_unsafe_ssl = True


def getClientToken(request):
    token = gateway.client_token.generate(

    )
    print(token)
    return JsonResponse({'status': "success", 'client_token': token})


class Checkout(APIView):

    def post(self, request, format=None):
        received_json_data = request.data
        print(received_json_data)
        # address = received_json_data['addressId']
        # print(address)
        received_params = received_json_data.get("params")
        nonce_from_the_client = received_json_data['payment_method_nonce']

        customer_kwargs = {
            "first_name": request.user.first_name,
            "last_name": request.user.last_name,
            "email": request.user.email,
        }
        customer = gateway.customer.create(customer_kwargs)
        # add = Address.objects.get(id=address)
        # address_dict = {
        #     "first_name": request.user.first_name,
        #     "last_name": request.user.last_name,
        #     "street_address": add.lineOne,
        #     "extended_address": add.lineTwo,
        #     "locality": add.city,
        #     "region": add.state,
        #     "postal_code": add.zip,
        #
        #     "country_name": add.country,
        #
        # }

        result = gateway.transaction.sale({
            "amount": received_json_data['amount'],
            "payment_method_nonce": nonce_from_the_client,
            "options": {
                "submit_for_settlement": True
            }
        })

        print(result)
        if result.is_success or result.transaction:
            return Response({"status":"success"}, status=200)
        else:
            return Response({'message': 'error in transaction'}, status=400)

class TicketFilter(filters.FilterSet):
    class Meta:
        model = Ticket
        fields = '__all__'


class TicketList(APIView):
    """
    Get / Create questions
    """
    queryset = Ticket.objects.all()
    serializer_class = TicketSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = TicketFilter
    permission_classes = [IsAuthenticated]

    def get(self, request, format=None):
        id = request.user.pk
        ticket = Ticket.objects.filter(user=id)
        serializer = TicketSerializer(ticket,many=True)
        return Response(serializer.data)

    def post(self, request, format=None):
        data = request.data.copy()


        ticket = Ticket.objects.create(user=request.user,price=Price.objects.get(id=data['price_id']),event=Event.objects.get(id=data['event_id']),transaction_id=data['transaction_id'])

        ticket.save()
        serializer = TicketSerializer(ticket)
        return Response(serializer.data)



class BookingList(APIView):
    """
    Get / Create questions
    """
    queryset = Ticket.objects.all()
    serializer_class = TicketSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = TicketFilter
    permission_classes = [IsAuthenticated]

    def get(self, request, format=None):
        id = request.user.pk
        ticket = Ticket.objects.filter(user=id)
        serializer = TicketSerializer(ticket,many=True)
        return Response(serializer.data)

    # def post(self, request, format=None):
    #     data = request.data.copy()
    #
    #
    #     cart = CartItem.objects.create(user=request.user,productId=Product.objects.get(id=data['productId']),quantity=data['quantity'],leadImageUrl=Product.objects.get(id=data['productId']).leadImageUrl)
    #
    #     cart.save()
    #     serializer = CartSerializer(cart)
    #     return Response(serializer.data)