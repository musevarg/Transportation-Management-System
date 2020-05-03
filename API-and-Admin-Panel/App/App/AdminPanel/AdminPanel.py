from flask import Blueprint, render_template, request, redirect, session, make_response, jsonify
from extensions import mysql
import json

admin_panel = Blueprint('admin_panel', __name__, template_folder='templates', static_folder='static')

@admin_panel.route("/", methods=['GET', 'POST'])
def loginPage():
	if request.method == 'GET':
		if 'username' in session:
			return redirect("main")
		else:
			return render_template('login.html', error_message='')
	elif request.method == 'POST':
		username=request.form['username']
		password=request.form['password']
		conn = mysql.connection
		cur = conn.cursor()
		cur.execute("SELECT * FROM Admins WHERE Username=%s AND Password=%s", (username,password))
		if cur.rowcount == 0:
			error_message = 'Invalid username or password'
			return render_template('login.html', error_message=error_message)
		else:
			session['username'] = username
			sql = "UPDATE Admins SET LastConnected=NOW() WHERE Username='"+session['username']+"'"
			cur.execute(sql)
			conn.commit()
			return redirect("main")

@admin_panel.route("/main")
def mainPage():
	if 'username' in session:
		return render_template('main.html', user_name=session['username'])
	else:
		return redirect("../admin")


@admin_panel.route('/logout')
def logout():
   session.pop('username', None)
   return redirect("../admin")

@admin_panel.route('/changepassword', methods=['PUT'])
def changePassword():
	if 'username' in session:
		if request.method == 'PUT':
			_req = request.args
			oldPas = _req['oldPassowrd']
			newPas = _req['newPassword']
			conn = mysql.connection
			cur = conn.cursor()
			cur.execute("SELECT * FROM Admins WHERE Username=%s AND Password=%s", (session['username'],oldPas))
			if cur.rowcount == 0:
				resp = make_response(jsonify({"Status":"Error","Message":"Old password invalid"}))
				return resp
			else:
				sql = "UPDATE Admins SET Password='"+newPas+"' WHERE Username='"+session['username']+"'"
				cur.execute(sql)
				conn.commit()
				resp = make_response(jsonify({"Status":"Success","Message":"Password changed!"}))
				return resp
	else:
		return redirect("../admin")