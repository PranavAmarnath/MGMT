import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.io.File;

public class Main {

    private static Model model;

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(View::new);
    }

    public static void createModel(JTable table, boolean refresh) {
        model = new Model(new File("C:\\Users\\zonep\\IdeaProjects\\MGMT\\src\\main\\resources\\data.csv"), table, refresh);
    }

    public static void saveModel(JTable table) {
        Model.save("C:\\Users\\zonep\\IdeaProjects\\MGMT\\src\\main\\resources\\data.csv", table);
    }

    public static Model getModel() {
        return model;
    }

}
