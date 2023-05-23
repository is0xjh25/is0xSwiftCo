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
_is0xSwiftCo_ is a painting application which allows multiple users to draw simultaneously on a canvas over the network. The system is implemented by Java, and it is using TCP and JSON as the tools for transporting message between server and client. The architecture of the application is structured by client-server model and the server applies multithreading technology to handle the requests from clients. 

## Getting Started
- Get the jar files:
> In teriminal (package directory) `jar cf ${name for the created jar} *` 

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
Input checking is used by the setting IP and port text field, the searching text field, and the text area for definition. Firstly, it ensures that the input cannot be empty. It means that the query with null content will not be sent. Moreover, the IP and port setting and word searching have their own helper function which are _validIP()_ and _validWord()_ to check if the input is valid or not. If the input is invalid, the warning message will replace the buttons, therefore, the users will not be able to process to the next action unless they correct the input. Every input field has been added a key listener, so the validation keeps checking while a character is entered.

- **Server Side**<br>
When a query is failed to be executed, the server will send back a response with an error message to reveal the error for the client. The error response also includes the status code which can be used in advanced.

- **Client Side**<br>
Input checking is used by the setting IP and port text field, the searching text field, and the text area for definition. Firstly, it ensures that the input cannot be empty. It means that the query with null content will not be sent. Moreover, the IP and port setting and word searching have their own helper function which are _validIP()_ and _validWord()_ to check if the input is valid or not. If the input is invalid, the warning message will replace the buttons, therefore, the users will not be able to process to the next action unless they correct the input. Every input field has been added a key listener, so the validation keeps checking while a character is entered.

- **Socket**<br>
When a query is failed to be executed, the server will send back a response with an error message to reveal the error for the client. The error response also includes the status code which can be used in advanced.

- **Graphic User Interface (GUI)**<br>
Every error response contains the suggestion from the server. It indicates the users what they can do when they receive an error message. For example, when a word is not found, the suggestion will be "Add the word to the dictionary". Therefore, the users not only understand what was going wrong, but also know how to handle with the issue with suggested feedback which makes the application more user friendly.

- **Concurrency**<br>
Every error response contains the suggestion from the server. It indicates the users what they can do when they receive an error message. For example, when a word is not found, the suggestion will be "Add the word to the dictionary". Therefore, the users not only understand what was going wrong, but also know how to handle with the issue with suggested feedback which makes the application more user friendly.

- **Critical Analysis**<br>
Every error response contains the suggestion from the server. It indicates the users what they can do when they receive an error message. For example, when a word is not found, the suggestion will be "Add the word to the dictionary". Therefore, the users not only understand what was going wrong, but also know how to handle with the issue with suggested feedback which makes the application more user friendly.

## Basic Features
- **Drawing**<br>
_Connect Page_ allows the users to input the server IP and port not only using the command argument but also changing the information via the GUI. To some extents, it improves the user experience.

- **Text Inputting**<br>
_Connect Page_ allows the users to input the server IP and port not only using the command argument but also changing the information via the GUI. To some extents, it improves the user experience.

- **Eraser**<br>
_Connect Page_ allows the users to input the server IP and port not only using the command argument but also changing the information via the GUI. To some extents, it improves the user experience.

- **Colour Selection**<br>
_Connect Page_ allows the users to input the server IP and port not only using the command argument but also changing the information via the GUI. To some extents, it improves the user experience.

- **User List**<br>
_Connect Page_ allows the users to input the server IP and port not only using the command argument but also changing the information via the GUI. To some extents, it improves the user experience.

## Advanced Features
- **Chat Window**<br>
Since the MongoDB is the database in this project, how to store the password for connecting to the database become an interesting problem, especially the server package is going to be a jar file. My solution is creating a password promote while the server starts up, so only the user who has the database password can run the server. Therefore, there is no concern with leaking password in jar file and on GitHub because the password will never be coded in the file. In addition, instead of using _String_ to save the inputting password, _char[]_ is the type to store the password. Since _char[]_ is mutable, the password can be rewrite after use which can hide the actual password, and it makes sure that the it would no longer in the memory and ensures the database security.

- **File Menu**<br>
_Connect Page_ allows the users to input the server IP and port not only using the command argument but also changing the information via the GUI. To some extents, it improves the user experience.

- **Kick Out**<br>
When the server socket is closed, the GUI will redirect to _Reconnect Page_ to ask for reconnect to the server. In this stage, instead of just terminating the application, the users can wait and attempt to reconnect, or they can edit the server IP and port to the other server.

## New Innovations
- **Argument Hint**<br>
Since the MongoDB is the database in this project, how to store the password for connecting to the database become an interesting problem, especially the server package is going to be a jar file. My solution is creating a password promote while the server starts up, so only the user who has the database password can run the server. Therefore, there is no concern with leaking password in jar file and on GitHub because the password will never be coded in the file. In addition, instead of using _String_ to save the inputting password, _char[]_ is the type to store the password. Since _char[]_ is mutable, the password can be rewrite after use which can hide the actual password, and it makes sure that the it would no longer in the memory and ensures the database security.

- **Client Menu**<br>
_Connect Page_ allows the users to input the server IP and port not only using the command argument but also changing the information via the GUI. To some extents, it improves the user experience.

- **Room Hosting**<br>
When the server socket is closed, the GUI will redirect to _Reconnect Page_ to ask for reconnect to the server. In this stage, instead of just terminating the application, the users can wait and attempt to reconnect, or they can edit the server IP and port to the other server.

- **Broke and Font Selection**<br>
The loading GIF will be illustrated on the _Waiting Pag_e when a query is sent. The design can let the users notice that the program is processing.

- **Save Before Leave**<br>
The loading GIF will be illustrated on the _Waiting Pag_e when a query is sent. The design can let the users notice that the program is processing.

- **Feedback Support**<br>
There is a reporting button on the footer which can email to is0.jimhsiao@gmail.com and the client can attach the client log file, so there is less bug in the next version of the _is0xCollectiveDict_.

## Demo
- **Connect Page**
<p align="center">
  <img alt="Connect Page" src="demo/connect-page.png" width="500">
</p>

- **Menu**
<p align="center">
  <img alt="Menu" src="demo/menu-2.png" width="500">
</p>

- **Search Page**
<p align="center">
  <img alt="Search Page" src="demo/search-page.png" width="500">
</p>

- **Status Page**
<p align="center">
  <img alt="Status Page" src="demo/status-page.png" width="500">
</p>

- **Log File - Client**
<p align="center">
  <img alt="Client Log File" src="demo/client-log.png" width="500">
</p>

- **Log File - Server**
<p align="center">
  <img alt="Server Log File" src="demo/server-log.png" width="500">
</p>

- **Password Promote**
<p align="center">
  <img alt="Password Promote" src="demo/password-promote.png" width="500">
</p>

## Developed By
- The application is developed by _[is0xjh25 (Yun-Chi Hsiao)](https://is0xjh25.github.io)_ 
<br/>
<p align="left">
  <img alt="Favicon" src="demo/is0-favicon.png" width="250" >
