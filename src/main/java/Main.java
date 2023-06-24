import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class Main {

    private static Model model;
    private static File file;

    public static void main(String[] args) throws URISyntaxException {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(View::new);
        String fileName = "data.csv";
        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        String inputFilePath = jarFile.getParent() + File.separator + fileName;
        file = getFileFromResource(inputFilePath);
        //file = getFileFromResource("data.csv");
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {
        /*
        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            showError("An Exception Occurred :(", new IllegalArgumentException("File not found! " + fileName));
        } else {
            return new File(resource.toURI());
        }
        */
        return new File(fileName);
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
