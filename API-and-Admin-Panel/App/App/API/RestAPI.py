import sys, os
[sys.path.append('/var/www/html/env/App/App')]

from flask import Blueprint, jsonify, make_response, request
from flask import current_app as app
from extensions import mysql
from flask_cors import CORS
import json, datetime, calendar, requests
from requests.auth import HTTPDigestAuth
from werkzeug.utils import secure_filename

rest_api = Blueprint('rest_api', __name__)
CORS(rest_api)

@rest_api.route("/")
def apiDefault():
    return make_response(jsonify([{"Name":"Aquarian REST API","Version":"0.1","Created":"08/02/2020","LastModified":"29/02/2020"}]))


# Jobs

@rest_api.route('/jobs/')
@rest_api.route('/jobs', methods=['GET', 'POST'])
def returnJobs():
  if request.method == 'GET':
     if "limit1" in request.args and "limit2" in request.args:
        return getTable("Jobs", request.args["limit1"], request.args["limit2"])
     else:
        return getAllTable("Jobs")
  elif request.method == 'POST':
    return createRecord(request, "Jobs", "JobID")



@rest_api.route('/jobs/<int:id>/')
@rest_api.route('/jobs/<int:id>', methods=['GET', 'PUT', 'DELETE'])
def get_job(id):
  if request.method == 'GET':
    return getRecord("Jobs", "JobID", id)
  elif request.method == 'PUT':
    return updateTable(request, 'Jobs', 'JobID', id)
  elif request.method == 'DELETE':
    return deleteRecord("Jobs", "JobID", id)


@rest_api.route('/jobs/pending', methods=['GET'])
def returnPendingJobs():
     if "limit1" in request.args and "limit2" in request.args:
       cur = mysql.connection.cursor()
       cur.execute("SELECT * FROM Jobs WHERE Status='Pending' LIMIT " + request.args['limit1'] + " OFFSET " + request.args['limit2'])
       row_headers=[x[0] for x in cur.description]
       rv = cur.fetchall()
       json_data=[]
       for result in rv:
            json_data.append(dict(zip(row_headers,result)))
       #return json.dumps(json_data, default=str)
       resp = make_response(json.dumps(json_data, default=str))
       resp.headers['Content-Type'] = 'application/json'
       return resp
     else:
       cur = mysql.connection.cursor()
       cur.execute("SELECT * FROM Jobs WHERE Status='Pending'")
       row_headers=[x[0] for x in cur.description]
       rv = cur.fetchall()
       json_data=[]
       for result in rv:
            json_data.append(dict(zip(row_headers,result)))
       #return json.dumps(json_data, default=str)
       resp = make_response(json.dumps(json_data, default=str))
       resp.headers['Content-Type'] = 'application/json'
       return resp

@rest_api.route('/jobs/delivered', methods=['GET'])
def returnDoneJobs():
     if "limit1" in request.args and "limit2" in request.args:
       cur = mysql.connection.cursor()
       cur.execute("SELECT * FROM Jobs WHERE Status='Delivered' LIMIT " + request.args['limit1'] + " OFFSET " + request.args['limit2'])
       row_headers=[x[0] for x in cur.description]
       rv = cur.fetchall()
       json_data=[]
       for result in rv:
            json_data.append(dict(zip(row_headers,result)))
       #return json.dumps(json_data, default=str)
       resp = make_response(json.dumps(json_data, default=str))
       resp.headers['Content-Type'] = 'application/json'
       return resp
     else:
       cur = mysql.connection.cursor()
       cur.execute("SELECT * FROM Jobs WHERE Status='Delivered'")
       row_headers=[x[0] for x in cur.description]
       rv = cur.fetchall()
       json_data=[]
       for result in rv:
            json_data.append(dict(zip(row_headers,result)))
       #return json.dumps(json_data, default=str)
       resp = make_response(json.dumps(json_data, default=str))
       resp.headers['Content-Type'] = 'application/json'
       return resp


