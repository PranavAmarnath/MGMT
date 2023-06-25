import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import com.jthemedetecor.OsThemeDetector;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.QuitStrategy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class Main {

    private static Model model;
    private static File file;
    private static final boolean app = false; // true if testing app, false if creating JAR

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "MGMT");
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.awt.antialiasing", "true");
        System.setProperty("apple.awt.textantialiasing", "true");
        if(System.getProperty("os.name").contains("Mac")) {
            try {
                SwingUtilities.invokeLater(() -> {
                    Desktop desktop = Desktop.getDesktop();

                    JPanel aboutPanel = View.createAboutPanel();
                    desktop.setAboutHandler(e -> {
                        JOptionPane.showOptionDialog(View.getFrame(), aboutPanel, "About MGMT", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                    });
                    desktop.setPreferencesHandler(e -> {
                        JOptionPane.showOptionDialog(View.getFrame(), "Preferences", "MGMT Preferences", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
                    });
                    desktop.setQuitHandler((e, r) -> {
                        saveModel(View.getTable());
                    });
                    //desktop.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
                });
            } catch (Exception e) { e.printStackTrace(); }
        }
        if(app) {
            file = getFileFromResource("data.csv");
        }
        else {
            String fileName = "data.csv";
            File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String inputFilePath = jarFile.getParent() + File.separator + fileName;
            file = getFileFromResource(inputFilePath);
        }
        final OsThemeDetector detector = OsThemeDetector.getDetector();
        if (detector.isDark()) {
            if(System.getProperty("os.name").contains("Win")) FlatDarkLaf.setup();
            else if(System.getProperty("os.name").contains("Mac")) FlatMacDarkLaf.setup();
            else FlatDarculaLaf.setup();
        } else {
            if(System.getProperty("os.name").contains("Win")) FlatLightLaf.setup();
            else if(System.getProperty("os.name").contains("Mac")) FlatMacLightLaf.setup();
            else FlatIntelliJLaf.setup();
        }
        SwingUtilities.invokeLater(View::new);
        detector.registerListener(isDark -> {
            SwingUtilities.invokeLater(() -> {
                if (detector.isDark()) {
                    if(System.getProperty("os.name").contains("Win")) FlatDarkLaf.setup();
                    else if(System.getProperty("os.name").contains("Mac")) FlatMacDarkLaf.setup();
                    else FlatDarculaLaf.setup();
                    FlatLaf.updateUI();
                } else {
                    if(System.getProperty("os.name").contains("Win")) FlatLightLaf.setup();
                    else if(System.getProperty("os.name").contains("Mac")) FlatMacLightLaf.setup();
                    else FlatIntelliJLaf.setup();
                    FlatLaf.updateUI();
                }
            });
        });
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException, IOException {
        if(app) {
            ClassLoader classLoader = Main.class.getClassLoader();
            URL resource = classLoader.getResource(fileName);
            if (resource == null) {
                showError("An Exception Occurred :(", new IllegalArgumentException("File not found! " + fileName));
            } else {
                return new File(resource.toURI());
            }
        }
        else {
            return new File(fileName);
        }
        return null;
    }

    public static void createModel(JTable table, boolean refresh) {
        model = new Model(file, table, refresh);
    }

    public static void saveModel(JTable table) {
        //Model.save("C:\\Users\\zonep\\IdeaProjects\\MGMT\\src\\main\\resources\\data.csv", table);
        Model.save(file.getPath(), table);
    }

    private static void showError(String title, Exception e) {
        JTextPane textPane = new JTextPane();
        textPane.setText(e.getMessage());
        JOptionPane.showMessageDialog(View.getFrame(), textPane, title, JOptionPane.ERROR_MESSAGE);
    }

    public static Model getModel() {
        return model;
    }

}
