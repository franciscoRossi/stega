package utils;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;
import model.InputImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Steganography {

    private HashSet<Integer> usedPixels = new HashSet<>();
    private final static byte imageMask = (byte) 0b11111100;
    private byte initialSeed = 0;
    private int collisionCount = 0;

    public void encode(String textToEncode, InputImage inputImage, byte initialSeed, boolean gzip) throws IOException {
        this.initialSeed = initialSeed;
        collisionCount = 0;
        usedPixels = new HashSet<>();
        byte[] inputImageMap = inputImage.getData();

        byte[] textBytes;
        if (gzip) {
            textBytes = GZip.compress(textToEncode);
            System.out.println(String.format("Encrypted and compressed text length: %d", textBytes.length));
        } else {
            textBytes = textToEncode.getBytes(StandardCharsets.UTF_8);
        }

        int imagePointer = getNextPixel(inputImageMap.length, initialSeed, inputImageMap);
        final int textBytesLength = textBytes.length;

        byte[] splittedChar = new byte[4];

        for (int i = 0; i < textBytes.length; i++) {
            splittedChar[0] = (byte) ( textBytes[i] & 0b00000011);
            splittedChar[1] = (byte) ((textBytes[i] & 0b00001100) >> 2);
            splittedChar[2] = (byte) ((textBytes[i] & 0b00110000) >> 4);
            splittedChar[3] = (byte) ((textBytes[i] & 0b11000000) >> 6);

            for(int j=0; j<4; j++) {
                final byte b0H = (byte) (inputImageMap[imagePointer + j] & imageMask);
                inputImageMap[imagePointer + j] = (byte) (splittedChar[j] + b0H);
                //inputImageMap[imagePointer + j] = (byte) 0;
            }

            if (i == textBytesLength - 1) {
                inputImageMap[imagePointer + 4] = (byte) (inputImageMap[imagePointer + 4] | 1);
            } else {
                inputImageMap[imagePointer + 4] = (byte) (inputImageMap[imagePointer + 4] & 254);
            }

            imagePointer = getNextPixel(imagePointer, inputImageMap[imagePointer], inputImageMap);
        }
        System.out.println(String.format("Amount of collisions: %d", collisionCount));

        Utils.saveImage(inputImageMap, inputImage.getPath() + "_out.png", inputImage.getWidth(),
                inputImage.getHeight());
    }

    public String decode(InputImage inputImage, byte initialSeed, boolean gzip) throws IOException {
        this.initialSeed = initialSeed;
        collisionCount = 0;
        usedPixels = new HashSet<>();
        byte[] inputImageMap = inputImage.getData();

        StringBuilder output = new StringBuilder();
        List<Byte> decodedTextBytes = new ArrayList<>();
        int imagePointer = getNextPixel(inputImageMap.length, initialSeed, inputImageMap);
        for (int i = 0; i < inputImageMap.length; i++) {


            byte[] acc = new byte[1];
            for (int j=0; j<4; j++) {
                acc[0] += (byte) ((inputImageMap[imagePointer + j] & ~imageMask) << (j * 2));
            }
            decodedTextBytes.add(acc[0]);

            boolean shouldStop = (byte) (inputImageMap[imagePointer + 4] & 1) == 1;
            if (shouldStop) {
                break;
            }

            imagePointer = getNextPixel(imagePointer, inputImageMap[imagePointer], inputImageMap);
        }

        System.out.println(String.format("Amount of collisions: %d", collisionCount));

        byte[] b = Bytes.toArray(decodedTextBytes);
        if (gzip) {
            return GZip.decompress(b);
        } else {
            return new String(b, Charsets.UTF_8);
        }
    }

    private int getNextPixel(int currentPos, byte currentPixelValue, byte[] imageMap) {
        currentPixelValue = (byte) (currentPixelValue >> 2);
        SecureRandom rand = new SecureRandom(generateNewRandomSeed(currentPos, currentPixelValue, imageMap, 0));
        int pixel = rand.nextInt(imageMap.length - 5);

        int i = collisionCount;
        while (usedPixels.contains(pixel) || usedPixels.contains(pixel + 1) || usedPixels.contains(pixel + 2) ||
                usedPixels.contains(pixel + 3) || usedPixels.contains(pixel + 4)) {

            rand = new SecureRandom(generateNewRandomSeed(currentPos, currentPixelValue, imageMap, i));
            pixel = rand.nextInt(imageMap.length - 5);
            i++;
        }

        usedPixels.add(pixel); usedPixels.add(pixel + 1); usedPixels.add(pixel + 2); usedPixels.add(pixel + 3); usedPixels.add(pixel + 4);
        collisionCount = i;
        return pixel;
    }

    private byte[] generateNewRandomSeed(int currentPos, byte currentPixelValue, byte[] imageMap, int incVal) {
        byte[] randomSeed = ByteBuffer.allocate(24).putInt(usedPixels.size()).array();
        byte[] currentPosBytes = ByteBuffer.allocate(4).putInt(currentPos).array();
        //----------
        randomSeed[5] = currentPixelValue;
        byte[] iArray = ByteBuffer.allocate(4).putInt(collisionCount + incVal).array();
        randomSeed[6] = iArray[0];
        randomSeed[7] = iArray[1];
        randomSeed[8] = iArray[2];
        randomSeed[9] = iArray[3];
        randomSeed[10] = this.initialSeed;
        randomSeed[11] = (byte) ~this.initialSeed;
        randomSeed[12] = currentPosBytes[3];
        randomSeed[13] = currentPosBytes[2];
        randomSeed[14] = currentPosBytes[1];
        randomSeed[15] = currentPosBytes[0];


        return randomSeed;
    }
}