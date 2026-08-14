package pl.genschu.bloomooemulator.loader.helpers;
import java.io.IOException;
public interface SeekableBinaryReader extends BinaryReader {

    long position() throws IOException;

    void seek(long position) throws IOException;
}
