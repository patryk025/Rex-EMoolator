package pl.genschu.bloomooemulator.ui;

import pl.genschu.bloomooemulator.GameListFrame;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class Dialogs {
    GameListFrame gameListFrame;
    GameManager gameManager;
    ResourceBundle resourceBundle;
    private static Dialogs instance = null;

    private Dialogs(GameListFrame gameListFrame, GameManager gameManager, ResourceBundle resourceBundle) {
        this.gameListFrame = gameListFrame;
        this.gameManager = gameManager;
        this.resourceBundle = resourceBundle;
    }

    public static Dialogs getInstance(GameListFrame gameListFrame, GameManager gameManager, ResourceBundle resourceBundle) {
        if (instance == null) {
            instance = new Dialogs(gameListFrame, gameManager, resourceBundle);
        }
        return instance;
    }

    public static Dialogs getInstance() {
        return instance;
    }

    public void showGameDialog() {
        showGameDialog(null);
    }

    public void showGameDialog(GameEntry game) {
        JDialog dialog = new JDialog(gameListFrame, game == null ? resourceBundle.getString("add_game") : resourceBundle.getString("edit_game"), true);
        dialog.setLayout(new GridLayout(0, 1, 10, 10));
        dialog.setSize(500, 500);

        // center dialog
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation(dim.width / 2 - dialog.getSize().width / 2, dim.height / 2 - dialog.getSize().height / 2);

        JTextField nameField = new JTextField(game != null ? game.getName() : "");
        JTextField pathField = new JTextField(game != null ? game.getPath() : "");
        JComboBox<String> mouseModeSelectBox = new JComboBox<>(new String[]{resourceBundle.getString("mouse_touch"), resourceBundle.getString("mouse_physical")});
        JCheckBox joystickCheckbox = new JCheckBox(resourceBundle.getString("use_virtual_joystick"));
        JCheckBox skipPoliceCheckbox = new JCheckBox(resourceBundle.getString("skip_code"));
        JCheckBox fullscreenCheckbox = new JCheckBox(resourceBundle.getString("stretch_fullscreen"));

        if (game != null) {
            mouseModeSelectBox.setSelectedItem(game.getMouseMode());
            joystickCheckbox.setSelected(game.isMouseVirtualJoystick());
            skipPoliceCheckbox.setSelected(game.isSkipLicenceCode());
            fullscreenCheckbox.setSelected(game.isMaintainAspectRatio());
        }

        dialog.add(new JLabel(resourceBundle.getString("game_name")));
        dialog.add(nameField);
        dialog.add(new JLabel(resourceBundle.getString("game_path")));
        dialog.add(pathField);
        dialog.add(new JLabel(resourceBundle.getString("mouse_mode")));
        dialog.add(mouseModeSelectBox);
        dialog.add(joystickCheckbox);
        dialog.add(skipPoliceCheckbox);
        dialog.add(fullscreenCheckbox);

        JButton saveButton = new JButton(resourceBundle.getString("save"));
        saveButton.addActionListener(e -> {
            if (game == null) {
                gameManager.addGame(new GameEntry(
                        nameField.getText(),
                        pathField.getText(),
                        mouseModeSelectBox.getSelectedItem().toString(),
                        joystickCheckbox.isSelected(),
                        skipPoliceCheckbox.isSelected(),
                        fullscreenCheckbox.isSelected()));
            } else {
                game.setName(nameField.getText());
                game.setPath(pathField.getText());
                game.setMouseMode(mouseModeSelectBox.getSelectedItem().toString());
                game.setMouseVirtualJoystick(joystickCheckbox.isSelected());
                game.setSkipLicenceCode(skipPoliceCheckbox.isSelected());
                game.setMaintainAspectRatio(fullscreenCheckbox.isSelected());
                gameManager.updateGame(game);
            }
            gameListFrame.refreshGameList();
            dialog.dispose();
        });

        JButton cancelButton = new JButton(resourceBundle.getString("cancel"));
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    public void showDeleteDialog(GameEntry game) {
        int response = JOptionPane.showConfirmDialog(gameListFrame, resourceBundle.getString("confirm_delete").replace("{0}", game.getName()), resourceBundle.getString("game_delete"), JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            gameManager.removeGame(game);
            gameListFrame.refreshGameList();
        }
    }
}
