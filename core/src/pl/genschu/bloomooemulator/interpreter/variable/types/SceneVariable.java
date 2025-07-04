package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import pl.genschu.bloomooemulator.engine.decision.events.AnimoEvent;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.ImageLoader;
import pl.genschu.bloomooemulator.loader.SoundLoader;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class SceneVariable extends Variable {
	private File path;
	private ImageVariable background;
	private Music music;
	private int musicVolume = 1000;

	private int minHotSpotZ = 0;
	private int maxHotSpotZ = 10000000;

	public SceneVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("GETMINHSPRIORITY", new Method(
				List.of(),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", getMinHotSpotZ(), getContext());
			}
		});
		this.setMethod("GETMAXHSPRIORITY", new Method(
				List.of(),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", getMaxHotSpotZ(), getContext());
			}
		});
		this.setMethod("GETPLAYINGANIMO", new Method(
				List.of(
						new Parameter("STRING", "groupName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				Variable groupVariable = getContext().getVariable(ArgumentsHelper.getString(arguments.get(0)));

				if(groupVariable instanceof GroupVariable) {
					GroupVariable group = (GroupVariable) groupVariable;
					group.variables.clear();

					Collection<Variable> drawList = getContext().getGraphicsVariables().values();
					for(Variable variable : drawList) {
						if(variable instanceof AnimoVariable) {
							AnimoVariable animo = (AnimoVariable) variable;
							if(animo.isPlaying()) {
								group.variables.add(animo);
							}
						}
					}
				}
				else {
					Gdx.app.log("SceneVariable", "Variable " + groupVariable.getName() + " is not GROUP. Ignoring it.");
				}
				return null;
			}
		});
		this.setMethod("PAUSE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(music != null) {
					music.pause();
				}

				Collection<Variable> graphicsVariables = getContext().getGraphicsVariables().values();
				for (Variable variable : graphicsVariables) {
					if (variable instanceof AnimoVariable) {
						AnimoVariable animo = (AnimoVariable) variable;
						if (animo.isPlaying()) {
							animo.changeAnimoState(AnimoEvent.PAUSE);
						}
					}
				}
				return null;
			}
		});
		this.setMethod("REMOVECLONES", new Method(
				List.of(
						new Parameter("STRING", "varName", true),
						new Parameter("INTEGER", "firstId", true),
						new Parameter("INTEGER", "lastId", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String varName = ArgumentsHelper.getString(arguments.get(0));
				int firstId = ArgumentsHelper.getInteger(arguments.get(1));
				int lastId = ArgumentsHelper.getInteger(arguments.get(2));

				Variable variable = getContext().getVariable(varName);
				if(variable == null) {
					Gdx.app.error("SceneVariable", "Variable " + varName + " not found");
					return null;
				}
				List<Variable> clones = variable.getClones();
				for(Variable clone : clones) {
					int id = Integer.parseInt(getName().substring(getName().lastIndexOf("_") + 1));
					if(id >= firstId && id <= lastId) {
						clones.remove(clone);
						getContext().removeVariable(clone.getName());
					}
				}
				return null;
			}
		});
		this.setMethod("RESUME", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(music != null) {
					music.setVolume(musicVolume);
					music.play();
				}
				return null;
			}
		});
		this.setMethod("RESUMEONLY", new Method(
				List.of(
						new Parameter("STRING", "groupName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				Variable groupVariable = getContext().getVariable(ArgumentsHelper.getString(arguments.get(0)));

				if(groupVariable instanceof GroupVariable) {
					GroupVariable group = (GroupVariable) groupVariable;
					for(Variable variable : group.variables) {
						if(variable instanceof AnimoVariable) {
							AnimoVariable animo = (AnimoVariable) variable;
							animo.fireMethod("RESUME");
						}
					}
				}
				return null;
			}
		});
		this.setMethod("RUN", new Method(
				List.of(
						new Parameter("STRING", "varName", true),
						new Parameter("STRING", "methodName", true),
						new Parameter("mixed", "param1...paramN", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String varName = ArgumentsHelper.getString(arguments.get(0));
				String methodName = ArgumentsHelper.getString(arguments.get(1));
				Object[] params = new Object[arguments.size() - 2];
				for(int i = 2; i < arguments.size(); i++) {
					params[i - 2] = arguments.get(i);
				}
				Context variableContext = ((Variable) arguments.get(0)).getContext(); // we need to get context from argument, as Scene has only context from Application.def

				Variable var = variableContext.getVariable(varName);
				if(var == null) {
					Gdx.app.error("SceneVariable", "Variable not found: " + varName);
					return null;
				}
				if(var instanceof ExpressionVariable) {
					var = (Variable) var.getValue();
				}
				if(var instanceof StringVariable) {
					var = variableContext.getVariable(((StringVariable) var).GET());
				}
                return var.fireMethod(methodName, params);
			}
		});
		this.setMethod("RUNCLONES", new Method(
				List.of(
						new Parameter("STRING", "varName", true),
						new Parameter("INTEGER", "firstCloneIndex", true),
						new Parameter("INTEGER", "lastCloneIndex", true),
						new Parameter("STRING", "behaviourName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String varName = ArgumentsHelper.getString(arguments.get(0));
				int firstIndex = ArgumentsHelper.getInteger(arguments.get(1));
				int lastIndex = ArgumentsHelper.getInteger(arguments.get(2));
				String behaviourName = ArgumentsHelper.getString(arguments.get(3));

				Context variableContext = ((Variable) arguments.get(0)).getContext(); // we need to get context from argument, as Scene has only context from Application.def

				Variable targetVariable = variableContext.getVariable(varName);

				if(targetVariable == null) {
					Gdx.app.error("SceneVariable", "RUNCLONES: Variable not found: " + varName);
					return null;
				}

				Variable behaviour = variableContext.getVariable(behaviourName);

				if(!(behaviour instanceof BehaviourVariable)) {
					Gdx.app.error("SceneVariable", "RUNCLONES: Behaviour not found: " + behaviourName);
					return null;
				}

				List<Variable> clones = targetVariable.getClones();

				if(firstIndex < 0) {
					firstIndex = 0;
				}
				if(lastIndex < 0 || lastIndex >= clones.size()) {
					lastIndex = clones.size() - 1;
				}

				for(int i = firstIndex; i <= lastIndex; i++) {
					Variable clone = clones.get(i);

					behaviour.fireMethod(behaviour.getAttribute("CONDITION") != null ? "RUNC" : "RUN", clone);
				}

				return null;
			}
		});
		this.setMethod("SETMINHSPRIORITY", new Method(
				List.of(
						new Parameter("INTEGER", "minHSPriority", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int minHSPriority = ArgumentsHelper.getInteger(arguments.get(0));
				setMinHotSpotZ(minHSPriority);
				return null;
			}
		});
		this.setMethod("SETMAXHSPRIORITY", new Method(
				List.of(
						new Parameter("INTEGER", "maxHSPriority", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int maxHSPriority = ArgumentsHelper.getInteger(arguments.get(0));
				setMaxHotSpotZ(maxHSPriority);
				return null;
			}
		});
		this.setMethod("SETMUSICVOLUME", new Method(
				List.of(
						new Parameter("INTEGER", "volume", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
	            musicVolume = ArgumentsHelper.getInteger(arguments.get(0));
				if(music != null) {
					music.setVolume(musicVolume / 1000.0f);
				}
				return null;
			}
		});
		this.setMethod("STARTMUSIC", new Method(
				List.of(
						new Parameter("STRING", "filename", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(music != null) {
					if(music.isPlaying()) {
						music.stop();
						music = null;
					}
				}
				setAttribute("MUSIC", new Attribute("STRING", ArgumentsHelper.getString(arguments.get(0))));
				getMusic();
				music.setLooping(true);
				music.setVolume(musicVolume / 1000.0f);
				music.play();
				return null;
			}
		});
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	@Override
	public String getType() {
		return "SCENE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("AUTHOR", "BACKGROUND", "CREATIONTIME", "DLLS", "LASTMODIFYTIME", "MUSIC", "PATH", "VERSION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public ImageVariable getBackground() {
		if(background == null && getAttribute("BACKGROUND") != null) {
			background = new ImageVariable(this.name + "_BACKGROUND", getContext());
			// check if background is present in language specific location
			File langDir = new File(getContext().getGame().getCurrentSceneFile().getAbsolutePath() + "/" + getContext().getGame().getApplicationVariable().getLanguage());
			if(langDir.exists()) {
				File bkgLangDir = new File(langDir.getAbsolutePath() + "/" + getAttribute("BACKGROUND").getValue().toString());
				if(bkgLangDir.exists()) {
					String filePath = getContext().getGame().getApplicationVariable().getLanguage()+"/"+getAttribute("BACKGROUND").getValue().toString();
					background.setAttribute("FILENAME", new Attribute("FILENAME", filePath));
					background.init();
					return background;
				}
			}

			background.setAttribute("FILENAME", new Attribute("FILENAME", getAttribute("BACKGROUND").getValue().toString()));
			background.init();
		}

		return background;
	}

	public boolean isBackgroundLoaded() {
		return background != null && background.getImage() != null;
	}

	public void setBackground(ImageVariable background) {
		this.background = background;
	}

	public Music getMusic() {
		if(music == null && getAttribute("MUSIC") != null) {
			String filePath = getAttribute("MUSIC").getValue().toString();
			if(!filePath.startsWith("$")) {
				filePath = "$\\"+ filePath;
			}
			filePath = FileUtils.resolveRelativePath(this, filePath);

			music = getContext().getGame().getMusicCache().get(filePath);
			if(music != null) {
				return music;
			}

			FileHandle soundFileHandle = Gdx.files.absolute(filePath);

			music = Gdx.audio.newMusic(soundFileHandle);
			if(music != null) {
				music.setLooping(true);
			}

			getContext().getGame().getMusicCache().put(filePath, music);
		}
		return music;
	}

	public void setMusic(Music music) {
		this.music = music;
	}

	public int getMinHotSpotZ() {
		return minHotSpotZ;
	}

	public void setMinHotSpotZ(int minHotSpotZ) {
		this.minHotSpotZ = minHotSpotZ;
	}

	public int getMaxHotSpotZ() {
		return maxHotSpotZ;
	}

	public void setMaxHotSpotZ(int maxHotSpotZ) {
		this.maxHotSpotZ = maxHotSpotZ;
	}

	public int getVolume() {
		if(music != null) {
			return musicVolume;
		}
		return 0;
	}
}
