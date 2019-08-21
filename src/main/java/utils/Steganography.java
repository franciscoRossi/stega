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
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Steganography {

    public void encode(String textToEncode, InputImage inputImage) throws IOException {
        textToEncode = new StringBuilder(textToEncode).reverse().toString();
        byte[] inputImageMap = inputImage.getData();
        byte[] textBytes = textToEncode.getBytes(StandardCharsets.UTF_8);

        int imagePointer = 0;
        for (int i = 0; i < textBytes.length; i++) {

            final byte b0L = (byte) (textBytes[i] >> 4);
            final byte b0H = (byte) (inputImageMap[imagePointer] & 0b11110000);
            inputImageMap[imagePointer] = (byte) (b0L + b0H);

            final byte b1L = (byte) (textBytes[i] & 0b00001111);
            final byte b1H = (byte) (inputImageMap[imagePointer + 1] & 0b11110000);
            inputImageMap[imagePointer + 1] = (byte) (b1L + b1H);

            final byte lastByte = inputImageMap[imagePointer + 2];
            if (i == textBytes.length - 1) {
                inputImageMap[imagePointer + 2] = (byte) (lastByte | 0b00000001);
            } else {
                inputImageMap[imagePointer + 2] = (byte) (lastByte & 0b11111110);
            }

            imagePointer = imagePointer + decideJumpLength(inputImageMap[imagePointer],
                    inputImageMap[imagePointer + 1]);


        }

        Utils.saveImage(inputImageMap, inputImage.getPath() + "_out.png", inputImage.getWidth(),
                inputImage.getHeight());
    }

    public String decode(InputImage inputImage) throws IOException {
        byte[] inputImageMap = inputImage.getData();

        StringBuilder output = new StringBuilder();
        int imagePointer = 0;
        for (int i = 0; i < inputImageMap.length; i++) {
            byte high = (byte) ((inputImageMap[imagePointer] & 0x0F) << 4);
            byte low = (byte) (inputImageMap[imagePointer + 1] & 0x0F);
            byte[] c = new byte[1];
            c[0] = (byte) (high + low);
            output.append(new String(c, StandardCharsets.UTF_8));

            byte shouldStop = (byte) (inputImageMap[imagePointer + 2] & 0b00000001);
            if (shouldStop == 0b00000001) {
                break;
            }

            int jumpLength = decideJumpLength(inputImageMap[imagePointer],
                    inputImageMap[imagePointer + 1]);
            imagePointer = imagePointer + jumpLength;

        }
        return new StringBuilder(output).reverse().toString();
    }

    private int decideJumpLength(byte b0, byte b1) {
        boolean isFirstByteEven = b0 % 2 == 0;
        boolean isSecondByteEven = b1 % 2 == 0;

        if (isFirstByteEven && isSecondByteEven) {
            return 3 * 4;
        } else if (isFirstByteEven) {
            return 3;
        } else if (isSecondByteEven) {
            return 3 * 2;
        } else {
            return 3 * 3;
        }
    }
}

