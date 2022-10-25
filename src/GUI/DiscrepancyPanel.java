package GUI;

import data.Discrepancy;

import javax.swing.*;

public class DiscrepancyPanel {
    private JComboBox cbTail;
    //private JTextField textField1;
    //private JTextField textField2;
    private JTextArea tbTurnover;
    private JTextArea tbPartsOnOrder;
    private JComboBox cbStatus;
    private JPanel contentPane;
    private JTextArea tbNarrative;

    private Discrepancy discrepancy;

    public JPanel getContentPane() {
        return this.contentPane;
    }

    public DiscrepancyPanel(Discrepancy discrepancy) {
        setDiscrepancy(discrepancy);
    }

    public Discrepancy getDiscrepancy() {
        return discrepancy;
    }

    public void setDiscrepancy(Discrepancy discrepancy) {
        this.discrepancy = discrepancy;

        cbTail.setSelectedItem(discrepancy.getTailNum());
        tbTurnover.setText(discrepancy.getTurnover());
        tbPartsOnOrder.setText(discrepancy.getPartsOnOrder());
        tbNarrative.setText(discrepancy.getNarrative());

        //TODO: also set the status
    }

}
