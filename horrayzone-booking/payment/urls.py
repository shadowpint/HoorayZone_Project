from django.conf.urls import url
from . import views

app_name = 'braintreeapi'

urlpatterns = [
    url(r'^getclienttoken/?$', views.getClientToken),
    url(r'^checkout/?$', views.Checkout.as_view()),
url(r'^addticket/?$', views.TicketList.as_view()),
    url(r'^bookings/?$', views.BookingList.as_view()),
]
