package pl.genschu.bloomooemulator.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

import pl.genschu.bloomooemulator.logic.GameEntry;

public class GameEntryRenderer extends JPanel implements ListCellRenderer<GameEntry> {
    private JLabel nameLabel;
    private JButton runButton;
    private JButton editButton;
    private JButton deleteButton;

    public GameEntryRenderer() {
        setLayout(new BorderLayout());

        Locale locale = Locale.getDefault();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("gui_strings.translation", locale);

        nameLabel = new JLabel();
        runButton = new JButton(resourceBundle.getString("add_game"));
        editButton = new JButton(resourceBundle.getString("edit_game"));
        deleteButton = new JButton(resourceBundle.getString("delete_game"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(runButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(nameLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends GameEntry> list, GameEntry value, int index, boolean isSelected, boolean cellHasFocus) {
        nameLabel.setText(value.toString());

        runButton.addActionListener(e -> runGame(value));
        editButton.addActionListener(e -> editGame(value));
        deleteButton.addActionListener(e -> deleteGame(value));

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }

    private void runGame(GameEntry game) {
        // Logic to run the game
    }

    private void editGame(GameEntry game) {
        // Logic to edit the game
    }

    private void deleteGame(GameEntry game) {
        // Logic to delete the game
    }
}
