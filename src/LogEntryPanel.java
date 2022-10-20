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
        tbDateSaved.setBackground(Color.LIGHT_GRAY);
        setEditable(false);
    }

    private void initListeners() {
        bSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //note the date saved and lock the form out of editing
                tbDateSaved.setText(new Date().toString());
                setEditable(false);
            }
        });
        bEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEditable(true);
            }
        });
    }

    public JPanel getContentPanel() {
        return this.panel1;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JScrollPane panelScroller = new JScrollPane(panel);
        panelScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panelScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        //LogEntryPanel entryPanel = new LogEntryPanel();
        //frame.setContentPane(entryPanel.getContentPanel());
        frame.setContentPane(panelScroller);

        for(int i = 0; i < 3; i++)
            panel.add(new LogEntryPanel().getContentPanel());

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

    /**
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        tbCrew.setEditable(editable);
        tbDetails.setEditable(editable);

        //if we are already in edit mode, grey out the button
        bEdit.setEnabled(!editable);
        bSave.setEnabled(editable);

        Color color = Color.WHITE;

        if (!editable)
            color = Color.LIGHT_GRAY;

        tbCrew.setBackground(color);
        tbDetails.setBackground(color);
    }
}

