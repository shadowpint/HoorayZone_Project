# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.contrib.auth.models import User
from django.db import models

# Create your models here.
from django.db.models.expressions import RawSQL


class LocationManager(models.Manager):
    def nearby(self, lat, lng, proximity):
        """
        Return all object which distance to specified coordinates
        is less than proximity given in kilometers
        """
        # Great circle distance formula
        gcd = """
              6371 * acos(
               cos(radians(%s)) * cos(radians(lat))
               * cos(radians(lng) - radians(%s)) +
               sin(radians(%s)) * sin(radians(lat))
              )
              """
        return self.get_queryset()\
                   .exclude(lat=None)\
                   .exclude(lng=None)\
                   .annotate(distance=RawSQL(gcd, (lat,
                                                   lng,
                                                   lat)))\
                   .filter(distance__lt=proximity)\
                   .order_by('distance')








class Category(models.Model):
    name = models.CharField(max_length=1000)
    description = models.CharField(max_length=1000, blank=True)
    filename = models.CharField(max_length=1000,blank=True)
    def __str__(self):
        return self.name

class Subcategory(models.Model):
    categoryId=models.ForeignKey(Category)
    name = models.CharField(max_length=1000)
    filename = models.CharField(max_length=1000, blank=True)
    def __str__(self):
        return self.name

class Brand(models.Model):
    name = models.CharField(max_length=1000)
    def __str__(self):
        return self.name




class Product(models.Model):
    code = models.CharField(max_length=1000, blank=True)
    name = models.CharField(max_length=1000)
    description = models.CharField(max_length=1000, blank=True)
    price = models.CharField(max_length=1000, blank=True)
    leadImageUrl = models.CharField(max_length=1000, blank=True)
    brandId = models.ForeignKey(Brand,blank=True)
    subcategoryId = models.ForeignKey(Subcategory,blank=True)
    def __str__(self):
        return self.name

class City(models.Model):
    name = models.CharField(max_length=1000)
    lat = models.DecimalField(max_digits=8, decimal_places=4)
    lng = models.DecimalField(max_digits=8, decimal_places=4)
    leadImageUrl = models.CharField(max_length=1000, blank=True)

    def __str__(self):
        return self.name


class Event(models.Model):
    objects = LocationManager()
    city = models.ForeignKey(City)
    code = models.CharField(max_length=1000, blank=True)
    name = models.CharField(max_length=1000)
    tags = models.CharField(max_length=1000)
    date = models.DateTimeField( blank=True)
    address = models.CharField(max_length=1000, blank=True)

    lat = models.DecimalField(max_digits=8, decimal_places=4)
    lng = models.DecimalField(max_digits=8, decimal_places=4)
    description = models.CharField(max_length=1000, blank=True)
    leadImageUrl = models.CharField(max_length=1000, blank=True)

    def __str__(self):
        return self.name
class Price(models.Model):
    event=models.ForeignKey(Event)
    name = models.CharField(max_length=1000)
    description = models.CharField(max_length=1000, blank=True)
    price = models.CharField(max_length=1000, blank=True)
    def __str__(self):
        return self.name
class CartItem(models.Model):
    user=models.ForeignKey(User)
    productId =models.ForeignKey(Product)
    quantity = models.IntegerField( blank=True)
    date = models.DateTimeField(auto_now_add=True, blank=True)
    leadImageUrl = models.CharField(max_length=1000, blank=True)


class Address(models.Model):
    user=models.ForeignKey(User)
    fullName = models.CharField(max_length=1000, blank=True)
    lineOne = models.CharField(max_length=1000, blank=True)
    lineTwo = models.CharField(max_length=1000, blank=True)
    city = models.CharField(max_length=1000, blank=True)
    state = models.CharField(max_length=1000, blank=True)
    country = models.CharField(max_length=1000, blank=True)
    zip = models.CharField(max_length=1000, blank=True)
    phoneNumber = models.CharField(max_length=1000, blank=True)

    def __str__(self):
        return str(self.user.username) + "- Address"


class Order(models.Model):
    STATUS = (('APPROVED', 'Approved'),
              ('CANCELED', 'Canceled'),
              ('PENDING', 'Pending'))
    user=models.ForeignKey(User)
    product=models.ForeignKey(Product)
    reference = models.CharField(max_length=1000, blank=True)
    date = models.DateTimeField(auto_now_add=True, blank=True)
    quantity = models.IntegerField(blank=True)
    subtotal = models.CharField(max_length=1000, blank=True)
    shippingPrice = models.CharField(max_length=1000, blank=True)
    tax = models.CharField(max_length=1000, blank=True)
    status = models.CharField(max_length=8,choices=STATUS)


    def __str__(self):
        return str(self.user.username) + "- Order"