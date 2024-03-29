package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import data.DBManager;
import data.tables.LogEntryTable;
import data.tables.Table;
import model.LogEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.SQLException;

public class LogEntrySnippet extends EditorPanel<LogEntry> implements Table.TableListener<LogEntry> {
    private JTextArea tfNarrative;
    private JPanel contentPane;

    public LogEntrySnippet(Window owner, LogEntry entry) {
        super(owner, LogEntryTable.getInstance(), null);

        setItem(entry);

        tfNarrative.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    EditorDialog.showLogEntry(getItem(), getOwner());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    @Override
    public void setItem(LogEntry item) {
        super.setItem(item);

        System.out.println("LogEntrySnippet setting item value to " + item);
        System.out.println("ID: " + item.getId());
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

    @Override
    public void onItemUpdated(LogEntry editedItem) {
        super.onItemUpdated(editedItem);
    }

    public static void main(String[] args) {
        try (Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            LogEntrySnippet snippet = new LogEntrySnippet(frame, LogEntryTable.getInstance().getItemById(1));

            JOptionPane.showMessageDialog(null, snippet.getItem().getParentDiscrepancy().getText());

            frame.setContentPane(snippet.contentPane);
            frame.pack();
            frame.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
