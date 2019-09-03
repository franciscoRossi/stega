package utils;

import model.InputImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class Utils {

    public static InputImage readImage(String imagePath) throws IOException {
        BufferedImage bImage = ImageIO.read(new FileInputStream(imagePath));
        WritableRaster raster = bImage.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();

        return new InputImage(imagePath, buffer.getData(), bImage.getWidth(), bImage.getHeight());
    }

    public static void saveImage(byte[] imageData, String imagePath, int imageWidth, int imageHeight) throws IOException {

        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
        img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(imageData, imageData.length), new Point() ) );
        ImageIO.write(img, "png", new FileOutputStream(imagePath));
    }

    public static byte getMeanValueOfSHAStringBytes(String key) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] shaBytes = digest != null ? digest.digest(key.getBytes(StandardCharsets.UTF_8)) : new byte[0];
        int acc = 0;
        for (int i = 0; i < shaBytes.length; i++) {
                acc = acc + shaBytes[i];
        }
        return (byte) (acc / shaBytes.length);
    }
}
