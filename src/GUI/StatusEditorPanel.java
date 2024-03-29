package GUI;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import data.DBManager;
import data.tables.StatusTable;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class StatusEditorPanel extends EditorPanel<Status> {

    private JPanel contentPane;
    private JTextField tfStatusTitle;
    private JCheckBox cbShowOnNotes;
    private JCheckBox cbCompletesJob;
    private JButton bColorPicker;
    private JButton bSave;
    private JButton bUndoChanges;

    private Color colorPlaceHolder = Color.WHITE;

    public StatusEditorPanel(Window owner, Status status, EditorPanelHost host){
        super(owner, StatusTable.getInstance(),host);

        //hook up all the events that get this item marked as unsaved
        tfStatusTitle.addKeyListener(getItemEditListener());
        cbShowOnNotes.addActionListener(getItemEditListener());
        cbCompletesJob.addActionListener(getItemEditListener());
        bColorPicker.addActionListener(listener -> changeColor());

        //hook up the save and cancel button
        bSave.addActionListener(ActionListener -> save());
        bUndoChanges.addActionListener(ActionListener -> refreshData());

        setItem(status);
    }

    public StatusEditorPanel(Window owner, Status status) {
        this(owner, status, null);
    }

    @Override
    public JPanel getContentPane() {
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

    /**
     * Called when the user click the change color button
     */
    private void changeColor() {
        colorPlaceHolder = JColorChooser.showDialog(null, "Status color", colorPlaceHolder, false);

        //if the user selected a color, we will set the button's background to reflect it
        if (colorPlaceHolder != null) {
            bColorPicker.setBackground(colorPlaceHolder);
            onItemEdited();
        }
        //if the user did not pick a color, revert the placeholder back to the item's pre-existing color
        else
            colorPlaceHolder = getItem().getColor();
    }

    @Override
    public boolean save() {
        if (super.save())
            return true;

        //show an error dialog if the save failed
        else {
            JOptionPane.showMessageDialog(null, "Save failed!", "Save failed!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public void refreshData() {
        tfStatusTitle.setText(getItem().getTitle());

        bColorPicker.setBackground(getItem().getColor());
        colorPlaceHolder = getItem().getColor();

        cbShowOnNotes.setSelected(getItem().isShowOnNotes());
        cbCompletesJob.setSelected(getItem().isCompletesJob());

        bSave.setEnabled(false);
        bUndoChanges.setEnabled(false);
    }

    @Override
    public void pushChanges() {
        getItem().setTitle(tfStatusTitle.getText());
        getItem().setColor(colorPlaceHolder);
        getItem().setCompletesJob(cbCompletesJob.isSelected());
        getItem().setShowOnNotes(cbShowOnNotes.isSelected());
    }

}
