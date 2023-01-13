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
- Encrypted Socket
- [ ] Infect other mods
- [ ] Command system enhancement
- [ ] Event System Enhancement

### Server

- [ ] Help command
- [ ] Better logging
- [ ] Web UI

### System administration

- [ ] Remote shell
- System information
- Execute shellcode on Windows system (32bit only)
- Shell commands
- Execute payloads on Windows system
- Get run arguments

### Files

- [ ] More config stealer
- [IAS](https://modrinth.com/mod/in-game-account-switcher) mod config steal
- Read text files

### Secret Stealer

- Grab offline server passwords
- Grab Mojang user tokens

### Player information

- Remote screenshot
- Mod list
- Player info like coordinates, real ip, etc.
- Player skin download
- Log chat & commands

### Player controlling

- [ ] Mind control
- [ ] Unnoticeable chat control
- [ ] Invisible player (Make the victim unable to see you)
- [ ] Spam
- /op @a
- Out-of-game chat system (With your victim in game)
- Chat control
- Filter & mute chat
- Kick people from server and prevent them from joining server

### Running

1. Run `git clone https://github.com/ThebestkillerTBK/witch.git`
2. Open in your favorite IDE

### Using

* [ ] Client: Build system working
* [ ] Better config
* Server: `java -jar server.jar <port> <name>`
* Default port and name: `11451` and `Witch`
* Client config in Cfg.java

#### Files:

* Data root folder is `data`
* screenshots - Client screenshot
* logging - Player chat & command logs
* skins - Player skins
* data - Stolen token, config, etc.

### Contributing to the project

* Create PRs to make this mod better!
* Leave a star if you like it!