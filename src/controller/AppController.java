package controller;

import service.LogReaderService;
import service.TelegramService;
import view.MainView;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;

/**
 * Controller to bridge view and service layers.
 * Handles event listeners and persistent user preferences with unique keys.
 */
public class AppController {
    private final MainView view;
    private final LogReaderService logService;
    private final Preferences prefs;

    //OS Registry/Preferences
    private static final String KEY_PATH = "xpm.settings.xpilot_path";
    private static final String KEY_TOKEN = "xpm.settings.telegram_bot_token";
    private static final String KEY_CHAT = "xpm.settings.telegram_chat_id";

    /**
     * Initializes the controller and loads saved configuration using unique keys.
     */
    public AppController(MainView view, LogReaderService logService) {
        this.view = view;
        this.logService = logService;

        // nodeForPackage ensures we stay within our app's scope,
        this.prefs = Preferences.userNodeForPackage(AppController.class);

        // Load saved settings on startup
        view.setFields(
                prefs.get(KEY_PATH, ""),
                prefs.get(KEY_TOKEN, ""),
                prefs.get(KEY_CHAT, "")
        );

        this.view.addSubmitListener(e -> handleStart());
    }

    private void handleStart() {
        String p = view.getPathInput();
        String t = view.getTokenInput();
        String c = view.getChatIdInput();

        if (p.isEmpty()) {
            view.showError("xPilot directory path is required.");
            return;
        }

        // Persist settings securely with namespaced keys
        prefs.put(KEY_PATH, p);
        prefs.put(KEY_TOKEN, t);
        prefs.put(KEY_CHAT, c);

        // Initialize Telegram as an object for this session
        TelegramService tgService = new TelegramService(t, c);

        view.setLoadingState(true);
        new Thread(() -> {
            try {
                logService.startWatching(p, msg -> {
                    SwingUtilities.invokeLater(() -> view.appendLog(msg.toString()));
                    tgService.sendNotification("🚨 Incoming xPilot Message:\n" + msg.toString());
                });
                SwingUtilities.invokeLater(view::showLogScreen);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    view.setLoadingState(false);
                    view.showError(ex.getMessage());
                });
            }
        }).start();
    }
}