@rest_api.route('/jobs/<string:id>/location', methods=['GET', 'PUT'])
def getParcelLocation(id):
  if request.method == 'GET':
     cur = mysql.connection.cursor()
     cur.execute("SELECT DriverID FROM Jobs WHERE TrackingID=%s", (id,))
     if cur.rowcount == 0:
        return make_response(jsonify([{"Error": "No matching ID found in database."}]), 404)
     else:
       result = cur.fetchone()
       cur.execute("SELECT DriverID, FirstName, Location FROM Drivers WHERE DriverID=%s", (result[0],))
       if cur.rowcount == 0:
          return make_response(jsonify([{"Error": "No matching ID found in database."}]), 404)
       else:
          row_headers=[x[0] for x in cur.description]
          rv = cur.fetchall()
          json_data=[]
          for result in rv:
              json_data.append(dict(zip(row_headers,result)))
          resp = make_response(json.dumps(json_data, default=str))
          resp.headers['Content-Type'] = 'application/json'
          return resp


# Drivers

@rest_api.route('/drivers/')
@rest_api.route('/drivers', methods=['GET', 'POST'])
def returnDrivers():
  if request.method == 'GET':
     if "limit1" in request.args and "limit2" in request.args:
         sql = 'SELECT DriverID, VehicleID, Username, LastName, FirstName, DOB, NINo, DrivingLicenseNo, DrivingLicensePic, Address1, Address2, City, PostCode, Country, Location, DateCreated, LastConnected FROM Drivers LIMIT ' + request.args["limit1"] + ' OFFSET ' + request.args["limit2"]
     else:
         sql = 'SELECT DriverID, VehicleID, Username, LastName, FirstName, DOB, NINo, DrivingLicenseNo, DrivingLicensePic, Address1, Address2, City, PostCode, Country, Location, DateCreated, LastConnected FROM Drivers'
     cur = mysql.connection.cursor()
     cur.execute(sql)
     row_headers=[x[0] for x in cur.description]
     rv = cur.fetchall()
     json_data=[]
     for result in rv:
          json_data.append(dict(zip(row_headers,result)))
     #return json.dumps(json_data, default=str)
     resp = make_response(json.dumps(json_data, default=str))
     resp.headers['Content-Type'] = 'application/json'
     return resp
  elif request.method == 'POST':
    try:
     _json = request.args
     conn = mysql.connection
     cur = conn.cursor()
     cur.execute("SELECT * FROM Drivers")
     if cur.rowcount == 0:
        return make_response(jsonify([{"Error": "No table " + table + " found in database."}]), 404)
     else:
        params = []
        headers = []
        row_headers=[x[0] for x in cur.description]
        for header in row_headers:
            headerExist = False
            if header in request.args:
               headerExist = True
            if not headerExist:
               if (header != 'DriverID'):
                   headers.append(header)
                   params.append('None')
            else:
               headers.append(header)
               params.append(_json[header])

        #Generate driver username
        uname = _json['FirstName'] + _json['LastName']
        x = 1
        while x != 0:
              cur.execute("SELECT * FROM Drivers WHERE Username='" + uname + "'")        
              if cur.rowcount != 0:
                 if x == 1:
                    uname += str(x)
                    x+=1
                 else:
                    uname = uname[:-1] + str(x)
                    x+=1
              else:
                x=0


        sql = "INSERT INTO Drivers ( "
        for x in range(0,len(headers)):
            if headers[x] == 'DateCreated':
               params[x] = 'NOW()'
            if headers[x] == 'Username':
               params[x] = uname
            if headers[x] == 'Password':
               params[x] = 'Aquarian'

            sql += ''.join(str(v) for v in headers[x]) + ','
        sql = sql[:-1]
        sql += ") VALUES ("
        for x in range(0,len(params)):
            if params[x] == 'NOW()':
              sql += ''.join(str(v) for v in params[x]) + ","
            else:
              sql += "'" + ''.join(str(v) for v in params[x]) + "',"
        sql = sql[:-1]

        sql += ")"
        sql = sql.replace("'None'","null")
        cur.execute(sql)
        conn.commit()
        resp = make_response(jsonify([{'Success': 'Record added to table Drivers successfully!'}]))
        resp.status_code = 200
        return resp

    except Exception as e:
       return jsonify({'Error':'Line {}: '.format(sys.exc_info()[-1].tb_lineno) + str(e)})


