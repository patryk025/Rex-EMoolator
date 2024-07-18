package pl.genschu.bloomooemulator;

import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.ui.ButtonColumn;
import pl.genschu.bloomooemulator.ui.Dialogs;
import pl.genschu.bloomooemulator.ui.TextTableRenderer;
import pl.genschu.bloomooemulator.ui.buttons.DeleteButton;
import pl.genschu.bloomooemulator.ui.buttons.EditButton;
import pl.genschu.bloomooemulator.ui.buttons.RunButton;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class GameListFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
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

        Dialogs.getInstance(this, gameManager, resourceBundle);

        setTitle("Lista Gier");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        model = new DefaultTableModel();
        model.addColumn("Nazwa");
        model.addColumn("");
        model.addColumn("");
        model.addColumn("");

        table = new JTable(model) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column > 0 ? JButton.class : String.class;
            }
        };

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setCellRenderer(new TextTableRenderer());

        // set row height
        table.setRowHeight(50);

        refreshGameList();

        // Setting renderers and editors
        ButtonColumn buttonColumn = new RunButton(table, 1, gameManager);
        ButtonColumn buttonColumn2 = new EditButton(table, 2, gameManager);
        ButtonColumn buttonColumn3 = new DeleteButton(table, 3, gameManager);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        addButton = new JButton(resourceBundle.getString("add_game"));
        addButton.addActionListener(e -> Dialogs.getInstance().showGameDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameListFrame().setVisible(true);
        });
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

    public void refreshGameList() {
        // remove all rows from model
        model.setRowCount(0);
        // add new rows
        for (GameEntry game : gameManager.getGames()) {
            model.addRow(new Object[]{
                    game.toString(),
                    resourceBundle.getString("run_game"),
                    resourceBundle.getString("edit_game"),
                    resourceBundle.getString("delete_game")
            });
        }
    }
}

