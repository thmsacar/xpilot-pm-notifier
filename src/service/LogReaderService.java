package service;

import model.PmMessage;
import java.io.*;
import java.nio.file.*;
import java.util.function.Consumer;

/**
 * Service for file system monitoring and log parsing.
 * Implements persistent file tracking for real-time updates.
 */
public class LogReaderService {
    private String currentCallsign = "UNKNOWN";
    private boolean isRunning = false;

    /**
     * Initiates the log file monitoring thread.
     * @param basePath Root directory for xPilot logs.
     * @param onMsg Consumer for processing new messages.
     * @throws Exception On directory access errors.
     */
    public void startWatching(String basePath, Consumer<PmMessage> onMsg) throws Exception {
        File baseDir = new File(basePath);
        if (!baseDir.exists() || !baseDir.isDirectory()) throw new Exception("Directory not found.");

        File logDir = new File(baseDir, "NetworkLogs");
        if (!logDir.exists()) throw new Exception("NetworkLogs directory missing.");

        File latestLog = getLatestLogFile(logDir);
        if (latestLog == null) throw new Exception("No valid log files detected.");

        updateCurrentCallsign(latestLog);
        isRunning = true;

        new Thread(() -> {
            try (RandomAccessFile reader = new RandomAccessFile(latestLog, "r")) {
                // Point reader to the end of the file to ignore old messages. Set to 0 for historical debugging.
                long lastPos = reader.length();

                while (isRunning) {
                    long currentLen = latestLog.length();
                    // If file size increased, new lines were added
                    if (currentLen > lastPos) {
                        reader.seek(lastPos);
                        String line;
                        while ((line = reader.readLine()) != null) {
                            parseLine(line, onMsg);
                        }
                        // Update pointer so we don't read the same lines again
                        lastPos = reader.getFilePointer();
                    }
                    // Avoid 100% CPU utilization by sleeping between checks
                    Thread.sleep(500);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private File getLatestLogFile(File logDir) {
        // Filter out irrelevant files and pick the one with the latest modification timestamp
        File[] files = logDir.listFiles((dir, name) -> name.startsWith("NetworkLog-") && name.endsWith(".txt"));
        if (files == null || files.length == 0) return null;
        File latest = files[0];
        for (File f : files) if (f.lastModified() > latest.lastModified()) latest = f;
        return latest;
    }

    private void updateCurrentCallsign(File logFile) {
        try {
            // Scan entire file to find the latest session ID (callsign)
            Files.lines(logFile.toPath()).forEach(line -> {
                if (line.contains(">>> $ID")) extractId(line);
            });
        } catch (Exception e) {}
    }

    private void extractId(String line) {
        try {
            int start = line.indexOf("$ID") + 3;
            int end = line.indexOf(":", start);
            currentCallsign = line.substring(start, end);
        } catch (Exception e) {}
    }

    /**
     * Evaluates a log line for Private Message criteria.
     */
    private void parseLine(String line, Consumer<PmMessage> callback) {
        // Update callsign dynamically if it changes during runtime
        if (line.contains(">>> $ID")) extractId(line);

        if (line.contains("<<< #TM")) {
            try {
                // Find '[' instead of hardcoded index to bypass invisible characters (e.g. \r\n from manual edits)
                int bracket = line.indexOf('[') + 1;
                String timestamp = line.substring(bracket, bracket + 8);

                // Get everything after the #TM marker
                String payload = line.substring(line.indexOf("#TM") + 3);

                // Split exactly into 3 components: Sender, Receiver, Message Content
                String[] parts = payload.split(":", 3);

                if (parts.length >= 3) {
                    String sender = parts[0];
                    String receiver = parts[1];
                    String content = parts[2];

                    // Ignore broadcast server messages and only accept PMs targeted to our callsign
                    if (!sender.equalsIgnoreCase("server") && receiver.equalsIgnoreCase(currentCallsign)) {
                        callback.accept(new PmMessage(timestamp, sender, receiver, content));
                    }
                }
            } catch (Exception e) {}
        }
    }
}