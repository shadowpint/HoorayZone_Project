from django.contrib import admin

# Register your models here.
from payment.models import Ticket

admin.site.register(Ticket)