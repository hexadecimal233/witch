<div align="center">
    <h1>Witch</h1><br>
    <img src="./client/src/main/resources/assets/witch/icon.png">  
</div>

New generation Minecraft RAT.

### For educational purposes only.

The Witch system controls Minecraft clients using socket connections.

**Forge will never be supported. I will not backport it to older versions.**

## Features

### Client

- Custom server name (for client display)

### System administration

- System information
- Execute shellcode (for Windows, 32 bit)
- Shell commands
- File upload & execute (for Windows)
- Read text files
- Get run arguments

### Social engineering

- [IAS](https://modrinth.com/mod/in-game-account-switcher) mod config steal
- Grab offline server passwords
- Grab Mojang user tokens
- Log chat & commands

### Player information

- Remote screenshot
- Mod list
- Player info like coordinates, real ip, etc.
- Player skin download

### Interaction

- Chat system (in game)
- Sending chat
- Filter & mute chat
- Kick people from server and prevent them from joining server

### Running

1. Run `git clone https://github.com/ThebestkillerTBK/witch.git`
2. Open in your favorite IDE

### Using

* Build client: run `build <server_address>` and you will see `client.jar` in `data` folder.
* Server: `java -jar server-OwO-all.jar <port> <name>`
* Default port and name: `11451` and `Witch`

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