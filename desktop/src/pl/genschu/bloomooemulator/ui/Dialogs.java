package pl.genschu.bloomooemulator.ui;

import pl.genschu.bloomooemulator.GameListFrame;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
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
        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        JPanel pathButtonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton chooseFolderButton = new JButton(resourceBundle.getString("choose_folder"));
        JButton chooseIsoButton = new JButton(resourceBundle.getString("choose_iso"));
        JComboBox<String> mouseModeSelectBox = new JComboBox<>(new String[]{resourceBundle.getString("mouse_touch"), resourceBundle.getString("mouse_physical")});
        JCheckBox joystickCheckbox = new JCheckBox(resourceBundle.getString("use_virtual_joystick"));
        JCheckBox skipPoliceCheckbox = new JCheckBox(resourceBundle.getString("skip_code"));
        JCheckBox fullscreenCheckbox = new JCheckBox(resourceBundle.getString("stretch_fullscreen"));
        JCheckBox fpsCounterCheckbox = new JCheckBox(resourceBundle.getString("show_fps_counter"));

        chooseFolderButton.addActionListener(e -> chooseGamePath(dialog, pathField, true));
        chooseIsoButton.addActionListener(e -> chooseGamePath(dialog, pathField, false));
        pathButtonPanel.add(chooseFolderButton);
        pathButtonPanel.add(chooseIsoButton);
        pathPanel.add(pathField, BorderLayout.CENTER);
        pathPanel.add(pathButtonPanel, BorderLayout.EAST);

        if (game != null) {
            mouseModeSelectBox.setSelectedItem(game.getMouseMode());
            joystickCheckbox.setSelected(game.isMouseVirtualJoystick());
            skipPoliceCheckbox.setSelected(game.isSkipLicenceCode());
            fullscreenCheckbox.setSelected(!game.isMaintainAspectRatio());
            fpsCounterCheckbox.setSelected(game.isShowFpsCounter());
        }

        dialog.add(new JLabel(resourceBundle.getString("game_name")));
        dialog.add(nameField);
        dialog.add(new JLabel(resourceBundle.getString("game_path")));
        dialog.add(pathPanel);
        dialog.add(new JLabel(resourceBundle.getString("mouse_mode")));
        dialog.add(mouseModeSelectBox);
        dialog.add(joystickCheckbox);
        dialog.add(skipPoliceCheckbox);
        dialog.add(fullscreenCheckbox);
        dialog.add(fpsCounterCheckbox);

        JButton saveButton = new JButton(resourceBundle.getString("save"));
        saveButton.addActionListener(e -> {
            if (game == null) {
                GameEntry newGame = new GameEntry(
                        nameField.getText(),
                        pathField.getText(),
                        mouseModeSelectBox.getSelectedItem().toString(),
                        joystickCheckbox.isSelected(),
                        skipPoliceCheckbox.isSelected(),
                        !fullscreenCheckbox.isSelected());
                newGame.setShowFpsCounter(fpsCounterCheckbox.isSelected());
                gameManager.addGame(newGame);
            } else {
                game.setName(nameField.getText());
                game.setPath(pathField.getText());
                game.setMouseMode(mouseModeSelectBox.getSelectedItem().toString());
                game.setMouseVirtualJoystick(joystickCheckbox.isSelected());
                game.setSkipLicenceCode(skipPoliceCheckbox.isSelected());
                game.setMaintainAspectRatio(!fullscreenCheckbox.isSelected());
                game.setShowFpsCounter(fpsCounterCheckbox.isSelected());
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

    private void chooseGamePath(Component parent, JTextField pathField, boolean directoryMode) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(directoryMode ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
        chooser.setDialogTitle(resourceBundle.getString(directoryMode ? "choose_folder" : "choose_iso"));
        if (!directoryMode) {
            chooser.setFileFilter(new FileNameExtensionFilter("Archives (*.iso, *.zip)", "iso", "zip"));
        }

        String currentPath = pathField.getText();
        if (currentPath != null && !currentPath.isBlank()) {
            File currentFile = new File(currentPath);
            File currentDirectory = currentFile.isDirectory() ? currentFile : currentFile.getParentFile();
            if (currentDirectory != null) {
                chooser.setCurrentDirectory(currentDirectory);
            }
            chooser.setSelectedFile(currentFile);
        }

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            pathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    public void showDeleteDialog(GameEntry game) {
        int response = JOptionPane.showConfirmDialog(gameListFrame, resourceBundle.getString("confirm_delete").replace("{0}", game.getName()), resourceBundle.getString("game_delete"), JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            gameManager.removeGame(game);
            gameListFrame.refreshGameList();
        }
    }
}
