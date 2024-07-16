package pl.genschu.bloomooemulator.ui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import pl.genschu.bloomooemulator.logic.GameManager;
import pl.genschu.bloomooemulator.logic.GameEntry;

public class GameListApp extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private List<GameEntry> gameList;
    private ScrollPane scrollPane;
    private Table listTable;
    private GameManager gameManager;

    @Override
    public void create() {
        gameManager = new GameManager(Gdx.files.internal("").path());
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("default_skin/uiskin.json"));

        listTable = new Table();

        gameList = new List<>(skin);
        gameList.setItems(gameManager.getGames());

        scrollPane = new ScrollPane(listTable, skin);
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Button addButton = new TextButton("Dodaj grę", skin);
        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showGameDialog(null);
            }
        });

        mainTable.add(new Label("Lista gier", skin)).colspan(2).pad(10);
        mainTable.row();
        mainTable.add(scrollPane).expand().fill().colspan(2).pad(10);
        mainTable.row();
        mainTable.add(addButton).expandX().fillX().colspan(2).pad(10);

        refreshGameList();
    }

    private void showGameDialog(GameEntry game) {
        Dialog dialog = new Dialog(game == null ? "Dodaj grę" : "Edytuj grę", skin);
        TextField nameField = new TextField(game != null ? game.getName() : "", skin);
        TextField pathField = new TextField(game != null ? game.getPath() : "", skin);

        SelectBox<String> mouseModeSelectBox = new SelectBox<>(skin);
        mouseModeSelectBox.setItems("Dotykowo", "Fizyczny kursor");

        CheckBox joystickCheckbox = new CheckBox("Używaj wirtualnego joysticka do obsługi myszy", skin);
        CheckBox skipPoliceCheckbox = new CheckBox("Pomiń wprowadzanie kodu (Pana Policjanta)", skin);
        CheckBox fullscreenCheckbox = new CheckBox("Rozciągaj na cały ekran (ignoruj proporcje ekranu)", skin);

        dialog.getContentTable().add(new Label("Nazwa gry:", skin));
        dialog.getContentTable().add(nameField).width(200).pad(10);
        dialog.getContentTable().row();
        dialog.getContentTable().add(new Label("Ścieżka do plików:", skin));
        dialog.getContentTable().add(pathField).width(200).pad(10);
        dialog.getContentTable().row();
        dialog.getContentTable().add(new Label("Tryb pracy myszy:", skin));
        dialog.getContentTable().add(mouseModeSelectBox).colspan(2).left().pad(10);
        dialog.getContentTable().row();
        dialog.getContentTable().add(joystickCheckbox).colspan(2).left().pad(10);
        dialog.getContentTable().row();
        dialog.getContentTable().add(skipPoliceCheckbox).colspan(2).left().pad(10);
        dialog.getContentTable().row();
        dialog.getContentTable().add(fullscreenCheckbox).colspan(2).left().pad(10);
        dialog.getContentTable().row();

        TextButton saveButton = new TextButton("Zapisz", skin);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getListenerActor() == saveButton) {
                    if (game == null) {
                        gameManager.addGame(new GameEntry(nameField.getText(), pathField.getText(), mouseModeSelectBox.getSelected(), joystickCheckbox.isChecked(), skipPoliceCheckbox.isChecked(), fullscreenCheckbox.isChecked()));
                    } else {
                        game.setName(nameField.getText());
                        game.setPath(pathField.getText());
                        game.setMouseMode(mouseModeSelectBox.getSelected());
                        game.setMouseVirtualJoystick(joystickCheckbox.isChecked());
                        game.setSkipLicenceCode(skipPoliceCheckbox.isChecked());
                        game.setMaintainAspectRatio(fullscreenCheckbox.isChecked());
                        gameManager.updateGame(game);
                    }
                    refreshGameList();
                    dialog.hide();
                }
            }
        });
        dialog.button(saveButton);
        dialog.button("Anuluj", false);
        dialog.show(stage);
    }

    private Table createGameRow(GameEntry game) {
        Table row = new Table();

        Button runButton = new TextButton("Uruchom", skin);
        runButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameManager.runGame(game);
            }
        });

        Button editButton = new TextButton("Edytuj", skin);
        editButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showGameDialog(game);
            }
        });

        Button deleteButton = new TextButton("Usuń", skin);
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showDeleteDialog(game);
            }
        });

        row.add(new Label(game.toString(), skin)).expandX().fillX();
        row.add(runButton);
        row.add(editButton);
        row.add(deleteButton);

        return row;
    }

    private void showDeleteDialog(GameEntry game) {
        Dialog dialog = new Dialog("Usuwanie gry", skin) {
            @Override
            protected void result(Object object) {
                if (object.equals(true)) {
                    gameManager.removeGame(game);
                    refreshGameList();
                }
            }
        };
        dialog.text("Czy aby na pewno chcesz usunąć " + game.getName() + "?");
        dialog.button("Tak", true);
        dialog.button("Nie", false);
        dialog.show(stage);
    }

    private void refreshGameList() {
        listTable.clear();
        for (GameEntry game : gameManager.getGames()) {
            listTable.add(createGameRow(game)).expandX().fillX();
            listTable.row();
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
