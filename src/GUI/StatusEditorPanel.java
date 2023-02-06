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
import java.util.ArrayList;

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

    @Override
    public Container getCustomContentPane() {
        return contentPane;
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
        if (super.save())
            return true;

        //show an error dialog if the save failed
        else
            JOptionPane.showMessageDialog(null, "Save failed!", "Save failed!", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    @Override
    public void refreshData() {
        tfStatusTitle.setText(getItem().getTitle());

        bColorPicker.setBackground(getItem().getColor());
        colorPlaceholder = getItem().getColor();

        bSave.setEnabled(false);
        bUndoChanges.setEnabled(false);
    }

    @Override
    public void pushChanges() {
        getItem().setTitle(tfStatusTitle.getText());
        getItem().setColor(colorPlaceholder);
    }

    public static void main(String[] args) throws SQLException {
        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            ArrayList<StatusEditorPanel> panels = new ArrayList<>();
            ArrayList<Status> statuses = StatusTable.getInstance().getAllItems();
            for(Status s : statuses)
                panels.add(new StatusEditorPanel(s));

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            for(StatusEditorPanel p : panels)
                panel.add(p.contentPane);

            JDialog dialog = new JDialog();
            dialog.setContentPane(panel);
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
