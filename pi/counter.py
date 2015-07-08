import RPi.GPIO as GPIO
import paho.mqtt.client as mqtt
import socket
import calendar
import time
import fcntl
import struct
import os

global MQTT_CLIENT_ID
global LOCAL_IP

def get_ip_address(ifname):
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    return socket.inet_ntoa(fcntl.ioctl(
        s.fileno(),
        0x8915,
        struct.pack('256s', ifname[:15])
    )[20:24])

GPIO.setmode(GPIO.BCM)
GPIO.setup(7, GPIO.IN)

MQTT_CLIENT_ID=socket.gethostname()

peoplecount = 0

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, rc):
    print("Connected with result code "+str(rc))
    client.subscribe("/heartbeat")
    client.subscribe("/global")
    client.subscribe("/" + MQTT_CLIENT_ID)

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
        print "test"
        global peoplecount
        print peoplecount
        if(msg.payload == "isAlive?"):
           client.publish("/beat", "{\"node\": \""+ MQTT_CLIENT_ID +"\",\"timestamp\": "+str(calendar.timegm(time.gmtime()))+",\"ip\": \""+get_ip_address('wlan0')+"\", peopleCount:"+str(peoplecount)+"}")
        elif(msg.payload == "poweroff"):
           os.system('shutdown -t %d 0')
        elif(msg.payload == "reboot"):
           os.system('shutdown -r -t %d 0')
        elif(msg.payload == "clear"):
           peoplecount = 0

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("10.59.32.55", 1883, 60)

client.loop_start()

iwhile(1):
    jsonPayload = "{\"node\": \"OT-9000-1\",\"timestamp\":"+ str(calendar.timegm(time.gmtime()))  +",\"peopleCount\":"+str(peoplecount)+"}"


    print jsonPayload
    #print peoplecount
    client.publish("SKYNET/OT9000-1", payload="test",qos=0, retain=False)
    presence = GPIO.input(7)

    if(presence):
        peoplecount += 1
        presence = 0
        time.sleep(1.5)

    time.sleep(1)

