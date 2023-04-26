package GUI;

import GUI.BaseClasses.Refreshable;
import GUI.actions.*;
import data.tables.AircraftTable;
import model.Aircraft;
import model.scheduler.Scheduler;

import javax.swing.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AppFrame extends JFrame implements Refreshable {

    private JPanel contentPane;
    private JTabbedPane tabbedPane1;
    private JPanel notesPanel;
    private JScrollPane notesScrollPane;

    private NotesPanel notesPanelMngr;

    public AppFrame() throws SQLException {
        super("Hawk Logbook");

        refresh();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(new MenuManager().menuBar);
        setContentPane(contentPane);
        setSize(1024,768);
        setLocationRelativeTo(null);
        setVisible(true);

        Scheduler.init();
    }

    public void refresh() {

        try {

            notesPanelMngr.refresh();

            Scheduler.getInstance().checkTriggers();

            revalidate();
            repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "The scheduler encountered an SQL error");
            System.err.println(ex.getMessage());
        }
    }

    private void createUIComponents() {

        //The NotesPanel has a good bit of custom initiation code
        //so its encapsulated in its own NotesPanel class
        notesPanelMngr = new NotesPanel(this);
        notesPanel = notesPanelMngr;
    }


    private class MenuManager {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenu editMenu = new JMenu("Edit");

        public MenuManager() {
            menuBar.add(fileMenu);
            fileMenu.add(new PrintAction(AppFrame.this, null, notesPanel));

            menuBar.add(editMenu);
            editMenu.add(new OpenStatusEditorAction(AppFrame.this));
            editMenu.add(new NewDiscrepancyAction(getOwner(), null, null, MenuType.JMenuBar));
            editMenu.add(new RefreshAction(getOwner(), null, AppFrame.this));
        }
    }
}
