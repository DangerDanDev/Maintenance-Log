package GUI;

import data.DatabaseManager;
import data.Discrepancy;
import data.Status;
import data.Tables.StatusTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DiscrepancySnippet {
    private JTextArea tbNarrative;
    private JPanel panel1;
    private JButton btnViewDiscrepancy;
    private JComboBox cbStatus;
    private JCheckBox cbShowOnNotes;
    private JTextField tbTailNumber;
    private JTextField tbDateCreated;
    private JTextField tbDiscoveredBy;

    private Discrepancy discrepancy;

    public DiscrepancySnippet(Discrepancy discrepancy) {

        try (Connection conn = DatabaseManager.getConnection()) {
            StatusTable.populateStatusComboBox(cbStatus, conn);

            btnViewDiscrepancy.addActionListener(new ViewDiscrepancyListener());

            setDiscrepancy(discrepancy);

        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public Discrepancy getDiscrepancy() {
        return discrepancy;
    }

    public void setDiscrepancy(Discrepancy discrepancy) {
        this.discrepancy = discrepancy;

        if(discrepancy != null) {
            tbNarrative.setText(discrepancy.getNarrative());
            cbStatus.setSelectedItem(discrepancy.getStatus());
            tbTailNumber.setText(discrepancy.getTailNum());
            tbDateCreated.setText(discrepancy.getDateCreated().truncatedTo(ChronoUnit.MINUTES).toString());

            setCBStatusSelection(discrepancy.getStatus());
        }
    }

    private void setCBStatusSelection(Status status) {
        for(int i = 0; i < cbStatus.getItemCount(); i++) {
            if(((Status)cbStatus.getItemAt(i)).getAbbreviation().equals(status.getAbbreviation()))
                cbStatus.setSelectedIndex(i);
        }
    }

    public JPanel getContentPane() {
        return panel1;
    }

    /**
     * Listens for when the user clicks the View Discrepancy button to open the
     * full discrepancy view and editing pane
     */
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
