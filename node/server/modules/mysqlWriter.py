import mysql.connector
import time
import logging

class MySQLWriter:
    # Init the logger
    logging.basicConfig(filename='/home/skynet/python/log/mysqlWriter.log', level=logging.DEBUG)

    # Global Class variables
    cnx    = None
    cursor = None

    # Constructor
    def __init__(self, username, db):
       for i in range(0, 100):
           try:
              # Declare the globals
              global cnx
              global cursor

              cnx = mysql.connector.connect(user=username, database=db)
              cursor = cnx.cursor()
           except mysql.connector.Error as err:
              logging.info(time.strftime("%d/%m/%Y %H:%M:%S") + ": Something went wrong with the database connection: {}".format(err))
              time.sleep(5)
              print("Retrying connection")
              continue
           break
    # Method to insert node values to db
    def writeNodeValue(self, node, timestamp, count):
       try:
          add_count = ("INSERT INTO iot_people_counter (node, event_time, peoplecount) VALUES (%s, FROM_UNIXTIME(%s), %s)")
          data_count = (node, timestamp, count)

          # Insert new record from node
          cursor.execute(add_count, data_count)
          record_no = cursor.lastrowid

          # Commit the record insert
          cnx.commit()

          logging.info("INSERT completed successfully " + time.strftime("%H:%M:%S"))
       except mysql.connector.Error as err:
          # Print the exception
          logging.error(time.strftime("%d/%m/%Y %H:%M:%S") + ": Something went wrong with the database INSERT: {}".format(err))

    def __exit__(self):
       cursor.close()
       cnx.close()
