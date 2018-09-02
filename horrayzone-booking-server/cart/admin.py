# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.contrib import admin

# Register your models here.
from cart.models import *
admin.site.register(Category)
admin.site.register(Subcategory)
admin.site.register(Brand)
admin.site.register(Product)
admin.site.register(Event)
admin.site.register(City)
admin.site.register(Price)
admin.site.register(CartItem)
admin.site.register(Address)
admin.site.register(Order)
