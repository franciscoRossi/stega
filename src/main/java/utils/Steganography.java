package utils;

import model.InputImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Steganography {

    private final static byte imageMask = (byte) 0b11111100;

    public void encode(String textToEncode, InputImage inputImage) throws IOException {
        textToEncode = new StringBuilder(textToEncode).reverse().toString();
        byte[] inputImageMap = inputImage.getData();
        byte[] textBytes = textToEncode.getBytes(StandardCharsets.UTF_8);

        int imagePointer = 0;
        for (int i = 0; i < textBytes.length; i++) {
            byte[] splittedChar = new byte[4];
            splittedChar[0] = (byte) ( textBytes[i] & 0b00000011);
            splittedChar[1] = (byte) ((textBytes[i] & 0b00001100) >> 2);
            splittedChar[2] = (byte) ((textBytes[i] & 0b00110000) >> 4);
            splittedChar[3] = (byte) ((textBytes[i] & 0b11000000) >> 6);

            for(int j=0; j<4; j++) {
                final byte b0H = (byte) (inputImageMap[imagePointer + j] & imageMask);
                inputImageMap[imagePointer + j] = (byte) (splittedChar[j] + b0H);
            }

            final byte lastByte = inputImageMap[imagePointer + 4];
            if (i == textBytes.length - 1) {
                inputImageMap[imagePointer + 4] = (byte) (lastByte | 1);
            } else {
                inputImageMap[imagePointer + 4] = (byte) (lastByte & 254);
            }

            imagePointer = imagePointer + 5;
        }

        Utils.saveImage(inputImageMap, inputImage.getPath() + "_out.png", inputImage.getWidth(),
                inputImage.getHeight());
    }

    public String decode(InputImage inputImage) throws IOException {
        byte[] inputImageMap = inputImage.getData();

        StringBuilder output = new StringBuilder();
        int imagePointer = 0;
        for (int i = 0; i < inputImageMap.length; i++) {

            byte[] acc = new byte[1];
            for (int j=0; j<4; j++) {
                acc[0] += (byte) ((inputImageMap[imagePointer + j] & ~imageMask) << (j * 2));
            }
            output.append(new String(acc, StandardCharsets.UTF_8));

            boolean shouldStop = (byte) (inputImageMap[imagePointer + 4] & 1) == 1;
            if (shouldStop) {
                break;
            }

            imagePointer = imagePointer + 5;
        }
        return output.reverse().toString();
    }
}