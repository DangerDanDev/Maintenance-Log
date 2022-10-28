package GUI;

import data.Discrepancy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DiscrepancyDialog {
    private JPanel contentPane;
    private JPanel westPanel;
    private JPanel centerPanel;
    private JButton btnNewLogEntry;
    private JScrollPane scrollLogEntries;
    private JPanel pnlLogEntries;

    private JDialog dialog;
    private Discrepancy discrepancy;

    private DiscrepancyPanel discrepancyPanel;

    public DiscrepancyDialog(JFrame parent, Discrepancy discrepancy) {
       setDialog(new JDialog(parent, Dialog.ModalityType.APPLICATION_MODAL));
       getDialog().setSize(800,600);
       getDialog().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
       getDialog().setContentPane(contentPane);

       initEventHandlers();

       setDiscrepancy(discrepancy);
    }

    private void initEventHandlers() {
        btnNewLogEntry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pnlLogEntries.add(new LogEntryPanel().getContentPanel());
                scrollLogEntries.revalidate();
            }
        });
    }

    public void show() {
        getDialog().setVisible(true);
    }

    public JDialog getDialog() {
        return dialog;
    }

    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    public Discrepancy getDiscrepancy() {
        return discrepancy;
    }

    public void setDiscrepancy(Discrepancy discrepancy) {
        this.discrepancy = discrepancy;

        discrepancyPanel = new DiscrepancyPanel(discrepancy);
        westPanel.removeAll();
        westPanel.add(discrepancyPanel.getContentPane());

        pnlLogEntries.removeAll();
        for(int i = 0; i < 3; i++) {
            pnlLogEntries.add(new LogEntryPanel().getContentPanel());
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        pnlLogEntries = new JPanel();
        pnlLogEntries.setLayout(new BoxLayout(pnlLogEntries, BoxLayout.Y_AXIS));
    }
}