@rest_api.route('/drivers/<int:id>/')
@rest_api.route('/drivers/<int:id>', methods=['GET', 'PUT', 'DELETE'])
def get_driver(id):
  if request.method == 'GET':
     cur = mysql.connection.cursor()
     cur.execute("SELECT DriverID, VehicleID, LastName, FirstName, DOB, NINo, DrivingLicenseNo, DrivingLicensePic, Address1, Address2, City, PostCode, Country, Location, DateCreated, LastConnected FROM Drivers WHERE DriverID = %s", (id,))
     if cur.rowcount == 0:
        return make_response(jsonify([{"Error": "No matching ID found in database."}]), 404)
     else:
        row_headers=[x[0] for x in cur.description]
        rv = cur.fetchall()
        json_data=[]
        for result in rv:
            json_data.append(dict(zip(row_headers,result)))
        resp = make_response(json.dumps(json_data, default=str))
        resp.headers['Content-Type'] = 'application/json'
        return resp
  elif request.method == 'PUT':
      return updateTable(request, 'Drivers', 'DriverID', id)
  elif request.method == 'DELETE':
    return deleteRecord("Drivers", "DriverID", id)


@rest_api.route('/drivers/<int:id>/location', methods=['GET', 'PUT'])
def getLocation(id):
  if request.method == 'GET':
     cur = mysql.connection.cursor()
     cur.execute("SELECT DriverID, FirstName, Location FROM Drivers WHERE DriverID=%s", (id,))
     if cur.rowcount == 0:
        return make_response(jsonify([{"Error": "No matching ID found in database."}]), 404)
     else:
        row_headers=[x[0] for x in cur.description]
        rv = cur.fetchall()
        json_data=[]
        for result in rv:
            json_data.append(dict(zip(row_headers,result)))
        resp = make_response(json.dumps(json_data, default=str))
        resp.headers['Content-Type'] = 'application/json'
        return resp
  elif request.method == 'PUT':
    return updateTable(request, 'Drivers', 'DriverID', id)

# Driver Login (Android App)
@rest_api.route('/drivers/login', methods=['POST'])
def driver_login():
    _json = request.args
    conn = mysql.connection
    cur = conn.cursor()
    cur.execute("SELECT Password FROM Drivers WHERE Username='" + _json['Username'] + "'")
    if cur.rowcount == 0:
       return make_response(jsonify([{"Status":"Error","Message":"Login failed: Wrong Username"}]), 200)
    else:
       result = cur.fetchone()
       if _json['Password'] == result[0]:
          cur.execute("UPDATE Drivers SET LastConnected = NOW() WHERE Username='" + _json['Username'] + "'")
          conn.commit()
          cur.execute("SELECT DriverID, FirstName, LastConnected, VehicleID FROM Drivers WHERE Username='" + _json['Username'] + "'")
          result = cur.fetchone()
          return make_response(jsonify([{"Status":"Success","Message":"Login Succesful","DriverID":result[0],"FirstName":result[1],"LastConnected":result[2],"VehicleID":result[3]}]), 200)
       else:
          return make_response(jsonify([{"Status":"Error","Message":"Login failed: Wrong Password"}]), 200)


# Vehicles

@rest_api.route('/vehicles/')
@rest_api.route('/vehicles', methods=['GET', 'POST'])
def returnVehicles():
  if request.method == 'GET':
     if "limit1" in request.args and "limit2" in request.args:
        return getTable("Vehicles", request.args["limit1"], request.args["limit2"])
     else:
        return getAllTable("Vehicles")
  elif request.method == 'POST':
    return createRecord(request, "Vehicles", "VehicleID")


