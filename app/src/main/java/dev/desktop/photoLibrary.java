package dev.desktop;
import org.checkerframework.checker.units.qual.A;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
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
        home.setSize(700, 500);
        home.setLocationRelativeTo(null);
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
            render();
        }
    } protected void read(File file) throws FileNotFoundException {
        Scanner s = new Scanner(file);
        while(s.hasNextLine()) {
            photos.add(s.nextLine());
        }
    } protected void render() {
        SwingUtilities.invokeLater(() -> {
            JFrame library = new JFrame("Lens v1");
            library.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            library.setSize(1600, 900);
            library.setLocationRelativeTo(null);
            JPanel panel = new JPanel(new GridLayout(0, 4, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            for (String path : photos) {
                ImageIcon icon = new ImageIcon(path);
                Image scaled = icon.getImage()
                        .getScaledInstance(300, -1, Image.SCALE_SMOOTH);
                JButton button = new JButton(new ImageIcon(scaled));
                button.setBorder(null);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setOpaque(false);
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setHorizontalAlignment(JLabel.CENTER);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // view the full size image
                        JFrame viewImage = new JFrame("Lens: "+path);
                        ImageIcon fullSizeImage = new ImageIcon(path);
                        viewImage.add(new JLabel(fullSizeImage));
                        viewImage.pack();
                        viewImage.setResizable(false);
                        viewImage.setLocationRelativeTo(null);
                        viewImage.setVisible(true);
                    }
                });
                panel.add(button);
            }
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.add(panel, BorderLayout.NORTH);
            JScrollPane scrollPane = new JScrollPane(wrapper);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            library.add(scrollPane);
            library.setVisible(true);
        });
    }

}
