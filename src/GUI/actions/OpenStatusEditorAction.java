package GUI.actions;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import GUI.StatusEditorPanel;
import data.tables.StatusTable;
import model.Discrepancy;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class OpenStatusEditorAction extends BaseAction {
    public OpenStatusEditorAction(Window owner) {
        super(owner, null,"Status Editor");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditorDialog<Status> dialog = new EditorDialog<Status>(getOwner(), "Status Editor");

        try {

            for (Status status : StatusTable.getInstance().getAllItems())
                dialog.addEditorPanel(new StatusEditorPanel(getOwner(), status, dialog));

            dialog.getMenuManager().addActionToFileMenu(new NewStatusAction(getOwner(), getEditorPanelHost()));
            /*JButton newStatusButton = new JButton("New Status");
            newStatusButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Status status = new Status();
                    status.setTitle("new_status");

                    dialog.addEditorPanel(new StatusEditorPanel(getOwner(), status, dialog));
                }
            });
            dialog.addComponent(newStatusButton, BorderLayout.NORTH);*/


            dialog.pack();
            dialog.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(getOwner(), "Unable to open status editor, the connection to the database failed.");
        }
    }
}
