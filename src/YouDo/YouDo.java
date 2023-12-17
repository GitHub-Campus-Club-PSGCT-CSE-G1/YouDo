package src.YouDo;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class YouDo {
    private static User currentUser;
    private static DefaultTableModel tableModel;
    private static List<Task> tasks;
    private static JTable table;

    public static void main(String[] args) {
        try {
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    UIManager.getLookAndFeelDefaults().put("TextField.caretForeground", Color.WHITE);
} catch (Exception e) {
    e.printStackTrace();
}

        
        // Initialize a user for testing
        currentUser = new User("Dhakkshin", "me@my.com");

        JFrame f = new JFrame("YouDo");
        f.setSize(1000, 700);
        f.setLayout(new BorderLayout());

        // Panel 1 - Welcome Message
        JPanel welcomePanel = createWelcomePanel();

        // Panel 2 - Task Input
        JPanel taskInputPanel = createTaskInputPanel();

        // Panel 3 - Display Area (JTable)
        JPanel displayPanel = createDisplayPanel();

        // Add panels to the main frame
        f.add(welcomePanel, BorderLayout.NORTH);
        f.add(taskInputPanel, BorderLayout.CENTER);
        f.add(displayPanel, BorderLayout.SOUTH);

        // project\tasks.txt
        // Load tasks from file on startup
        loadTasksFromFile(currentUser, "src\\YouDo\\tasks.txt");

        // Save tasks to file on program exit
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                saveTasksToFile(currentUser, "src\\YouDo\\tasks.txt")
        ));

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    private static void saveTasksToFile(User user, String fileName) {
        user.saveTasksToFile(fileName);
    }

    private static void loadTasksFromFile(User user, String fileName) {
        user.loadTasksFromFile(fileName);
        updateDisplayArea(user);
    }

    private static JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new FlowLayout());
        JLabel welcomeLabel = new JLabel("Welcome to YouDo, " + currentUser.getName() + "!");
        JLabel instructionLabel = new JLabel("Here, you have to do!!");
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(instructionLabel);
        // welcomepanel style
        welcomeLabel.setBackground(Color.BLACK);
        welcomeLabel.setForeground(Color.WHITE);
        welcomePanel.setBackground(Color.BLACK);
        welcomePanel.setForeground(Color.WHITE);
        instructionLabel.setForeground(Color.WHITE);
        return welcomePanel;
    }

    private static JPanel createTaskInputPanel() {
        JPanel taskInputPanel = new JPanel(new GridLayout(8, 2, 0, 10)); // Increased rows for the new component and added vertical gap
        JLabel taskLabel = new JLabel("  Enter your task here:");
        JTextField taskField = new JTextField(20);
        JLabel priorityLabel = new JLabel("Enter Priority:");
        JTextField priorityField = new JTextField(20);
        JLabel descriptionLabel = new JLabel("Enter Description:");
        JTextField descriptionField = new JTextField(20);

        // Set background and foreground for taskLabel
        taskLabel.setBackground(Color.BLACK);
        taskLabel.setForeground(Color.WHITE);

        // Set background and foreground for priorityLabel
        priorityLabel.setBackground(Color.BLACK);
        priorityLabel.setForeground(Color.WHITE);

        // Set background and foreground for descriptionLabel
        descriptionLabel.setBackground(Color.BLACK);
        descriptionLabel.setForeground(Color.WHITE);

        // Add JDatePicker for Due Date
        JLabel dueDateLabel = new JLabel("Select Due Date:");
        UtilDateModel model = new UtilDateModel();
        Properties properties = new Properties();
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        // Set background and foreground for dueDateLabel
        dueDateLabel.setBackground(Color.BLACK);
        dueDateLabel.setForeground(Color.WHITE);

        JButton resetButton = new JButton("Reset");
        JButton submitButton = new JButton("Submit");
        styleResetButton(resetButton);
        styleSubmitButton(submitButton);

        // Style for text fields and date picker
        styleTextField(taskField);
        styleTextField(priorityField);
        styleTextField(descriptionField);
        styleDatePicker(datePicker);

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset input fields
                taskField.setText("");
                priorityField.setText("");
                descriptionField.setText("");
                datePicker.getModel().setValue(null); // Reset the JDatePicker
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String taskName = taskField.getText();
                String taskPriority = priorityField.getText();
                String taskDescription = descriptionField.getText();

                // Retrieve the selected due date from JDatePicker
                Date dueDate = (Date) datePicker.getModel().getValue();

                // Continue with task creation
                Task newTask = new Task(taskName, taskDescription, taskPriority, dueDate);
                currentUser.addTask(newTask);
                updateDisplayArea(currentUser);

                // Reset input fields
                taskField.setText("");
                priorityField.setText("");
                descriptionField.setText("");
                datePicker.getModel().setValue(null); // Reset the JDatePicker
            }
        });

        // Adding an empty label to create space between buttons and text fields
        taskInputPanel.add(new JLabel());
        taskInputPanel.add(new JLabel());

        taskInputPanel.add(taskLabel);
        taskInputPanel.add(taskField);
        taskInputPanel.add(priorityLabel);
        taskInputPanel.add(priorityField);
        taskInputPanel.add(descriptionLabel);
        taskInputPanel.add(descriptionField);
        taskInputPanel.add(dueDateLabel);
        taskInputPanel.add(datePicker);

        taskInputPanel.add(resetButton);
        taskInputPanel.add(submitButton);

        // Set background for taskInputPanel
        taskInputPanel.setBackground(Color.BLACK);
        taskInputPanel.setForeground(Color.WHITE);

        return taskInputPanel;
    }

    private static void styleResetButton(JButton button) {
        button.setBackground(Color.GRAY); // Set background color to light gray
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Adjust the values as needed
        button.setOpaque(true);
        button.setBorderPainted(false); // Remove default button border
        button.setFocusPainted(false); // Remove focus border
        button.setContentAreaFilled(true); // Ensure that the background is filled
        button.setFont(button.getFont().deriveFont(16f)); // Optional: Set font size
        button.setBorder(new RoundedBorder(30));
    }

    private static void styleSubmitButton(JButton button) {
        button.setBackground(new Color(0, 128, 0)); // Set background color to green
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Adjust the values as needed
        button.setOpaque(true);
        button.setBorderPainted(false); // Remove default button border
        button.setFocusPainted(false); // Remove focus border
        button.setContentAreaFilled(true); // Ensure that the background is filled
        button.setFont(button.getFont().deriveFont(16f));
                button.setBorder(new RoundedBorder(30));
 // Optional: Set font size
    }
