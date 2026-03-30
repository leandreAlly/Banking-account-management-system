package com.leandre.cli;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *   [FILE]   — file read/write operations
 *   [THREAD] — thread lifecycle and activity
 *   [TXN]    — transaction execution
 *   [SYSTEM] — startup, shutdown, general info
 *   [OK]     — success confirmations
 *   [WARN]   — warnings
 *   [ERROR]  — error messages
 */
public class ConsoleLogger {

    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private static String timestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FMT);
    }

    private static void log(String tag, String message) {
        System.out.printf("[%s] [%-6s] %s%n", timestamp(), tag, message);
    }

    public static void fileInfo(String message) {
        log("FILE", message);
    }

    public static void fileSaved(String path, int count) {
        log("FILE", "Saved " + count + " account(s) to " + path);
    }

    public static void fileLoaded(String path, int count) {
        log("FILE", "Loaded " + count + " account(s) from " + path);
    }

    public static void fileNotFound(String path) {
        log("FILE", "No data file found at " + path + " — starting fresh");
    }

    public static void fileError(String operation, String detail) {
        log("ERROR", "File " + operation + " failed: " + detail);
    }

    public static void threadStarted(String threadName) {
        log("THREAD", threadName + " started");
    }

    public static void threadCompleted(String threadName, String detail) {
        log("THREAD", threadName + " completed — " + detail);
    }

    public static void threadFailed(String threadName, String reason) {
        log("THREAD", threadName + " FAILED — " + reason);
    }

    public static void threadSummary(int total, long elapsedMs) {
        log("THREAD", "All " + total + " threads finished in " + elapsedMs + "ms");
    }

    public static void txnAdded(String txnId, String accountNumber, String type, double amount) {
        log("TXN", String.format("%s | %s | %-12s | $%,.2f", txnId, accountNumber, type, amount));
    }

    public static void system(String message) {
        log("SYSTEM", message);
    }

    public static void success(String message) {
        log("OK", message);
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void printStartupBanner() {
        System.out.println();
        System.out.println("+==============================================+");
        System.out.println("|     BANK ACCOUNT MANAGEMENT SYSTEM v2.0     |");
        System.out.println("|     Thread-Safe | File-Persistent | CLI      |");
        System.out.println("+==============================================+");
        system("Application starting...");
    }

    public static void printShutdownMessage() {
        system("Shutting down...");
        System.out.println();
        System.out.println("+==============================================+");
        System.out.println("|              Goodbye! See you soon.          |");
        System.out.println("+==============================================+");
        System.out.println();
    }
}
