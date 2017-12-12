# BlueBotController

NOTE:
This app is in the very early stages of development and will be getting a lot of love as it matures.

Features:
1) Bluetooth connection runs on a dedicated thread
2) A custom JoystickView mimicking a joystic interface
3) Normalized joystick positions taking with between -100 and 100
4) Joystick "hold" functionality implemented using a separate thread
5) Command interface

This is a basic Android App which controls Raspberry Pi W-based rover.  
It communicates over standard Bluetooth sockets using Serial Port Profile.  
A custom JoystickView serves as a user input mechanisms to control the rover.
The JoystickView takes the handle position and translates it into control input.
The X and Y coordinates specify direction, while the distance from the center specifies the motor speed.

The control commands are sent in a special string format over the the serial Bluetooth connection.  
The Python script on the Pi side seeks out valid commands using regular expressions,
parses the input and activates appropriate GPIO pins [See python page](https://github.com/vrestivo/pibluebot_public).

