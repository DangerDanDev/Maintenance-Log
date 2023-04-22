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


    public AppFrame() throws SQLException {
        super("Hawk Logbook");

        loadNotes();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(new MenuManager().menuBar);
        setContentPane(contentPane);
        setSize(1024,768);
        setLocationRelativeTo(null);
        setVisible(true);

        Scheduler.init();
    }
    private HashMap<Aircraft, AircraftHeader> aircraftHeaders = new HashMap<>();
    /**
     * Loads all the relevant discrepancies and populates the NotesPanel with them
     * @throws SQLException
     */
    private void loadNotes() {
        try {

            notesPanel.removeAll();
            aircraftHeaders.clear();

            notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));

            for (Aircraft aircraft : AircraftTable.getInstance().getAllItems()) {
                AircraftHeader header = new AircraftHeader(this, null, aircraft);
                aircraftHeaders.put(aircraft, header);
                notesPanel.add(header.getContentPane());
            }


        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "There was an error loading aircraft from the table.");
            System.err.println(ex.getMessage());
        }
    }

    public void refresh() {
        loadNotes();

        try {
            Scheduler.getInstance().checkTriggers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "The scheduler encountered an SQL error");
            System.err.println(ex.getMessage());
        }
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
