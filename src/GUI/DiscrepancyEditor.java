package GUI;

import data.DBManager;
import data.DatabaseObject;
import data.tables.DiscrepancyTable;
import data.tables.Table;
import model.Discrepancy;

import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;

public class DiscrepancyEditor extends EditorDialogAbstract<Discrepancy> {
    private JPanel contentPane;
    private JButton buttonOK;
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
        getRootPane().setDefaultButton(buttonOK);

        setLocation(2300, 440);

        setItem(discrepancy);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        tfTurnover.addKeyListener(getItemEditListener());
        tfDiscoveredBy.addKeyListener(getItemEditListener());
        tfNarrative.addKeyListener(getItemEditListener());
        tfPartsOnOrder.addKeyListener(getItemEditListener());
        cbStatus.addItemListener(getItemEditListener());
        cbTailNumber.addItemListener(getItemEditListener());
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

        tfDateCreated.setText(getItem().getDateCreated().toString());
        tfDateLastEdited.setText(getItem().getDateLastEdited().toString());
    }

    public static final String TITLE = "Discrepancy Editor";

    public static void main(String[] args) {

        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            DiscrepancyEditor dialog = new DiscrepancyEditor(DiscrepancyTable.getInstance().getItemById(3));
            dialog.pack();
            dialog.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
