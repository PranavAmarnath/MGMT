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
        file = getFileFromResource("data.csv");
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
    }

    public static void createModel(JTable table, boolean refresh) {
        model = new Model(file, table, refresh);
    }

    public static void saveModel(JTable table) {
        //Model.save("C:\\Users\\zonep\\IdeaProjects\\MGMT\\src\\main\\resources\\data.csv", table);
        Model.save(file.getPath(), table);
    }

    public static Model getModel() {
        return model;
    }

}
