package pl.genschu.bloomooemulator.objects;
import pl.genschu.bloomooemulator.encoding.CLZW2Compression;
import pl.genschu.bloomooemulator.encoding.CRLECompression;
import com.badlogic.gdx.Gdx;

public class Image {
	private class CompressionTypes {
        public static final int None = 0;
        public static final int CLZW = 2;
        public static final int CRLEinCLZW = 3;
        public static final int CRLE = 4;
        public static final int JPEG = 5;
    }

    private byte[] rgba32;
    
    public void decodeData(byte[] imageData, byte[] alphaData, int compressionType) {
        if(compressionType == CompressionTypes.CRLE) {
            imageData = CRLECompression.decodeCRLE(imageData, 2);
            alphaData = CRLECompression.decodeCRLE(alphaData);
        }
        else if(compressionType == CompressionTypes.CRLEinCLZW || compressionType == CompressionTypes.CLZW) {
            try {
                imageData = CLZW2Compression.decompress(imageData);
                alphaData = CLZW2Compression.decompress(alphaData);
                
                if(compressionType == CompressionTypes.CRLEinCLZW) {
                    imageData = CRLECompression.decodeCRLE(imageData, 2);
                    alphaData = CRLECompression.decodeCRLE(alphaData);
                }
            } catch (IllegalArgumentException e) {
                Gdx.app.error("ImageDecompression", "Wystąpił problem z CLZW, przerywam ładowanie");
                return;
            }
        }
    }
}