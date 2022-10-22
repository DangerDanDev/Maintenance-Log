import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(1024,768);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setResizable(false);

        JPanel borderPanel = new JPanel(new BorderLayout());
        frame.setContentPane(borderPanel);

        JPanel logEntriesPanel = getLogEntriesPanel();
        JScrollPane logEntriesPanelScroller = new JScrollPane(logEntriesPanel);
        logEntriesPanelScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        logEntriesPanelScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        borderPanel.add(logEntriesPanelScroller, BorderLayout.CENTER);

        DiscrepancyPanel discrepancyPanel = new DiscrepancyPanel();
        borderPanel.add(discrepancyPanel.getContentPane(), BorderLayout.WEST);

        for(int i = 0; i < 3; i++)
            logEntriesPanel.add(new LogEntryPanel().getContentPanel());

        frame.setVisible(true);
    }

    private static JPanel getLogEntriesPanel() {
        JPanel logEntriesPanel = new JPanel();
        logEntriesPanel.setLayout(new BoxLayout(logEntriesPanel, BoxLayout.Y_AXIS));

        return logEntriesPanel;

    }
}