private static void styleTextField(JTextField textField) {
    textField.setBackground(new Color(30, 30, 30));
    textField.setForeground(Color.WHITE);
    textField.setCaretColor(Color.BLACK); // Set caret color to be visible
    textField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Use a LineBorder
    textField.setMargin(new Insets(5, 5, 5, 5));
}
private static void styleDatePicker(JDatePickerImpl datePicker) {
    datePicker.setBackground(Color.GRAY); // Set background color to gray for the entire date picker panel
    datePicker.getJFormattedTextField().setBackground(Color.GRAY); // Set background color to gray for the text field
    datePicker.setForeground(Color.WHITE);
    datePicker.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Optional: Add a border
    datePicker.getJFormattedTextField().setCaretColor(Color.WHITE);
    datePicker.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
}
    private static JPanel createDisplayPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout());
    
        // Table headers and initial data
        String[] columnNames = {"S.No", "ID", "Name", "Description", "Priority", "Due Date", "Status", "Creation Date"};
        Object[][] data = {};
    
        // Create a DefaultTableModel with data and column names
        tableModel = new DefaultTableModel(data, columnNames);
    
        table = new JTable(tableModel);
        CenterRenderer centerRenderer = new CenterRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.BLACK);
        // Add a right-click listener to the table
        table.addMouseListener(new TableRightClickListener());
    
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDisplayArea(currentUser);
            }
        });
    
        displayPanel.add(scrollPane, BorderLayout.CENTER);
        displayPanel.add(refreshButton, BorderLayout.SOUTH);
    
        // Set background for displayPanel (including white space)
        displayPanel.setBackground(Color.BLACK);
        displayPanel.setForeground(Color.WHITE);
        JTableHeader header = table.getTableHeader();
    header.setDefaultRenderer(new CenterHeaderRenderer());
    
        // Set background for table
        table.setBorder(null);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.WHITE);
    
        // Set background for table headers
        table.getTableHeader().setBackground(Color.BLACK);
        table.getTableHeader().setForeground(Color.WHITE);
    
        // Set background for refreshButton
        refreshButton.setBackground(new Color(0, 128, 0)); // Green color
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Adjust the values as needed
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false); // Remove default button border
        refreshButton.setFocusPainted(false); // Remove focus border
        refreshButton.setContentAreaFilled(true); // Ensure that the background is filled
        refreshButton.setFont(refreshButton.getFont().deriveFont(16f)); // Optional: Set font size
        refreshButton.setBorder(new RoundedBorder(30));
    
        return displayPanel;
    }
            private static void updateDisplayArea(User user) {
        tasks = user.getTasks();

        // Clear the existing data in the table
        tableModel.setRowCount(0);


        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            Object[] rowData = {i + 1, task.getId(), task.getName(), task.getDescription(), task.getPriority(),
                    task.getDueDate(), task.getStatus(), task.getCreationDate()};

            tableModel.addRow(rowData);
        }
    }

    private static class TableRightClickListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopup(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                showPopup(e);
            }
        }

        private void showPopup(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int column = table.columnAtPoint(e.getPoint());

            if (row >= 0 && row < table.getRowCount() && column >= 0 && column < table.getColumnCount()) {
                table.setRowSelectionInterval(row, row);

                JPopupMenu popup = createPopupMenu();
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        private JPopupMenu createPopupMenu() {
            JPopupMenu popup = new JPopupMenu();

            JMenuItem deleteItem = new JMenuItem("Delete");
            deleteItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement delete functionality here
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < tasks.size()) {
                        currentUser.deleteTask(tasks.get(selectedRow).getId());
                        updateDisplayArea(currentUser);
                    }
                }
            });

            JMenuItem completeItem = new JMenuItem("Mark as Complete");
            completeItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement mark as completed functionality here
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < tasks.size()) {
                        Task task = tasks.get(selectedRow);
                        task.setStatus("Completed");
                        task.setCompletionDate(new Date());
                        updateDisplayArea(currentUser);
                    }
                }
            });

            JMenuItem startItem = new JMenuItem("Start");
            startItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement started functionality here
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < tasks.size()) {
                        Task task = tasks.get(selectedRow);
                        task.setStatus("Started");
                        updateDisplayArea(currentUser);
                    }
                }
            });

            popup.add(deleteItem);
            popup.add(completeItem);
            popup.add(startItem);

            return popup;
        }
    }

    // Custom DateLabelFormatter to format the date in the JDatePicker
    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private String datePattern = "yyyy-MM-dd";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null && value instanceof Date) {
                return dateFormatter.format(value);
            }
            return "";
        }
    }

    // Helper class for creating rounded borders
// Helper class for creating rounded borders
// Helper class for creating rounded borders
// Helper class for creating rounded borders
static class RoundedBorder implements Border {
    private int radius;

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius, this.radius, this.radius, this.radius);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (c instanceof JComponent) {
            Graphics2D g2d = (Graphics2D) g.create();

            int arcDiameter = radius * 2;
            g2d.drawRoundRect(x, y, width - 1, height - 1, arcDiameter, arcDiameter);

            g2d.dispose();
        }
    }
}
private static class CenterRenderer extends DefaultTableCellRenderer {
    public CenterRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }
}
private static class CenterHeaderRenderer extends DefaultTableCellRenderer {
    public CenterHeaderRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Adjust the padding as needed
    }
}


}



