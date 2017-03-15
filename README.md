# context-aware-smart-blinds

## Introduction –
The Smart Blinds System developed by us senses the temperature and ambient of the surroundings of the Raspberry Pi, which also runs a server for the system, and updates the position of the blinds (positions are represented by LED states) based on the Fuzzy Logic rules that have been given to the system with 8 predefined fuzzy rules that can be altered (remove existing rules / add new rules) from the user interface (android device) of the system.

## Getting to know the system –
### Extracting the files
The server (Raspberry pi) code is present in the folder ‘RaspberryPiCode.zip’ and the user interface (Android device) code is present in the folder ‘AndroidDeviceCode.zip’ inside the zipped folder ‘Group7_SmartBlind.zip’. The screenshots are present in the folder ‘Screenhots.zip’ in ‘Group7_SmartBlind.zip’. Extract these folders to access the code an screenshots.

### Compiling and Running the server on Raspberry Pi
1. Enter the ‘ritcoursematerials_student/PervasiveCourse_student/’ directory.
2. Run the command ‘sudo ant build’ to perform the build.
3. Run the command ‘sudo ant JsonRPCServer’ to build and run the Smart Blind System server on Raspberry Pi.

### Running the app on Android device
Open the application ‘SmartBlinds’ on your android device after installing.

### Communication model
1. This system uses a Publisher – Subscriber model as a means of communication between the Raspberry Pi server and the Android device.
2. The Raspberry Pi running the server is the Publisher
3. The Android device connecting to the server is the subscriber
4. The Publisher provides the Subscriber with temperature, ambient and blind status updates when the constraints for a minimum required temperature change have been met.

## Required features met
1. Developing a JSON – RPC program to answer queries about current sensor readings.
2. Developing a Fuzzy Logic controller for the Smart Blind system.
3. Using LED blinking patterns to mimic the motor.
4. Adding the given 5 fuzzy rules in a .fcl file and adding 3 additional rules.
5. Retrieving the current temperature and light intensity using JSON – RPC.
6. Notification – based data retrieval to receive updates when conditions are met.
7. Displaying history of updates using ListView.
8. Retrieval of list of rules that are currently registered in the controller.
9. UI for displaying the current temperature and light intensity, updating periodically using the notification based updates received from the Raspberry Pi server.
10. UI for adding and removing adaptation (Fuzzy) rules.
11. Raspberry Pi stores all new rules in the SD card. It loads the updated rules in the case of restarting from crash.

### Extra features introduced
#### Sensitivity change
   The minimum temperature change difference required for publishing an update from the Raspberry Pi to the Subscribing android device (Default value set to 2) can be altered to be set by the user to whatever they wish for it to be.
#### Subscription selection
The Android device can subscribe to any server that it wishes to before the main system starts up at the Android device side (Default IP in textbox is team’s Raspberry Pi IP).
#### Reconnect
If the server crashes, the Android app will not crash, and when the server starts again, the user can reconnect to it if they wish to.
#### Handoff between multiple devices and servers
By using the reconnect option, the user can also toggle between Android devices to get published notifications from the Raspberry Pi. A smooth handoff is done between devices by preserving the current rule updates made using the earlier device and reflecting them perfectly on the new device. The user may choose to seamlessly switch between Raspberry Pi servers and Android devices if they wish to even on the same session.
#### Notification bar updates
Every time an update is received from the Raspberry Pi server (Publisher), the Subscribing device gets a notification on the notification bar for events such as connection establishment, blind’s state change and altering of temperature change notification.
#### Temperature scale options
The user has an option available to view the temperature changes in either degree Celsius or degree Fahrenheit.
