package src.YouDo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
// import java.util.Scanner;

public class User {
    private Long userId;
    private String username;
    private String email;
    private List<Task> tasks;

    // Constructors
    public User() {
        // Default constructor
        this.tasks = new ArrayList<>();
    }

    public String getName() {
        return username;
    }
    
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.tasks = new ArrayList<>();
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    // Method to add a task to the user's task list with user input
    public void addTask(Task newTask) {
        this.tasks.add(newTask);
    }

    // Method to remove a task from the user's task list by task name
    public void deleteTask(Long taskId) {
        Iterator<Task> iterator = this.tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getId() == taskId) {
                iterator.remove();
                System.out.println("Task '" + taskId + "' deleted.");
                return; // Stop after deleting the first matching task
            }
        }
        System.out.println("Task '" + taskId + "' not found.");
    }

    public void saveTasksToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Task task : tasks) {
                writer.println(taskToFileString(task));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load tasks from a text file
    public void loadTasksFromFile(String fileName) {
        tasks.clear(); // Clear existing tasks before loading from file
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(fileStringToTask(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to convert a Task object to a string for saving to file
    private String taskToFileString(Task task) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return task.getId() + "|" + task.getName() + "|" + task.getDescription() + "|" +
               task.getPriority() + "|" + dateFormat.format(task.getDueDate()) + "|" +
               task.getStatus() + "|" + dateFormat.format(task.getCreationDate());
    }

    // Helper method to convert a string from file to a Task object
    private Task fileStringToTask(String line) {
        String[] parts = line.split("\\|");
        return new Task(Long.parseLong(parts[0]), parts[1], parts[2], parts[3], parseDate(parts[4]), parts[5], parseDate(parts[6]));
    }

    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Error parsing date. Using current date instead.");
            return new Date();
        }
    }
}
