package pl.genschu.bloomooemulator.encoding;

public class ScriptDecypher
{
	public static String decode(String cod, int offset) {
		String[] lines = cod.split("\n");
		StringBuilder decode = new StringBuilder();
		int i = 0;
		
		boolean add = false;
		int shift=1;
		int reset = offset/2;
		char letter;
		
		for(String line : lines) {
			i = 0;
			while(i < line.length()) {
				if(line.startsWith("<E>", i)) {
                    decode.append("\n");
                    i += 2;
				}
				else {
					if(!add){
                        letter = (char) (line.charAt(i) - shift);
                    }else{
                        letter = (char) (line.charAt(i) + shift);
                        shift++;
                    }
					
					if(
                        letter == 8361 // on Windows
                    ||  letter == 65530 // on Linux
                    ) letter = '}'; //naprawa drobnych artefaktów

                    decode.append(letter);
                    add=!add;
                    if(shift>reset)
                        shift=1;
				}
				i++;
			}
		}
		//System.out.println(decode);
		return decode.toString();
	}
}
