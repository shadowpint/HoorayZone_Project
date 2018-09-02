from django.conf.urls import include, url
from django.contrib import admin
from django.views.static import serve

import blog
import cart.urls

import payment.urls
import blog.urls
import users.urls
from booking import settings
import cart.views
urlpatterns = [
    # End points of social oauth.For more detail Go to the 'rest_framework_social_oauth2.urls' file
    url(r'^$', cart.views.index),
    url(r'^auth/', include('rest_framework_social_oauth2.urls')),
    # Including all end points of admin panel.
    url(r'^admin/', include(admin.site.urls)),
    # Including all end points related to user. Go to 'users.urls' file
    url(r'^user/', include(users.urls)),
    # Including all end points related to aggregate records

url(r'^news/', include(blog.urls)),
url(r'^api/shop/', include(cart.urls)),
url(r'^api/payment/', include(payment.urls)),
]
urlpatterns += [
    # End point to access uploaded live or environment files from browser
        url(r'^media/(?P<path>.*)$', serve, {
            'document_root': settings.MEDIA_URL,
        }),
    ]
urlpatterns += [
    # End point to access uploaded live or environment files from browser
        url(r'^static/(?P<path>.*)$', serve, {
            'document_root': settings.STATIC_ROOT,
        }),
    ]
