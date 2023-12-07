package YouDo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
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
        String[] columnNames = {"S.No", "ID", "Name", "Description", "Priority", "Due Date", "Status", "Creation Date", "Delete", "Completed", "Start"};
        Object[][] data = {};

        // Create a DefaultTableModel with data and column names
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex >= 8 ? JButton.class : super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 8;  // Set buttons as editable
            }
        };

        table = new JTable(tableModel);
        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new ButtonEditor(table, new JCheckBox()));
        table.getColumn("Completed").setCellRenderer(new ButtonRenderer());
        table.getColumn("Completed").setCellEditor(new ButtonEditor(table, new JCheckBox()));
        table.getColumn("Start").setCellRenderer(new ButtonRenderer());
        table.getColumn("Start").setCellEditor(new ButtonEditor(table, new JCheckBox()));

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

    public static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private static class ButtonEditor extends DefaultCellEditor {
        private JTable table;
        protected JButton button;
        private String label;
        private boolean isPushed;

        // Constructor to receive the JTable instance
        public ButtonEditor(JTable table, JCheckBox checkBox) {
            super(checkBox);
            this.table = table;  // Store the reference to the JTable
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Perform the action based on the button clicked
                handleButtonClick(label, table.getSelectedRow());
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return true;
        }

        // Custom method to handle button click actions
        private static void handleButtonClick(String buttonText, int selectedRow) {
            Task task = tasks.get(selectedRow);
            switch (buttonText) {
                case "Delete":
                    currentUser.deleteTask(task.getId());
                    break;
                case "Mark as Complete":
                    task.setStatus("Completed");
                    task.setCompletionDate(new Date());
                    break;
                case "Start":
                    task.setStatus("Started");
                    break;
            }
            updateDisplayArea(currentUser);
        }

    }

    private static void updateDisplayArea(User user) {
        tasks = user.getTasks();
    
        // Clear the existing data in the table
        tableModel.setRowCount(0);
        
        int i = 0;
        for (Task task : tasks) {
            // Task task = tasks.get(i);
            JButton deleteButton = new StyledButton("Delete");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ButtonEditor.handleButtonClick("Delete", i); // Use 'i' instead of table.getSelectedRow()
                }
            });
    
            JButton completeButton = new StyledButton("Mark as Complete");
            completeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ButtonEditor.handleButtonClick("Mark as Complete", i); // Use 'i' instead of table.getSelectedRow()
                }
            });
    
            JButton startButton = new StyledButton("Start");
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ButtonEditor.handleButtonClick("Start", i); // Use 'i' instead of table.getSelectedRow()
                }
            });
    
            Object[] rowData = {
                    i + 1,
                    task.getId(),
                    task.getName(),
                    task.getDescription(),
                    task.getPriority(),
                    task.getDueDate(),
                    task.getStatus(),
                    task.getCreationDate(),
                    deleteButton,
                    completeButton,
                    startButton
            };
    
            tableModel.addRow(rowData);
        }
    }

    private static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(dateString);
    }
}

class StyledButton extends JButton {

    public StyledButton(String text) {
        super(text);
        initButton();
    }

    private void initButton() {
        // Set desired style properties
        setBackground(Color.BLUE);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 14));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
