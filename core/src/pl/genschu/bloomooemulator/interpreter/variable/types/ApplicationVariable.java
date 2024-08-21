package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApplicationVariable extends Variable {
	private List<EpisodeVariable> episodes;
	private EpisodeVariable firstEpisode;
	private File path;

	public ApplicationVariable(String name, Context context) {
		super(name, context);

		this.setMethod("EXIT", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				Gdx.app.exit();
				return null;
			}
		});
		this.setMethod("GETLANGUAGE", new Method(
			"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: check if is get from system
				return VariableFactory.createVariable("STRING", null, "POL", getContext()); // default
			}
		});
		this.setMethod("RUN", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("STRING", "methodName", true),
				new Parameter("mixed", "param1...paramN", true)
			),
			"mixed?"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RUN is not implemented yet");
			}
		});
		this.setMethod("RUNENV", new Method(
			List.of(
				new Parameter("STRING", "sceneName", true),
				new Parameter("STRING", "behaviourName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RUNENV is not implemented yet");
			}
		});
		this.setMethod("SETLANGUAGE", new Method(
			List.of(
				new Parameter("STRING", "languageCode", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETLANGUAGE is not implemented yet");
			}
		});
	}

	@Override
	public String getType() {
		return "APPLICATION";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("AUTHOR", "BLOOMOO_VERSION", "CREATIONTIME", "DESCRIPTION", "EPISODES", "LASTMODIFYTIME", "PATH", "SCENES", "STARTWITH", "VERSION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public List<EpisodeVariable> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(List<EpisodeVariable> episodes) {
		this.episodes = episodes;
	}

	public EpisodeVariable getFirstEpisode() {
		return firstEpisode;
	}

	public void setFirstEpisode(EpisodeVariable firstEpisode) {
		this.firstEpisode = firstEpisode;
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public void reloadEpisodes() {
		String[] episodes = this.getAttribute("EPISODES").getValue().toString().split(",");
		String firstEpisode = this.getAttribute("STARTWITH").getValue().toString();

		this.episodes = new ArrayList<>();

		for(String episode : episodes) {
			Variable episodeVariable = this.getContext().getVariable(episode);
			if(episodeVariable.getName().equals(firstEpisode)) {
				this.firstEpisode = (EpisodeVariable) episodeVariable;
			}
			this.episodes.add((EpisodeVariable) episodeVariable);
		}
	}
}
