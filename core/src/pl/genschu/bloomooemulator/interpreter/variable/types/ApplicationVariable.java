package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;
import pl.genschu.bloomooemulator.utils.LangCodeConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApplicationVariable extends Variable {
	private List<EpisodeVariable> episodes;
	private EpisodeVariable firstEpisode;
	private File path;
	private String language = "POL";  // default language is Polish

	public ApplicationVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	public String getType() {
		return "APPLICATION";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("EXIT", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				Gdx.app.exit();
				return null;
			}
		});
		this.setMethod("GETLANGUAGE", new Method(
				"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return VariableFactory.createVariable("STRING", null, language, getContext()); // default
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
			public Variable execute(List<Object> arguments) {
				String varName = ArgumentsHelper.getString(arguments.get(0));
				String methodName = ArgumentsHelper.getString(arguments.get(1));
				Object[] params = new Object[arguments.size() - 2];
				for(int i = 2; i < arguments.size(); i++) {
					params[i - 2] = arguments.get(i);
				}
				Context variableContext = getContext().getGame().getCurrentSceneContext(); // we need to get current scene context

				Variable var = variableContext.getVariable(varName);
				Variable currentThis = variableContext.getThisVariable();
				variableContext.setThisVariable(var);
				if(var == null) {
					Gdx.app.error("ApplicationVariable", "Variable not found: " + varName);
					return null;
				}
				if(var instanceof ExpressionVariable) {
					var = (Variable) var.getValue();
				}
				Gdx.app.log("ApplicationVariable", "Running method " + methodName + " in variable " + varName + " of type " + var.getType());
				Variable result = var.fireMethod(methodName, params);
				variableContext.setThisVariable(currentThis);
				return result;
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
			public Variable execute(List<Object> arguments) {
				// TODO: check what if scene is not loaded
				String sceneName = ArgumentsHelper.getString(arguments.get(0));
				String behaviourName = ArgumentsHelper.getString(arguments.get(1));

				if(sceneName.equals(getContext().getGame().getCurrentScene())) {
					Variable behaviour = getContext().getGame().getCurrentSceneContext().getVariable(behaviourName); // we need to get current scene context because getContext() have only definition variables
					if(behaviour instanceof BehaviourVariable) {
						behaviour.fireMethod(behaviour.getAttribute("CONDITION") != null ? "RUNC" : "RUN", new StringVariable("", behaviourName, context));
					}
				}
				return null;
			}
		});
		this.setMethod("SETLANGUAGE", new Method(
				List.of(
						new Parameter("STRING", "languageCode", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String languageCode = ArgumentsHelper.getString(arguments.get(0));
				language = LangCodeConverter.lcidToIsoCode(languageCode);
				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("AUTHOR", "BLOOMOO_VERSION", "CREATIONTIME", "DESCRIPTION", "EPISODES", "LASTMODIFYTIME", "PATH", "SCENES", "STARTWITH", "VERSION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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
