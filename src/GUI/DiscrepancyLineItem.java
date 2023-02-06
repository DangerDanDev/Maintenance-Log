package GUI;

import data.tables.StatusTable;
import model.Discrepancy;
import model.Status;

import javax.swing.*;
import java.sql.SQLException;

public class DiscrepancyLineItem extends LineItemBase<Discrepancy> {
    private JComboBox cbStatus;
    private JPanel panel1;
    private JTextField tfNarrative;
    private JTextField tfTurnover;
    private JTextField tfPartsOnOrder;

    public DiscrepancyLineItem(Discrepancy disc) throws SQLException {

        for(Status s : StatusTable.getInstance().getAllItems())
            cbStatus.addItem(s);

        setItem(disc);
    }

    @Override
    public void refreshData() {
        tfNarrative.setText(getItem().getText());
        tfTurnover.setText(getItem().getTurnover());
        tfPartsOnOrder.setText(getItem().getPartsOnOrder());

        getItem().getStatus().selectInComboBox(cbStatus);
    }

    @Override
    public JPanel getCustomContentPane() {
        return panel1;
    }
}
