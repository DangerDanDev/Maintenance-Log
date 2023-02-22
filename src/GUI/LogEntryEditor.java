package GUI;

import GUI.BaseClasses.EditorPanel;
import GUI.BaseClasses.PopupMenuListener;
import data.tables.LogEntryTable;
import model.LogEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LogEntryEditor extends EditorPanel<LogEntry> {
    private JTextArea tfNarrative;
    private JPanel borderPanel;
    private JCheckBox cbShowOnNotes;
    private JTextField tfDateCreated;
    private JTextField tfDateLastEdited;
    private JTextArea tfCrew;
    private JPanel eastPanel;

    /**
     * The popup menu used on the tfNarrative field; will allow the user to enter
     * edit mode, or (if in edit mode) save the Log Entry
     */
    private JPopupMenu popupMenu = new JPopupMenu("Log Entry Menu");
    private JMenuItem editMenuItem = new JMenuItem("Edit log entry");
    private JMenuItem saveMenuItem = new JMenuItem("Save log entry");
    private JMenuItem deleteMenuItem = new JMenuItem("Delete Log Entry");

    public LogEntryEditor(LogEntry entry, Window owner, EditorPanelHost host, Mode mode) {
        super(owner, LogEntryTable.getInstance(), host);

        tfNarrative.addKeyListener(getItemEditListener());
        cbShowOnNotes.addActionListener(getItemEditListener());

        initPopupMenu();

        setItem(entry);
        setMode(mode);
    }

    @Override
    public JPanel getContentPane() {
        return borderPanel;
    }

    @Override
    public void refreshData() {
        tfNarrative.setText(getItem().getNarrative());
        tfDateCreated.setText(getItem().getDateCreated().toString());

        refreshDateLastEdited();

        cbShowOnNotes.setSelected(getItem().isShowOnNotes());
    }

    @Override
    public void refreshDateLastEdited() {
        LocalDateTime dateLastEdited = getDateTime(getItem().getDateLastEdited());

        tfDateLastEdited.setText(getInstantToString(getItem().getDateLastEdited()));
    }

    @Override
    public boolean isDataValid() {
        boolean valid = true;

        if(tfNarrative.getText().length() == 0) {
            valid = false;
            JOptionPane.showMessageDialog(getOwner(), "A valid narrative is required to save this log entry.", "Narrative required",
                    JOptionPane.ERROR_MESSAGE);
        }

        return valid;
    }

    @Override
    public void pushChanges() {
        getItem().setNarrative(tfNarrative.getText());
        getItem().setShowOnNotes(cbShowOnNotes.isSelected());
    }

    @Override
    public void setMode(Mode mode) {

        boolean editable = mode == Mode.EDIT;

        Color color = Color.WHITE;
        if(!editable)
            color = Color.LIGHT_GRAY;

        //if we are in edit mode, disable the edit button
        //and enable the save button
        editMenuItem.setEnabled(!editable);
        saveMenuItem.setEnabled(editable);

        tfNarrative.setEditable(editable);
        tfNarrative.setBackground(color);

        tfCrew.setEditable(editable);
        tfCrew.setBackground(color);

        super.setMode(mode);
    }

    private void saveAndReturnToViewOnlyMode() {
        if(save())
            setMode(Mode.VIEW_ONLY);
        else
            JOptionPane.showMessageDialog(null, "There was an error saving the log entry. Good luck with that!");
    }

    private void initPopupMenu() {
        editMenuItem.addActionListener(e -> setMode(Mode.EDIT));
        popupMenu.add(editMenuItem);

        saveMenuItem.addActionListener(e -> saveAndReturnToViewOnlyMode());
        popupMenu.add(saveMenuItem);

        deleteMenuItem.addActionListener(e -> removeLogEntry());
        popupMenu.add(deleteMenuItem);

        PopupMenuListener popupMenuListener = new PopupMenuListener(popupMenu);
        tfNarrative.addMouseListener(popupMenuListener);
    }

    private void removeLogEntry(){
        try {
            LogEntryTable.getInstance().removeItem(getItem());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(getOwner(),"Deleting log entry operation was unsuccessful.");
        }
    }
}
