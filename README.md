This is a simple command implementation for WhatsApp, using Niobio Cash (NBR) bot.

The basic idea is:
1) Add WhatsApp bot +55 61 8220-9940 to your WhatsApp contacts
2) Send a message to create a command: /createcmd yourcommand http://yourserver:port
3) Run this small java server in youserver:port (default port is 80)
4) Send a message: /yourcommand
5) In an web browser, go to http://yourserver:port (press F5) and see if your server was called "Comando nao encontrado" (command not found)     
6) Implement your command in src/your/YourMain.java and try /yourcommand again
