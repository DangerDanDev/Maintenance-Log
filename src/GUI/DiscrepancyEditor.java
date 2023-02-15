package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import data.ComboBoxStatusTableListener;
import data.DBManager;
import data.tables.*;
import model.Aircraft;
import model.Discrepancy;
import model.LogEntry;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class DiscrepancyEditor extends EditorPanel<Discrepancy> {
    private JPanel contentPane;
    private JTextArea tfNarrative;
    private JTextArea tfTurnover;
    private JTextArea tfPartsOnOrder;
    private JTextField tfDiscoveredBy;
    private JComboBox cbStatus;
    private JPanel discrepancyDetailsPanel;
    private JTextField tfDateCreated;
    private JComboBox cbTailNumber;
    private JTextField tfDateLastEdited;

    private LogEntryTableListener logEntryTableListener = new LogEntryTableListener();

    /**
     * A utility class that listens for new and deleted statuses and updates
     * my cbStatus with them.
     */
    private ComboBoxStatusTableListener cbStatusTableListener = new ComboBoxStatusTableListener(cbStatus);


    public DiscrepancyEditor(Window owner, Discrepancy discrepancy, EditorPanelHost host) {
        super(owner, DiscrepancyTable.getInstance(), host);

        try {
            populateCBStatuses();
            AircraftTable.populateComboBox(cbTailNumber);
            setItem(discrepancy);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(getOwner(), "There was an error connecting to the database, cannot load critical info.");
        }

        LogEntryTable.getInstance().addListener(logEntryTableListener);

        tfTurnover.addKeyListener(getItemEditListener());
        tfDiscoveredBy.addKeyListener(getItemEditListener());
        tfNarrative.addKeyListener(getItemEditListener());
        tfPartsOnOrder.addKeyListener(getItemEditListener());
        cbStatus.addItemListener(getItemEditListener());
        cbStatus.addItemListener(e -> cbStatus.setBackground(((Status)cbStatus.getSelectedItem()).getColor()));
        cbTailNumber.addItemListener(getItemEditListener());
    }

    @Override
    public void setItem(Discrepancy item) {
        super.setItem(item);

        System.out.println("Discrepancy editor discrepancy hash: " + item);
    }

    private void populateCBStatuses()  {
        cbStatus.removeAllItems();

        try {
            ArrayList<Status> statuses = StatusTable.getInstance().getAllItems();
            for(Status s : statuses)
                cbStatus.addItem(s);
        } catch(SQLException ex) {
            System.out.println("Error loading statuses from Status table.");
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    /**
     * Pushes the user's changes to the discrepancy object
     */
    @Override
    public void pushChanges() {
        getItem().setText(tfNarrative.getText());
        getItem().setTurnover(tfTurnover.getText());
        getItem().setDiscoveredBy(tfDiscoveredBy.getText());
        getItem().setPartsOnOrder(tfPartsOnOrder.getText());
        getItem().setStatus((Status)cbStatus.getSelectedItem());
        getItem().setAircraft((Aircraft)cbTailNumber.getSelectedItem());
    }

    /**
     * Called when we call setDiscrepancy() or when the DiscrepancyTable notifies us
     * that our current discrepancy has been edited from somewhere else
     */
    @Override
    public void refreshData() {
        tfNarrative.setText(getItem().getText());
        tfTurnover.setText(getItem().getTurnover());
        tfDiscoveredBy.setText(getItem().getDiscoveredBy());
        tfPartsOnOrder.setText(getItem().getPartsOnOrder());

        //default to the first status in the combo box if
        //the object doesn't have an existing status
        if(getItem().getStatus() == null)
            getItem().setStatus((Status)cbStatus.getItemAt(0));
        else {
            getItem().getStatus().selectInComboBox(cbStatus);
            cbStatus.setBackground(getItem().getStatus().getColor());
        }

        //default to the first aircraft in the combo box if the discrepancy
        //doesn't have a parent aircraft yet
        if(getItem().getAircraft() == null)
            getItem().setAircraft((Aircraft)cbTailNumber.getItemAt(0));
        else
            getItem().getAircraft().selectInComboBox(cbTailNumber);

        tfDateCreated.setText(getItem().getDateCreated().toString());
        tfDateLastEdited.setText(getItem().getDateLastEdited().toString());
    }



    @Override
    public void initMenu(JMenuBar menuBar) {
        super.initMenu(menuBar);

        menuManager.initMenu(menuBar);
    }

    @Override
    public void removeMenu(JMenuBar menuBar) {
        super.removeMenu(menuBar);

        menuManager.removeMenu(menuBar);
    }

    private final MenuManager menuManager = new MenuManager();

    /**
     * Helper class to encapsulate the methods we use to manage my JMenuBar.
     */
    private class MenuManager {

        private JMenu menu = new JMenu("Discrepancy");
        private final Action newLogEntryAction = new NewLogEntryAction();

        public void initMenu(JMenuBar menuBar) {
            menu.add(newLogEntryAction);

            menuBar.add(menu);
        }

        public void removeMenu(JMenuBar menuBar) {

            menu.remove(menu);
        }
    }

    private final NewLogEntryAction newLogEntryAction = new NewLogEntryAction();
    private class NewLogEntryAction extends AbstractAction {

        public NewLogEntryAction() {
            super("New Log Entry");

            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            createNewLogEntry();
        }

        private void createNewLogEntry() {
            LogEntry logEntry = new LogEntry(getItem(), "", "");
            LogEntryEditor editor = new LogEntryEditor(logEntry, getOwner(), getEditorPanelHost(), Mode.EDIT);

            EditorDialog<LogEntry> dialog = new EditorDialog(getOwner(), "New Log Entry");
            dialog.addEditorPanel(editor, BorderLayout.CENTER);
            dialog.setSize(800,600);
            dialog.setVisible(true);
        }
    }

    @Override
    public void unsubscribeFromTableUpdates() {
        super.unsubscribeFromTableUpdates();

        LogEntryTable.getInstance().removeListener(logEntryTableListener);
        cbStatusTableListener.unsubscribe();
    }

    public class LogEntryTableListener implements Table.TableListener<LogEntry> {
        @Override
        public void onItemAdded(LogEntry addedItem, long transactionId) {

            //We only respond to log entries that are added to the table if they have my discrepancy
            //as their parent discrepancy
            if(addedItem.getParentDiscrepancy().equals(getItem()) && transactionId != getLastTransactionId()) {

                //if my owner is an instanceof EditorDialog, get a reference to that
                //and add the log entry to it
                if(getOwner() instanceof EditorDialog) {
                    //The code to accomplish this section has been moved into DiscrepancyEditorDialog

                    //EditorDialog dialog = (EditorDialog) getOwner();

                    //dialog.addEditorPanel(new LogEntryEditor(addedItem, getOwner(), getEditorPanelHost(), Mode.VIEW_ONLY), BorderLayout.CENTER);
                }
            }
        }

        @Override
        public void onItemUpdated(LogEntry editedItem, long transactionId) {

        }

        @Override
        public void onItemDeleted(LogEntry deletedItem, long transactionId) {

        }
    }

    public static final String TITLE = "Discrepancy Editor";

    public static void main(String[] args) throws SQLException{

        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DiscrepancyEditor editor = new DiscrepancyEditor(frame, DiscrepancyTable.getInstance().getItemById(1), null);
            JPanel contentPane = new JPanel();
            contentPane.add(editor.getContentPane());
            frame.setContentPane(contentPane);
            frame.pack();
            frame.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {

        }
    }
}
