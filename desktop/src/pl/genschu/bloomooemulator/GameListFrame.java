package pl.genschu.bloomooemulator;

import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.ui.GameEntryRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class GameListFrame extends JFrame {
    private JList<GameEntry> gameList;
    private DefaultListModel<GameEntry> gameListModel;
    private JButton addButton;
    ResourceBundle resourceBundle;
    GameManager gameManager = new GameManager(getFolderPath());

    public GameListFrame() {
        Locale locale = Locale.getDefault();
        resourceBundle = ResourceBundle.getBundle("gui_strings.translation", locale);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Lista Gier");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gameListModel = new DefaultListModel<>();
        gameList = new JList<>(gameListModel);
        gameList.setCellRenderer(new GameEntryRenderer());
        JScrollPane scrollPane = new JScrollPane(gameList);
        add(scrollPane, BorderLayout.CENTER);

        addButton = new JButton(resourceBundle.getString("add_game"));
        addButton.addActionListener(e -> showGameDialog());
        /*editButton = new JButton("Edytuj");
        editButton.addActionListener(e -> showEditGameDialog());
        deleteButton = new JButton("Usuń");
        deleteButton.addActionListener(e -> showDeleteGameDialog());*/

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        //buttonPanel.add(editButton);
        //buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshGameList();
    }

    private String getFolderPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (osName.contains("win")) {
            return System.getenv("USERPROFILE") + "\\.rexemoolator";
        } else if (osName.contains("mac") || osName.contains("nix") || osName.contains("nux")) {
            return userHome + "/.rexemoolator/games.json";
        } else {
            return userHome + "/.rexemoolator/games.json";
        }
    }

    private void refreshGameList() {
        gameListModel.clear();
        for (GameEntry game : gameManager.getGames()) {
            gameListModel.addElement(game);
        }
    }

    private void showGameDialog() {
        showGameDialog(null);
    }

    private void showGameDialog(GameEntry game) {
        JDialog dialog = new JDialog(this, game == null ? resourceBundle.getString("add_game") : resourceBundle.getString("edit_game"), true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 300);

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
            refreshGameList();
            dialog.dispose();
        });

        JButton cancelButton = new JButton(resourceBundle.getString("cancel"));
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    private void showDeleteDialog(GameEntry game) {
        int response = JOptionPane.showConfirmDialog(this, resourceBundle.getString("confirm_delete").replace("{0}", game.getName()), resourceBundle.getString("game_delete"), JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            gameManager.removeGame(game);
            refreshGameList();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameListFrame frame = new GameListFrame();
            frame.setVisible(true);
        });
    }
}
