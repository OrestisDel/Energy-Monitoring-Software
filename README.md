# Energy-Monitoring-Software
This is an Energy Monitoring Software developed as part of my final year research project. 

The solution is comprised of a TCP server and multiple TCP client software existing either on Windows or Android devices connected to a Local Area Network using Ethernet or Wi-Fi.

The server connects to a MySQL data table and imports data figures from it.

Clients get this data from the server in order to display it and do their own analysis on it. The application also gives the option to filter out specific time periods, plot graphs, calculate stats and costs, generate reports to Excel or send via e-mail.

For Windows machines, the Visual Studio 2019 Windows Forms with the MySQL add-on has been used to develop the server and windows client apps in C#.

For Android machines the Android studio v3.6 with the MPAndroid Charts add-on has been used to develop the client app in Java.
