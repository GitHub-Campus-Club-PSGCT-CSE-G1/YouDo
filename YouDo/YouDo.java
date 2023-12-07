package YouDo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class YouDo {
    private static User currentUser;
    private static DefaultTableModel tableModel;
    private static List<Task> tasks;
    private static JTable table;

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
        loadTasksFromFile(currentUser, "YouDo\\tasks.txt");

        // Save tasks to file on program exit
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                saveTasksToFile(currentUser, "YouDo\\tasks.txt")
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
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String taskName = taskField.getText();
                String taskPriority = priorityField.getText();
                String taskDescription = descriptionField.getText();
                String dueDateString = dueDateField.getText();

                try {
                    Date dueDate = parseDate(dueDateString);
                    Task newTask = new Task(taskName, taskDescription, taskPriority, dueDate);
                    currentUser.addTask(newTask);
                    updateDisplayArea(currentUser);

                    // Reset input fields
                    taskField.setText("");
                    priorityField.setText("");
                    descriptionField.setText("");
                    dueDateField.setText("");
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

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add a right-click listener to the table
        table.addMouseListener(new TableRightClickListener());

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
        tasks = user.getTasks();

        // Clear the existing data in the table
        tableModel.setRowCount(0);

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            Object[] rowData = {
                    i + 1,
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getPriority(),
                    task.getDueDate(),
                    task.getStatus(),
                    task.getCreationDate()
            };

            tableModel.addRow(rowData);
        }
    }

    private static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(dateString);
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
}
