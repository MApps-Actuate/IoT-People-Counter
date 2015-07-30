import RPi.GPIO as GPIO
import logging
import time
import threading

class MotionSensor(object):
    global counter
    counter = 0
    # Constructor
    def __init__(self, interval=3):
       # Init the logger
       logging.basicConfig(filename='/home/pi/counter/log/sensor.log', level=logging.DEBUG)

       # Set Class interval 
       self.interval = interval

       # Setup the GPIO for our motion sensors
       try:
           GPIO.setmode(GPIO.BCM)
           GPIO.setup(7, GPIO.IN)
       except KeyboardInterrupt:
           print("\n") # Do nothing with this 
       except:
           logging.error(time.strftime("%d/%m/%Y %H:%M:%S") + ": GPIO Setup error!")

       # Allow for and start threading
       thread = threading.Thread(target=self.run, args=())
       thread.daemon = True
       thread.start() 

    # Deconstructor
    def __exit__(self):
       try:
           GPIO.cleanup()
       except KeyboardInterrupt:
           print("\n") # Do nothing with this
       except:
           logging.error(time.strftime("%d/%m/%Y %H:%M:%S") + ": GPIO Cleanup error!")

    # Motion sensor
    def run(self):
       global counter

       while(1):
           presence = GPIO.input(7)
           
           if(presence):
               counter += 1 
               presence = 0
               time.sleep(self.interval)

    # People count getter
    def getCounter(self):
        #global counter
        return counter
