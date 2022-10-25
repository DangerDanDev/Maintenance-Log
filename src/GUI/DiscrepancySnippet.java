package GUI;

import data.DatabaseManager;
import data.Discrepancy;
import data.Tables.StatusTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.SQLException;

public class DiscrepancySnippet {
    private JTextArea tbNarrative;
    private JPanel panel1;
    private JButton btnViewDiscrepancy;
    private JComboBox cbStatus;
    private JCheckBox cbShowOnNotes;

    private Discrepancy discrepancy;

    public DiscrepancySnippet(Discrepancy discrepancy) {

        try (Connection conn = DatabaseManager.getConnection()) {
            StatusTable.get().populateStatusComboBox(cbStatus, conn);
        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
        }

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
            cbStatus.setSelectedItem(discrepancy.getStatus());
            cbShowOnNotes.setText(getDiscrepancy().getStatus().getTitle());
            System.out.println("Status of Discrepancy '" + discrepancy.getNarrative() + ": " + getDiscrepancy().getStatus().getTitle());
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
