package dev.desktop;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.BorderUIResource;

import java.awt.*;
import java.io.File;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class photoLibrary {
    ArrayList<String> photos;
    String catalogPath = "";

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
        ImageIcon image = new ImageIcon(photoLibrary.class.getResource("/splashScreen1.png"));
        splash.add(new JLabel(image));
        splash.pack();
        splash.setResizable(false);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        Thread.sleep(2000);
        splash.dispose();
    }

    protected void home() throws IOException {
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
    }

    protected void openCatalog(JFrame parent) throws FileNotFoundException {
        // open a catalog
        JFileChooser f = new JFileChooser();
        f.setDialogTitle("Select a Lens Catalog to open.");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Lens Catalog Files (*.lcatalog)", "lcatalog");
        f.setFileFilter(filter);
        int result = f.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = f.getSelectedFile();
            catalogPath = selectedFile.getPath();
            photos = new ArrayList<>();
            read(selectedFile);
            render();
        }
    }

    protected void read(File file) throws FileNotFoundException {
        Scanner s = new Scanner(file);
        while (s.hasNextLine()) {
            photos.add(s.nextLine());
        }
    }

    protected void render() {
        SwingUtilities.invokeLater(() -> {
            JFrame library = new JFrame("Lens v1.1: " + catalogPath);
            library.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            library.setSize(1600, 900);
            library.setLocationRelativeTo(null);
            JPanel panel = new JPanel(new GridLayout(0, 4, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            // toolbar to add photos
            JToolBar c = new JToolBar();
            JButton newc = new JButton("New Catalog");
            JButton newb = new JButton("Open Catalog");
            newc.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    newCatalog(library);
                }
            });
            // open catalog
            newb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        openCatalog(library);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            JButton addPhoto = new JButton("Add Photo");
            addPhoto.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser f = new JFileChooser();
                    f.setDialogTitle("Select an Image to open.");
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files (*.png, *.jpeg, *.jpg)",
                            "png", "jpeg", "jpg");
                    f.setFileFilter(filter);
                    int result = f.showOpenDialog(library);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = f.getSelectedFile();
                        String s = selectedFile.getPath();
                        photos.add(s);
                        render();
                    }
                    rewrite();
                }
            });
            c.add(newc);
            c.add(newb);
            c.add(addPhoto);
            library.add(c, BorderLayout.NORTH);
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
                        // ImageIcon deleteIcon = new ImageIcon(photoLibrary.class.getResource(""));
                        JToolBar viewImageToolbar = new JToolBar();
                        JButton deleteButton = new JButton("Remove");
                        viewImageToolbar.add(deleteButton);
                        JFrame viewImage = new JFrame("Lens: " + path);
                        deleteButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int result = JOptionPane.showConfirmDialog(null, "Remove Image?", "Alert!",
                                        JOptionPane.YES_NO_OPTION);
                                if (result == 0) {
                                    photos.remove(path);
                                    render();
                                    rewrite();
                                    viewImage.dispose();
                                }
                            }
                        });
                        ImageIcon fullSizeImage = new ImageIcon(path);
                        ImagePanel im = new ImagePanel(fullSizeImage.getImage());
                        viewImage.add(im);
                        viewImage.setSize(1200, 800);
                        viewImage.add(viewImageToolbar, BorderLayout.NORTH);
                        viewImage.setResizable(true);
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
            // todo add star ratings 
        });
    }
    private void rewrite() {
        // rewrite the catalog to reflect changes
        try {
            FileWriter p = new FileWriter(catalogPath, false);
            for (String path : photos) {
                p.write(path + "\n");
            }
            p.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void newCatalog(JFrame parent) {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Lens Catalog (*.lcatalog)","lcatalog");
        JFileChooser f = new JFileChooser();
        f.addChoosableFileFilter(filter);
        if (f.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = f.getSelectedFile();
            try {
                file.createNewFile();
                catalogPath = file.getAbsolutePath();
                rewrite();
                // reset for new catalog
                photos = new ArrayList<>();
                render();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ImagePanel extends JPanel {
    // dynamically rescale image
    private final Image image;

    public ImagePanel(Image image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = image.getWidth(this);
        int h = image.getHeight(this);
        int panelW = getWidth();
        int panelH = getHeight();
        double scale = Math.min((double) panelW / w, (double) panelH / h);
        int drawW = (int) (w * scale);
        int drawH = (int) (h * scale);
        int x = (panelW - drawW) / 2;
        int y = (panelH - drawH) / 2;
        g.drawImage(image, x, y, drawW, drawH, this);
    }
}