import logging
import time
import threading
import paho.mqtt.client as mqtt
import socket
import calendar
import fcntl 
import struct

class HALComm(object):
    peopleCount = 0
    
    def __init__(self, interval=3):
       # Init the logger
       logging.basicConfig(filename='/home/pi/counter/log/halComm.log', level=logging.DEBUG)

       # Set Class Interval
       self.interval = interval

       # Allow for and start threading
       thread = threading.Thread(target=self.run, args=())
       thread.daemon = True
       thread.start()

    # Deconstructor
    def __exit__(self):
       print "Deconstructor"

    # People counter setter
    def setPeopleCount(self, count):
       global peopleCount
       peopleCount = count

    # People counter getter
    #def getPeopleCount(self):
    #   global peopleCount
    #   return peopleCount

    # Run thread
    def run(self):
       global peopleCount
       # People counter getter
       #def getPeopleCount(self):
       #   global peopleCount
       #   test = peopleCount
       #   print test
       #   return test 

       def onConnect(client, userdata, rc):
           logging.info("Connect with result code " + str(rc))

           client.subscribe("/heartbeat")
           client.subscribe("/global")
           client.subscribe("/" + socket.gethostname())

           logging.info(socket.gethostname() + " Connected!")
           logging.info(socket.gethostname() + " Subscribed to /heartbeat")
           logging.info(socket.gethostname() + " Subscribed to /global")
           logging.info(socket.gethostname() + " Subscribed to /" + socket.gethostname())

       def onMessage(client,userdata, msg):
           if(msg.payload == "isAlive?"):
              client.publish("/beat", "{\"node\": \""+ socket.gethostname() +"\",\"timestamp\": "+str(calendar.timegm(time.gmtime()))+",\"ip\": \""+get_ip_address('wlan0')+"\", \"peopleCount\":"+str(peopleCount)+"}")
           elif(msg.payload == "poweroff"):
              print("poweroff")
           elif(msg.payload == "reboot"):
              print("reboot")
           elif(msg.payload == "clear"):
              print("clear")

       def get_ip_address(ifname):
           s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
           return socket.inet_ntoa(fcntl.ioctl(
               s.fileno(),
               0x8915,
               struct.pack('256s', ifname[:15])
           )[20:24])


       client = mqtt.Client()
       client.on_connect = onConnect 
       client.on_message = onMessage
       client.connect("192.168.1.254", 1883, 60)
       client.loop_start() 

    # Get the local IP
    def getLocalIP(ifname):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        return socket.inet_ntoa(fcntl.ioctl(s.fileno(), 0x8915, struct.pack('256s', ifname[:15]))[20:24])
