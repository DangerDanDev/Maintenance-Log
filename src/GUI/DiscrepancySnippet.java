package GUI;

import data.Discrepancy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DiscrepancySnippet {
    private JTextArea tbNarrative;
    private JPanel panel1;
    private JButton btnViewDiscrepancy;

    private Discrepancy discrepancy;

    public DiscrepancySnippet(Discrepancy discrepancy) {
        setDiscrepancy(discrepancy);
        btnViewDiscrepancy.addActionListener(new ViewDiscrepancyListener());
    }

    public Discrepancy getDiscrepancy() {
        return discrepancy;
    }

    public void setDiscrepancy(Discrepancy discrepancy) {
        this.discrepancy = discrepancy;

        if(discrepancy != null) {
            tbNarrative.setText(discrepancy.getNarrative());
        }
    }

    public JPanel getContentPane() {
        return panel1;
    }

    private class ViewDiscrepancyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame();
            frame.setSize(800,600);

            DiscrepancyPanel discrepancyPanel = new DiscrepancyPanel(getDiscrepancy());
            frame.setContentPane(discrepancyPanel.getContentPane());

            frame.setVisible(true);
        }
    }
}
