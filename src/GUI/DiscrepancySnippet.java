package GUI;

import data.Discrepancy;

import javax.swing.*;

public class DiscrepancySnippet {
    private JTextArea tbNarrative;
    private JPanel panel1;
    private JComboBox comboBox1;
    private JCheckBox checkBox1;

    private Discrepancy discrepancy;

    public DiscrepancySnippet(Discrepancy discrepancy) {
        setDiscrepancy(discrepancy);
    }

    public Discrepancy getDiscrepancy() {
        return discrepancy;
    }

    public void setDiscrepancy(Discrepancy discrepancy) {
        this.discrepancy = discrepancy;

        tbNarrative.setText(discrepancy.getNarrative());
    }

    public JPanel getContentPane() {
        return panel1;
    }
}
