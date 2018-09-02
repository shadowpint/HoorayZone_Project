from django.conf.urls import url
from django.conf import settings
from django.conf.urls.static import static

from cart.views import *
from . import views


urlpatterns = [
    # Regular Django Views


    # API views
url(r'^event_nearby/$', NearbyEvent.as_view()),
url(r'^event/(?P<pk>[0-9]+)/$', EventList.as_view()),
url(r'^city/$', CityList.as_view()),
url(r'^product/$', ProductList.as_view()),
url(r'^productdetail/', views.ProductDetail.as_view()),
url(r'^price/(?P<pk>[0-9]+)/$', views.PriceView.as_view()),
# url(r'^productdetail/(?P<pk>[0-9]+)/$', views.ProductDetail.as_view()),
url(r'^shoppingCart/$', CartList.as_view()),
url(r'^shoppingCart/(?P<pk>[0-9]+)/$', views.CartDetail.as_view()),
url(r'^address-book/$', AddressList.as_view()),
url(r'^orders/$', OrderList.as_view()),
url(r'^categories/$', CategoryList.as_view()),
url(r'^subcategories/$', SubcategoryList.as_view()),
url(r'^brands/$', BrandList.as_view()),
] + static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)
