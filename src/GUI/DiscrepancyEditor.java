package GUI;

import GUI.BaseClasses.EditorDialog;
import data.DBManager;
import data.tables.DiscrepancyTable;
import data.tables.StatusTable;
import model.Discrepancy;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class DiscrepancyEditor extends EditorDialog<Discrepancy> {
    private JPanel contentPane;
    private JButton buttonSave;
    private JButton buttonCancel;
    private JTextArea tfNarrative;
    private JTextArea tfTurnover;
    private JTextArea tfPartsOnOrder;
    private JTextField tfDiscoveredBy;
    private JComboBox cbStatus;
    private JPanel discrepancyDetailsPanel;
    private JPanel bottomPanel;
    private JTextField tfDateCreated;
    private JComboBox cbTailNumber;
    private JTextField tfDateLastEdited;


    public DiscrepancyEditor(Discrepancy discrepancy) {
        super("Discrepancy Editor", DiscrepancyTable.getInstance());

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonSave);

        setLocation(2300, 440);

        populateCBStatuses();
        setItem(discrepancy);

        buttonSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        tfTurnover.addKeyListener(getItemEditListener());
        tfDiscoveredBy.addKeyListener(getItemEditListener());
        tfNarrative.addKeyListener(getItemEditListener());
        tfPartsOnOrder.addKeyListener(getItemEditListener());
        cbStatus.addItemListener(getItemEditListener());
        cbTailNumber.addItemListener(getItemEditListener());

        pack();
    }

    @Override
    public Container getCustomContentPane() {
        return contentPane;
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
        getItem().getStatus().selectInComboBox(cbStatus);

        tfDateCreated.setText(getItem().getDateCreated().toString());
        tfDateLastEdited.setText(getItem().getDateLastEdited().toString());
    }

    public static final String TITLE = "Discrepancy Editor";

    public static void main(String[] args) throws SQLException{

        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            DiscrepancyEditor dialog = new DiscrepancyEditor(DiscrepancyTable.getInstance().getItemById(3));
            dialog.pack();
            dialog.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            System.exit(0);
        }
    }
}
