package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.actions.OpenStatusEditorAction;
import GUI.actions.PrintAction;
import data.tables.AircraftTable;
import data.tables.DiscrepancyTable;
import data.tables.LogEntryTable;
import data.tables.Table;
import model.Aircraft;
import model.Discrepancy;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AppFrame extends JFrame {

    private JPanel contentPane;
    private JTabbedPane tabbedPane1;
    private JPanel notesPanel;

    private HashMap<Discrepancy, DiscrepancySnippet> discrepancySnippets = new HashMap<>();

    public AppFrame() throws SQLException {
        super("Hawk Logbook");

        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));

        loadNotes();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(new MenuManager().menuBar);
        setContentPane(contentPane);
        setSize(1024,768);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Loads all the relevant discrepancies and populates the NotesPanel with them
     * @throws SQLException
     */
    private void loadNotes() {
        try {
            for (Aircraft aircraft : AircraftTable.getInstance().getAllItems()) {
                AircraftHeader header = new AircraftHeader(this, null, aircraft);

                notesPanel.add(header.getContentPane());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "There was an error loading aircraft from the table.");
            System.err.println(ex.getMessage());
        }
    }



    private void createNewDiscrepancy() {
        EditorDialog.showDiscrepancy(new Discrepancy(), this);
    }

    private class MenuManager {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenu editMenu = new JMenu("Edit");
        JMenuItem editStatuses = new JMenuItem("Edit Statuses");
        JMenuItem newDiscrepancy = new JMenuItem("New Discrepancy");

        public MenuManager() {
            menuBar.add(fileMenu);

            menuBar.add(editMenu);
            editMenu.add(new OpenStatusEditorAction(AppFrame.this));
            editMenu.add(newDiscrepancy).addActionListener(event ->createNewDiscrepancy());
            newDiscrepancy.setAccelerator(KeyStroke.getKeyStroke("control N"));

            fileMenu.add(new PrintAction(AppFrame.this, null, notesPanel));
        }
    }
}