@rest_api.route('/vehicles/<int:id>/')
@rest_api.route('/vehicles/<int:id>', methods=['GET', 'PUT', 'DELETE'])
def get_vehicle(id):
  if request.method == 'GET':
    return getRecord("Vehicles", "VehicleID", id)
  elif request.method == 'PUT':
    return updateTable(request, 'Vehicles', 'VehicleID', id)
  elif request.method == 'DELETE':
    return deleteRecord("Vehicles", "VehicleID", id)





# Customers

@rest_api.route('/customers/')
@rest_api.route('/customers', methods=['GET', 'POST'])
def returnCustomers():
  if request.method == 'GET':
     if "limit1" in request.args and "limit2" in request.args:
        return getTable("Customers", request.args["limit1"], request.args["limit2"])
     else:
        return getAllTable("Customers")
  elif request.method == 'POST':
    return createRecord(request, "Customers", "CustomerID")


@rest_api.route('/customers/<int:id>/')
@rest_api.route('/customers/<int:id>', methods=['GET', 'PUT', 'DELETE'])
def get_customer(id):
  if request.method == 'GET':
    return getRecord("Customers", "CustomerID", id)
  elif request.method == 'PUT':
    return updateTable(request, 'Customers', 'CustomerID', id)
  elif request.method == 'DELETE':
    return deleteRecord("Customers", "CustomerID", id)



# Pick-Up and Drop-Off Locations

@rest_api.route('/locations/')
@rest_api.route('/locations', methods=['GET', 'POST'])
def returnLocations():
  if request.method == 'GET':
     if "limit1" in request.args and "limit2" in request.args:
        return getTable("Locations", request.args["limit1"], request.args["limit2"])
     else:
        return getAllTable("Locations")
  elif request.method == 'POST':
    return createRecord(request, "Locations", "CustomerID")

@rest_api.route('/locations/<int:id>/')
@rest_api.route('/locations/<int:id>', methods=['GET', 'PUT', 'DELETE'])
def get_location(id):
  if request.method == 'GET':
    return getRecord("Locations", "LocationID", id)
  elif request.method == 'PUT':
    return updateTable(request, 'Locations', 'LocationID', id)
  elif request.method == 'DELETE':
    return deleteRecord("Locations", "LocationID", id)



# Receipts

@rest_api.route('/receipts/')
@rest_api.route('/receipts', methods=['GET', 'POST'])
def returnReceipts():
  if request.method == 'GET':
     if "limit1" in request.args and "limit2" in request.args:
        return getTable("Receipts", request.args["limit1"], request.args["limit2"])
     else:
        return getAllTable("Receipts")
  elif request.method == 'POST':
     return createRecord(request, "Receipts", "ReceiptID")


@rest_api.route('/receipts/<int:id>/')
@rest_api.route('/receipts/<int:id>', methods=['GET', 'PUT', 'DELETE'])
def get_receipt(id):
  if request.method == 'GET':
    return getRecord("Receipts", "ReceiptID", id)
  elif request.method == 'PUT':
    return updateTable(request, 'Receipts', 'ReceiptID', id)
  elif request.method == 'DELETE':
    return deleteRecord("Receipts", "ReceiptID", id)


@rest_api.route('/receipts/driver/<int:id>/')
@rest_api.route('/receipts/driver/<int:id>', methods=['GET'])
def get_receipt_by_driver(id):
     cur = mysql.connection.cursor()
     cur.execute("SELECT * FROM Receipts WHERE DriverID=%s AND DATE(DateCreated)=DATE(NOW())", (id,))
     row_headers=[x[0] for x in cur.description]
     rv = cur.fetchall()
     json_data=[]
     for result in rv:
          json_data.append(dict(zip(row_headers,result)))
     #return json.dumps(json_data, default=str)
     resp = make_response(json.dumps(json_data, default=str))
     resp.headers['Content-Type'] = 'application/json'
     return resp
  


# FULL JOB

