package pl.genschu.bloomooemulator.utils;

import java.nio.charset.Charset;
import java.util.*;

public class StringUtils
{
	public static String convertNullTerminatedText(byte[] input, Charset charset) {
		if (input == null) {
			return null;
		}

		int nullIndex = 0;
		while (nullIndex < input.length && input[nullIndex] != 0) {
			nullIndex++;
		}

		return new String(input, 0, nullIndex, charset);
	}
}
