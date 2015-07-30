import time;
import paho.mqtt.client as mqtt

print "Welcome to Skynet!"

def on_connect(client, userdata, rc):
    print "Connected to Skynet!"

#def on_message(client, userdata, msg):
    # Do nothing

client = mqtt.Client()
client.on_connect = on_connect
#client.on_message = on_message

client.connect("localhost", 1883, 60)
client.loop()

while True:
    client.publish("/heartbeat", "isAlive?")
    time.sleep(5)
