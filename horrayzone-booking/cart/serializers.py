from rest_framework import serializers
from taggit_serializer.serializers import TaggitSerializer, TagListSerializerField

from users.serializers import UserSerializer
from .models import *



class CategorySerializer(serializers.ModelSerializer):

    class Meta:

        model = Category
        fields = '__all__'


class SubcategorySerializer(serializers.ModelSerializer):

    class Meta:

        model = Subcategory
        fields = '__all__'


class BrandSerializer(serializers.ModelSerializer):

    class Meta:

        model = Brand
        fields = '__all__'



class ProductSerializer(serializers.ModelSerializer):

    class Meta:

        model = Product
        fields = '__all__'


class EventSerializer(serializers.ModelSerializer):

    class Meta:

        model = Event
        fields = '__all__'
class PriceSerializer(serializers.ModelSerializer):

    class Meta:

        model = Price
        fields = '__all__'

class CitySerializer(serializers.ModelSerializer):

    class Meta:

        model = City
        fields = '__all__'

class CartSerializer(serializers.ModelSerializer):

    class Meta:

        model = CartItem
        fields = '__all__'


class AddressSerializer(serializers.ModelSerializer):

    class Meta:

        model = Address
        fields = '__all__'


class OrderSerializer(serializers.ModelSerializer):

    class Meta:

        model = Order
        fields = '__all__'