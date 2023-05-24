# is0xSwiftCo
 
## Table of Contents
* [About the Application](#about-the-application)
* [Getting Started](#getting-started)
* [System Design](#system-design)
* [Basic Features](#basic-features)
* [Advanced Features](#advanced-features)
* [New Innovations](#new-innovations)
* [Demo](#demo)
* [Developed By](#developed-by)

## About the Application
_is0xSwiftCo_ is a painting application which allows multiple users to draw simultaneously on a canvas over the network. The system is implemented by Java, and it is using TCP and JSON as the tools for transmitting message between server and client. The architecture of the application is structured by client-server model and the server applies multithreading technology to handle the requests from clients. 

## Getting Started
- Get the jar files:
> In teriminal (package directory): `jar cf ${name for the created jar} *` 

- Start up the server:
 >In terminal: `java -jar server.jar -p <server-port>`<br>
 
 Type the first command in the terminal (the port argument is mandatory).

- Open the graphic user interface on the client side:
 >In terminal: `java -jar client.jar -a <server-address> -p <server-port>`<br>
 
 Type the first command in the terminal (the address and the port argument are mandatory).<br>
 For connecting to the local server, typing in “localhost” would work.
 
- Note:
 
 The user needs to make sure that the server is initiated before running the client application, otherwise the pipeline would just fail to connection.
 The command lines should be executed at the same directory where the .jar file is stored.

## System Design
- **Overview**<br>
To develop the program that allows multiple users to draw simultaneously on a canvas over the network, the applied concepts are socket, multi-threading, concurrency, and graphic user interface (GUI). The program is written by Java and several external libraries. Furthermore, the fundamental system architecture is client-server model which would be mainly relied on the performance of the server.

- **Socket**<br>
The socket applied for the connection between the server and client is TCP to make sure the connection is reliable and the message is sent through JSON format. Since the message includes the user’s stat, the content of chat and the canvas, all bytes are significant. Therefore, TCP could be the better choice than UDP in this circumstance.

- **Graphic User Interface (GUI)**<br>
The GUI in this program is built by _Swing (Jframe)_. There are two main panels: _Gui_ and _Whiteboard_ which have their individual task. To satisfy the chain of responsibility in behavioural design patterns, the socket will be passed into different panels (e.g., _ChatBox, ParticipantBox, Whiteboard_), therefore each component can use the socket to send the message to the server directly. Lastly, _Swing.Canvas_ is the parent class of _Whiteboard_ which supports the drawing features by using _Graphics2D_ and _BufferedImage_.

- **Concurrency**<br>
To ensure that access to shared resources is properly handled and that simultaneous actions lead to a reasonable state, _concurrentHashMap_ and _synchronized_ are used. Especially, when username and roomID cannot be duplicated, the function of join and create room need to be synchronized.

- **Server Side**<br>
On the server side, since TCP server socket is used which is reusable for connecting different clients at the same time, multi-threading is required for handling the connection between each client. To achieve multi-threading, each client is run on its belonged runner, and on each runner will record the individual stats including _username, roomID, isManager and participating_. The stat on the server side is important since the users would not exist when they leave the room, instead they back to the menu and the stat can track the user’s status and record in the room list as well. The room list is a _concurrentHasMap_ which key is the _roomID_ (in String) and the value is _Room_ object to make sure the roomID is unique. Also, each _Room_ contains a user list and the manager of the room. Except the setting up process for each user, the centralised server also has the duty of broadcasting the latest canvas and user list to the participants in the same room and forwarding join-in request to the manager of the room. In general, all requests from the clients would be processed by _requestHandler()_, and it would send the response to the client.

- **Client Side**<br>
_ClientManager_ is the main class for the application on the client side. It is responsible for sending query and handle the response from the server. Also, it starts up the graphic user interface. The GUI will use the setters and getters in _ClientProcessor_ to keep the data running at the backend and the data displayed on the UI is consistent. Furthermore, since _ClientManager_ is staying in the while loop when it is connected to the server, the GUI cannot be re-rendered because _Swing (JFrame)_ is using the Event Dispatch Thread. Therefore, another thread for _ClientProcessor_ is used to ensure the connection block and GUI can execute at the same time.

- **Critical Analysis**<br>
The current system design is stable with good error handling. However, the time cost is high because of the usage of TCP. To enrich the speed of transmission, the whiteboard can be considered to use UDP while it is constantly updated by participants (therefore, the accuracy of the canvas is not that important at all). Furthermore, the current centralised server is having heavy workload when it needs to handle all the request from all clients. The better system design could be hosting every room on the server and allow user in the same room to use peer to peer network exchange data for drawing and chatting to decrease the burden on the server and higher the speed of the data transmission. The alternative way is deploying more servers to construct a cluster server to achieve load balancing.

## Basic Features
- **Drawing**<br>
The system supports for drawing in different shapes, such as line, circle, oval and rectangle. The shapes are resizable which could be drawn of variable sizes by dragging the mouse and not of a fixed size. Moreover, the feature of handing drawing is provided which supports the users to be more creative while they are painting.

- **Text Inputting**<br>
The user could press the text button and select a location for the input text. The user could type the word in pop-up window and could select the font size, font type, and font family.

- **Eraser**<br>
In this whiteboard, the eraser function is built in as well. The implementation is utilising the hand drawing feature with white to overlay the original painting. Since the background is white, the default colour of the hand drawing is set to white. In the future version, when the functionality of painting background is introduced, the colour of the eraser could be set to be as same as the background’s colour.

- **Colour Selection**<br>
The applied colour pan is from _JColourChooser_ which provides a pane of controls designed to allow a user to manipulate and select a colour. The users could pick up any colour they want.

- **User List**<br>
Since it is a “shared” whiteboard, the user in a same room should be allowed to see each other’s username. Therefore, the user would know who are currently editing the same whiteboard. The manager would be always coloured in magenta and be on the top of list. Alternatively, the user him/herself would be highlighted by blue.

## Advanced Features
- **Chat Window**<br>
It allows users to communicate with each other by typing a text.

- **File Menu**<br>
_Connect Page_ allows the users to input the server IP and port not only using the command argument but also changing the information via the GUI. To some extents, it improves the user experience.

- **Kick Out**<br>
This is the manager’s privilege to remove a participant from the room. On the user list, only the manager can see the remove button beside every participant. After the manager presses the button and confirms on the confirmed dialogue, the selected user would be removed, and he/she would be notified as well. After kicking a person out, the user list would be updated immediately.

## New Innovations
- **Argument Hint**<br>
To start up the server, the only required argument is server port number, and server address and port are mandatory for the client side. If any one of the arguments is missing, an error message would alert the specific one and it would be printed out in the terminal.

- **Client Menu**<br>
It is designed for enhancing the user experience and supporting the feature of room hosting (more details in the following section). In the _Client Menu_, there are room id text field, username text field, create button and join button. All the textboxes and buttons are working with input validations. For instance, “username cannot be empty” would appear if the user presses the create or join button without typing the username. In this scenario, the request would not be sent to the server (since the error is already detected) to avoid using unnecessary resource on the server. The menu has a text field to display the message which allows the user to see the warning and notification from the system.

- **Room Hosting**<br>
The design of _Room Hosting_ empowers the server to host several whiteboards at the same time on the same address and same port. Every room id should be four digits and it should be identical when it is created. The username would be enforced unique in a single room. The taken room id and taken username would be handled by the validation from the server.

- **Stroke and Font Selection**<br>
To enrich the drawing feature, _Stroke Selection_ and _Font Selection_ are introduced. _Stroke Selection_ is for any type of drawing and eraser. _Font Selection_ is for _Text Inputting_.

- **Save Before Leave**<br>
_Save Before Leave_ is a functionality designed for the manager of the room. Since the manager can have a new canvas and close a canvas, the application would like to ensure that the manager has considered if the created canvas wants to be saved. There is a pop-up dialogue to warn the manager if the canvas is unsaved when the manager is leaving, or the app is closing.

- **Feedback Support**<br>
There is a reporting button on the footer which can email to is0.jimhsiao@gmail.com and the user can attach the error message, so there is less bug in the next version of _is0xSwiftCo_.

## Demo
- **Menu**
<p align="center">
  <img alt="Menu" src="demo/menu.png" width="500">
</p>

- **Menu Hint**
<p align="center">
  <img alt="Menu Hint" src="demo/send-join-request.png" width="500">
</p>

- **Whiteboard**
<p align="center">
  <img alt="Whiteboard" src="demo/whiteboard.png" width="500">
</p>

- **Co Whiteboard**
<p align="center">
  <img alt="Co Whiteboard" src="demo/chat.png" width="500">
</p>

- **Save Canvas**
<p align="center">
  <img alt="Save Canvas" src="demo/save.png" width="500">
</p>

- **Argument Hint**
<p align="center">
  <img alt="Argument Hint" src="demo/argument-hint.png" width="500">
</p>

## Developed By
- The application is developed by _[is0xjh25 (Yun-Chi Hsiao)](https://is0xjh25.github.io)_ 
<br/>
<p align="left">
  <img alt="Favicon" src="demo/is0-favicon.png" width="250" >
