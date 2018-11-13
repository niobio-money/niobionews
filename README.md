This is a simple command implementation for WhatsApp, using Niobio Cash (NBR) bot.

The basic idea is (read all first before start doing it):

1) Add WhatsApp bot +55 61 8220-9940 to your WhatsApp contacts

2) Create your server (valid ip). If you dont have, you can create here https://m.do.co/c/1ac0e19b0bcd 
TIP: Pickup the minimal machine (low cost after free period. 5$), install 4gb of swap memory and small visual interface
https://www.digitalocean.com/community/tutorials/how-to-add-swap-space-on-ubuntu-16-04
https://www.digitalocean.com/community/tutorials/how-to-install-and-configure-vnc-on-ubuntu-16-04

3) Run this small java server (java -jar prod/server.jar) in youserver (try see in browser http://yourserver:port -  Default port is 80)

4) Send a WhatsApp message to create a command: /createcmd yourcommand http://yourserver:port

5) Send a WhatsApp message: /yourcommand

6) In an web browser, go to http://yourserver:port (press F5) and see if your server receive the message => text="Comando nao encontrado" (command not found)     

7) Develop your command in src/your/YourMain.java, generate and run a new server.jar, and try send /yourcommand again

