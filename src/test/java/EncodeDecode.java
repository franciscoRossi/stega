import model.Message;
import org.junit.Test;
import utils.AES;
import utils.Steganography;

/**
 * Created by pupi on 16/07/19.
 */
public class EncodeDecode {

    @Test
    public void testEncodeAndDecode() {
//        Steganography stega = new Steganography();
//        AES aes = new AES();
//
//        Message textInput = new Message("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
//        String passphrase = "supersecretpasssword!";
//
//        //encrypt the string
//        String encryptedText = aes.encrypt(textInput.toString(), passphrase);
//
//        //steganography encode
//        String resourcePath = this.getClass().getResource("").getFile();
//        stega.encode(resourcePath, "bliss", "jpg", "out", encryptedText);
//
//        //steganography decode
//        String decodedImageContent = stega.decode(resourcePath, "out");
//
//        //decrypt
//        String decryptedText = aes.decrypt(decodedImageContent, passphrase);
//
//        System.out.println(decryptedText);
//        assert decryptedText.equals(textInput.toString());
    }
}
