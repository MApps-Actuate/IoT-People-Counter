import time
import paho.mqtt.client as mqtt
import json
from . import MySQLWriter

writer = MySQLWriter('skynet', 'people_counter')

def on_connect(client, userdata, rc):
    print "Connected to Skynet!"
    client.subscribe("/beat")

def on_message(client, userdata, msg):
    data = json.loads(msg.payload)
    writer.writeNodeValue(data['node'], data['timestamp'], data['peopleCount'])
client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("localhost", 1883, 60)
client.loop_forever()
class MQTTListener:
     def __init__(self):
        print("Now listening")
