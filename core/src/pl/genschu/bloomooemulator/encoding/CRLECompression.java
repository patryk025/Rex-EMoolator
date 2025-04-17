package pl.genschu.bloomooemulator.encoding;

import com.badlogic.gdx.Gdx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CRLECompression {

    public static byte[] decodeCRLE(byte[] data) {
        return decodeCRLE(data, 1);
    }

    public static byte[] decodeCRLE(byte[] data, int bulk) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int i = 0;
        try {
            while (i < data.length) {
                if (byteToInt(data[i]) < 128) {
                    outputStream.write(data, i + 1, byteToInt(data[i]) * bulk);
                    i += byteToInt(data[i]) * bulk + 1;
                } else {
                    int helper = byteToInt(data[i]) - 128;
                    byte[] repeatedBytes = new byte[helper * bulk];
                    for (int k = 0; k < helper; k++) {
                        for (int l = 0; l < bulk; l++) {
                            repeatedBytes[k * bulk + l] = data[i + l + 1];
                        }
                    }
                    try {
                        outputStream.write(repeatedBytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    i += 1 + bulk;
                }
            }
        }
        catch(Exception e) {
            Gdx.app.error("CRLECompression", "Error while decoding CRLE: " + e.getMessage());
        }
        return outputStream.toByteArray();
    }

    static int byteToInt(byte bajt) {
        return bajt & 0xff;
    }
}
