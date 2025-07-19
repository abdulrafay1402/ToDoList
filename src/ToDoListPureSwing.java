import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ToDoListPureSwing extends JFrame {

    private final ArrayList<Task> tasks = new ArrayList<>();
    private final String dataFile = "tasks.dat";

    // UI Components
    private JPanel taskListPanel;
    private JTextField taskInput;
    private JButton addTaskButton;
    private JLabel dateTimeLabel;
    private JLabel titleLabel;
    private JLabel completedLabel;
    private JProgressBar progressBar;
    private JScrollPane scrollPane;
    private JPanel headerPanel;
    private JPanel centerPanel;
    private JPanel inputPanel;
    private JPanel footerPanel;
    private JButton darkModeButton;

    // Mode state
    private boolean darkMode = false;

    // Colors - Light Mode
    private Color PRIMARY_COLOR = new Color(64, 115, 255);
    private Color PRIMARY_HOVER = new Color(48, 95, 240);
    private Color SECONDARY_COLOR = new Color(255, 105, 140);
    private Color SECONDARY_HOVER = new Color(240, 90, 125);
    private Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private Color CARD_COLOR = Color.WHITE;
    private Color TEXT_COLOR = new Color(40, 42, 53);
    private Color TEXT_SECONDARY = new Color(120, 124, 140);
    private Color ACCENT_COLOR = new Color(100, 220, 180);
    private Color ACCENT_HOVER = new Color(80, 200, 160);
    private Color BORDER_COLOR = new Color(80, 80, 80);  // Dark gray border for light mode
    private Color COMPLETED_COLOR = new Color(160, 170, 190);
    private Color COMPLETED_BG = new Color(245, 248, 250);
    private Color HOVER_COLOR = new Color(240, 243, 248);
    private Color SUCCESS_COLOR = new Color(50, 200, 120);
    private Color WARNING_COLOR = new Color(255, 180, 70);
    private Color ERROR_COLOR = new Color(255, 90, 90);
    private Color TEXT_FIELD_TEXT_COLOR = new Color(30, 30, 30); // Dark text for text field

    // Colors - Dark Mode
    private final Color PRIMARY_COLOR_DARK = new Color(100, 150, 255);
    private final Color PRIMARY_HOVER_DARK = new Color(130, 170, 255);
    private final Color SECONDARY_COLOR_DARK = new Color(255, 110, 140);
    private final Color SECONDARY_HOVER_DARK = new Color(255, 130, 160);
    private final Color BACKGROUND_COLOR_DARK = new Color(18, 20, 24);
    private final Color CARD_COLOR_DARK = new Color(30, 32, 38);
    private final Color TEXT_COLOR_DARK = new Color(230, 235, 240);
    private final Color TEXT_SECONDARY_DARK = new Color(160, 165, 170);
    private final Color ACCENT_COLOR_DARK = new Color(50, 220, 180);
    private final Color ACCENT_HOVER_DARK = new Color(80, 240, 200);
    private final Color BORDER_COLOR_DARK = new Color(70, 70, 70); // Lighter border for dark mode
    private final Color COMPLETED_COLOR_DARK = new Color(130, 140, 150);
    private final Color COMPLETED_BG_DARK = new Color(35, 38, 42);
    private final Color HOVER_COLOR_DARK = new Color(40, 42, 48);
    private final Color SUCCESS_COLOR_DARK = new Color(80, 220, 130);
    private final Color WARNING_COLOR_DARK = new Color(255, 210, 100);
    private final Color ERROR_COLOR_DARK = new Color(255, 120, 120);
    private final Color TEXT_FIELD_TEXT_COLOR_DARK = new Color(220, 220, 220); // Light text for dark mode

    // Fonts
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font TASK_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font COMPLETED_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font PROGRESS_FONT = new Font("Segoe UI", Font.BOLD, 12);

    public ToDoListPureSwing() {
        loadTasks();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Modern To-Do List");
        setSize(850, 700);
        setMinimumSize(new Dimension(600, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveTasks();
            }
        });

        // Header Panel
        headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel
        centerPanel = new JPanel(new BorderLayout(10, 15));
        centerPanel.setOpaque(false);

        // Input Panel
        inputPanel = createInputPanel();
        centerPanel.add(inputPanel, BorderLayout.NORTH);

        // Task List Panel with improved layout
        createTaskListPanel();
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Footer Panel
        footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        updateUI();
        updateDateTime();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(darkMode ? CARD_COLOR_DARK : new Color(240, 245, 250));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(15, BORDER_COLOR, 2), // Added border thickness
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Top row panel for dark mode toggle
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        darkModeButton = createStyledButton(darkMode ? "Light Mode" : "Dark Mode", e -> {
            darkMode = !darkMode;
            darkModeButton.setText(darkMode ? "Light Mode" : "Dark Mode");
            switchMode();
        });
        darkModeButton.setPreferredSize(new Dimension(120, 35));
        darkModeButton.setBackground(darkMode ? ACCENT_COLOR_DARK : ACCENT_COLOR);
        darkModeButton.setForeground(Color.WHITE);

        topRow.add(darkModeButton, BorderLayout.EAST);
        headerPanel.add(topRow);

        // Title
        titleLabel = new JLabel("My To-Do List", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel);

        // Progress section
        JPanel progressContainer = createProgressSection();
        headerPanel.add(progressContainer);

        // DateTime Label
        dateTimeLabel = new JLabel("", SwingConstants.CENTER);
        dateTimeLabel.setFont(SUBTITLE_FONT);
        dateTimeLabel.setForeground(TEXT_SECONDARY);
        dateTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateTimeLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        headerPanel.add(dateTimeLabel);

        return headerPanel;
    }

    private void switchMode() {
        if (darkMode) {
            PRIMARY_COLOR = PRIMARY_COLOR_DARK;
            PRIMARY_HOVER = PRIMARY_HOVER_DARK;
            SECONDARY_COLOR = SECONDARY_COLOR_DARK;
            SECONDARY_HOVER = SECONDARY_HOVER_DARK;
            BACKGROUND_COLOR = BACKGROUND_COLOR_DARK;
            CARD_COLOR = CARD_COLOR_DARK;
            TEXT_COLOR = TEXT_COLOR_DARK;
            TEXT_SECONDARY = TEXT_SECONDARY_DARK;
            ACCENT_COLOR = ACCENT_COLOR_DARK;
            ACCENT_HOVER = ACCENT_HOVER_DARK;
            BORDER_COLOR = BORDER_COLOR_DARK;
            COMPLETED_COLOR = COMPLETED_COLOR_DARK;
            COMPLETED_BG = COMPLETED_BG_DARK;
            HOVER_COLOR = HOVER_COLOR_DARK;
            SUCCESS_COLOR = SUCCESS_COLOR_DARK;
            WARNING_COLOR = WARNING_COLOR_DARK;
            ERROR_COLOR = ERROR_COLOR_DARK;
        } else {
            PRIMARY_COLOR = new Color(64, 115, 255);
            PRIMARY_HOVER = new Color(48, 95, 240);
            SECONDARY_COLOR = new Color(255, 105, 140);
            SECONDARY_HOVER = new Color(240, 90, 125);
            BACKGROUND_COLOR = new Color(245, 247, 250);
            CARD_COLOR = Color.WHITE;
            TEXT_COLOR = new Color(40, 42, 53);
            TEXT_SECONDARY = new Color(120, 124, 140);
            ACCENT_COLOR = new Color(100, 220, 180);
            ACCENT_HOVER = new Color(80, 200, 160);
            BORDER_COLOR = new Color(50, 50, 50);
            COMPLETED_COLOR = new Color(160, 170, 190);
            COMPLETED_BG = new Color(245, 248, 250);
            HOVER_COLOR = new Color(240, 243, 248);
            SUCCESS_COLOR = new Color(50, 200, 120);
            WARNING_COLOR = new Color(255, 180, 70);
            ERROR_COLOR = new Color(255, 90, 90);
        }

        // Update background
        getContentPane().setBackground(BACKGROUND_COLOR);
        if (headerPanel != null) {
            headerPanel.setBackground(darkMode ? CARD_COLOR_DARK : new Color(240, 245, 250));
            headerPanel.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(15, BORDER_COLOR, 2),
                    new EmptyBorder(20, 20, 20, 20)
            ));
        }
        if (centerPanel != null) centerPanel.setBackground(BACKGROUND_COLOR);
        if (inputPanel != null) inputPanel.setBackground(BACKGROUND_COLOR);
        if (taskListPanel != null) taskListPanel.setBackground(BACKGROUND_COLOR);
        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(15, BORDER_COLOR, 2),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }
        if (footerPanel != null) footerPanel.setBackground(BACKGROUND_COLOR);

        // Update UI components
        updateUI();
        updateDateTime();
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void createTaskListPanel() {
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setOpaque(false);
        taskListPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(15, BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Improve scrolling performance
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    private JPanel createProgressSection() {
        JPanel progressContainer = new JPanel();
        progressContainer.setLayout(new BoxLayout(progressContainer, BoxLayout.Y_AXIS));
        progressContainer.setOpaque(false);
        progressContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Progress Bar - Updated with new colors
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(SUCCESS_COLOR);
        progressBar.setBackground(darkMode ? CARD_COLOR_DARK : new Color(230, 235, 240));
        progressBar.setFont(PROGRESS_FONT);
        progressBar.setBorder(new RoundBorder(10, BORDER_COLOR, 1));
        progressBar.setPreferredSize(new Dimension(400, 30));
        progressBar.setMaximumSize(new Dimension(400, 30));

        JPanel progressWrapper = new JPanel();
        progressWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
        progressWrapper.setOpaque(false);
        progressWrapper.add(progressBar);
        progressContainer.add(progressWrapper);

        // Completed tasks label
        completedLabel = new JLabel("0 tasks completed", SwingConstants.CENTER);
        completedLabel.setFont(COMPLETED_FONT);
        completedLabel.setForeground(TEXT_COLOR);
        completedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        completedLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        progressContainer.add(completedLabel);

        return progressContainer;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(0, 100, 15, 100)); // Centering

        // Text field
        taskInput = new JTextField();
        taskInput.setFont(TASK_FONT);
        taskInput.setForeground(darkMode ? TEXT_FIELD_TEXT_COLOR_DARK : TEXT_FIELD_TEXT_COLOR);
        taskInput.setBackground(darkMode ? CARD_COLOR_DARK : Color.WHITE);
        taskInput.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(10, BORDER_COLOR, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        taskInput.setPreferredSize(new Dimension(300, 40));
        taskInput.setMaximumSize(new Dimension(300, 40));
        taskInput.addActionListener(e -> addTask());
        addPlaceholderToTextField(); // Custom placeholder logic

        // Use reusable button function
        addTaskButton = createStyledButton("Add Task", e -> addTask());
        addTaskButton.setPreferredSize(new Dimension(120, 40));

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(taskInput);
        centerPanel.add(addTaskButton);

        inputPanel.add(centerPanel, BorderLayout.CENTER);
        return inputPanel;
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text) {
            private boolean hover = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (hover) {
                    g2.setColor(darkMode ? ACCENT_HOVER_DARK : ACCENT_HOVER);
                } else {
                    g2.setColor(ACCENT_COLOR);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(this.getText(), g2);
                int x = (this.getWidth() - (int) r.getWidth()) / 2;
                int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2.drawString(this.getText(), x, y);

                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.addActionListener(action);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createTaskCard(Task task, int taskNumber) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setMaximumSize(new Dimension(700, 60));
        card.setPreferredSize(new Dimension(700, 60));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Improved task states - set background and border based on completion status
        if (task.isDone()) {
            card.setBackground(COMPLETED_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(12, COMPLETED_COLOR, 1),
                    new EmptyBorder(10, 18, 10, 18)
            ));
        } else {
            card.setBackground(CARD_COLOR);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(12, BORDER_COLOR, 1),
                    new EmptyBorder(10, 18, 10, 18)
            ));

            // Add hover effect only for incomplete tasks
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(HOVER_COLOR);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBackground(CARD_COLOR);
                }
            });
        }

        // Left panel with checkbox and task
        JPanel leftPanel = createTaskLeftPanel(task, taskNumber);

        // Right panel with action buttons
        JPanel buttonPanel = createTaskButtonPanel(task);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createTaskLeftPanel(Task task, int taskNumber) {
        JPanel leftPanel = new JPanel(new BorderLayout(8, 0));
        leftPanel.setOpaque(false);

        // Task number
        JLabel numberLabel = new JLabel(String.valueOf(taskNumber) + ".");
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        numberLabel.setForeground(COMPLETED_COLOR);
        numberLabel.setPreferredSize(new Dimension(25, 20));

        // Improved checkbox
        JCheckBox checkBox = new ImprovedCheckBox(task.isDone());
        checkBox.addActionListener(e -> toggleTaskComplete(task));

        // Task text with better styling
        JLabel taskLabel = createTaskLabel(task);

        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.setOpaque(false);
        checkboxPanel.add(checkBox);

        leftPanel.add(numberLabel, BorderLayout.WEST);
        leftPanel.add(checkboxPanel, BorderLayout.CENTER);
        leftPanel.add(taskLabel, BorderLayout.EAST);

        return leftPanel;
    }

    private JPanel createTaskButtonPanel(Task task) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        JButton editButton = createStyledButton("Edit", e -> editTask(task));
        editButton.setPreferredSize(new Dimension(70, 34));
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        editButton.setToolTipText("Edit task");

        JButton deleteButton = createStyledButton("Delete", e -> deleteTask(task));
        deleteButton.setPreferredSize(new Dimension(80, 34));
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteButton.setToolTipText("Delete task");
        deleteButton.setBackground(ERROR_COLOR);

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        return buttonPanel;
    }

    private JLabel createTaskLabel(Task task) {
        JLabel taskLabel = new JLabel(task.getText()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (task.isDone()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(COMPLETED_COLOR);
                    g2.setStroke(new BasicStroke(2));
                    int y = getHeight() / 2;
                    g2.drawLine(0, y, getWidth(), y);
                    g2.dispose();
                }
            }
        };

        taskLabel.setFont(TASK_FONT);
        taskLabel.setBorder(new EmptyBorder(0, 10, 0, 10));

        if (task.isDone()) {
            taskLabel.setForeground(COMPLETED_COLOR);
        } else if (task.getText().toLowerCase().contains("urgent")) {
            taskLabel.setForeground(ERROR_COLOR);
            taskLabel.setFont(taskLabel.getFont().deriveFont(Font.BOLD));
        } else if (task.getText().toLowerCase().contains("important")) {
            taskLabel.setForeground(WARNING_COLOR);
            taskLabel.setFont(taskLabel.getFont().deriveFont(Font.BOLD));
        } else {
            taskLabel.setForeground(TEXT_COLOR);
        }

        return taskLabel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel creditLabel = new JLabel("Created by Abdul Rafay", SwingConstants.CENTER);
        creditLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        creditLabel.setForeground(TEXT_SECONDARY);
        creditLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel detailsLabel = new JLabel("Contact: abdulrafay1402@gmail.com | GitHub: github.com/abdulrafay1402", SwingConstants.CENTER);
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsLabel.setForeground(TEXT_SECONDARY);
        detailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        footerPanel.add(creditLabel);
        footerPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        footerPanel.add(detailsLabel);

        return footerPanel;
    }

    private void addPlaceholderToTextField() {
        final String placeholder = "Enter a new task...";
        taskInput.setText(placeholder);
        taskInput.setForeground(Color.GRAY);

        taskInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (taskInput.getText().equals(placeholder)) {
                    taskInput.setText("");
                    taskInput.setForeground(darkMode ? TEXT_FIELD_TEXT_COLOR_DARK : TEXT_FIELD_TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (taskInput.getText().isEmpty()) {
                    taskInput.setText(placeholder);
                    taskInput.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void addTask() {
        String text = taskInput.getText().trim();
        final String placeholder = "Enter a new task...";
        if (!text.isEmpty() && !text.equals(placeholder)) {
            Task newTask = new Task(text);
            tasks.add(newTask);
            taskInput.setText(placeholder);
            taskInput.setForeground(Color.GRAY);
            updateUI();
            taskInput.requestFocusInWindow();
        }
    }

    private void deleteTask(Task task) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this task?\n\"" + task.getText() + "\"",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            tasks.remove(task);
            updateUI();
            saveTasks();
        }
    }

    private void toggleTaskComplete(Task task) {
        task.setDone(!task.isDone());
        updateUI();
        saveTasks();
    }

    private void editTask(Task task) {
        String newText = (String) JOptionPane.showInputDialog(
                this,
                "Edit your task:",
                "Edit Task",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                task.getText()
        );

        if (newText != null && !newText.trim().isEmpty()) {
            task.setText(newText.trim());
            updateUI();
            saveTasks();
        }
    }

    private void updateUI() {
        SwingUtilities.invokeLater(() -> {
            if (taskListPanel != null) {
                taskListPanel.removeAll();

                if (tasks.isEmpty()) {
                    JPanel emptyPanel = createEmptyStatePanel();
                    taskListPanel.add(emptyPanel);
                } else {
                    for (int i = 0; i < tasks.size(); i++) {
                        if (i > 0) {
                            taskListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                        }
                        taskListPanel.add(createTaskCard(tasks.get(i), i + 1));
                    }
                    taskListPanel.add(Box.createVerticalGlue());
                }

                updateProgressBar();
                taskListPanel.revalidate();
                taskListPanel.repaint();
            }
        });
    }

    private JPanel createEmptyStatePanel() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setOpaque(false);
        emptyPanel.setBorder(new EmptyBorder(50, 20, 50, 20));

        JLabel emptyLabel = new JLabel("No tasks yet!", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        emptyLabel.setForeground(COMPLETED_COLOR);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptySubLabel = new JLabel("Add your first task above to get started", SwingConstants.CENTER);
        emptySubLabel.setFont(SUBTITLE_FONT);
        emptySubLabel.setForeground(COMPLETED_COLOR);
        emptySubLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        emptyPanel.add(emptyLabel);
        emptyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        emptyPanel.add(emptySubLabel);

        return emptyPanel;
    }

    private void updateProgressBar() {
        long completedTasks = tasks.stream().filter(Task::isDone).count();
        long totalTasks = tasks.size();

        if (totalTasks > 0) {
            int percentage = (int) (100 * completedTasks / totalTasks);
            progressBar.setValue(percentage);
            progressBar.setString(completedTasks + "/" + totalTasks + " completed (" + percentage + "%)");
            completedLabel.setText(completedTasks + " of " + totalTasks + " tasks completed");
        } else {
            progressBar.setValue(0);
            progressBar.setString("No tasks");
            completedLabel.setText("No tasks yet");
        }
    }

    private void updateDateTime() {
        Timer timer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy - hh:mm:ss a");
            dateTimeLabel.setText(now.format(formatter));
            dateTimeLabel.setForeground(TEXT_SECONDARY);
        });
        timer.start();
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
            ArrayList<Task> loadedTasks = (ArrayList<Task>) ois.readObject();
            tasks.clear();
            tasks.addAll(loadedTasks);
        } catch (FileNotFoundException e) {
            System.out.println("No existing task file found - starting fresh");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error loading saved tasks. Starting with empty list.",
                    "Load Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error saving tasks: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ToDoListPureSwing app = new ToDoListPureSwing();
            app.setVisible(true);
        });
    }

    // Custom Components
    private static class RoundBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        private final int thickness;

        public RoundBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }

    private class ImprovedCheckBox extends JCheckBox {
        public ImprovedCheckBox(boolean selected) {
            super();
            setSelected(selected);
            setOpaque(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(24, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw checkbox background
            if (isSelected()) {
                g2.setColor(PRIMARY_COLOR);
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
            } else {
                g2.setColor(darkMode ? CARD_COLOR_DARK : Color.WHITE);
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);
            }

            // Draw border
            g2.setColor(isSelected() ? PRIMARY_COLOR : BORDER_COLOR);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 6, 6);

            // Draw checkmark
            if (isSelected()) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int[] xPoints = {6, 10, 18};
                int[] yPoints = {12, 16, 8};
                g2.drawPolyline(xPoints, yPoints, 3);
            }

            g2.dispose();
        }
    }

    static class Task implements Serializable {
        private static final long serialVersionUID = 1L;
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
            return text;
        }
    }
}