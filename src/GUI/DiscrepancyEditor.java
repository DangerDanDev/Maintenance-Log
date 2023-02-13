package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import data.DBManager;
import data.tables.DiscrepancyTable;
import data.tables.LogEntryTable;
import data.tables.StatusTable;
import model.Discrepancy;
import model.LogEntry;
import model.Status;

import javax.swing.*;
import java.awt.*;
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
    private JButton bAddLogEntry;


    public DiscrepancyEditor(Window owner, Discrepancy discrepancy, EditorPanelHost host) {
        super(owner, DiscrepancyTable.getInstance(), host);

        populateCBStatuses();
        setItem(discrepancy);

        tfTurnover.addKeyListener(getItemEditListener());
        tfDiscoveredBy.addKeyListener(getItemEditListener());
        tfNarrative.addKeyListener(getItemEditListener());
        tfPartsOnOrder.addKeyListener(getItemEditListener());
        cbStatus.addItemListener(getItemEditListener());
        cbTailNumber.addItemListener(getItemEditListener());
        cbStatus.addItemListener(e -> cbStatus.setBackground(((Status)cbStatus.getSelectedItem()).getColor()));
        bAddLogEntry.addActionListener(e -> createNewLogEntry());
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

        if(getItem().getStatus() == null)
            getItem().setStatus((Status)cbStatus.getItemAt(0));

        getItem().getStatus().selectInComboBox(cbStatus);
        cbStatus.setBackground(getItem().getStatus().getColor());

        tfDateCreated.setText(getItem().getDateCreated().toString());
        tfDateLastEdited.setText(getItem().getDateLastEdited().toString());
    }

    private void createNewLogEntry() {
        LogEntry logEntry = new LogEntry(this.getItem(), "", "");
        LogEntryEditor editor = new LogEntryEditor(logEntry, getOwner(), getEditorPanelHost());

        EditorDialog<LogEntry> dialog = new EditorDialog(this.getOwner(), "New Log Entry");
        dialog.addEditorPanel(editor, BorderLayout.CENTER);
        dialog.setSize(800,600);
        dialog.setVisible(true);
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
