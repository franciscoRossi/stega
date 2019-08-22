package gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Message;
import utils.AES;
import utils.Steganography;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class Main extends JFrame {
    private JPanel contentPane;
    private JTabbedPane tabbedPane1;
    private JTextField textFieldSourceImage;
    private JButton browseImageButton;
    private JPasswordField passwordFieldSecret;
    private JTextArea textAreaMessage;
    private JButton encodeButton;
    private JButton decodeButton;
    private JButton showButton;

    private String selectedFile;

    public Main() {
        setContentPane(contentPane);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        browseImageButton.addActionListener(e -> {
            String file = browseFile();
            if (file != null) {
                this.selectedFile = file;
                textFieldSourceImage.setText(file);
            }
        });

        showButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                passwordFieldSecret.setEchoChar((char) 0);
            }
        });
        showButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                passwordFieldSecret.setEchoChar('*');
            }
        });

        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    encodeIntoImage(new Message(textAreaMessage.getText()), new String(passwordFieldSecret.getPassword()),
                            selectedFile);
                    try {
                        Desktop.getDesktop().open(new File(selectedFile + "_out.png"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        decodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    textAreaMessage.setText(decodeImage(new String(passwordFieldSecret.getPassword()), selectedFile));
                }
            }
        });
    }

    private String decodeImage(String secretKey, String inputImageName) {
        Steganography stega = new Steganography();

        try {
            String encryptedMessage = stega.decode(Utils.readImage(inputImageName));
            AES aes = new AES();
            String decryptedMessage = aes.decrypt(encryptedMessage, secretKey);
            Message message = new ObjectMapper().readValue(decryptedMessage, Message.class);

            JOptionPane.showMessageDialog(this, "Message successfully extracted and decrypted from the image");

            //check CRC
            Long crc = message.getCRC32();
            if (!message.compareCRC32(crc)) {
                JOptionPane.showMessageDialog(this, "The message integrity verification failed. Message may be corrupted.", "Warning!",
                        JOptionPane.WARNING_MESSAGE);
            }
            return message.getContent();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error on message decryption", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void encodeIntoImage(Message textInput, String secretKey, String inputImageName) {

        AES aes = new AES();
        String encodedMessage = aes.encrypt(textInput.toString(), secretKey);

        Steganography stega = new Steganography();
        try {
            stega.encode(encodedMessage, Utils.readImage(inputImageName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String browseFile() {
        FileDialog fd = new FileDialog(this, "Choose an image file", FileDialog.LOAD);
        fd.setFilenameFilter((File dir, String name) -> name.endsWith(".jpg") | name.endsWith(".png") | name.endsWith(".jpeg"));
        fd.setVisible(true);
        return fd.getFile() == null ? null : fd.getDirectory() + fd.getFile();
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Main frame = new Main();
        frame.setTitle("Stega");
        frame.pack();
        frame.setSize(1280, 720);
        frame.setVisible(true);
    }


}
