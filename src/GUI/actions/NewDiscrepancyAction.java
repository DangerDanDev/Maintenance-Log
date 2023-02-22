package GUI.actions;

import GUI.BaseClasses.EditorPanel;
import GUI.DiscrepancyEditorDialog;
import model.Aircraft;
import model.Discrepancy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class NewDiscrepancyAction extends BaseAction {

    private final Aircraft aircraft;

    public NewDiscrepancyAction(Window owner, EditorPanel.EditorPanelHost host, Aircraft aircraft, MenuType menuType) {
        super(owner, host, "New Discrepancy");

        this.aircraft = aircraft;

        if(menuType == MenuType.JPopupMenu)
            putValue(MNEMONIC_KEY, KeyEvent.VK_N);

        else if(menuType == MenuType.JMenuBar)
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Discrepancy d = new Discrepancy();
        d.setAircraft(aircraft);

        DiscrepancyEditorDialog dialog = new DiscrepancyEditorDialog(getOwner(), d);
        dialog.setVisible(true);
    }
}
