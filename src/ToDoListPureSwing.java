import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ToDoListPureSwing extends JFrame {

    // --- Data ---
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final String dataFile = "tasks.dat";

    // --- UI Components ---
    private final JPanel taskListPanel = new JPanel();
    private final JTextField taskInput = new JTextField();
    private final JButton addTaskButton = new JButton("+");
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel progressLabel = new JLabel();
    private final JLabel dateTimeLabel = new JLabel();

    // --- Colors & Fonts ---
    private final Color colorBackground = new Color(0x3a, 0x40, 0x42);
    private final Color colorPrimary = new Color(0x06, 0x2e, 0x3f);
    private final Color colorText = new Color(0x21, 0x9e, 0xbc);
    private final Color colorProgress = new Color(0x59, 0x83, 0x92);
    private final Font fontTitle = new Font("Inter", Font.BOLD, 32);
    private final Font fontDefault = new Font("Inter", Font.PLAIN, 14);
    private final Font fontButton = new Font("Inter", Font.BOLD, 24);

    public ToDoListPureSwing() {
        // --- 1. Load Data ---
        loadTasks();

        // --- 2. Frame Setup ---
        setTitle("JUST DO IT");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(colorBackground);
        // Add a window listener to save tasks on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveTasks();
            }
        });

        // Use an EmptyBorder to create padding around the main content pane
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 3. Create and Style Components ---

        // == Top Stats Panel ==
        JPanel statsContainer = new JPanel(new BorderLayout(20, 10));
        statsContainer.setOpaque(false);

        // Details (Title, Progress Bar, Date)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Todo List.");
        titleLabel.setFont(fontTitle);
        titleLabel.setForeground(colorText);
        detailsPanel.add(titleLabel);

        JLabel encouragementLabel = new JLabel("Keep it up!");
        encouragementLabel.setFont(fontDefault);
        encouragementLabel.setForeground(colorText);
        detailsPanel.add(encouragementLabel);

        detailsPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacing

        progressBar.setStringPainted(false);
        progressBar.setForeground(colorProgress);
        progressBar.setBackground(colorPrimary);
        progressBar.setBorderPainted(false);
        detailsPanel.add(progressBar);

        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing

        dateTimeLabel.setFont(fontDefault);
        dateTimeLabel.setForeground(colorText);
        detailsPanel.add(dateTimeLabel);
        updateDateTime(); // Start the clock

        // Numbers (Progress Counter)
        JPanel statsNumbersPanel = new JPanel(new GridBagLayout());
        statsNumbersPanel.setOpaque(false);
        statsNumbersPanel.setPreferredSize(new Dimension(100, 100));
        progressLabel.setFont(new Font("Inter", Font.BOLD, 24));
        progressLabel.setForeground(Color.WHITE);
        statsNumbersPanel.add(progressLabel);

        statsContainer.add(detailsPanel, BorderLayout.CENTER);
        statsContainer.add(statsNumbersPanel, BorderLayout.EAST);

        // == Center Content (Form and List) ==
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);

        // Input Form
        JPanel formPanel = new JPanel(new BorderLayout(10, 0));
        formPanel.setOpaque(false);
        taskInput.setFont(fontDefault);
        taskInput.setOpaque(false);
        taskInput.setForeground(colorText);
        taskInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorPrimary, 2, true),
                new EmptyBorder(10, 15, 10, 15))); // Rounded border + padding
        taskInput.setCaretColor(colorText);

        addTaskButton.setFont(fontButton);
        addTaskButton.setBackground(colorPrimary);
        addTaskButton.setForeground(Color.WHITE);
        addTaskButton.setFocusPainted(false);
        addTaskButton.setPreferredSize(new Dimension(50, 50));

        formPanel.add(taskInput, BorderLayout.CENTER);
        formPanel.add(addTaskButton, BorderLayout.EAST);

        // Task List
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);


        // --- 4. Add Components to Frame ---
        add(statsContainer, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);


        // --- 5. Add Action Listeners ---
        addTaskButton.addActionListener(e -> addTask());
        taskInput.addActionListener(e -> addTask()); // Allow adding with Enter key


        // --- 6. Initial UI Update ---
        updateUI();
    }

    private void addTask() {
        String text = taskInput.getText().trim();
        if (!text.isEmpty()) {
            tasks.add(new Task(text));
            taskInput.setText("");
            updateUI();
        }
    }

    private void deleteTask(Task task) {
        tasks.remove(task);
        updateUI();
    }

    private void toggleTaskComplete(Task task) {
        task.setDone(!task.isDone());
        updateUI();
    }

    private void editTask(Task task) {
        String newText = JOptionPane.showInputDialog(this, "Edit Task", task.getText());
        if (newText != null && !newText.trim().isEmpty()) {
            task.setText(newText.trim());
            updateUI();
        }
    }

    private void updateUI() {
        // Clear and rebuild the task list panel
        taskListPanel.removeAll();
        for (Task task : tasks) {
            taskListPanel.add(createTaskPanel(task));
            taskListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Update progress stats
        long completedTasks = tasks.stream().filter(Task::isDone).count();
        long totalTasks = tasks.size();
        progressLabel.setText(String.format("%d / %d", completedTasks, totalTasks));
        if (totalTasks > 0) {
            progressBar.setValue((int) (100 * completedTasks / totalTasks));
        } else {
            progressBar.setValue(0);
        }

        // Refresh the frame to show changes
        revalidate();
        repaint();
    }

    private JPanel createTaskPanel(Task task) {
        JPanel taskPanel = new JPanel(new BorderLayout(10, 0));
        taskPanel.setOpaque(false);
        taskPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left side: Checkbox and Task Text
        JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
        leftPanel.setOpaque(false);

        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(task.isDone());
        checkBox.setOpaque(false);
        checkBox.addActionListener(e -> toggleTaskComplete(task));

        JLabel taskTextLabel = new JLabel(task.getText());
        taskTextLabel.setFont(fontDefault);
        if (task.isDone()) {
            // Use HTML to get the strikethrough effect
            taskTextLabel.setText("<html><strike>" + task.getText() + "</strike></html>");
            taskTextLabel.setForeground(Color.GRAY);
        } else {
            taskTextLabel.setForeground(Color.WHITE);
        }

        leftPanel.add(checkBox, BorderLayout.WEST);
        leftPanel.add(taskTextLabel, BorderLayout.CENTER);

        // Right side: Edit and Delete Buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> editTask(task));

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteTask(task));

        rightPanel.add(editButton);
        rightPanel.add(deleteButton);

        taskPanel.add(leftPanel, BorderLayout.CENTER);
        taskPanel.add(rightPanel, BorderLayout.EAST);

        return taskPanel;
    }

    private void updateDateTime() {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eeee, MMMM d, yyyy HH:mm:ss");
            dateTimeLabel.setText(now.format(formatter));
        });
        timer.start();
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
            ArrayList<Task> loadedTasks = (ArrayList<Task>) ois.readObject();
            tasks.addAll(loadedTasks);
        } catch (FileNotFoundException e) {
            // This is normal for the first run, do nothing.
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Could not load tasks.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not save tasks.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Set a more modern look and feel for components like buttons
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new ToDoListPureSwing().setVisible(true));
    }
}

/**
 * A simple data class to hold task information.
 * Must implement Serializable to be saved to a file.
 */
class Task implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization
    private String text;
    private boolean done;

    public Task(String text) {
        this.text = text;
        this.done = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return text; // Useful for debugging
    }
}