@rest_api.route('/jobs/full/<int:id>/')
@rest_api.route('/jobs/full/<int:id>', methods=['GET'])
def get_full_job(id):
   cur = mysql.connection.cursor()

   cur.execute("SELECT JobId, TrackingID, Status, ParcelType, ParcelSize, ParcelWeight, DateCreated, DateDue, DateDelivered, DistanceTravelled, Picture1, Picture2, Comments FROM Jobs WHERE JobID = %s", (id,))
   if cur.rowcount == 0:
      return '{"Error":"No matching ID was found in the database."}'
   else:
      row_headers=[x[0] for x in cur.description]
      rv = cur.fetchall()
      job_data=[]
      for result in rv:
          job_data.append(dict(zip(row_headers,result)))
      job_data = json.dumps(job_data, default=str)

      cur.execute("SELECT CustomerID FROM Jobs WHERE JobID = %s", (id,))
      rv = cur.fetchall()
      for row in rv:
          cid=row[0]

      cur.execute("SELECT * FROM Customers WHERE CustomerID = %s", (cid,))
      row_headers=[x[0] for x in cur.description]
      rv = cur.fetchall()
      customer_data=[]
      for result in rv:
          customer_data.append(dict(zip(row_headers,result)))
      customer_data = json.dumps(customer_data, default=str)

      cur.execute("SELECT PickupID FROM Jobs WHERE JobID = %s", (id,))
      rv = cur.fetchall()
      for row in rv:
          cid=row[0]

      cur.execute("SELECT * FROM Locations WHERE LocationID = %s", (cid,))
      row_headers=[x[0] for x in cur.description]
      rv = cur.fetchall()
      pickup_data=[]
      for result in rv:
          pickup_data.append(dict(zip(row_headers,result)))
      pickup_data = json.dumps(pickup_data, default=str)

      cur.execute("SELECT DropOffID FROM Jobs WHERE JobID = %s", (id,))
      rv = cur.fetchall()
      for row in rv:
          cid=row[0]

      cur.execute("SELECT * FROM Locations WHERE LocationID = %s", (cid,))
      row_headers=[x[0] for x in cur.description]
      rv = cur.fetchall()
      dropoff_data=[]
      for result in rv:
          dropoff_data.append(dict(zip(row_headers,result)))
      dropoff_data = json.dumps(dropoff_data, default=str)

      #return str('[{"Job":%s,"Customer":%s,"Pickup":%s,"Dropoff":%s}]' % (job_data, customer_data, pickup_data, dropoff_data))
      resp = make_response(str('{"Job":%s,"Customer":%s,"Pickup":%s,"Dropoff":%s}' % (job_data, customer_data, pickup_data, dropoff_data)))
      resp.headers['Content-Type'] = 'application/json'
      return resp


@rest_api.route('/drivers/assigned/<int:id>/')
@rest_api.route('/drivers/assigned/<int:id>', methods=['GET'])
def getAssignedJobs(id):
   cur = mysql.connection.cursor()
   cur.execute("SELECT JobID FROM Jobs WHERE Status='Pending' AND DriverID=%s", (id,))
   if cur.rowcount == 0:
      return '{"Error":"No jobs found."}'
   else:
      row_headers=[x[0] for x in cur.description]
      rv = cur.fetchall()
      json_data=[]
      for result in rv:
          json_data.append(dict(zip(row_headers,result)))
      #resp = make_response(json.dumps(json_data, default=str))
      #resp.headers['Content-Type'] = 'application/json'
      #return resp
      #resp = json.loads(json.dumps(json_data, default=str))
      #resp = json.loads(resp.content)
      full_array=[]
      url = "http://soc-web-liv-82.napier.ac.uk/api/jobs/full/"
      for data in json_data:
        myResponse = requests.get(url + str(data['JobID']))
        if (myResponse.ok):
           full_array.append(json.loads(myResponse.content))
      resp = make_response(json.dumps(full_array, default=str))
      resp.headers['Content-Type'] = 'application/json'
      return resp

# 404 Error Handler
@rest_api.route("<path:invalid_path>")
def missing_resource(invalid_path):
    return make_response(jsonify([{"Error": "Not Found"}]), 404) 


# Admin Login
@rest_api.route('/admin', methods=['POST'])
def admin_login():
    return login(request, "Admins")


# GET METHOD

