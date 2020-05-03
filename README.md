# Transportation Management System

This project is a full-suite for a delivery company. It includes:

 - Customers website: general information about the company and parcel tracking option
 - Admin Panel: allows an admin user to manage the entire database in a user-firendly environment and also provide business oriented applications (revenue and spendings overview, jobs and drivers assignments and management...)
 - Drivers App: android application that allows drivers to see the jobs and vehicle assigned to them, mark jobs as completed, request for a customer signature, upload receipts and parcel pictures to the server, etc.
 - API: the pivotal element that connects and makes the three above services work

### 0. Web Server

The web server uses *Python FLASK*. The API, Admin Panel and Customer's Website are therefore flask applications.  
The whole setup uses Blueprints to separate the three areas. A main app is initiated, and sub-apps (api, admin panel and customer websites) are inititated within the main App.  
*Python* is used as server-side language.

[See App.py setup and blueprints registration here](https://github.com/musevarg/Transportation-Management-System/blob/master/API-and-Admin-Panel/App/App/App.py).

### 1. API

The API is written in *Python* and *SQL*.

The API is used to fetch, update and remove content from the database. It returns JSON responses and handles GET, POST, PUT and DELETE methods.

[See API code here](https://github.com/musevarg/Transportation-Management-System/blob/master/API-and-Admin-Panel/App/App/API/RestAPI.py).

Below is a sample output for each request method:
![](https://www.sedhna.com/tps/scr1.png)

### 2. Admin Panel

The admin panel allows an admin user to update the MySQL database. The admin can add, remove and amend records.

It is developed using *HTML*, *CSS*, *JavaScript* and *jQuery* to perform API calls. It makes extensive use of bootstrap and the above API.

It also contains a dashboard screen that allows for the admin to check the monthly revenue and the monthly fees (fuel, lunch, MOT).

[See Admin Panel code here](https://github.com/musevarg/Transportation-Management-System/tree/master/API-and-Admin-Panel/App/App/AdminPanel).

![](https://www.sedhna.com/tps/scr2.png)

### 3. Android Application

The API allows for users authentication and also provides content to the native application.
It allows for delivery drivers to log in and see what vehicle has been assigned to them, how many jobs have been assigned to them and allows them to mark a job as completed. This updates the status of the job in the database and uploads a picture of the parcel and the customer's signature.
It also permits for uploading receipts. This content can be retrieved in the admin panel.

[See Android App code here](https://github.com/musevarg/Transportation-Management-System/tree/master/Drivers-Android-App/app/src/main).

![](https://www.sedhna.com/tps/scr3.png)

### 4. Customers Website

This simple website gives information about the company and allows sutomers to track their parcel (the API is used for that).

[See website code here](https://github.com/musevarg/Transportation-Management-System/tree/master/API-and-Admin-Panel/App/App/Website).

Below is an example of a parcel being tracked:
![](https://www.sedhna.com/tps/scr4.png)
