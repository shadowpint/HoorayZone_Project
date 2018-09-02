from rest_framework import serializers
from taggit_serializer.serializers import TaggitSerializer, TagListSerializerField

from users.serializers import UserSerializer
from .models import *



class BlogSerializer(serializers.ModelSerializer):

    class Meta:

        model = Blog
        fields = '__all__'


