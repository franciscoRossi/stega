package utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

/**
 * Created by pupi on 3/9/2019.
 */
class CustomGZIPOutputStream
        extends GZIPOutputStream {

    public CustomGZIPOutputStream( OutputStream out ) throws IOException {
        super( out );
        def.setLevel(Deflater.BEST_COMPRESSION);
    }
}