def getAllTable(table):
     cur = mysql.connection.cursor()
     cur.execute('SELECT * FROM ' + table)
     row_headers=[x[0] for x in cur.description]
     rv = cur.fetchall()
     json_data=[]
     for result in rv:
          json_data.append(dict(zip(row_headers,result)))
     #return json.dumps(json_data, default=str)
     resp = make_response(json.dumps(json_data, default=str))
     resp.headers['Content-Type'] = 'application/json'
     return resp

def getTable(table, limit1, limit2):
     cur = mysql.connection.cursor()
     cur.execute('SELECT * FROM ' + table + ' LIMIT ' + limit1 + ' OFFSET ' + limit2)
     row_headers=[x[0] for x in cur.description]
     rv = cur.fetchall()
     json_data=[]
     for result in rv:
          json_data.append(dict(zip(row_headers,result)))
     #return json.dumps(json_data, default=str)
     resp = make_response(json.dumps(json_data, default=str))
     resp.headers['Content-Type'] = 'application/json'
     return resp


# GET RECORD
def getRecord(table, idname, id):
     cur = mysql.connection.cursor()
     cur.execute("SELECT * FROM " + table + " WHERE " + idname + " = %s", (id,))
     if cur.rowcount == 0:
        return make_response(jsonify([{"Error": "No matching ID found in database."}]), 404)
     else:
        row_headers=[x[0] for x in cur.description]
        rv = cur.fetchall()
        json_data=[]
        for result in rv:
            json_data.append(dict(zip(row_headers,result)))
        resp = make_response(json.dumps(json_data, default=str))
        resp.headers['Content-Type'] = 'application/json'
        return resp



# DELETE RECORD
def deleteRecord(table, idname, id):
    conn = mysql.connection
    cursor = conn.cursor()
    cursor.execute("DELETE FROM " + table + " WHERE " + idname + " = %s", (id,))
    conn.commit()
    resp = make_response(jsonify([{"Status":"One row with id " + str(id) + " deleted from " + table}]))
    return resp

# CREATE METHOD

def createRecord(request, table, idname):
    try:
     _json = request.args
     conn = mysql.connection
     cur = conn.cursor()
     cur.execute("SELECT * FROM "+ table)
     if cur.rowcount == 0:
        return make_response(jsonify([{"Error": "No table " + table + " found in database."}]), 404)
     else:
        params = []
        headers = []
        row_headers=[x[0] for x in cur.description]
        for header in row_headers:
            headerExist = False
            if header in request.args:
               headerExist = True
            if not headerExist:
               if header != idname:
                  headers.append(header)
                  params.append('None')
            else:
               headers.append(header)
               params.append(_json[header])
         
        #params.append(idname)
        #params.append(_id)
        sql = "INSERT INTO "+ table +"( "
        for x in range(0,len(headers)):
            if headers[x] == 'DateCreated':
               params[x] = 'NOW()'
            sql += ''.join(str(v) for v in headers[x]) + ','
        sql = sql[:-1]
        sql += ") VALUES ("
        for x in range(0,len(params)):
            if params[x] == 'NOW()':
              sql += ''.join(str(v) for v in params[x]) + ","
            else:
              sql += "'" + ''.join(str(v) for v in params[x]) + "',"
        sql = sql[:-1]

        sql += ")"
        sql = sql.replace("'None'","null")
        cur.execute(sql)
        conn.commit()
        if table == 'Jobs':
           current_date = datetime.date.today()
           trackid = 'AQ' + current_date.strftime("%m%y") + str(cur.lastrowid)
           sql = 'UPDATE Jobs SET TrackingID=\'' + trackid + '\' WHERE JobID=' + str(cur.lastrowid)
           cur.execute(sql)
           conn.commit()
        resp = make_response(jsonify([{'Success': 'Record added to table ' + table + ' successfully!'}]))
        resp.status_code = 200
        return resp

    except Exception as e:
       return jsonify({'Error':'Line {}: '.format(sys.exc_info()[-1].tb_lineno) + str(e)})


# UPDATE METHOD

