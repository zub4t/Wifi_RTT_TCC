
<h1>WIFI RTT SCAN APP</h1>
<h2> Introduction </h2>
This document contains detailed information on how to use the WIFI RTT SCAN APP to build datasets and also what the steps will be for the next week (Fourth week of December 2022) to generate a systematic dataset of Wifi RTT measurements

<h2> 802.11mc intro </h2>
802.11mc is a standard of Wi-Fi technology developed by the IEEE. It is also known as Wi-Fi Round Trip Time (RTT). It is designed to improve the accuracy of location services, indoor navigation, and other location-based services. It enables devices to measure the round-trip time (RTT) of signals between devices and use this information to calculate the distance between the devices. It also provides location accuracy within 1-2 meters. This technology is also used for indoor positioning, asset tracking, and other indoor navigation applications. It is an important component of the Internet of Things (IoT) and connected devices.

<h2> WIFI RTT SCAN APP intro </h2>
<h3>
First look
</h3>
This app contains two activities that will work together to get Wifi RTT measurements. The first activity is the main activity and this activity will be scanned to find access points that support 802.11mc technology.

In the figure bellow it is possible to see the look and feel for this activity.
<p align='center'>
<img src='https://raw.githubusercontent.com/zub4t/Wifi_RTT_TCC/master/Screenshot_20221218-135643%5B1%5D.png' width='200'/>
</p>
<h3>
Scan Wifi
</h3>
When the Scan WiFi button is clicked, the app will search for all access points that support 802.11mc and list them. The image below demonstrates how the activity will appear.

<p align='center'>
<img src='https://raw.githubusercontent.com/zub4t/Wifi_RTT_TCC/master/Screenshot_20221218-135652%5B1%5D.png' width='200'/>
</p>
<h3>
Adding more information
</h3>
There is some information that is not possible to infer from the app. Therefore, when clicking on one of the listed SSIDs, the application will focus on another activity. Here it is possible to define the real distance and X , Y , Z Coordinates. Once the user finishes to fulfill the form and click in the return button the application will bring the main activity again and the SSIDs that already have additional information will be displayed in red color text.
<p align='center'>
<img src='https://raw.githubusercontent.com/zub4t/Wifi_RTT_TCC/master/Screenshot_20221218-135703%5B1%5D.png' width='200'/>
</p>
<h3>
Back to the main activity
</h3>
When the user has finished filling in all the additional information for all listed SSIDs, the application will allow the user to start building the RTT dataset
<p align='center'>
<img src='
https://raw.githubusercontent.com/zub4t/Wifi_RTT_TCC/master/Screenshot_20221218-135732%5B1%5D.png' width='200'/>
</p>
<h3>
Realtime Database
</h3>
All information collected will be sent to a real-time database that can be accessed through this <a href='https://console.firebase.google.com/u/0/project/ftm-dataset/database/ftm-dataset-default-rtdb/data'>link </a>. The information is stored in JSON format. Below is an example of the saved data.

<pre>
BSSID :"7c:df:a1:db:03:49"
RSSI :"-59"
SSID:"FTM_Responder_4"
distance: 35.417999267578125
distanceStdDevM: 0.24300000071525574
numAttemptedMeasurements: 8
numSuccessfulMeasurements: 5
rangingTimestampMillis: 183758450
time: 1671371859303
trueDistance: 1
xCoordinate: 1
yCoordinate: 1
zCoordinate: 1
</pre>
