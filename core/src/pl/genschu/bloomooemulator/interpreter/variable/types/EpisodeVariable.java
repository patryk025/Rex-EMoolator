package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EpisodeVariable extends Variable {
	private List<SceneVariable> scenes;
	private SceneVariable firstScene;
	private File path;

	public EpisodeVariable(String name, Context context) {
		super(name, context);

		this.setMethod("BACK", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method BACK is not implemented yet");
			}
		});
		this.setMethod("GETCURRENTSCENE", new Method(
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String currentScene = context.getGame().getCurrentScene();
				return VariableFactory.createVariable("STRING", null, currentScene, getContext());
			}
		});
		this.setMethod("GOTO", new Method(
			List.of(
				new Parameter("STRING", "sceneName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GOTO is not implemented yet");
			}
		});
	}

	@Override
	public String getType() {
		return "EPISODE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("AUTHOR", "CREATIONTIME", "DESCRIPTION", "LASTMODIFYTIME", "PATH", "SCENES", "STARTWITH", "VERSION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public List<SceneVariable> getScenes() {
		return scenes;
	}

	public void setScenes(List<SceneVariable> scenes) {
		this.scenes = scenes;
	}

	public SceneVariable getFirstScene() {
		return firstScene;
	}

	public void setFirstScene(SceneVariable firstScene) {
		this.firstScene = firstScene;
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public void reloadScenes() {
		String[] scenes = this.getAttribute("SCENES").getValue().toString().split(",");
		String firstEpisode = this.getAttribute("STARTWITH").getValue().toString();

		this.scenes = new ArrayList<>();

		for(String scene : scenes) {
			Variable sceneVariable = this.getContext().getVariable(scene, this);
			if(sceneVariable.getName().equals(firstEpisode)) {
				this.firstScene = (SceneVariable) sceneVariable;
			}
			this.scenes.add((SceneVariable) sceneVariable);
		}
	}
}
