package utils;
/*
 *@author  William_Wilson
 *@version 1.6
 *Created: May 8, 2007
 */

import model.InputImage;

import java.io.File;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Steganography {

    public void encode(String textToEncode, InputImage inputImage) throws IOException {
        byte[] inputImageMap = inputImage.getData();
        byte[] textBytes = textToEncode.getBytes(StandardCharsets.UTF_8);

        int imagePointer = 0;
        for (int i = 0; i < textBytes.length; i++) {
            final int rLow = textBytes[i] >> 4;
            final int rHigh = inputImageMap[imagePointer] & 0xF0;
            inputImageMap[imagePointer] = (byte) (rLow + rHigh);

            final int gLow = textBytes[i] & 0x0F;
            final int gHigh = inputImageMap[imagePointer + 1] & 0xF0;
            inputImageMap[imagePointer + 1] = (byte) (gLow + gHigh);

            //int bLow = (i == textBytes.length - 1) ? 0b00000001 : 0b00000000;
            final int lastByte = inputImageMap[imagePointer + 2];
            if (i == textBytes.length -1) {
                inputImageMap[imagePointer + 2] = (byte) ( (lastByte << 1) | 0b00000001);
            } else {
                inputImageMap[imagePointer + 2] = (byte) ( lastByte << 1);
            }

            if (inputImageMap[imagePointer + 1] % 2 == 0) {
                imagePointer = imagePointer + (3 * 16);
            } else {
                imagePointer = imagePointer + (3);
            }

        }

        Utils.saveImage(inputImageMap, inputImage.getPath() + "_out.png", inputImage.getWidth(),
                inputImage.getHeight());
    }

    public String decode(InputImage inputImage) throws IOException {
        byte[] inputImageMap = inputImage.getData();

        StringBuilder output = new StringBuilder();
        int imagePointer = 0;
        for (int i = 0; i < inputImageMap.length; i++) {
            byte low = (byte) (inputImageMap[imagePointer] << 4);
            byte high = (byte) (inputImageMap[imagePointer + 1] & 0x0F);
            byte[] c = new byte[1];
            c[0] = (byte) (low + high);
            output.append(new String(c, StandardCharsets.UTF_8));
            byte shouldStop = (byte) (inputImageMap[imagePointer + 2] & 0b00000001);
            if (shouldStop == 1) {
                break;
            }
            if (inputImageMap[imagePointer + 1] % 2 == 0) {
                imagePointer = imagePointer + (3 * 16);
            } else {
                imagePointer = imagePointer + (3);
            }
        }
        return output.toString();
    }
}

