import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogEntryPanel {
    private JTextArea tfLogEntryDetails;
    private JPanel panel1;
    private JCheckBox cbShowOnNotes;
    private JButton deleteButton;
    private JButton bSave;

    public JPanel getContentPanel() {
        return this.panel1;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setContentPane(new LogEntryPanel().getContentPanel());
        frame.setSize(500,150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

