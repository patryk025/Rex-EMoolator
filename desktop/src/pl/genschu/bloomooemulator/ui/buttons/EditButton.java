package pl.genschu.bloomooemulator.ui.buttons;

import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.ui.ButtonColumn;
import pl.genschu.bloomooemulator.ui.Dialogs;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class EditButton extends ButtonColumn {
    GameManager gameManager;

    public EditButton(JTable table, int column, GameManager gameManager) {
        super(table, column);
        this.gameManager = gameManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // get selected row
        int row = table.getSelectedRow();
        GameEntry game = gameManager.getGames().get(row);
        Dialogs.getInstance().showGameDialog(game);
    }
}
