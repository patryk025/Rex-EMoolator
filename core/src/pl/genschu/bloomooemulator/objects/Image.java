package pl.genschu.bloomooemulator.objects;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;
import pl.genschu.bloomooemulator.encoding.CLZW2Compression;
import pl.genschu.bloomooemulator.encoding.CRLECompression;
import com.badlogic.gdx.Gdx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Image {
	private class CompressionTypes {
        public static final int None = 0;
        public static final int CLZW = 2;
        public static final int CRLEinCLZW = 3;
        public static final int CRLE = 4;
        public static final int JPEG = 5;
    }

    public int width;
    public int height;
    public int offsetX;
    public int offsetY;
    public int colorDepth;
    private Texture imageTexture;

    public Image(int width, int height, int offsetX, int offsetY, int colorDepth, byte[] imageData, byte[] alphaData, int compressionType) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.colorDepth = colorDepth;
        decodeData(imageData, alphaData, compressionType);
    }
    
    public void decodeData(byte[] imageData, byte[] alphaData, int compressionType) {
        boolean isJPEG = false;

        if(compressionType == CompressionTypes.CRLE) {
            imageData = CRLECompression.decodeCRLE(imageData, 2);
            if(alphaData != null)
                alphaData = CRLECompression.decodeCRLE(alphaData);
        }
        else if(compressionType == CompressionTypes.CRLEinCLZW || compressionType == CompressionTypes.CLZW) {
            try {
                imageData = CLZW2Compression.decompress(imageData);
                if(alphaData != null)
                    alphaData = CLZW2Compression.decompress(alphaData);
                
                if(compressionType == CompressionTypes.CRLEinCLZW) {
                    imageData = CRLECompression.decodeCRLE(imageData, 2);
                    if(alphaData != null)
                        alphaData = CRLECompression.decodeCRLE(alphaData);
                }
            } catch (IllegalArgumentException e) {
                Gdx.app.error("ImageDecompression", "Wystąpił problem z CLZW, przerywam ładowanie");
                return;
            }
        }
        else if(compressionType == CompressionTypes.JPEG) {
            isJPEG = true;
            try {
                BufferedImage bufferedImage = ImageIO.read(new java.io.ByteArrayInputStream(imageData));
                imageData = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

                if(alphaData != null)
                    alphaData = CLZW2Compression.decompress(alphaData);
            } catch (IllegalArgumentException e) {
                Gdx.app.error("ImageDecompression", "Wystąpił problem z CLZW, przerywam ładowanie");
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        imageTexture = combineImageDataWithAlpha(new int[]{width, height}, imageData, alphaData, colorDepth, isJPEG);
    }

    private int[] convertRgbToRgb888(int rgb, int colorDepth) {
        int r, g, b;
        if (colorDepth == 16) {  // RGB565
            r = (rgb >> 11) & 0x1F;
            g = (rgb >> 5) & 0x3F;
            b = rgb & 0x1F;
            r = (r << 3) | (r >> 2);
            g = (g << 2) | (g >> 4);
            b = (b << 3) | (b >> 2);
        } else if (colorDepth == 15) {  // RGB555
            r = (rgb >> 10) & 0x1F;
            g = (rgb >> 5) & 0x1F;
            b = rgb & 0x1F;
            r = (r << 3) | (r >> 2);
            g = (g << 3) | (g >> 2);
            b = (b << 3) | (b >> 2);
        } else if(colorDepth == 24) {  // RGB888
            r = (rgb >> 16) & 0xFF;
            g = (rgb >> 8) & 0xFF;
            b = rgb & 0xFF;
        } else {
            throw new IllegalArgumentException("Nieobsługiwana głębia kolorów: " + colorDepth);
        }
        return new int[]{r, g, b};
    }

    private Texture combineImageDataWithAlpha(int[] dimensions, byte[] imageData, byte[] alphaData, int colorDepth, boolean isJPEG) {
        int width = dimensions[0];
        int height = dimensions[1];
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        int counter = 0;
        for (int i = 0; i < imageData.length; i += (isJPEG ? 3 : 2)) {
            int[] rgb888;
            if(isJPEG) {
                rgb888 = new int[] {imageData[i + 2] & 0xFF, imageData[i + 1] & 0xFF, imageData[i] & 0xFF};
            }
            else {
                int rgb565 = (imageData[i] & 0xFF) + ((imageData[i + 1] & 0xFF) << 8);
                rgb888 = this.convertRgbToRgb888(rgb565, colorDepth);
            }
            int alpha = alphaData != null && alphaData.length > 0 ? alphaData[counter] & 0xFF : 255;

            pixmap.drawPixel(counter % width, counter / width, Color.rgba8888(rgb888[0] / 255f, rgb888[1] / 255f, rgb888[2] / 255f, alpha / 255f));
            counter++;
        }

        return new Texture(pixmap);
    }

    public Texture getImageTexture() {
        return imageTexture;
    }
}