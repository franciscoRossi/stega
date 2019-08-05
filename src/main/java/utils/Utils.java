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
}
