package pl.genschu.bloomooemulator;

import android.content.Intent;
import pl.genschu.bloomooemulator.logic.GameEntry;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        Intent i = getIntent();
        GameEntry game = (GameEntry) i.getSerializableExtra("game");
        
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new BlooMooEngine(game), config);
	}
}
