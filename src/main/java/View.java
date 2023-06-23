import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
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
    private JTable table;
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
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                Main.saveModel(table);
                System.exit(0);
            }
        });
        URL iconURL = getClass().getResource("/icon.png");
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(iconURL));
        frame.setIconImage(icon.getImage());
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        frame.getContentPane().add(mainPanel);
        mainPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        scrollPane = new JScrollPane();
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        textArea = new JTextArea();
        scrollPane.setViewportView(textArea);
        textArea.setEditable(false);

        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        menu1 = new JMenu("File");
        menuBar.add(menu1);
        menu2 = new JMenu("Help");
        menuBar.add(menu2);
        addItem = new JMenuItem("Add...");
        menu1.add(addItem);
        addItem.addActionListener(e -> {
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
            AbstractAction idAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String id = idField.getText();
                    JPanel namePanel = new JPanel(new BorderLayout());
                    namePanel.setBorder(new EmptyBorder(30, 50, 30, 50));
                    JLabel nameLabel = new JLabel("Enter name:", JLabel.LEFT);
                    namePanel.add(nameLabel, BorderLayout.NORTH);
                    JTextField nameField = new JTextField();
                    namePanel.add(nameField);
                    JPanel buttonPanel = new JPanel();
                    JButton finishButton = new JButton("Finish");
                    buttonPanel.add(finishButton, BorderLayout.CENTER);
                    namePanel.add(buttonPanel, BorderLayout.SOUTH);
                    dialogPanel.add(namePanel, "Name");
                    cl.show(dialogPanel, "Name");
                    nameField.requestFocus();
                    AbstractAction nameAction = new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.out.println(id);
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
        });
        helpItem = new JMenuItem("About");
        menu2.add(helpItem);

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

        frame.pack();
        frame.setVisible(true);
        textField.requestFocus();
    }

    public static JFrame getFrame() {
        return frame;
    }

}
