package project;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class YouDo {
    private static User currentUser;
    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        // Initialize a user for testing
        currentUser = new User("Dhakkshin", "me@my.com");

        JFrame f = new JFrame("YouDo");
        f.setSize(1000, 700);
        f.setLayout(new BorderLayout());

        // Panel 1 - Welcome Message
        JPanel welcomePanel = createWelcomePanel();

        // Panel 2 - Task Input
        JPanel taskInputPanel = createTaskInputPanel();

        // Panel 3 - Display Area (JTable) and Submit Button
        JPanel displayPanel = createDisplayPanel();

        // Add panels to the main frame
        f.add(welcomePanel, BorderLayout.NORTH);
        f.add(taskInputPanel, BorderLayout.CENTER);
        f.add(displayPanel, BorderLayout.SOUTH);
// project\tasks.txt
        // Load tasks from file on startup
        loadTasksFromFile(currentUser, "project\\tasks.txt");

        // Save tasks to file on program exit
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                saveTasksToFile(currentUser, "project\\tasks.txt")
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
        return welcomePanel;
    }

    private static JPanel createTaskInputPanel() {
        JPanel taskInputPanel = new JPanel(new GridLayout(6, 2));
        JLabel taskLabel = new JLabel("Enter your task here:");
        JTextField taskField = new JTextField(20);
        JLabel priorityLabel = new JLabel("Enter Priority:");
        JTextField priorityField = new JTextField(20);
        JLabel descriptionLabel = new JLabel("Enter Description:");
        JTextField descriptionField = new JTextField(20);
        JLabel dueDateLabel = new JLabel("Enter Due Date (yyyy-MM-dd HH:mm:ss):");
        JTextField dueDateField = new JTextField(20);
        JLabel statusLabel = new JLabel("Enter Status:");
        JTextField statusField = new JTextField(20);
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String taskName = taskField.getText();
                String taskPriority = priorityField.getText();
                String taskDescription = descriptionField.getText();
                String dueDateString = dueDateField.getText();
                String taskStatus = statusField.getText();

                try {
                    Date dueDate = parseDate(dueDateString);
                    Task newTask = new Task(taskName, taskDescription, taskPriority, dueDate, taskStatus);
                    currentUser.addTask(newTask);
                    updateDisplayArea(currentUser);

                    // Reset input fields
                    taskField.setText("");
                    priorityField.setText("");
                    descriptionField.setText("");
                    dueDateField.setText("");
                    statusField.setText("");
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Error parsing date. Please enter a valid date format.");
                }
            }
        });

        taskInputPanel.add(taskLabel);
        taskInputPanel.add(taskField);
        taskInputPanel.add(priorityLabel);
        taskInputPanel.add(priorityField);
        taskInputPanel.add(descriptionLabel);
        taskInputPanel.add(descriptionField);
        taskInputPanel.add(dueDateLabel);
        taskInputPanel.add(dueDateField);
        taskInputPanel.add(statusLabel);
        taskInputPanel.add(statusField);
        taskInputPanel.add(new JPanel());  // Empty panel for spacing
        taskInputPanel.add(submitButton);

        return taskInputPanel;
    }

    private static JPanel createDisplayPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout());

        // Table headers and initial data
        String[] columnNames = {"S.No", "ID", "Name", "Description", "Priority", "Due Date", "Status", "Creation Date"};
        Object[][] data = {};

        // Create a DefaultTableModel with data and column names
        tableModel = new DefaultTableModel(data, columnNames);

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDisplayArea(currentUser);
            }
        });

        displayPanel.add(scrollPane, BorderLayout.CENTER);
        displayPanel.add(refreshButton, BorderLayout.SOUTH);

        return displayPanel;
    }

    private static void updateDisplayArea(User user) {
        List<Task> tasks = user.getTasks();

        // Clear the existing data in the table
        tableModel.setRowCount(0);

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            Object[] rowData = {i + 1, task.getId(), task.getName(), task.getDescription(), task.getPriority(), task.getDueDate(), task.getStatus(), task.getCreationDate()};
            tableModel.addRow(rowData);
        }
    }

    private static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(dateString);
    }
}
