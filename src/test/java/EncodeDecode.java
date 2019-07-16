import org.junit.Test;

/**
 * Created by pupi on 16/07/19.
 */
public class EncodeDecode {

    @Test
    public void testEncodeAndDecode() {
        Steganography stega = new Steganography();
        AES aes = new AES();

        String textInput = "This is a secret string for testing";
        String passphrase = "supersecretpassword!";

        //encrypt the string
        String encryptedText = aes.encrypt(textInput, passphrase);

        //steganography encode
        String resourcePath = this.getClass().getResource("").getFile();
        stega.encode(resourcePath, "bliss", "jpg", "out", encryptedText);

        //steganography decode
        String decodedImageContent = stega.decode(resourcePath, "out");

        //decrypt
        String decryptedText = aes.decrypt(decodedImageContent, passphrase);

        assert decryptedText.equals(textInput);
    }
}
