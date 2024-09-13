package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;
import pl.genschu.bloomooemulator.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CNVLoaderVariable extends Variable {
	private Map<String, Context> loadedContexts = new HashMap<>();

	public CNVLoaderVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	public String getType() {
		return "CNVLOADER";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("LOAD", new Method(
				List.of(
						new Parameter("STRING", "cnvFile", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String cnvFile = ArgumentsHelper.getString(arguments.get(0));
				if (loadedContexts.containsKey(cnvFile)) { // TODO: check if stay intact or reload CNV
					Gdx.app.log("CNVLoaderVariable", "CNV already loaded " + cnvFile);
					return null;
				}

				Context cnvContext = new Context();
				cnvContext.setParentContext(getContext());
				String cnvPath = FileUtils.resolveRelativePath(CNVLoaderVariable.this, cnvFile);

				CNVParser cnvParser = new CNVParser();
				try {
					Gdx.app.log("CNVLoaderVariable", "Loading " + cnvFile);
					cnvParser.parseFile(new File(cnvPath), cnvContext);

					for (Map.Entry<String, Variable> entry : cnvContext.getVariables().entrySet()) {
						getContext().setVariable(entry.getKey(), entry.getValue());
					}

					loadedContexts.put(cnvFile, cnvContext);
					Gdx.app.log("CNVLoaderVariable", "Loaded " + cnvFile);
				} catch (IOException e) {
					Gdx.app.error("CNVLoaderVariable", "Error loading " + cnvFile, e);
					throw new RuntimeException(e);
				}

				return null;
			}
		});

		this.setMethod("RELEASE", new Method(
				List.of(
						new Parameter("STRING", "cnvFile", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String cnvFile = ArgumentsHelper.getString(arguments.get(0));
				if (!loadedContexts.containsKey(cnvFile)) {
					Gdx.app.log("CNVLoaderVariable", "CNV not loaded " + cnvFile);
					return null;
				}

				Context cnvContext = loadedContexts.get(cnvFile);

				Gdx.app.log("CNVLoaderVariable", "Unloading " + cnvFile);

				for (String variableName : cnvContext.getVariables().keySet()) {
					getContext().removeVariable(variableName);
				}

				loadedContexts.remove(cnvFile);
				Gdx.app.log("CNVLoaderVariable", "Unloaded " + cnvFile);
				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}
}
