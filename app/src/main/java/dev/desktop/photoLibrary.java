package dev.desktop;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
class Photo {
    String path;
    int rating;
    Photo(String path, int rating) {
        this.path = path;
        this.rating = rating;
    }
}
class core extends JFrame{
    JFrame frame;
    public core(String windowName) {
        this.frame = new JFrame(windowName);
        this.frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
public class photoLibrary {
    public ArrayList<Photo> photos;
    ArrayList<Photo> displayPhotos = new ArrayList<>();
    String catalogPath = "";
    int filterRating = -1;

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
        ImageIcon image = new ImageIcon(photoLibrary.class.getResource("/splashScreen3.png"));
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
                home.setSize(700, 500);
        JButton newCatalog = new JButton("New Catalog");
        JButton openCatalog = new JButton("Open Catalog");
        home.setLocationRelativeTo(null);
        newCatalog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newCatalog(home);
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
        JToolBar open = new JToolBar();
        open.add(newCatalog);
        open.add(openCatalog);
        home.add(open, BorderLayout.NORTH);
        home.pack();
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
            photos.add(new Photo(s.nextLine(), Integer.parseInt(s.nextLine().trim())));
        }
    }
    protected boolean validate(String input, JFrame parent) {
        if (input == null) {
            JOptionPane.showMessageDialog(parent,"No Input! No changes were made.","Alert!",JOptionPane.ERROR_MESSAGE);
        }
        try {
            int value = Integer.parseInt(input.trim());
            if (value < 0 || value > 5) {
                JOptionPane.showMessageDialog(parent,"Invalid Input!","Alert!",JOptionPane.ERROR_MESSAGE);
            }
            else {
                return true;
            }
        } catch (NumberFormatException g) {
            JOptionPane.showMessageDialog(parent,"Invalid Input Format!","Alert!",JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    protected void render() {
        SwingUtilities.invokeLater(() -> {
            displayPhotos = new ArrayList<>();
            JFrame library = new JFrame("Lens v2: " + catalogPath);
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
                    f.setMultiSelectionEnabled(true);
                    f.setDialogTitle("Select an Image to open.");
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files (*.png, *.jpeg, *.jpg)",
                            "png", "jpeg", "jpg");
                    f.setFileFilter(filter);
                    int result = f.showOpenDialog(library);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File[] selectedFile = f.getSelectedFiles();
                        for (File m:selectedFile) {
                            String path = m.getAbsolutePath();
                            photos.add(new Photo(path, -1));
                        }
                        render();
                        rewrite();
                    }
                }
            });
            JButton filter = new JButton("Filter Photos");
            filter.addActionListener(new ActionListener() {
                @Override 
                public void actionPerformed(ActionEvent e) {
                    String input = JOptionPane.showInputDialog(library,"Enter rating to filter by: ","Filter",JOptionPane.QUESTION_MESSAGE);
                    if (validate(input, library)) {
                        filterRating = Integer.parseInt(input);
                        displayPhotos = new ArrayList<>();
                        render();
                    }
                }
            });
            JButton clearFilter = new JButton("Clear Filter");
            clearFilter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filterRating = -1;
                    displayPhotos = new ArrayList<>();
                    render();
                }
            });
            JButton quitButton = new JButton("Quit");
            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int choice = JOptionPane.showConfirmDialog(library,"Are you sure you want to quit Lens?", "Alert!",
                    JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    } 
                }
            });
            c.add(newc);
            c.add(newb);
            c.add(addPhoto);
            c.add(filter);
            c.add(clearFilter);
            c.add(quitButton);
            for (Photo l:photos) {
                if (filterRating == -1) {
                    displayPhotos.add(l);
                } else if (l.rating == filterRating) {
                    displayPhotos.add(l);
                } 
            }
            library.add(c, BorderLayout.NORTH);
            for (Photo p : displayPhotos) {
                ImageIcon icon = new ImageIcon(p.path);
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
                        JButton rateButton = new JButton("Modify Rating (Current: " + p.rating+")");
                        viewImageToolbar.add(rateButton);
                        JFrame viewImage = new JFrame("Lens: " + p.path);
                        deleteButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int result = JOptionPane.showConfirmDialog(null, "Remove Image?", "Alert!",
                                        JOptionPane.YES_NO_OPTION);
                                if (result == 0) {
                                    photos.remove(p);
                                    render();
                                    rewrite();
                                    viewImage.dispose();
                                }
                            }
                        });
                        rateButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String input = JOptionPane.showInputDialog(viewImage,"Enter a new Rating!","Modify Rating",JOptionPane.QUESTION_MESSAGE);
                                if (validate(input, library)) {
                                    p.rating = Integer.parseInt(input.trim());
                                    rateButton.setText("Modify Rating (Current: " + p.rating+")");
                                    rewrite();
                                    }
                                }
                            });
                        ImageIcon fullSizeImage = new ImageIcon(p.path);
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
            // work on filtering in the main library based on rating
        });
    }
    private void rewrite() {
        // rewrite the catalog to reflect changes
        try {
            FileWriter p = new FileWriter(catalogPath, false);
            for (Photo g : photos) {
                p.write(g.path + "\n");
                p.write(Integer.toString(g.rating));
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
                // reset for new catalog
                photos = new ArrayList<>();
                rewrite();
                render();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public ArrayList<Photo> getPhotos() {
        return photos;
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