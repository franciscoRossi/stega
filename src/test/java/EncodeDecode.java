import model.Message;
import org.junit.Test;
import utils.AES;
import utils.Steganography;
import utils.Utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
        for (int i = 0; i<5000; i++) {
            message.append(UUID.randomUUID().toString().replace("-", ""));
        }
        Message textInput = new Message(message.toString());
        String passphrase = "supersecretpasssword!";

        //encrypt the string
        String encryptedText = aes.encrypt(textInput.toString(), passphrase);

        //steganography encode
        String resourcePath = this.getClass().getResource("").getFile();
        stega.encode( encryptedText, Utils.readImage(resourcePath + "obelisco.jpg"));

        //steganography decode
        String decodedImageContent = stega.decode(Utils.readImage(resourcePath + "obelisco.jpg_out.png"));

        //decrypt
        String decryptedText = aes.decrypt(decodedImageContent, passphrase);

        System.out.println(decryptedText);
        assert decryptedText.equals(textInput.toString());
    }
}
