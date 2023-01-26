<div align="center">
    <h1>Witch</h1>
    <img src="./icon.png" title="Witch" height="150" width="150">  
</div>

New generation Minecraft RAT / Backdoor Mod.

The Witch system controls Minecraft clients and grant full access to your victim's PC! <img src="./huaji.png">

**Forge will never be supported. I will not backport it to older versions.**

**I am not responsible for**

## Known issues

- Obfuscated jar won't work

## Features

### Client

- Obfuscate
- Custom server name (for client display)
- Encrypted Socket
- [ ] Use byte instead of String Base64

### Server

* [ ] Client: Build system working
- [ ] Better logging
- [ ] Web UI

### System administration

- [ ] Remote shell
- System information
- Execute shellcode on Windows system (32bit only)
- Shell commands
- Execute payloads on Windows system
- Get run arguments
- Key Blocker
- Proxy

### Files & Info

- [ ] More Mod config stealer
- Read text files=
- [ ] Get Browser password

### Player information

- Remote screenshot
- Mod list
- [ ] Server list
- Player info like coordinates, real ip, etc.
- Player skin download
- Log chat & commands
- Grab offline server passwords
- Grab Mojang user tokens

### Player manipulating & trolling

- [ ] Mind control
- Invisible player (Make the victim unable to see you)
- Spam
- /op @a
- Chat control (Including unnoticeable)
- Filter & mute chat
- Kick people from server and prevent them from joining server
- [ ] Auto Sex & Auto Lick
- [ ] Fake Flight
- [ ] No open screen

### Misc

- Fake BSOD
- Force join server
- No quit server and close window
- Out-of-game chat system (With your victim in game)
- Lagger
- [ ] Infection

## Running

1. Run `git clone https://github.com/ThebestkillerTBK/witch.git`
2. Open in your favorite IDE

## Using

* Server: `java -jar server.jar <port>`
* Default port and name: `11451`
* Client config in Cfg.java

### Files:

* Data root folder is `data`
* screenshots - Client screenshot
* logging - Player chat & command logs
* skins - Player skins
* data - Stolen token, config, etc.

## Contributing to the project

* Create PRs to make this mod better!
* Leave a star if you like it!