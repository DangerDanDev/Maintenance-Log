package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import data.DBManager;
import data.tables.DiscrepancyTable;
import data.tables.StatusTable;
import model.Discrepancy;
import model.Status;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppFrame extends JFrame {

    private JPanel contentPane;
    private JTabbedPane tabbedPane1;
    private JPanel notesPanel;

    public AppFrame() throws SQLException {
        super("Hawk Logbook");

        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));

        loadNotes();


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(new MenuManager().menuBar);
        setContentPane(contentPane);
        setSize(1024,768);
        setVisible(true);
    }

    private void showStatusEditor()  {

        try {

            ArrayList<EditorPanel<Status>> statusEditorPanels = new ArrayList<>();
            for(Status s : StatusTable.getInstance().getAllItems())
                statusEditorPanels.add(new StatusEditorPanel(s));

            new EditorDialog<Status>("Status Editor", statusEditorPanels).setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "There was an error trying to open the status editor.");
        }
    }

    /**
     * Loads all the relevant discrepancies and populates the NotesPanel with them
     * @throws SQLException
     */
    private void loadNotes() throws SQLException {
        ArrayList<Discrepancy> discrepancies = DiscrepancyTable.getInstance().getAllItems();

        for(Discrepancy d : discrepancies) {
            notesPanel.add(new DiscrepancyLineItem(d).getContentPane());
        }
    }

    private void createNewDiscrepancy() {
        EditorDialog<Discrepancy> dialog = new EditorDialog<>("New Discrepancy",
                new DiscrepancyEditor(new Discrepancy(), null));

        dialog.setVisible(true);
    }

    private class MenuManager {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");

        JMenu edit = new JMenu("Edit");
        JMenuItem editStatuses = new JMenuItem("Edit Statuses");
        JMenuItem newDiscrepancy = new JMenuItem("New Discrepancy");

        public MenuManager() {
            menuBar.add(file);

            menuBar.add(edit);
            edit.add(editStatuses).addActionListener(event -> showStatusEditor());
            edit.add(newDiscrepancy).addActionListener(event ->createNewDiscrepancy());
        }

    }

    public static void main(String[] args) {
        try (Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            AppFrame appFrame = new AppFrame();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "App crashed with SQL Error");
        }
    }
}
