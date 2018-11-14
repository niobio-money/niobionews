This is a simple command implementation for WhatsApp, using Niobio Cash (NBR) bot.

The basic idea is (read all first before start doing it):

1) Add WhatsApp bot +55 61 8220-9940 to your WhatsApp contacts

2) Create your server (valid ip). If you dont have, you can create here https://m.do.co/c/1ac0e19b0bcd 
TIP: Pickup the minimal machine (low cost after free period. 5$), install 4gb of swap memory and small visual interface
https://www.digitalocean.com/community/tutorials/how-to-add-swap-space-on-ubuntu-16-04
https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-vnc-on-ubuntu-16-04

3) Fork this repository https://github.com/soldate/MyServer

4) In your server terminal:
type: git clone yourforkrepository (or https://github.com/soldate/MyServer)

5) Run this small java server (java -jar prod/server.jar). Try see in browser http://yourserver:port (Default port is 80)

6) Send a WhatsApp message to create a command: /createcmd yourcommand http://yourserver:port

7) Send a WhatsApp message: /yourcommand

8) In an web browser, go to http://yourserver:port (press F5) and see if your server receive the message => text="Comando nao encontrado" (command not found)     

9) Develop (see below) your command in src/your/YourMain.java, generate and run a new server.jar, and try send /yourcommand again

--------- To develop: ---------------

You need Java (JDK for run jar command, not only JRE) and Git. 
Probably you should use Eclipse editor. (read all first before start doing it):

1) Open your terminal and go to your workspace. (ex: c:\workspace)

type: git clone yourforkrepository (or https://github.com/soldate/MyServer)

2) In Eclipse, go to Import.. Git.. Exisitng.. find c:\workspace\MyServer

Open src/your/YourMain and play a little.
IMPORTANT: The real void main is in jetty.Server class.
You should probably use Show View -> Git Staging to see your changes
 
3) So, to run go to (Eclipse) menu Debug -> Server class (not "on Server")

4) When is ready, generate new server.jar 
Open terminal and go to c:\workspace\MyServer\bin
execute: jar cfm ..\prod\server.jar ..\META-INF\Manifest.txt * ..\lib

5) git push

6) in your server terminal. 
git pull 
java -jar prod/server.jar

7) try again :-D



 