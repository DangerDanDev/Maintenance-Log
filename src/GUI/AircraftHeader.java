package GUI;

import GUI.BaseClasses.EditorPanel;
import data.tables.AircraftTable;
import data.tables.DiscrepancyTable;
import data.tables.LogEntryTable;
import data.tables.Table;
import model.Aircraft;
import model.Discrepancy;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AircraftHeader extends EditorPanel<Aircraft> {
    private JComboBox cbTailNumber;
    private JPanel aircraftInfoPanel;
    private JPanel discrepanciesPanel;
    private JPanel contentPane;

    private HashMap<Discrepancy, DiscrepancySnippet> discrepancySnippets = new HashMap<>();

    public AircraftHeader(Window owner, EditorPanelHost host, Aircraft aircraft) {
        super(owner, AircraftTable.getInstance(), host);

        setItem(aircraft);
    }

    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    @Override
    public void refreshData() {
        getItem().selectInComboBox(cbTailNumber);
    }

    @Override
    public void pushChanges() {
        //As of right now, the aircraft header should not allow the user to do any changes
    }

    @Override
    public void unsubscribeFromTableUpdates() {
        super.unsubscribeFromTableUpdates();

        DiscrepancyTable.getInstance().removeListener(discrepancyTableListener);
    }

    @Override
    public void setItem(Aircraft item) {
        final DiscrepancyTable discrepancyTable = DiscrepancyTable.getInstance();

        super.setItem(item);

        cbTailNumber.removeAllItems();
        cbTailNumber.addItem(item);
        item.selectInComboBox(cbTailNumber);

        try {
            for (Discrepancy d : discrepancyTable.getDiscrepanciesForNotes(getItem())) {
                addDiscrepancy(d);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "There was an error adding a discrepancy.");
            System.err.println(ex.getMessage());
        }
    }

    private void addDiscrepancy(Discrepancy d) {
        try {
            DiscrepancySnippet snippet = new DiscrepancySnippet(getOwner(), d);
            snippet.setQueryType(LogEntryTable.QueryType.ON_NOTES_ONLY);

            discrepancySnippets.put(d, snippet);
            discrepanciesPanel.add(discrepancySnippets.get(d).getContentPane());

            getContentPane().revalidate();
            getContentPane().repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "There was an error adding a discrepancy.");
            System.err.println(ex.getMessage());
        }
    }

    private void removeDiscrepancy(Discrepancy d) {
        discrepanciesPanel.remove(discrepancySnippets.get(d).getContentPane());
        discrepancySnippets.remove(d);

        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void clearDiscrepancies() {
        discrepancySnippets.clear();
        discrepanciesPanel.removeAll();
    }

    private void createUIComponents() {
        discrepanciesPanel = new JPanel();
        discrepanciesPanel.setLayout(new BoxLayout(discrepanciesPanel,BoxLayout.Y_AXIS));
    }

    private DiscrepancyTableListener discrepancyTableListener = new DiscrepancyTableListener();

    public class DiscrepancyTableListener implements Table.TableListener<Discrepancy> {

        public DiscrepancyTableListener() {
            DiscrepancyTable.getInstance().addListener(this);
        }

        @Override
        public void onItemAdded(Discrepancy addedItem) {
            if(getItem().equals(addedItem.getAircraft()))
                addDiscrepancy(addedItem);
        }

        @Override
        public void onItemUpdated(Discrepancy editedItem) {
            //check if the item was moved from another tail number to mine
            if(editedItem.getAircraft().equals(getItem()) && !discrepancySnippets.containsKey(editedItem))
                addDiscrepancy(editedItem);

            //check if the discrepancy was moved from my tail number to another
            else if(!editedItem.getAircraft().equals(getItem()) && discrepancySnippets.containsKey(editedItem))
                removeDiscrepancy(editedItem);
        }

        @Override
        public void onItemDeleted(Discrepancy deletedItem) {
            removeDiscrepancy(deletedItem);
        }
    }
}
