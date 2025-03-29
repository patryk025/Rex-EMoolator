package pl.genschu.bloomooemulator.objects;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;
import pl.genschu.bloomooemulator.encoding.CLZW2Compression;
import pl.genschu.bloomooemulator.encoding.CRLECompression;
import com.badlogic.gdx.Gdx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Image {
	private static class CompressionTypes {
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
    private Texture originalImageTexture; // ładowane z pliku
    private Texture imageTexture;

    private boolean isLoaded = false;

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
            } catch (Exception e) {
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
            } catch (Exception e) {
                Gdx.app.error("ImageDecompression", "Wystąpił problem z CLZW, przerywam ładowanie");
                return;
            }
        }

        imageTexture = combineImageDataWithAlpha(width, height, imageData, alphaData, colorDepth, isJPEG);

        if(originalImageTexture != null)
            originalImageTexture.dispose();

        originalImageTexture = imageTexture;

        isLoaded = true;
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

    private Texture combineImageDataWithAlpha(int width, int height, byte[] imageData, byte[] alphaData, int colorDepth, boolean isJPEG) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        ByteBuffer pixelBuffer = pixmap.getPixels();

        int counter = 0;
        for (int i = 0; i < imageData.length; i += (isJPEG ? 3 : 2)) {
            int[] rgb888;
            if (isJPEG) {
                rgb888 =
                        new int[] {
                                imageData[i + 2] & 0xFF, imageData[i + 1] & 0xFF, imageData[i] & 0xFF
                        };
            } else {
                int rgb565 = (imageData[i] & 0xFF) + ((imageData[i + 1] & 0xFF) << 8);
                rgb888 = this.convertRgbToRgb888(rgb565, colorDepth);
            }
            int alpha = alphaData != null ? alphaData[counter] & 0xFF : 255;

            try {
                pixelBuffer.put(counter * 4, (byte) rgb888[0]);
                pixelBuffer.put(counter * 4 + 1, (byte) rgb888[1]);
                pixelBuffer.put(counter * 4 + 2, (byte) rgb888[2]);
                pixelBuffer.put(counter * 4 + 3, (byte) alpha);
            } catch (IndexOutOfBoundsException e) {
                Gdx.app.error("Image", "During writing to pixel buffer something went wrong at offset " + (counter * 4) + "-" + (counter * 4 + 3));
                break;
            }

            counter++;
        }

        pixelBuffer.rewind();
        return new Texture(pixmap);
    }

    public static Texture applyAlphaMask(Texture base, Pixmap mask, int offsetX, int offsetY) {
        TextureData textureData = base.getTextureData();
        if (!textureData.isPrepared()) textureData.prepare();
        Pixmap basePixmap = textureData.consumePixmap();

        for (int x = 0; x < mask.getWidth(); x++) {
            for (int y = 0; y < mask.getHeight(); y++) {
                int alpha = new Color(mask.getPixel(x, y)).a > 0.1f ? 0 : 255;
                int bx = offsetX + x;
                int by = offsetY + (mask.getHeight() - 1 - y);
                if (bx < 0 || by < 0 || bx >= basePixmap.getWidth() || by >= basePixmap.getHeight()) continue;

                Color c = new Color(basePixmap.getPixel(bx, by));
                basePixmap.drawPixel(bx, by, Color.rgba8888(c.r, c.g, c.b, alpha / 255f));
            }
        }

        return new Texture(basePixmap);
    }

    public Texture getImageTexture() {
        return imageTexture;
    }

    public Texture getOriginalImageTexture() {
        return originalImageTexture;
    }

    public void setImageTexture(Texture imageTexture) {
        this.imageTexture = imageTexture;
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}