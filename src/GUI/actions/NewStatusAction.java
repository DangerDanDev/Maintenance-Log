package GUI.actions;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import GUI.StatusEditorPanel;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class NewStatusAction extends BaseAction {

    public NewStatusAction(Window owner, EditorPanel.EditorPanelHost host) {
        super(owner, host, "New Status");

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditorDialog<Status> dialog = new EditorDialog<Status>(getOwner(), "New Status");

        Status status = new Status();
        StatusEditorPanel panel = new StatusEditorPanel(getOwner(), status, getEditorPanelHost());

        dialog.addEditorPanel(panel);

        dialog.pack();
        dialog.setVisible(true);
    }
}
