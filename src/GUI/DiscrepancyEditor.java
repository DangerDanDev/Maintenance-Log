package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import data.ComboBoxStatusTableListener;
import data.DBManager;
import data.DatabaseObject;
import data.tables.*;
import jdk.jshell.spi.ExecutionControl;
import model.Aircraft;
import model.Discrepancy;
import model.LogEntry;
import model.Status;
import model.actions.NewLogEntryAction;

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
    private JTextField tfDateCreated;
    private JComboBox cbTailNumber;
    private JTextField tfDateLastEdited;
    private JPanel discrepancyDetailsPanel;

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


    @Override
    public boolean isDataValid() {
        boolean validData = true;
        String errorText = "";
        String errorTitle = "";

        if(tfNarrative.getText().length() == 0) {
            errorText += "You must give the discrepancy a valid narrative, 1 character minimum.";
            errorTitle += "Narrative Required";
            validData = false;
        }

        if(!validData)
            JOptionPane.showMessageDialog(getOwner(), errorText, errorTitle, JOptionPane.ERROR_MESSAGE);

        return validData;
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

        private final JMenu menu = new JMenu("Discrepancy");

        private final JMenu scheduleMenu = new JMenu("Schedule");

        public void initMenu(JMenuBar menuBar) {

            menu.add(new NewLogEntryAction(getOwner(), getItem(), getEditorPanelHost()));

            scheduleMenu.add(new ScheduleStatusChangeAction());
            menu.add(scheduleMenu);

            menuBar.add(menu);
        }

        public void removeMenu(JMenuBar menuBar) {
            menuBar.remove(menu);
        }
    }

    private class ScheduleStatusChangeAction extends  AbstractAction {
        public ScheduleStatusChangeAction() {
            super("Status Change");

            setEnabled(false); //TODO: functionality not yet implemented
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: Implement
        }
    }

    @Override
    public void unsubscribeFromTableUpdates() {
        super.unsubscribeFromTableUpdates();

        cbStatusTableListener.unsubscribe();
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
