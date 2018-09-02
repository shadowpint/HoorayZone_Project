from django.conf.urls import url
from django.conf import settings
from django.conf.urls.static import static

from blog.views import BlogList
from cart.views import *
from . import views


urlpatterns = [
    # Regular Django Views


    # API views

url(r'^blog/$', BlogList.as_view()),
url(r'^blog/(?P<pk>[0-9]+)/$', views.BlogDetail.as_view()),

] + static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)
