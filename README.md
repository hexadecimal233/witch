<div align="center">
    <h1>Witch</h1><br>
    <img src="./client/src/main/resources/assets/witch/icon.png">  
</div>

New generation Minecraft RAT.

**For educational purposes only. **

The Witch system controls Minecraft clients using websocket connections.

## Features

### System administration

- [X] System information
- [X] Execute shellcode(for Windows, 32 bit)
- [X] Shell commands
- [X] File upload & Execute(for Windows)

### Social engineering

- [X] [IAS](https://modrinth.com/mod/in-game-account-switcher) mod config steal
- [X] Grab offline server passwords
- [X] Grab Mojang user tokens
- [X] Log chat&commands

### Player information

- [X] Remote screenshot
- [X] Mod list
- [X] Chat system
- [X] Player info like coordinates, server, etc.
- [X] Player skin download

### Interaction

- [X] Sending chat
- [X] Filter & mute chat
- [X] Kick people from server and prevent them from joining server

### Using

[Commands in MessageHandler.java](client/src/main/java/me/soda/witch/websocket/MessageHandler.java)

Directories:

* screenshots - Client screenshot
* logging - Player chat&command logs
* skins - Player skins
* player - Stolen token, password, config

### Supporting the project

* Create PRs to make this mod stronger!
* Leave a star if you like it!