package pl.genschu.bloomooemulator.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class GameManager {
    private Array<GameEntry> games;

    public GameManager() {
        games = new Array<>();

        try {
            String json = Gdx.files.local("games.json").readString();

            Json jsonParser = new Json();
            games = jsonParser.fromJson(Array.class, GameEntry.class, json);
        } catch(Exception e) {
            e.printStackTrace();
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
        // save data as JSON by libGDX
        Json json = new Json();
        String jsonStr = json.toJson(games);
        Gdx.files.local("games.json").writeString(jsonStr, false);
    }
}