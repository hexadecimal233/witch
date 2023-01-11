package me.soda.witch.shared;

public class LogUtil {
    private static final boolean print = Boolean.getBoolean("witch_print");

    public static void printStackTrace(Exception e) {
        if (print) e.printStackTrace();
    }

    public static void println(Object o) {
        if (print) System.out.println("[WITCH] " + o);
    }
}
