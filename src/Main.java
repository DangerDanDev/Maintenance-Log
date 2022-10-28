import GUI.DiscrepancyPanel;
import GUI.LogEntryPanel;
import GUI.TailNumberBrowserPanel;
import data.DatabaseManager;
import data.Tables.DiscrepancyTable;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        DatabaseManager.initialize();

        TailNumberBrowserPanel tailNumberBrowserPanel = new TailNumberBrowserPanel();
        tailNumberBrowserPanel.show();

    }

    private static JPanel getLogEntriesPanel() {
        JPanel logEntriesPanel = new JPanel();
        logEntriesPanel.setLayout(new BoxLayout(logEntriesPanel, BoxLayout.Y_AXIS));

        return logEntriesPanel;
    }
}
