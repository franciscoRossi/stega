package gui;

import model.Message;
import utils.AES;
import utils.Steganography;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class Main extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabbedPane1;
    private JTextField textFieldSourceImage;
    private JButton browseImageButton;
    private JPasswordField passwordFieldSecret;
    private JTextArea textAreaMessage;
    private JButton encodeButton;
    private JButton decodeButton;
    private JButton showButton;

    private String[] selectedFile;

    public Main() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        browseImageButton.addActionListener(e -> {
            String[] file = browseFile();
            String fileDir = file[0];
            String fileName = file[1];

            if (fileName != null) {
                this.selectedFile = file;
                textFieldSourceImage.setText(fileDir + fileName );
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
                            selectedFile[0].substring(0, selectedFile[0].length() - 1), selectedFile[1]);
                    try {
                        Desktop.getDesktop().open(new File(selectedFile[0] + selectedFile[1]));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void encodeIntoImage(Message textInput, String secretKey, String imageFolder, String inputImageName) {

        AES aes = new AES();
        aes.setKey(secretKey);
        String encodedMessage = aes.encrypt(textInput.toString(), secretKey);

        Steganography stega = new Steganography();
        stega.encode(imageFolder, inputImageName.split("\\.")[0], inputImageName.split("\\.")[1], inputImageName + "_out",
                encodedMessage);
    }

    private String[] browseFile() {
        FileDialog fd = new FileDialog(this, "Choose an image file", FileDialog.LOAD);
        fd.setFilenameFilter((File dir, String name) -> name.endsWith(".jpg") | name.endsWith(".png") | name.endsWith(".jpeg"));
        fd.setVisible(true);
        String[] ret = new String[2];
        ret[0] = fd.getDirectory();
        ret[1] = fd.getFile();
        return ret;
    }

    public static void main(String[] args) {
        Main dialog = new Main();

        dialog.pack();
        dialog.setSize(1280, 720);
        dialog.setVisible(true);
    }
}
