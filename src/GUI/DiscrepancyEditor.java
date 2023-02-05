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

public class DiscrepancyEditor extends EditorDialogAbstract<Discrepancy> implements DatabaseObject.ChangeListener{
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

    //private Discrepancy discrepancy;

    public DiscrepancyEditor(Discrepancy discrepancy) {
        super("Discrepancy Editor");

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle(TITLE);

        setLocation(2300, 440);

        setItem(discrepancy);

        //subscribe to updates from the discrepancy table
        DiscrepancyTable.getInstance().addListener(discrepancyTableListener);

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

    }

    private void onOK() {

        //if the discrepancy has not been edited, we don't need to go through any of that saving
        //funny business
        if(!getItem().isSaved()) {

            //push the changes to the discrepancy
            //save the pushed changes
            getItem().setText(tfNarrative.getText());
            getItem().setTurnover(tfTurnover.getText());
            getItem().setDiscoveredBy(tfDiscoveredBy.getText());
            getItem().setPartsOnOrder(tfPartsOnOrder.getText());

            try {
                DiscrepancyTable.getInstance().updateItem(getItem());
            } catch (SQLException ex) {
                String options[] = {
                        "Close Window Anyway",
                        "Continue Editing",
                };

                int result = JOptionPane.showOptionDialog(null, "Save failed. Would Would you like to discard changes and close anyways or remain on " +
                        "this screen?", "Save error!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{}, 1);

                if (result != 0)
                    return;
            }
        }

        closeWindow();
    }

    private void onCancel() {
        closeWindow();
    }

    /**
     * unsubscribes me from DiscrepanciesTable and closes the window
     */
    private void closeWindow() {
        dispose();
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

            dialog = new DiscrepancyEditor(DiscrepancyTable.getInstance().getItemById(3));
            dialog.pack();
            dialog.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    /**
     * Listens for updates to the discrepancy table
     */
    private DiscrepancyTableListener discrepancyTableListener = new DiscrepancyTableListener();

    /**
     * Used to listen for updates from the Discrepancies Table
     */
    private class DiscrepancyTableListener implements Table.TableListener{

        @Override
        public void onItemAdded(Object addedItem) {
            //do nothing; we are ONLY concerned about our current discrepancy
        }

        @Override
        public void onItemUpdated(Object editedItem) {
            //if our current discrepancy was updated, we need to refresh
            if(editedItem.equals(getItem()))
                refreshData();
        }

        @Override
        public void onItemDeleted(Object deletedItem) {
            //do nothing; we are only concerned about our CURRENT discrepancy
        }
    }
}
