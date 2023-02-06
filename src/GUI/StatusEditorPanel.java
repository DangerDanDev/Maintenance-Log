package GUI;

import data.DBManager;
import data.tables.DiscrepancyTable;
import data.tables.StatusTable;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class StatusEditorPanel extends EditorDialogAbstract<Status>{

    private JPanel contentPane;
    private JTextField tfStatusTitle;
    private JCheckBox cbShowOnNotes;
    private JCheckBox cbCompletesJob;
    private JButton bColorPicker;
    private JButton bSave;
    private JButton bUndoChanges;

    private Color colorPlaceholder = Color.WHITE;

    public StatusEditorPanel(Status status){
        super("Status Editor", StatusTable.getInstance());

        //hook up all the events that get this item marked as unsaved
        tfStatusTitle.addKeyListener(getItemEditListener());
        cbShowOnNotes.addActionListener(getItemEditListener());
        cbCompletesJob.addActionListener(getItemEditListener());
        bColorPicker.addActionListener(getItemEditListener());

        //hook up the save and cancel button
        bSave.addActionListener(ActionListener -> save());
        bUndoChanges.addActionListener(ActionListener -> refreshData());

        setItem(status);
    }

    /**
     * This method is called when the user changes data on one of the controls;
     * effectively, it happens when anything that would require the item to be
     * re-saved.
     */
    @Override
    public void onItemEdited() {
        super.onItemEdited();

        bSave.setEnabled(true);
        bUndoChanges.setEnabled(true);
    }

    @Override
    public boolean save() {
        if(!super.save()) {

            //show an error dialog
            JOptionPane.showMessageDialog(null,"Save failed!", "Save failed!", JOptionPane.ERROR_MESSAGE);

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void refreshData() {
        tfStatusTitle.setText(getItem().getTitle());

        bColorPicker.setBackground(getItem().getColor());
        colorPlaceholder = getItem().getColor();
    }

    @Override
    public void pushChanges() {
        getItem().setTitle(tfStatusTitle.getText());
        getItem().setColor(colorPlaceholder);
    }

    public static void main(String[] args) throws SQLException {
        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            StatusEditorPanel panel = new StatusEditorPanel(StatusTable.getInstance().getItemById(5));

            JDialog dialog = new JDialog();
            dialog.setContentPane(panel.contentPane);
            dialog.pack();
            dialog.setLocation(3000, 600);
            dialog.setModal(true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
