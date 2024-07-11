package pl.genschu.bloomooemulator.utils;

public class CodeBeautifier
{
	private static String addIntent(int lvl) {
		String intent = "";
		for(int i = 0; i < lvl; i++) {
			intent += "\t";
		}
		return intent;
	}
	public static void beautify(String code) {
		int indentLvl = 0;
		String[] chars = code.split("");
		StringBuilder beautified = new StringBuilder();
		boolean newline = false;
		for(String character : chars) {
			if(character.equals("{")) {
				beautified.append(addIntent(indentLvl++)).append(character).append("\n").append(addIntent(indentLvl));
				newline = false;
			}
			else if(character.equals("}")) {
				beautified.append(addIntent(--indentLvl)).append(character).append("\n").append(addIntent(indentLvl));
				newline = false;
			}
			else if(character.equals(";")) {
				beautified.append(character).append("\n").append(addIntent(indentLvl-1));
				newline = true;
			}
			else {
				if(newline) {
					beautified.append("\t");
					newline = false;
				}
				beautified.append(character);
			}
		}
		System.out.println(beautified);
	}
}
