import sys
import os
[sys.path.append('/var/www/html/env/App/App')]
from flask import Flask, redirect
from App.Website.LandingPage import landing
from App.API.RestAPI import rest_api
from App.AdminPanel.AdminPanel import admin_panel

app = Flask(__name__)
app.secret_key = 'yuveyuveyu'

from extensions import mysql
app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'aquarian'
app.config['MYSQL_PASSWORD'] = 'Aquarian123*'
app.config['MYSQL_DB'] = 'aquarian'

APP_ROOT = os.path.dirname(os.path.abspath(__file__))
UPLOAD_FOLDER = os.path.join(APP_ROOT, 'AdminPanel/static/images/receipts')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

mysql.init_app(app)

app.register_blueprint(landing, url_prefix='/home')
app.register_blueprint(rest_api, url_prefix='/api')
app.register_blueprint(admin_panel, url_prefix='/admin')

@app.route('/')
def hello():
    return redirect("/home", code=301)

if __name__ == '__main__':
    app.run()


