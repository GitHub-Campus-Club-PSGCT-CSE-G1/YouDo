package src.YouDo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Task {

    private Long id;
    private String name;
    private String description;
    private String priority;
    private Date dueDate;
    private String status;
    private Date creationDate;
    private Date completionDate;

    // Constructors
    public Task() {
        // Default constructor
        this.creationDate = new Date(); // Set current date as creation date
    }

    // new task constructor
    public Task(String name, String description, String priority, Date dueDate) {
        this.id = getNextTaskId();
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = "Not Started";
        this.creationDate = new Date(); // Set creation date to current date
        // completionDate is not set initially; you can set it when the task is completed
    }

    // load task constructor
    public Task(Long id, String name, String description, String priority, Date dueDate, String status, Date creationDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.creationDate = creationDate;
        // completionDate is not set initially; you can set it when the task is completed
    }

    private static final String TASK_ID_FILE = "src\\YouDo\\taskId.txt";

    // Load the last assigned task ID from a file
    private static long loadLastTaskId() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TASK_ID_FILE))) {
            String line = reader.readLine();
            if (line != null && line.matches("\\d+")) {
                return Long.parseLong(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
        return 1; // Default to 1 if the file doesn't exist or an error occurs
    }

    // Save the last assigned task ID to a file
    private static void saveLastTaskId(long lastTaskId) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASK_ID_FILE))) {
            writer.write(Long.toString(lastTaskId));
        } catch (IOException e) {
            e.printStackTrace(); // handle this later if needed
        }
    }

    // Initialize the last assigned task ID
    private static long taskIdCounter = loadLastTaskId();

    // Get the next task ID and increment the counter
    private static long getNextTaskId() {
        long taskId = taskIdCounter;
        taskIdCounter++;
        saveLastTaskId(taskIdCounter); // Save the updated counter to the file
        return taskId;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    // Display task details
    public void displayTaskDetails() {
        System.out.println("Task ID: " + id);
        System.out.println("Task Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("Priority: " + priority);
        System.out.println("Due Date: " + dueDate);
        System.out.println("Status: " + status);
        System.out.println("Creation Date: " + creationDate);
        System.out.println("Completion Date: " + completionDate);
        System.out.println();
    }
}
