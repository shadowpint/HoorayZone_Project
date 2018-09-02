from rest_framework import serializers

from payment.models import Ticket


class TicketSerializer(serializers.ModelSerializer):

    class Meta:

        model = Ticket
        depth=1
        fields = ('id','price', 'transaction_id', 'event','active')
