package GUI.actions;

import GUI.BaseClasses.EditorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class PrintAction extends BaseAction {

    private final JPanel printPanel;

    public PrintAction(Window owner, EditorPanel.EditorPanelHost host, JPanel printPanel) {
        super(owner, host, "Print");

        this.printPanel = printPanel;

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(getOwner(), "Printing functionality not yet implemented. Sorry!");
    }
}
