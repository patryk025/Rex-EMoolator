package pl.genschu.bloomooemulator.ui;

import pl.genschu.bloomooemulator.GameListFrame;
import pl.genschu.bloomooemulator.logic.AppPaths;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.logic.MouseMode;
import pl.genschu.bloomooemulator.patch.PatchCatalog;
import pl.genschu.bloomooemulator.patch.PatchCompatibility;
import pl.genschu.bloomooemulator.patch.PatchIssue;
import pl.genschu.bloomooemulator.patch.PatchManagerController;
import pl.genschu.bloomooemulator.patch.PatchRowVM;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
        JTextField familyField = new JTextField(game != null && game.getFamilyOverride() != null ? game.getFamilyOverride() : "");
        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        JPanel pathButtonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton chooseFolderButton = new JButton(resourceBundle.getString("choose_folder"));
        JButton chooseIsoButton = new JButton(resourceBundle.getString("choose_iso"));
        final MouseMode[] mouseModes = MouseMode.values();
        String[] mouseModeLabels = new String[mouseModes.length];
        for (int i = 0; i < mouseModes.length; i++) {
            mouseModeLabels[i] = mouseModeLabel(mouseModes[i]);
        }
        JComboBox<String> mouseModeSelectBox = new JComboBox<>(mouseModeLabels);
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
            mouseModeSelectBox.setSelectedIndex(game.getMouseModeEnum().ordinal());
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
        dialog.add(new JLabel(resourceBundle.getString("family_override")));
        dialog.add(familyField);

        JButton saveButton = new JButton(resourceBundle.getString("save"));
        saveButton.addActionListener(e -> {
            if (game == null) {
                GameEntry newGame = new GameEntry(
                        nameField.getText(),
                        pathField.getText(),
                        mouseModes[mouseModeSelectBox.getSelectedIndex()].key(),
                        joystickCheckbox.isSelected(),
                        skipPoliceCheckbox.isSelected(),
                        !fullscreenCheckbox.isSelected());
                newGame.setShowFpsCounter(fpsCounterCheckbox.isSelected());
                newGame.setFamilyOverride(familyField.getText());
                gameManager.addGame(newGame);
            } else {
                game.setName(nameField.getText());
                game.setPath(pathField.getText());
                game.setMouseMode(mouseModes[mouseModeSelectBox.getSelectedIndex()]);
                game.setMouseVirtualJoystick(joystickCheckbox.isSelected());
                game.setSkipLicenceCode(skipPoliceCheckbox.isSelected());
                game.setMaintainAspectRatio(!fullscreenCheckbox.isSelected());
                game.setShowFpsCounter(fpsCounterCheckbox.isSelected());
                game.setFamilyOverride(familyField.getText());
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

    /**
     * Per-game patch manager: lists patches relevant to this game (catalog ∪ installed,
     * filtered by compatibility), and lets the user install (download from the manifest
     * source) and enable/disable them. Enabling a FAMILY-level patch sets {@code forceEnable}
     * automatically, since {@link PatchManager#activeFor} only mounts EXACT matches otherwise.
     */
    public void showPatchesDialog(GameEntry game) {
        if (game.ensureDllHash()) {
            gameManager.updateGame(game);
        }
        final String hash = game.getDllHash();
        if (hash == null || hash.isBlank()) {
            JOptionPane.showMessageDialog(gameListFrame, resourceBundle.getString("patch_no_hash"));
            return;
        }
        final String family = game.resolveFamily();
        final PatchManagerController controller = new PatchManagerController(
                hash, family, AppPaths.patchesRootDir(), AppPaths.patchesIndexFile(), PatchCatalog.loadBundled());

        final JDialog dialog = new JDialog(gameListFrame,
                resourceBundle.getString("patch_dialog_title").replace("{0}", game.getName()), true);
        dialog.setSize(760, 480);
        dialog.setLayout(new BorderLayout(8, 8));
        dialog.setLocationRelativeTo(gameListFrame);

        String[] columns = {
                resourceBundle.getString("patch_col_name"),
                resourceBundle.getString("patch_col_author"),
                resourceBundle.getString("patch_col_version"),
                resourceBundle.getString("patch_col_compat"),
                resourceBundle.getString("patch_col_installed"),
                resourceBundle.getString("patch_col_enabled")
        };
        final DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        final JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        final JTextArea notes = new JTextArea(4, 40);
        notes.setEditable(false);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);

        // Clickable info link for the selected patch's `reference` URL (hidden when none).
        final String[] referenceUrl = {null};
        final JLabel referenceLink = new JLabel();
        referenceLink.setVisible(false);
        referenceLink.setForeground(new Color(0x1A0DAB));
        referenceLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        referenceLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openUrl(referenceUrl[0]);
            }
        });

        final List<PatchRowVM> rows = new ArrayList<>();
        final Runnable rebuild = () -> {
            rows.clear();
            rows.addAll(controller.rows());
            model.setRowCount(0);
            for (PatchRowVM row : rows) {
                model.addRow(new Object[]{row.getDisplayName(), row.getAuthor(), row.getVersion(),
                        compatLabel(row.getCompat()), yesNo(row.isInstalled()), yesNo(row.isEnabled())});
            }
            StringBuilder sb = new StringBuilder();
            if (rows.isEmpty()) {
                sb.append(resourceBundle.getString("patch_none_relevant"));
            } else {
                for (PatchIssue issue : controller.issues()) {
                    sb.append("• [").append(issue.getSeverity()).append("] ").append(issue.getMessage()).append('\n');
                }
            }
            notes.setText(sb.toString());
        };

        // Show/refresh the reference link for whichever row is currently selected.
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int sel = table.getSelectedRow();
            String url = (sel >= 0 && sel < rows.size()) ? rows.get(sel).getReference() : null;
            referenceUrl[0] = url;
            if (url != null && !url.isBlank()) {
                referenceLink.setText("<html><u>" + resourceBundle.getString("patch_open_source") + "</u></html>");
                referenceLink.setToolTipText(url);
                referenceLink.setVisible(true);
            } else {
                referenceLink.setVisible(false);
                referenceLink.setToolTipText(null);
            }
        });

        JButton installButton = new JButton(resourceBundle.getString("patch_install"));
        JButton toggleButton = new JButton(resourceBundle.getString("patch_toggle"));
        JButton uninstallButton = new JButton(resourceBundle.getString("patch_uninstall"));
        JButton moveUpButton = new JButton(resourceBundle.getString("patch_move_up"));
        JButton moveDownButton = new JButton(resourceBundle.getString("patch_move_down"));
        JButton closeButton = new JButton(resourceBundle.getString("close"));

        final JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        installButton.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_select_row"));
                return;
            }
            PatchRowVM row = rows.get(sel);
            if (row.isInstalled()) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_already_installed"));
                return;
            }
            if (!row.isDownloadable()) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_not_downloadable"));
                return;
            }
            final String patchId = row.getId();
            installButton.setEnabled(false);
            progressBar.setIndeterminate(false);
            progressBar.setValue(0);
            progressBar.setVisible(true);
            // Switch to indeterminate at most once, when the server omits Content-Length.
            final boolean[] indeterminateSet = {false};
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                private Exception error;

                @Override
                protected Void doInBackground() {
                    try {
                        controller.install(patchId, (bytesRead, totalBytes) -> {
                            if (totalBytes > 0) {
                                setProgress((int) (bytesRead * 100 / totalBytes)); // SwingWorker dedupes same value
                            } else if (!indeterminateSet[0]) {
                                indeterminateSet[0] = true;
                                SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(true));
                            }
                        });
                    } catch (Exception ex) {
                        error = ex;
                    }
                    return null;
                }

                @Override
                protected void done() {
                    installButton.setEnabled(true);
                    progressBar.setVisible(false);
                    progressBar.setIndeterminate(false);
                    if (error != null) {
                        JOptionPane.showMessageDialog(dialog,
                                resourceBundle.getString("patch_install_failed").replace("{0}", String.valueOf(error.getMessage())));
                    }
                    rebuild.run();
                }
            };
            worker.addPropertyChangeListener(evt -> {
                if ("progress".equals(evt.getPropertyName())) {
                    progressBar.setValue((Integer) evt.getNewValue());
                }
            });
            worker.execute();
        });

        toggleButton.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_select_row"));
                return;
            }
            PatchRowVM row = rows.get(sel);
            if (!row.isInstalled()) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_install_first"));
                return;
            }
            controller.toggle(row.getId());
            rebuild.run();
            if (sel < table.getRowCount()) {
                table.setRowSelectionInterval(sel, sel);
            }
        });

        uninstallButton.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_select_row"));
                return;
            }
            PatchRowVM row = rows.get(sel);
            if (!row.isInstalled()) {
                return;
            }
            int resp = JOptionPane.showConfirmDialog(dialog,
                    resourceBundle.getString("patch_confirm_uninstall").replace("{0}", row.getDisplayName()),
                    resourceBundle.getString("patch_uninstall"), JOptionPane.YES_NO_OPTION);
            if (resp != JOptionPane.YES_OPTION) {
                return;
            }
            controller.uninstall(row.getId());
            rebuild.run();
        });

        moveUpButton.addActionListener(e -> movePatchOrder(table, rows, controller, -1, rebuild));
        moveDownButton.addActionListener(e -> movePatchOrder(table, rows, controller, 1, rebuild));

        closeButton.addActionListener(e -> {
            gameListFrame.refreshGameList();
            dialog.dispose();
        });

        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.add(new JLabel(resourceBundle.getString("patch_notes")), BorderLayout.NORTH);
        notesPanel.add(new JScrollPane(notes), BorderLayout.CENTER);
        notesPanel.add(referenceLink, BorderLayout.SOUTH);
        JPanel buttons = new JPanel();
        buttons.add(installButton);
        buttons.add(toggleButton);
        buttons.add(uninstallButton);
        buttons.add(moveUpButton);
        buttons.add(moveDownButton);
        buttons.add(closeButton);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(buttons, BorderLayout.CENTER);
        bottom.add(progressBar, BorderLayout.SOUTH);
        JPanel south = new JPanel(new BorderLayout(8, 4));
        south.add(notesPanel, BorderLayout.CENTER);
        south.add(bottom, BorderLayout.SOUTH);
        dialog.add(south, BorderLayout.SOUTH);

        rebuild.run();

        // Merge the remote catalog off the EDT, then re-render once it arrives.
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                controller.refreshRemote();
                return null;
            }

            @Override
            protected void done() {
                rebuild.run();
            }
        }.execute();

        dialog.setVisible(true);
    }

    /**
     * Moves the selected registry-tracked patch up ({@code -1}) or down ({@code +1})
     * in mount order via the controller, then re-renders and keeps it selected.
     */
    private void movePatchOrder(JTable table, List<PatchRowVM> rows, PatchManagerController controller,
                                int direction, Runnable rebuild) {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            return;
        }
        String movedId = rows.get(sel).getId();
        if (!controller.move(movedId, direction)) {
            return;
        }
        rebuild.run();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getId().equals(movedId)) {
                table.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private String mouseModeLabel(MouseMode mode) {
        switch (mode) {
            case PHYSICAL:
                return resourceBundle.getString("mouse_physical");
            case TOUCH:
            default:
                return resourceBundle.getString("mouse_touch");
        }
    }

    private String compatLabel(PatchCompatibility compat) {
        switch (compat) {
            case EXACT:
                return resourceBundle.getString("patch_compat_exact");
            case FAMILY:
                return resourceBundle.getString("patch_compat_family");
            default:
                return resourceBundle.getString("patch_compat_none");
        }
    }

    private String yesNo(boolean value) {
        return resourceBundle.getString(value ? "yes" : "no");
    }

    /** Opens {@code url} in the user's default browser; no-op when blank or unsupported. */
    private void openUrl(String url) {
        if (url == null || url.isBlank()) {
            return;
        }
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(gameListFrame,
                    resourceBundle.getString("patch_open_source_failed").replace("{0}", String.valueOf(ex.getMessage())));
        }
    }
}
