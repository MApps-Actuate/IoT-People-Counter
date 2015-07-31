from modules import MotionSensor 
from modules import HALComm
import time

halComm = HALComm()
sensor  = MotionSensor()

while(True):
    halComm.setPeopleCount(sensor.getCounter())
    sensor.resetCounter()
    time.sleep(15)
