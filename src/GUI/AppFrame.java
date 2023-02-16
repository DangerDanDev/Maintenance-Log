package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.actions.OpenStatusEditorAction;
import data.tables.AircraftTable;
import data.tables.DiscrepancyTable;
import data.tables.Table;
import model.Discrepancy;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AppFrame extends JFrame implements Table.TableListener<Discrepancy> {

    private JPanel contentPane;
    private JTabbedPane tabbedPane1;
    private JPanel notesPanel;

    private HashMap<Discrepancy, DiscrepancySnippet> discrepancySnippets = new HashMap<>();

    public AppFrame() throws SQLException {
        super("Hawk Logbook");

        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));

        loadNotes();

        DiscrepancyTable.getInstance().addListener(this);

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
    private void loadNotes() throws SQLException {
        ArrayList<Discrepancy> discrepancies =
                DiscrepancyTable.getInstance().getDiscrepanciesForNotes(AircraftTable.getInstance().getAllItems().get(1));

        for(Discrepancy d : discrepancies) {
            addDiscrepancy(d);
        }
    }

    /**
     * Adds a discrepancy to the notesPanel
     * @param d
     * @throws SQLException
     */
    private void addDiscrepancy(Discrepancy d)  {
        try {
            DiscrepancySnippet snippet = new DiscrepancySnippet(this, d);

            discrepancySnippets.put(d, snippet);
            notesPanel.add(discrepancySnippets.get(d).getContentPane());

            revalidate();
            repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "There was an error adding a discrepancy.");
            System.err.println(ex.getMessage());
        }
    }

    private void removeDiscrepancy(Discrepancy d) {
        notesPanel.remove(discrepancySnippets.get(d).getContentPane());

        revalidate();
        repaint();
    }

    private void createNewDiscrepancy() {
        EditorDialog.showDiscrepancy(new Discrepancy(), this);
    }

    private class MenuManager {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");

        JMenu editMenu = new JMenu("Edit");
        JMenuItem editStatuses = new JMenuItem("Edit Statuses");
        JMenuItem newDiscrepancy = new JMenuItem("New Discrepancy");

        public MenuManager() {
            menuBar.add(file);

            menuBar.add(editMenu);
            editMenu.add(new OpenStatusEditorAction(AppFrame.this));
            editMenu.add(newDiscrepancy).addActionListener(event ->createNewDiscrepancy());
            newDiscrepancy.setAccelerator(KeyStroke.getKeyStroke("control N"));
        }
    }

    /**
     * Called when a discrepancy is added to the table
     * @param addedItem
     * @param transactionId
     */
    @Override
    public void onItemAdded(Discrepancy addedItem, long transactionId) {
        if(addedItem.getStatus().isShowOnNotes())
            addDiscrepancy(addedItem);
    }

    @Override
    public void onItemUpdated(Discrepancy editedItem, long transactionId) {
        //if the item should's status has changed such that it now belongs on the notes
        //but is not already shown
        if(editedItem.getStatus().isShowOnNotes() && !discrepancySnippets.containsKey(editedItem))
            addDiscrepancy(editedItem);

        //now if we need to remove an item due to a status change
        if(!editedItem.getStatus().isShowOnNotes())
            removeDiscrepancy(editedItem);
    }

    @Override
    public void onItemDeleted(Discrepancy deletedItem, long transactionId) {
        removeDiscrepancy(deletedItem);
    }
}
