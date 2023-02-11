package GUI;

import GUI.BaseClasses.EditorPanel;
import data.DBManager;
import data.tables.LogEntryTable;
import model.LogEntry;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class LogEntrySnippet extends EditorPanel<LogEntry> {
    private JTextArea tfNarrative;
    private JPanel contentPane;

    public LogEntrySnippet(LogEntry entry) {
        super(LogEntryTable.getInstance(), null);

        setItem(entry);
    }

    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    @Override
    public void refreshData() {
        tfNarrative.setText(getItem().getNarrative());
    }

    @Override
    public void pushChanges() {
        getItem().setNarrative(tfNarrative.getText());
    }

    public static void main(String[] args) {
        try (Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            LogEntrySnippet snippet = new LogEntrySnippet(LogEntryTable.getInstance().getItemById(1));

            JOptionPane.showMessageDialog(null, snippet.getItem().getParentDiscrepancy().getText());

            frame.setContentPane(snippet.contentPane);
            frame.pack();
            frame.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
