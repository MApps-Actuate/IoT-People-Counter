from modules import MotionSensor 
from modules import HALComm
import time

halComm = HALComm()
sensor  = MotionSensor()

while(True):
    halComm.setPeopleCount(sensor.getCounter())
    time.sleep(1)
