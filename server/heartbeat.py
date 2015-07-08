import time;
import paho.mqtt.client as mqtt

def on_connect(client, userdata, rc):
    print "Connected to Skynet!"
    client.subscribe("/heartbeat")

def on_message(client, userdata, msg):
    print "Message recieved!"

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("10.59.32.55", 1883, 60)
client.loop()
while True:
    client.publish("/heartbeat", "isAlive?")
    print "Sending heartbeat request"
    time.sleep(5)
