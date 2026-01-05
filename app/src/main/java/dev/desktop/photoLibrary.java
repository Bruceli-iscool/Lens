package dev.desktop;
import org.checkerframework.checker.units.qual.A;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
public class photoLibrary {
    ArrayList<String> photos = new ArrayList<>();
    protected photoLibrary(boolean firstInCurrentSession) throws InterruptedException, IOException {
        if (firstInCurrentSession) {
            splashScreen();
            home();
        }
    }
    protected void splashScreen() throws InterruptedException {
        // splashScreen
        JFrame splash = new JFrame("Lens");
        splash.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ImageIcon image = new ImageIcon(photoLibrary.class.getResource("/splashScreen.png"));
        splash.add(new JLabel(image));
        splash.pack();
        splash.setResizable(false);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        Thread.sleep(2000);
        splash.dispose();
    }protected void home() throws IOException {
        // asks to open or create a catalog.
        JFrame home = new JFrame("Lens Home");
        JButton newCatalog = new JButton("New Catalog");
        JButton openCatalog = new JButton("Open Catalog");
        newCatalog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        openCatalog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openCatalog(home);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        home.add(openCatalog);
        home.setVisible(true);
    } protected void openCatalog(JFrame parent) throws FileNotFoundException {
        // open a catalog
        JFileChooser f = new JFileChooser();
        f.setDialogTitle("Select a Lens Catalog to open.");
        FileNameExtensionFilter filter =
                new FileNameExtensionFilter("Lens Catalog Files (*.lcatalog)", "lcatalog");
        f.setFileFilter(filter);
        int result = f.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = f.getSelectedFile();
            read(selectedFile);
        }
    } protected void read(File file) throws FileNotFoundException {
        Scanner s = new Scanner(file);
        while(s.hasNextLine()) {
            photos.add(s.nextLine());
        }
        // todo
    }
}
