import sys
[sys.path.append('/var/www/html/env/App/App')]
from flask import Blueprint
import json

rest_api = Blueprint('rest_api', __name__)

from extensions import mysql

@rest_api.route("/")
def apiDefault():
    return '{"Name":"Aquarian REST API","Version":"0.1","Created":"08/02/2020","LastModified":"08/02/2020"}'

@rest_api.route('/jobs/')
@rest_api.route('/jobs', methods=['GET'])
def returnJobs():
   cur = mysql.connection.cursor()
   cur.execute('SELECT * FROM Jobs')
   row_headers=[x[0] for x in cur.description] #this will extract row headers
   rv = cur.fetchall()
   json_data=[]
   for result in rv:
        json_data.append(dict(zip(row_headers,result)))
   return json.dumps(json_data, default=str)

@rest_api.route('/jobs/<int:id>/')
@rest_api.route('/jobs/<int:id>', methods=['GET'])
def get_job(id):
   cur = mysql.connection.cursor()
   cur.execute("SELECT * FROM Jobs WHERE JobID = %s", (id,))
   if cur.rowcount == 0:
      return '{"Error":"No matching ID was found in the database."}'
   else:
      row_headers=[x[0] for x in cur.description] #this will extract row headers
      rv = cur.fetchall()
      json_data=[]
      for result in rv:
          json_data.append(dict(zip(row_headers,result)))
      return json.dumps(json_data, default=str)

@rest_api.route('/drivers/')
@rest_api.route('/drivers')
def returnDrivers():
   cur = mysql.connection.cursor()
   cur.execute('SELECT VehicleID, LastName, FirstName, DOB, NINo, DrivingLicenseNo, DrivingLicensePic, Address1, Address2, City, PostCode, Country, DateCreated, LastConnected FROM Drivers')
   row_headers=[x[0] for x in cur.description] #this will extract row headers
   rv = cur.fetchall()
   json_data=[]
   for result in rv:
        json_data.append(dict(zip(row_headers,result)))
   return json.dumps(json_data, default=str)

@rest_api.route('/vehicles/')
@rest_api.route('/vehicles')
def returnVehicles():
   cur = mysql.connection.cursor()
   cur.execute('SELECT * FROM Vehicles')
   row_headers=[x[0] for x in cur.description] #this will extract row headers
   rv = cur.fetchall()
   json_data=[]
   for result in rv:
        json_data.append(dict(zip(row_headers,result)))
   return json.dumps(json_data, default=str)

@rest_api.route('/customers/')
@rest_api.route('/customers')
def returnCustomers():
   cur = mysql.connection.cursor()
   cur.execute('SELECT * FROM Customers')
   row_headers=[x[0] for x in cur.description] #this will extract row headers
   rv = cur.fetchall()
   json_data=[]
   for result in rv:
        json_data.append(dict(zip(row_headers,result)))
   return json.dumps(json_data, default=str)

@rest_api.route('/locations/')
@rest_api.route('/locations')
def returnLocations():
   cur = mysql.connection.cursor()
   cur.execute('SELECT * FROM Jobs')
   row_headers=[x[0] for x in cur.description] #this will extract row headers
   rv = cur.fetchall()
   json_data=[]
   for result in rv:
        json_data.append(dict(zip(row_headers,result)))
   return json.dumps(json_data, default=str)

@rest_api.route('/receipts/')
@rest_api.route('/receipts')
def returnReceipts():
   cur = mysql.connection.cursor()
   cur.execute('SELECT * FROM Receipts')
   row_headers=[x[0] for x in cur.description] #this will extract row headers
   rv = cur.fetchall()
   json_data=[]
   for result in rv:
        json_data.append(dict(zip(row_headers,result)))
   return json.dumps(json_data, default=str)
