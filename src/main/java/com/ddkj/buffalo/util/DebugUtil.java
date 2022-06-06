package com.ddkj.buffalo.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DebugUtil {
    public DebugUtil() {
    }

    public static StringBuilder printStack() {
        Throwable t = new Throwable();
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] var2 = t.getStackTrace();
        int var3 = var2.length;

        for (StackTraceElement ste : var2) {
            sb.append(ste).append("\n");
        }

        return sb;
    }

    public static String printTrack() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        StringBuilder sbf = new StringBuilder();
        int var3 = st.length;

        for (StackTraceElement e : st) {
            if (sbf.length() > 0) {
                sbf.append(" <- ");
                sbf.append(System.getProperty("line.separator"));
            }
            sbf.append(e.getClassName()).append(".").append(e.getMethodName()).append("() ").append(e.getLineNumber());
        }

        return sbf.toString();
    }

    public static String printStack(Exception e) {
        StringWriter message = new StringWriter();
        PrintWriter writer = new PrintWriter(message);
        e.printStackTrace(writer);
        return message.toString();
    }

    public static StringBuilder printStack(Throwable t) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] var2 = t.getStackTrace();
        int var3 = var2.length;

        for (StackTraceElement ste : var2) {
            sb.append(ste).append("\n");
        }

        return sb;
    }
}