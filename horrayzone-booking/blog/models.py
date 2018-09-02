from django.db import models

# Create your models here.
class Blog(models.Model):
    name = models.CharField(max_length=1000)
    content = models.TextField(max_length=10000, blank=True)
    url = models.CharField(max_length=1000,blank=True)
    def __str__(self):
        return self.name