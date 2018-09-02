from django.http import Http404
from django.shortcuts import render

# Create your views here.
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from blog.models import Blog
from blog.serializers import BlogSerializer


class BlogList(APIView):
    """
    Get / Create questions
    """
    queryset = Blog.objects.all()
    serializer_class = BlogSerializer

    permission_classes = [IsAuthenticated]

    def get(self, request, format=None):

        blog = Blog.objects.all()
        serializer = BlogSerializer(blog,many=True)
        return Response(serializer.data)

class BlogDetail(APIView):
    """
    Retrieve, update or delete a snippet instance.
    """
    def get_object(self, pk):
        try:
            return Blog.objects.get(pk=pk)
        except Blog.DoesNotExist:
            raise Http404

    def get(self, request, pk, format=None):
        snippet = self.get_object(pk)
        serializer = BlogSerializer(snippet)
        return Response(serializer.data)