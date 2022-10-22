package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class LogEntryPanel {
    private JTextArea tbDetails;
    private JPanel panel1;
    private JCheckBox cbShowOnNotes;
    private JButton deleteButton;
    private JButton bSave;
    private JButton bEdit;
    private JComboBox cbShift;
    private JTextArea tbCrew;
    private JTextField tbDateSaved;

    public LogEntryPanel() {
        initListeners();
        setEditable(false);
    }

    private void initListeners() {
        bSave.addActionListener(new SaveButtonListener());
        bEdit.addActionListener(new EditButtonListener());
    }

    /**
     * Easily available public access to the panel with all the log entry information
     * @return the primary JPanel of this form, to be put into other forms or content areas
     */
    public JPanel getContentPanel() {
        return this.panel1;
    }



    private void createUIComponents() {
        // TODO: place custom component creation code here
        String shifts[] = { "Mids", "Days", "Swings"};
        cbShift = new JComboBox(shifts);
        cbShift.setSelectedItem(shifts[1]);
    }

    /**
     * Switches the form to either edit mode or view-only mode
     * @param editable pass True if you want the user to be able to edit this log entry,
     *                 false if you want to grey it out for read-only mode.
     */
    public void setEditable(boolean editable) {
        tbCrew.setEditable(editable);
        tbDetails.setEditable(editable);

        //save button will always be disabled if we are in edit mode already
        //or enabled if we are NOT in edit mode
        bEdit.setEnabled(!editable);

        //we can only save if we are in edit mode. If we can't edit,
        //there is nothing to say!
        bSave.setEnabled(editable);

        Color color = Color.WHITE;

        if (!editable)
            color = Color.LIGHT_GRAY;

        tbCrew.setBackground(color);
        tbDetails.setBackground(color);
    }

    public void save() {
        tbDateSaved.setText(new Date().toString());

        //TODO: implement save functionality
        System.out.println("Reminder: Save method not implemented yet.");
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            save();
            setEditable(false);
        }
    }

    private class EditButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setEditable(true);
        }
    }
}

