import controller.AppController;
import service.LogReaderService;
import view.MainView;
import javax.swing.SwingUtilities;

/**
 * Entry point for the application.
 * Initializes the MVCS components and launches the GUI.
 */
public class Main {
    /**
     * Main method that launches the application on the Event Dispatch Thread (EDT).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();
            LogReaderService service = new LogReaderService();
            new AppController(view, service);
            view.setVisible(true);
        });
    }
}