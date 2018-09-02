from django.contrib.auth.models import User
from django.db import models

# Create your models here.
from cart.models import Event, Price


class Ticket(models.Model):
    user=models.ForeignKey(User)
    price = models.ForeignKey(Price)
    transaction_id = models.CharField(max_length=1000)
    event = models.ForeignKey(Event)
    active=models.BooleanField(default=False)


    def __str__(self):
        return (self.event.name+"-Ticket-"+self.user.username)