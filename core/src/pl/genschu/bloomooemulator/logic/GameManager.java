package pl.genschu.bloomooemulator.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class GameManager {
    private Array<GameEntry> games;
    private final String filePath;

    public GameManager(String filePath) {
        games = new Array<>();
        this.filePath = filePath + "/games.json";

        try {
            File file = new File(this.filePath);

            // to, tylko żeby IntelliJ nie narzekał na nieużywaną wartość zwracaną
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            if (file.exists()) {
                // load json from file
                String json = loadFile(file);
                Json jsonParser = new Json();
                GameEntry[] entries = jsonParser.fromJson(GameEntry[].class, json);
                games = new Array<>(entries);
            }
        } catch(Exception e) {
            Gdx.app.error("GameManager", "Error loading games.json", e);
        }
    }

    private String loadFile(File file) {
        StringBuilder content = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            int data;
            while ((data = reader.read()) != -1) {
                content.append((char) data);
            }
        } catch (IOException e) {
            Gdx.app.error("GameManager", "Error reading file: " + file.getAbsolutePath(), e);
        } catch (Exception e) {
            Gdx.app.error("GameManager", "Unexpected error", e);
        }
        return content.toString();
    }

    private void saveFile(File file, String data) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(data);
        } catch (IOException e) {
            Gdx.app.error("GameManager", "Error writing file: " + file.getAbsolutePath(), e);
        } catch (Exception e) {
            Gdx.app.error("GameManager", "Unexpected error", e);
        }
    }

    public Array<GameEntry> getGames() {
        return games;
    }

    public void addGame(GameEntry game) {
        games.add(game);
        game.setId(games.size - 1);
        saveData();
    }

    public void removeGame(GameEntry game) {
        games.removeValue(game, true);
        saveData();
    }

    public void updateGame(GameEntry game) {
        for(int i = 0; i < games.size; i++) {
            if(games.get(i).getId() == game.getId()) {
                games.set(i, game);
                break;
            }
        }
        saveData();
    }

    public void runGame(GameEntry game) {
        // Run game
    }

    public void saveData() {
        Json json = new Json();
        String jsonStr = json.toJson(games);
        saveFile(new File(filePath), jsonStr);
    }
}