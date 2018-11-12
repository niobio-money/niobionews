This is a simple command implementation for WhatsApp, using Niobio Cash (NBR) bot.

The basic idea is:
1) Add WhatsApp bot +55 61 8220-9940 to your WhatsApp contacts
2) Create your server (valid ip). If you dont have, you can create here https://m.do.co/c/1ac0e19b0bcd 
3) Send a WhatsApp message to create a command: /createcmd yourcommand http://yourserver:port (default port is 80)
4) Run this small java server (java -jar prod/server.jar) in youserver (try see in browser http://yourserver:port)
5) Send a WhatsApp message: /yourcommand
6) In an web browser, go to http://yourserver:port (press F5) and see if your server was called "Comando nao encontrado" (command not found)     
7) Implement your command in src/your/YourMain.java, generate a new server.jar, and try /yourcommand again
