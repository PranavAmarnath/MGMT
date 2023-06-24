import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.jthemedetecor.OsThemeDetector;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class Main {

    private static Model model;
    private static File file;
    private static final boolean app = true; // true if testing app, false if creating JAR

    public static void main(String[] args) throws URISyntaxException, IOException {
        FlatLightLaf.setup();
        if(app) {
            file = getFileFromResource("data.csv");
        }
        else {
            String fileName = "data.csv";
            File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String inputFilePath = jarFile.getParent() + File.separator + fileName;
            file = getFileFromResource(inputFilePath);
        }
        SwingUtilities.invokeLater(View::new);
        final OsThemeDetector detector = OsThemeDetector.getDetector();
        detector.registerListener(isDark -> {
            SwingUtilities.invokeLater(() -> {
                if (isDark) {
                    FlatDarkLaf.setup();
                } else {
                    FlatLightLaf.setup();
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
