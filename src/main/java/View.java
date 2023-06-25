import com.formdev.flatlaf.util.SystemInfo;
import org.jdesktop.swingx.JXHyperlink;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class View {
    private static JFrame frame;
    private JPanel mainPanel, textPanel;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu menu1, menu2;
    private JMenuItem addItem, helpItem;
    private JLabel typeLabel;
    private static JTable table;
    private JTextField textField;
    public View() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("MGMT") {
            public Dimension getPreferredSize() {
                return new Dimension(600, 500);
            }
        };
        if(SystemInfo.isMacFullWindowContentSupported) {
            frame.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
            frame.getRootPane().putClientProperty( "apple.awt.fullWindowContent", true );
        }
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                Main.saveModel(table);
                System.exit(0);
            }
        });
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        frame.getContentPane().add(mainPanel);
        mainPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        scrollPane = new JScrollPane();
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        textArea = new JTextArea();
        scrollPane.setViewportView(textArea);
        textArea.setEditable(false);
        textArea.setFocusable(false);

        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        menu1 = new JMenu("File");
        menuBar.add(menu1);
        menu2 = new JMenu("Help");
        menuBar.add(menu2);
        addItem = new JMenuItem("New User");
        addItem.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        menu1.add(addItem);
        addItem.addActionListener(e -> {
            addDialog();
        });
        JPanel aboutPanel = createAboutPanel();
        helpItem = new JMenuItem("About");
        menu2.add(helpItem);
        helpItem.addActionListener(e -> {
            JOptionPane.showOptionDialog(View.getFrame(), aboutPanel, "About MGMT", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
        });

        textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(0, 2));
        mainPanel.add(textPanel, BorderLayout.SOUTH);
        textPanel.setBorder(new EmptyBorder(5, 50, 5, 50));

        typeLabel = new JLabel("Type id:");
        textPanel.add(typeLabel, BorderLayout.NORTH);

        table = new JTable();
        Main.createModel(table, false);

        textField = new JTextField();
        textField.addActionListener(e -> {
            long content;
            try {
                content = Long.parseLong(textField.getText());
            } catch (Exception e1) {
                textArea.append("INVALID ID: Please try again.\n");
                return;
            }
            String name = null;
            boolean signedIn = false;
            int index = 0;
            LocalDateTime signInTime = null;
            //Main.createModel(table, true);
            for(int i = 0; i < table.getModel().getRowCount(); i++) {
                if(table.getModel().getValueAt(i, 1).equals(String.valueOf(content))) {
                    name = String.valueOf(table.getModel().getValueAt(i, 2));
                    signedIn = Boolean.parseBoolean(String.valueOf(table.getModel().getValueAt(i, 4)));
                    index = i;
                }
            }
            if(name != null) {
                if (!signedIn) {
                    //System.out.println("1 " + table.getModel().getValueAt(index, 4));
                    textArea.append("Signed in: " + name + "\n");
                    signInTime = LocalDateTime.now();
                    table.getModel().setValueAt(signInTime, index, 5);
                    table.getModel().setValueAt(true, index, 4);
                    //System.out.println("2 " + table.getModel().getValueAt(index, 4));
                } else {
                    long millis;
                    if(table.getModel().getValueAt(index, 5) instanceof String) {
                        millis = Duration.between(LocalDateTime.parse((CharSequence) table.getModel().getValueAt(index, 5)), LocalDateTime.now()).toMillis();
                    }
                    else {
                        millis = Duration.between((Temporal) table.getModel().getValueAt(index, 5), LocalDateTime.now()).toMillis();
                    }
                    String hours = String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis),
                            TimeUnit.MILLISECONDS.toSeconds(millis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                    textArea.append("Signed out: " + name + " — " + hours + "\n");
                    String newHours = "";
                    if(table.getModel().getValueAt(index, 3) != "00:00:00") {
                        String currentHours = (String) table.getModel().getValueAt(index, 3);
                        int numHours = Integer.parseInt(currentHours.substring(0, 2));
                        int numMinutes = Integer.parseInt(currentHours.substring(3, 5));
                        int numSeconds = Integer.parseInt(currentHours.substring(6, 8));
                        numHours += Integer.parseInt(hours.substring(0, 2));
                        numMinutes += Integer.parseInt(hours.substring(3, 5));
                        numSeconds += Integer.parseInt(hours.substring(6, 8));
                        if(numMinutes >= 60) {
                            numHours += numMinutes / 60;
                            numMinutes -= 60 * (numMinutes / 60);
                        }
                        if(numSeconds >= 60) {
                            numMinutes += numSeconds / 60;
                            numSeconds -= 60 * (numSeconds / 60);
                        }
                        newHours = String.format("%02d:%02d:%02d",
                                numHours,
                                numMinutes,
                                numSeconds);
                        table.getModel().setValueAt(newHours, index, 3);
                        //System.out.println(table.getModel().getValueAt(index, 3));
                    }
                    else {
                        table.getModel().setValueAt(hours, index, 3);
                    }
                    table.getModel().setValueAt(false, index, 4);
                    //System.out.println("3 " + table.getModel().getValueAt(index, 4));
                }
            }
            else {
                textArea.append("INVALID ID: Please try again.\n");
            }
            //Main.saveModel(table);
            textField.setText("");
        });
        textPanel.add(textField, BorderLayout.SOUTH);

        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        URL iconURL = getClass().getResource("/icon.png");
        Image image = defaultToolkit.getImage(iconURL);
        try {
            Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(image);
        } catch (UnsupportedOperationException e) {
            frame.setIconImage(image);
        }

        if(Taskbar.getTaskbar().isSupported(Taskbar.Feature.MENU)) {
            PopupMenu popupMenu = new PopupMenu(); // popup menu for macOS dock icon
            MenuItem add = new MenuItem("New User");
            add.addActionListener(e -> {
                addDialog();
            });
            popupMenu.add(add);
            Taskbar.getTaskbar().setMenu(popupMenu);
        }

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        textField.requestFocus();
    }

    private void addDialog() {
        JDialog dialog = new JDialog(frame, "New") {
            public Dimension getPreferredSize() {
                return new Dimension(300, 170);
            }
        };
        JPanel dialogPanel = new JPanel(new CardLayout());
        JPanel idPanel = new JPanel(new BorderLayout());
        idPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        JLabel idLabel = new JLabel("Enter id:", JLabel.LEFT);
        idPanel.add(idLabel, BorderLayout.NORTH);
        JTextField idField = new JTextField();
        idPanel.add(idField);
        JPanel buttonPanel = new JPanel();
        JButton nextButton = new JButton("Next >>");
        buttonPanel.add(nextButton, BorderLayout.CENTER);
        idPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialogPanel.add(idPanel, "ID");
        dialog.add(dialogPanel);
        CardLayout cl = (CardLayout) dialogPanel.getLayout();
        cl.show(dialogPanel, "ID");
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        JLabel nameLabel = new JLabel("Enter name:", JLabel.LEFT);
        namePanel.add(nameLabel, BorderLayout.NORTH);
        JTextField nameField = new JTextField();
        namePanel.add(nameField);
        JPanel buttonPanel2 = new JPanel();
        JButton finishButton = new JButton("Finish");
        buttonPanel2.add(finishButton, BorderLayout.CENTER);
        namePanel.add(buttonPanel2, BorderLayout.SOUTH);
        dialogPanel.add(namePanel, "Name");
        AbstractAction idAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                for(int i = 0; i < table.getModel().getRowCount(); i++) {
                    if(table.getModel().getValueAt(i, 1).equals(id)) {
                        JOptionPane.showMessageDialog(dialog, "ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                        idField.setText("");
                        return;
                    }
                }
                cl.show(dialogPanel, "Name");
                nameField.requestFocus();
                AbstractAction nameAction = new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        model.addRow(new Object[]{model.getRowCount()+1, id, nameField.getText(), "00:00:00", "false", "0"});
                        table.setModel(model);
                        dialog.dispose();
                    }
                };
                nameField.addActionListener(nameAction);
                finishButton.addActionListener(nameAction);
            }
        };
        idField.addActionListener(idAction);
        nextButton.addActionListener(idAction);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    static JPanel createAboutPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        URL imageResource = View.class.getResource("/icon.png");
        BufferedImage img = toBufferedImage(new ImageIcon(imageResource).getImage());
        JLabel icon = new JLabel();
        icon.setIcon(new ImageIcon(img));
        Image dimg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        icon.setIcon(new ImageIcon(dimg));
        JPanel imgPanel = new JPanel();
        imgPanel.add(icon);
        mainPanel.add(imgPanel);

        JPanel namePanel = new JPanel();
        JXHyperlink nameLink = new JXHyperlink();
        nameLink.setFocusPainted(false);
        nameLink.setText("MGMT");
        nameLink.setToolTipText("<html>MGMT<br>https://github.com/PranavAmarnath/MGMT</html>");
        nameLink.addActionListener(e -> {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI("https://github.com/PranavAmarnath/MGMT"));
                    nameLink.setClicked(true);
                    nameLink.setClickedColor(new Color(70, 39, 89)); // purple
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        namePanel.add(nameLink, SwingConstants.CENTER);
        JPanel versionPanel = new JPanel();
        JLabel versionLabel = new JLabel("Version 1.0", SwingConstants.CENTER);
        versionLabel.setForeground(new Color(150, 150, 150));
        versionLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        versionPanel.add(versionLabel);
        JPanel copyrightPanel = new JPanel();
        JLabel copyrightLabel = new JLabel("<html>Copyright © 2023 Pranav Amarnath<br><div style='text-align: center;'>All Rights Reserved.</div><br><div style='text-align: center;'>\"Icon\" Provided By Icons8.</div></html>", SwingConstants.CENTER);
        copyrightLabel.setForeground(new Color(150, 150, 150));
        copyrightLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        copyrightPanel.add(copyrightLabel);

        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.PAGE_AXIS));
        productPanel.add(namePanel);
        productPanel.add(versionPanel);
        productPanel.add(copyrightPanel);
        mainPanel.add(productPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return mainPanel;
    }

    private static BufferedImage toBufferedImage(Image img) {
        /** Reference: @see https://stackoverflow.com/a/13605411 */
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static JTable getTable() {
        return table;
    }

    public static JFrame getFrame() {
        return frame;
    }

}
