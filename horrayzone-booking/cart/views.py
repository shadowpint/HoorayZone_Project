# -*- coding: utf-8 -*-
from __future__ import unicode_literals

import django_filters
from django.http import Http404
from django.shortcuts import render
from django.views.decorators.http import condition
from rest_framework import generics, filters, status
# Create your views here.
from rest_framework import generics
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.views import APIView

from cart.models import *
from cart.serializers import *
from rest_framework.response import Response



def index(request):
    return render(request, 'index.html')
class CategoryFilter(filters.FilterSet):
    class Meta:
        model = Category
        fields = '__all__'





class SubcategoryFilter(filters.FilterSet):
    class Meta:
        model = Subcategory
        fields = '__all__'




class BrandFilter(filters.FilterSet):
    class Meta:
        model = Brand
        fields = '__all__'





class ProductFilter(filters.FilterSet):

    class Meta:
        model = Product
        fields = '__all__'

class EventFilter(filters.FilterSet):

    class Meta:
        model = Event
        fields = '__all__'



class CityFilter(filters.FilterSet):

    class Meta:
        model = City
        fields = '__all__'

class CartFilter(filters.FilterSet):
    class Meta:
        model = CartItem
        fields = '__all__'

class AddressFilter(filters.FilterSet):
    class Meta:
        model = Address
        fields = '__all__'


class OrderFilter(filters.FilterSet):
    class Meta:
        model = Order
        fields = '__all__'

class CategoryList(APIView):
    """
    Get / Create questions
    """
    queryset = Category.objects.all()
    serializer_class = CategorySerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = CategoryFilter
    permission_classes = [AllowAny]

    def get(self, request, format=None):
        category = Category.objects.all()

        serializer = CategorySerializer(category, many=True)
        return Response(serializer.data)
class SubcategoryList(APIView):
    """
    Get / Create questions
    """
    queryset = Subcategory.objects.all()
    serializer_class = SubcategorySerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = SubcategoryFilter
    permission_classes = [AllowAny]

    def get(self, request, format=None):
        subcategory = Subcategory.objects.all()

        serializer = SubcategorySerializer(subcategory, many=True)
        return Response(serializer.data)
class BrandList(APIView):
    """
    Get / Create questions
    """
    queryset =Brand.objects.all()
    serializer_class = BrandSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = BrandFilter
    permission_classes = [AllowAny]

    def get(self, request, format=None):
        brand = Brand.objects.all()

        serializer = BrandSerializer(brand, many=True)
        return Response(serializer.data)

class ProductList(APIView):
    """
    Get / Create questions
    """
    queryset = Product.objects.all()
    serializer_class = ProductSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = ProductFilter
    permission_classes = [AllowAny]

    def get(self, request, format=None):
        data = request.data.copy()
        print(data)
        product = Product.objects.all()

        serializer = ProductSerializer(product,many=True)
        return Response(serializer.data)
class ProductDetail(APIView):
    """
    Get / Update a Choice
    """

    queryset = Product.objects.all()
    serializer_class = ProductSerializer
    permission_classes = [AllowAny]

    def get(self, request, format=None):
        data = request.data.copy()
        print(data)
        product = Product.objects.get(code=request.GET['code'])

        serializer = ProductSerializer(product)
        return Response(serializer.data)


class CartList(APIView):
    """
    Get / Create questions
    """
    queryset = CartItem.objects.all()
    serializer_class = CartSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = CartFilter
    permission_classes = [IsAuthenticated]

    def get(self, request, format=None):
        id = request.user.pk
        cart = CartItem.objects.filter(user=id)
        serializer = CartSerializer(cart,many=True)
        return Response(serializer.data)

    def post(self, request, format=None):
        data = request.data.copy()


        cart = CartItem.objects.create(user=request.user,productId=Product.objects.get(id=data['productId']),quantity=data['quantity'],leadImageUrl=Product.objects.get(id=data['productId']).leadImageUrl)

        cart.save()
        serializer = CartSerializer(cart)
        return Response(serializer.data)



class CartDetail(APIView):
    """
    Retrieve, update or delete a snippet instance.
    """
    def get_object(self, pk):
        try:
            return CartItem.objects.get(pk=pk)
        except CartItem.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        snippet = self.get_object(pk)
        serializer = CartSerializer(snippet)
        return Response(serializer.data)

    def put(self, request, pk, format=None):
        snippet = self.get_object(pk)
        serializer = CartSerializer(snippet, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        snippet = self.get_object(pk)
        snippet.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class PriceView(APIView):
    """
    Retrieve, update or delete a snippet instance.
    """
    queryset = Price.objects.all()
    serializer_class = PriceSerializer
    permission_classes = [AllowAny]
    def get_object(self, pk):
        try:
            return Price.objects.filter(event_id=pk)
        except Price.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        snippet = self.get_object(pk)
        serializer = PriceSerializer(snippet,many=True)
        return Response(serializer.data)

    def put(self, request, pk, format=None):
        snippet = self.get_object(pk)
        serializer = PriceSerializer(snippet, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        snippet = self.get_object(pk)
        snippet.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)

class AddressList(APIView):
    """
    Get / Create questions
    """
    queryset = Address.objects.all()
    serializer_class = AddressSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = AddressFilter
    permission_classes = [IsAuthenticated]

    def get(self, request, format=None):
        id = request.user.pk
        address = Address.objects.filter(user=id)
        serializer = AddressSerializer(address,many=True)
        return Response(serializer.data)

class OrderList(APIView):
    """
    Get / Create questions
    """
    queryset = Order.objects.all()
    serializer_class = OrderSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = OrderFilter
    permission_classes = [IsAuthenticated]

    def get_object(self, pk):
        try:
            return Order.objects.get(pk=pk)
        except Order.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        snippet = self.get_object(pk)
        serializer = OrderSerializer(snippet)
        return Response(serializer.data)

    def post(self, request, format=None):
        data = request.data.copy()

        print(data)
        order = Order.objects.create(user=request.user,productId=Product.objects.get(id=data['productId']),quantity=data['quantity'],leadImageUrl=Product.objects.get(id=data['productId']).leadImageUrl)

        order.save()
        serializer = OrderSerializer(order)
        return Response(serializer.data)

    def delete(self, request, pk, format=None):
        snippet = self.get_object(pk)
        snippet.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class EventList(APIView):
    """
    Get / Create questions
    """
    queryset = Event.objects.all()
    serializer_class = EventSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = EventFilter
    permission_classes = [AllowAny]

    def get_object(self, pk):
        try:
            return Event.objects.filter(city_id=pk)
        except Event.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        snippet = self.get_object(pk)
        serializer = EventSerializer(snippet,many=True)
        return Response(serializer.data)

    def put(self, request, pk, format=None):
        snippet = self.get_object(pk)
        serializer = EventSerializer(snippet, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, pk, format=None):
        snippet = self.get_object(pk)
        snippet.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)






class CityList(APIView):
    """
    Get / Create questions
    """
    queryset = City.objects.all()
    serializer_class = CitySerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = CityFilter
    permission_classes = [AllowAny]

    def get(self, request, format=None):
        data = request.data.copy()
        print(data)
        city = City.objects.all()

        serializer = CitySerializer(city,many=True)
        return Response(serializer.data)




class NearbyEvent(APIView):
    """
    Get / Update a Choice
    """


    serializer_class = EventSerializer
    permission_classes = [AllowAny]

    def get(self, request, format=None):
        data = request.data.copy()
        print(request.GET['lat'])

        event = Event.objects.nearby(request.GET['lat'],request.GET['lng'],150)

        serializer = EventSerializer(event,many=True)
        return Response(serializer.data)