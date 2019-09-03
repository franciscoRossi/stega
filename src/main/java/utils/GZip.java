package utils;

import java.io.*;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

/**
 * Created by pupi on 3/9/2019.
 */
public class GZip {
    public static byte[] compress(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream obj=new ByteArrayOutputStream();
        CustomGZIPOutputStream gzip;
        try {
            gzip = new CustomGZIPOutputStream(obj);
            gzip.write(str.getBytes());
            gzip.close();
            return obj.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[1];
    }

    public static String decompress(byte[] str) {
        if (str == null ) {
            return null;
        }

        GZIPInputStream gis;
        try {
            gis = new GZIPInputStream(new ByteArrayInputStream(str));
            BufferedReader bf = new BufferedReader(new InputStreamReader(gis));
            StringBuilder outStr = new StringBuilder();
            String line;
            while ((line=bf.readLine())!=null) {
                outStr.append(line);
            }
            return outStr.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
