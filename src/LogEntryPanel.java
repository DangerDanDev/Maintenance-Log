import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogEntryPanel {
    private JTextArea tfLogEntryDetails;
    private JPanel panel1;
    private JCheckBox cbShowOnNotes;
    private JButton deleteButton;
    private JButton bSave;
    private JButton bEdit;
    private JComboBox cbShift;
    private JTextArea textArea1;
    private JTextField textField1;

    public JPanel getContentPanel() {
        return this.panel1;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        LogEntryPanel entryPanel = new LogEntryPanel();
        frame.setContentPane(entryPanel.getContentPanel());
        frame.setSize(800,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        String shifts[] = { "Mids", "Days", "Swings"};
        cbShift = new JComboBox(shifts);
        cbShift.setSelectedItem(shifts[1]);
    }
}

