package pl.genschu.bloomooemulator.ui;

import pl.genschu.bloomooemulator.GameListFrame;
import pl.genschu.bloomooemulator.logic.AppPaths;
import pl.genschu.bloomooemulator.logic.GameEntry;
import pl.genschu.bloomooemulator.logic.GameFamilies;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.patch.InstalledPatch;
import pl.genschu.bloomooemulator.patch.PatchCatalog;
import pl.genschu.bloomooemulator.patch.PatchCompatibility;
import pl.genschu.bloomooemulator.patch.PatchInstaller;
import pl.genschu.bloomooemulator.patch.PatchIssue;
import pl.genschu.bloomooemulator.patch.PatchManager;
import pl.genschu.bloomooemulator.patch.PatchManifest;
import pl.genschu.bloomooemulator.patch.PatchRegistry;
import pl.genschu.bloomooemulator.patch.PatchRegistryEntry;
import pl.genschu.bloomooemulator.patch.PatchSource;
import pl.genschu.bloomooemulator.patch.PatchSourceType;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        final String family = GameFamilies.familyFor(hash, game.getGameName());
        final File patchesRoot = AppPaths.patchesRootDir();
        final PatchRegistry registry = new PatchRegistry(AppPaths.patchesIndexFile());
        final PatchManager manager = new PatchManager(patchesRoot, registry);
        final PatchCatalog catalog = PatchCatalog.loadBundled();

        final JDialog dialog = new JDialog(gameListFrame,
                resourceBundle.getString("patch_dialog_title").replace("{0}", game.getName()), true);
        dialog.setSize(760, 480);
        dialog.setLayout(new BorderLayout(8, 8));
        dialog.setLocationRelativeTo(gameListFrame);

        String[] columns = {
                resourceBundle.getString("patch_col_name"),
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

        final List<PatchRow> rows = new ArrayList<>();
        final Runnable rebuild = () -> {
            rows.clear();
            rows.addAll(buildPatchRows(catalog, manager, registry, hash, family));
            model.setRowCount(0);
            for (PatchRow row : rows) {
                String name = row.manifest.getName() != null ? row.manifest.getName() : row.manifest.getId();
                String version = row.manifest.getVersion() != null ? row.manifest.getVersion() : "";
                model.addRow(new Object[]{name, version, compatLabel(row.compat), yesNo(row.installed), yesNo(row.enabled)});
            }
            StringBuilder sb = new StringBuilder();
            if (rows.isEmpty()) {
                sb.append(resourceBundle.getString("patch_none_relevant"));
            } else {
                for (PatchIssue issue : manager.validate(manager.activeFor(hash, family))) {
                    sb.append("• [").append(issue.getSeverity()).append("] ").append(issue.getMessage()).append('\n');
                }
            }
            notes.setText(sb.toString());
        };

        JButton installButton = new JButton(resourceBundle.getString("patch_install"));
        JButton toggleButton = new JButton(resourceBundle.getString("patch_toggle"));
        JButton closeButton = new JButton(resourceBundle.getString("close"));

        installButton.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_select_row"));
                return;
            }
            PatchRow row = rows.get(sel);
            if (row.installed) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_already_installed"));
                return;
            }
            PatchSource src = row.manifest.getSource();
            if (src == null || src.getType() != PatchSourceType.URL) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_not_downloadable"));
                return;
            }
            installButton.setEnabled(false);
            new SwingWorker<Void, Void>() {
                private Exception error;

                @Override
                protected Void doInBackground() {
                    try {
                        PatchInstaller.installFromSource(row.manifest, patchesRoot);
                    } catch (Exception ex) {
                        error = ex;
                    }
                    return null;
                }

                @Override
                protected void done() {
                    installButton.setEnabled(true);
                    if (error != null) {
                        JOptionPane.showMessageDialog(dialog,
                                resourceBundle.getString("patch_install_failed").replace("{0}", String.valueOf(error.getMessage())));
                    }
                    manager.rescan();
                    rebuild.run();
                }
            }.execute();
        });

        toggleButton.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_select_row"));
                return;
            }
            PatchRow row = rows.get(sel);
            if (!row.installed) {
                JOptionPane.showMessageDialog(dialog, resourceBundle.getString("patch_install_first"));
                return;
            }
            PatchRegistryEntry entry = registry.find(hash, row.manifest.getId());
            boolean nowEnabled = !(entry != null && entry.isEnabled());
            boolean force = row.compat != PatchCompatibility.EXACT;
            int order = entry != null ? entry.getOrder() : nextOrder(registry, hash);
            registry.upsert(new PatchRegistryEntry(hash, row.manifest.getId(), nowEnabled, nowEnabled && force, order));
            registry.save();
            rebuild.run();
            if (sel < table.getRowCount()) {
                table.setRowSelectionInterval(sel, sel);
            }
        });

        closeButton.addActionListener(e -> {
            gameListFrame.refreshGameList();
            dialog.dispose();
        });

        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.add(new JLabel(resourceBundle.getString("patch_notes")), BorderLayout.NORTH);
        notesPanel.add(new JScrollPane(notes), BorderLayout.CENTER);
        JPanel buttons = new JPanel();
        buttons.add(installButton);
        buttons.add(toggleButton);
        buttons.add(closeButton);
        JPanel south = new JPanel(new BorderLayout(8, 4));
        south.add(notesPanel, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        dialog.add(south, BorderLayout.SOUTH);

        rebuild.run();

        // Merge the remote catalog off the EDT, then re-render once it arrives.
        new SwingWorker<List<PatchManifest>, Void>() {
            @Override
            protected List<PatchManifest> doInBackground() {
                return PatchCatalog.loadRemote(PatchCatalog.DEFAULT_REMOTE_URL);
            }

            @Override
            protected void done() {
                try {
                    catalog.mergeFrom(get());
                    rebuild.run();
                } catch (Exception ignored) {
                    // remote merge is best-effort; bundled catalog already shown
                }
            }
        }.execute();

        dialog.setVisible(true);
    }

    private List<PatchRow> buildPatchRows(PatchCatalog catalog, PatchManager manager,
                                          PatchRegistry registry, String hash, String family) {
        LinkedHashMap<String, PatchManifest> byId = new LinkedHashMap<>();
        for (PatchManifest m : catalog.all()) {
            byId.put(m.getId(), m);
        }
        for (InstalledPatch p : manager.allInstalled()) {
            byId.put(p.getManifest().getId(), p.getManifest()); // installed manifest is authoritative
        }
        List<PatchRow> rows = new ArrayList<>();
        for (PatchManifest m : byId.values()) {
            PatchCompatibility compat = m.compatibilityFor(hash, family);
            boolean installed = manager.byId(m.getId()) != null;
            if (compat == PatchCompatibility.NONE && !installed) {
                continue; // not for this game
            }
            PatchRegistryEntry entry = registry.find(hash, m.getId());
            boolean enabled = entry != null && entry.isEnabled();
            rows.add(new PatchRow(m, compat, installed, enabled));
        }
        return rows;
    }

    private int nextOrder(PatchRegistry registry, String hash) {
        int max = -1;
        for (PatchRegistryEntry e : registry.entriesFor(hash)) {
            max = Math.max(max, e.getOrder());
        }
        return max + 1;
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

    private static final class PatchRow {
        final PatchManifest manifest;
        final PatchCompatibility compat;
        final boolean installed;
        final boolean enabled;

        PatchRow(PatchManifest manifest, PatchCompatibility compat, boolean installed, boolean enabled) {
            this.manifest = manifest;
            this.compat = compat;
            this.installed = installed;
            this.enabled = enabled;
        }
    }
}
