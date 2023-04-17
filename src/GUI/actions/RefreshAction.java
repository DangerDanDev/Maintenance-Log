package GUI.actions;

import GUI.BaseClasses.EditorPanel;
import GUI.BaseClasses.Refreshable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class RefreshAction extends BaseAction {

    public final Refreshable refreshable;

    public RefreshAction(Window owner, EditorPanel.EditorPanelHost host, Refreshable refreshable) {
        super(owner, host, "Refresh");

        this.refreshable = refreshable;

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() ->refreshable.refresh());
    }
}
