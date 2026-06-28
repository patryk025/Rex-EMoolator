package pl.genschu.bloomooemulator.ui.buttons;

import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.ui.ButtonColumn;
import pl.genschu.bloomooemulator.ui.Dialogs;

import javax.swing.*;
import java.awt.event.ActionEvent;

/** Game-list column button that opens the per-game patch manager dialog. */
public class PatchesButton extends ButtonColumn {
    private final GameManager gameManager;

    public PatchesButton(JTable table, int column, GameManager gameManager) {
        super(table, column);
        this.gameManager = gameManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        GameEntry game = gameManager.getGames().get(row);
        Dialogs.getInstance().showPatchesDialog(game);
    }
}