def updateTable(request, table, idname, id):
    try:
     _json = request.args
     conn = mysql.connection
     cur = conn.cursor()
     cur.execute("SELECT * FROM "+ table +" WHERE "+ idname + "=" + str(id))
     if cur.rowcount == 0:
        return make_response(jsonify([{"Error": "No matching ID found in database."}]), 404)
     else:
        params = []
        row_headers=[x[0] for x in cur.description]
        for header in row_headers:
            headerExist = False
            if header in request.args:
               headerExist = True
            if not headerExist:
               cur.execute("SELECT " + header + " FROM " + table + " WHERE " + idname +  "=" + str(id))
               result = cur.fetchone()
               params.append(header)
               params.append(result)
            else:
               params.append(header)
               params.append(_json[header])

        sql = "UPDATE "+ table +" SET "
        for x in range(0,len(params),2):
            sql += ''.join(str(v) for v in params[x]) + "='" + ''.join(str(v) for v in params[x+1]) + "',"
        sql = sql[:-1]
        sql += " WHERE " + idname + "=" + str(id)
        sql = sql.replace("'None'","null")

        cur.execute(sql)
        conn.commit()
        resp = make_response(jsonify([{'Success': 'Table ' + table + ' edited successfully!'}]))
        resp.status_code = 200
        return resp

    except Exception as e:
       return jsonify({'Error':str(e)})


def login(request, table):
    _json = request.args
    conn = mysql.connection
    cur = conn.cursor()
    cur.execute("SELECT Password FROM " + table + " WHERE Username='" + _json['Username'] + "'")
    if cur.rowcount == 0:
       return make_response(jsonify([{"Status":"Error","Message":"Login failed: Wrong Username"}]), 200)
    else:
       result = cur.fetchone()
       if _json['Password'] == result[0]:
          cur.execute("UPDATE " + table + " SET LastConnected = NOW() WHERE Username='" + _json['Username'] + "'")
          conn.commit()
          return make_response(jsonify([{"Status":"Success","Message":"Login Succesful","Username":_json['Username']}]), 200)
       else:
          return make_response(jsonify([{"Status":"Error","Message":"Login failed: Wrong Password"}]), 200)


# Admin Login
@rest_api.route('/money', methods=['GET'])
def getMoney():
    current_date = datetime.date.today()
    current_month = current_date.month + 1
    json = '['
    for month in range(1, current_month):
        json += '{"Month":"'+ calendar.month_name[month] +'",'
        amount = monthRevenue(month)
        receipt = monthReceipts(month)
        if amount != None:
           json += '"Amount":"'+str(round(amount,2))+'",'
        else:
           json += '"Amount":null,'
        if receipt != None:
           json += '"Receipts":"'+str(round(receipt,2))+'"},'
        else:
           json += '"Receipts":null},'
    json = json[:-1] + ']'
    return json


def monthRevenue(month):
    sql = 'SELECT SUM(PricePaid) FROM Jobs WHERE MONTH(DateDelivered)=' + str(month)
    conn = mysql.connection
    cur = conn.cursor()
    cur.execute(sql)
    if cur.rowcount == 0:
        return "No data for this month"
    else:
       result = cur.fetchone()
       return result[0]

def monthReceipts(month):
    sql = 'SELECT SUM(Amount) FROM Receipts WHERE MONTH(DateCreated)=' + str(month)
    conn = mysql.connection
    cur = conn.cursor()
    cur.execute(sql)
    if cur.rowcount == 0:
        return "No data for this month"
    else:
       result = cur.fetchone()
       return result[0]

ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@rest_api.route('/receipts/uploads/')
@rest_api.route('/receipts/uploads', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        # check if the post request has the file part
        if 'file' not in request.files:
          return make_response(jsonify([{"Message": "ERROR - No file present."}]), 200)
        file = request.files['file']
        # if user does not select file, browser also
        # submit a empty part without filename
        if file.filename == '':
          return make_response(jsonify([{"Message": "ERROR - No filename"}]), 200)
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
            return make_response(jsonify([{"Message": "File uploaded successfully."}]), 200)