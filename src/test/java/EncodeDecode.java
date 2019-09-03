import model.Message;
import org.junit.Test;
import utils.AES;
import utils.GZip;
import utils.Steganography;
import utils.Utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by pupi on 16/07/19.
 */
public class EncodeDecode {

    @Test
    public void testEncodeAndDecode() throws GeneralSecurityException, IOException {
            Steganography stega = new Steganography();
            AES aes = new AES();

            StringBuilder message = new StringBuilder();
            for (int i = 0; i<1024; i++) {
                message.append(UUID.randomUUID().toString().replace("-", ""));
            }
            Message textInput = new Message(message.toString());
            System.out.println(String.format("Raw text length: %d", textInput.getContent().length()));

            String passphrase = "supersecretpassword!";

            //encrypt the string
            String encryptedText = aes.encrypt(textInput.toString(), passphrase);
            System.out.println(String.format("Encrypted text length: %d", encryptedText.length()));

            //steganography encode
            String resourcePath = this.getClass().getResource("").getFile();
            Long t0encode = System.currentTimeMillis();
            stega.encode( encryptedText, Utils.readImage(resourcePath + "obelisco.jpg"), Utils.getMeanValueOfSHAStringBytes(passphrase), true);
            Long t1encode = System.currentTimeMillis();

            //steganography decode
            Long t0decode = System.currentTimeMillis();
            String decodedImageContent = stega.decode(Utils.readImage(resourcePath + "obelisco.jpg_out.png"), Utils.getMeanValueOfSHAStringBytes(passphrase),true);
            Long t1decode = System.currentTimeMillis();

            //decrypt
            String decryptedText = aes.decrypt(decodedImageContent, passphrase);

            System.out.println(decryptedText);
            System.out.println(String.format("Time taken to encode: %d millis.", t1encode - t0encode));
            System.out.println(String.format("Time taken to decode: %d millis.", t1decode - t0decode));
            System.out.println(String.format("Input String length was: %d characters.", textInput.getContent().length()));
            assert decryptedText.equals(textInput.toString());

    }

    @Test
    public void testGZIP() throws Exception {
            String toComp = "This text is gonna be compressed";
            byte[] comp = GZip.compress(toComp);
            String decomp = GZip.decompress(comp);

            assert (toComp.equals(decomp));
    }
}
