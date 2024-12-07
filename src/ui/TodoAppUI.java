package ui;

import model.Task;
import service.TaskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class TodoAppUI {
    private final TaskService taskService;
    private JPanel centerPanel; // 중앙 패널
    private JPanel tasksPanel; // 작업 리스트를 표시하는 패널
    private JPanel addTaskPanel; // 할 일 추가 UI 패널
    private JTextField taskInputField;
    private JTextField dueDateField;
    private JComboBox<String> priorityField;
    private JLabel dateLabel;
    private JLabel[] timeBlocks;
    private JButton selectedCategoryButton;
    private JTextField searchField;
    private Font cuteFont = new Font("Tahoma", Font.PLAIN, 14);




    public TodoAppUI() {
        this.taskService = new TaskService();
        initializeUI();
        loadTasks();
    }

    private void initializeUI() {
        JFrame frame = new JFrame("To-Do List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        // 상단 패널 (날짜 및 시간)
        JPanel topPanel = createTopPanel();
        frame.add(topPanel, BorderLayout.NORTH);

        // 중앙 패널 (카테고리 버튼 및 할 일 리스트)
        centerPanel = createCenterPanel();
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(126, 121, 121, 255));
        panel.setBorder(new EmptyBorder(20, 10, 0, 10));

        // 날짜 라벨
        dateLabel = new JLabel();
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 시간 패널
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        timePanel.setBackground(new Color(119, 122, 122, 223));

        // 시간 블록
        timeBlocks = new JLabel[6]; // 시, 분, 초 (각각 두 블록씩)
        for (int i = 0; i < timeBlocks.length; i++) {
            timeBlocks[i] = new JLabel("0");
            timeBlocks[i].setOpaque(true);
            timeBlocks[i].setBackground(Color.BLACK);
            timeBlocks[i].setForeground(Color.WHITE);
            timeBlocks[i].setFont(new Font("Tahoma", Font.BOLD, 20));
            timeBlocks[i].setHorizontalAlignment(SwingConstants.CENTER);
            timeBlocks[i].setPreferredSize(new Dimension(30, 40));
            timePanel.add(timeBlocks[i]);

            // 시, 분, 초 사이에 ":" 추가
            if (i == 1 || i == 3) {
                JLabel colonLabel = new JLabel(":");
                colonLabel.setForeground(Color.WHITE);
                colonLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
                timePanel.add(colonLabel);
            }
        }

        panel.add(dateLabel, BorderLayout.NORTH);
        panel.add(timePanel, BorderLayout.CENTER);

        updateDateTime(); // 첫 번째 업데이트

        // 실시간으로 날짜와 시간 업데이트
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDateTime();
            }
        }, 0, 1000); // 1초 간격

        return panel;
    }

    private void updateDateTime() {
        SwingUtilities.invokeLater(() -> {
            // 현재 날짜와 시간 가져오기
            LocalDateTime now = LocalDateTime.now();
            String currentDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String currentTime = now.format(DateTimeFormatter.ofPattern("HHmmss")); // 시, 분, 초 숫자만 가져옴

            // 날짜 업데이트
            dateLabel.setText(currentDate);

            // 시간 블록 업데이트
            for (int i = 0; i < timeBlocks.length; i++) {
                timeBlocks[i].setText(String.valueOf(currentTime.charAt(i)));
            }
        });
    }


    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(158, 154, 147));

        // 상단 패널: 카테고리 버튼과 검색 패널을 포함
        JPanel topPanel = new JPanel(new GridLayout(2, 1)); // 2행 1열 레이아웃으로 설정
        topPanel.setBackground(new Color(119, 122, 122, 223));

        // 카테고리 버튼 패널
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // 가운데 정렬
        categoryPanel.setBackground(new Color(119, 122, 122, 223));

        JButton allButton = createCategoryButton("ALL");
        JButton activeButton = createCategoryButton("ACTIVE");
        JButton completedButton = createCategoryButton("COMPLETED");

        JButton addTaskButton = new JButton("Add");
        addTaskButton.setBackground(new Color(0, 0, 0));
        addTaskButton.setForeground(Color.WHITE);
        addTaskButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        addTaskButton.setFocusPainted(false);
        addTaskButton.addActionListener(e -> showAddTaskPanel());

        categoryPanel.add(allButton);
        categoryPanel.add(activeButton);
        categoryPanel.add(completedButton);
        categoryPanel.add(addTaskButton);

        // 검색 패널
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // 가운데 정렬
        searchPanel.setBackground(new Color(119, 122, 122, 223));

        searchField = new JTextField(15); // 검색 입력 필드
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(0, 0, 0));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchTasks(searchField.getText().trim())); // 검색 동작 추가

        searchPanel.add(new JLabel("Find your todo 🔎"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 카테고리 버튼과 검색 패널을 상단 패널에 추가
        topPanel.add(categoryPanel); // 첫 번째 행: 카테고리 버튼
        topPanel.add(searchPanel);   // 두 번째 행: 검색창

        panel.add(topPanel, BorderLayout.NORTH); // 상단 패널 추가

        // 작업 리스트 패널
        tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }



    private void searchTasks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a keyword to search!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // 검색어가 없으면 실행하지 않음
        }

        // 검색어로 작업 필터링
        tasksPanel.removeAll();
        for (Task task : taskService.getAllTasks()) {
            if (task.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                tasksPanel.add(createTaskBox(task));
            }
        }

        tasksPanel.revalidate();
        tasksPanel.repaint();
    }




    private void showAddTaskPanel() {
        if (addTaskPanel == null) {
            addTaskPanel = createAddTaskPanel();
            centerPanel.add(addTaskPanel, BorderLayout.SOUTH);
        }
        addTaskPanel.setVisible(true);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void hideAddTaskPanel() {
        if (addTaskPanel != null) {
            addTaskPanel.setVisible(false);
        }
    }

    private JPanel createAddTaskPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBackground(new Color(245, 222, 179));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        taskInputField = new JTextField(20);
        dueDateField = new JTextField(20);
        priorityField = new JComboBox<>(new String[]{"High", "Medium", "Low"});

        JButton addButton = new JButton("ADD");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        addButton.addActionListener(e -> addTask());

        JButton cancelButton = new JButton("CANCEL");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        cancelButton.addActionListener(e -> hideAddTaskPanel());

        panel.add(new JLabel("TITLE"));
        panel.add(taskInputField);
        panel.add(new JLabel("DUE DATE (YYYY-MM-DD)"));
        panel.add(dueDateField);
        panel.add(new JLabel("PRIORITY LEVEL"));
        panel.add(priorityField);
        panel.add(addButton);
        panel.add(cancelButton);

        return panel;
    }

    private void addTask() {
        String title = taskInputField.getText().trim();
        String dueDateText = dueDateField.getText().trim();
        String priority = (String) priorityField.getSelectedItem();

        if (title.isEmpty() || dueDateText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "INPUT ERROR!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateText);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "DUE DATE ERROR! (YYYY-MM-DD)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int priorityLevel = priority.equals("High") ? 1 : (priority.equals("Medium") ? 2 : 3);

        // 수정된 부분: description 제거
        Task newTask = new Task(title, dueDate, priorityLevel);

        taskService.addTask(newTask);

        taskInputField.setText("");
        dueDateField.setText("");
        priorityField.setSelectedIndex(0);

        hideAddTaskPanel();
        loadTasks();
    }


    private JButton createCategoryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(220, 106, 106));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.addActionListener(e -> {
            // 버튼 클릭 시 모든 버튼의 색상 초기화
            resetCategoryButtonColors();

            // 현재 버튼 강조 표시
            button.setBackground(new Color(255, 255, 255)); // 강조 색상
            button.setForeground(new Color(240, 128, 128)); // 텍스트 강조 색상
            selectedCategoryButton = button; // 현재 버튼 저장

            // 검색 필드 초기화
            searchField.setText("");

            // 각 카테고리 로드
            if (text.equals("ALL")) {
                loadTasks();
            } else if (text.equals("ACTIVE")) {
                loadActiveTasks();
            } else if (text.equals("COMPLETED")) {
                loadCompletedTasks();




            }
        });

        return button;
    }


    private void resetCategoryButtonColors() {
        if (selectedCategoryButton != null) {
            selectedCategoryButton.setBackground(new Color(240, 128, 128)); // 기본 배경색
            selectedCategoryButton.setForeground(Color.WHITE); // 기본 텍스트 색상
        }
    }



    private void loadTasks() {
        tasksPanel.removeAll();

        // 작업 리스트 가져오기
        java.util.List<Task> tasks = taskService.getAllTasks();

        // 정렬: 완료되지 않은 작업이 먼저, 그다음 완료된 작업
        tasks.sort(Comparator
                .comparing(Task::isCompleted) // 완료 여부를 기준으로 정렬
                .thenComparing(Task::getDueDate)); // 완료 상태가 같으면 기한 기준으로 정렬

        // 정렬된 리스트로 작업 추가
        for (Task task : tasks) { // 정렬된 tasks 사용
            tasksPanel.add(createTaskBox(task));
        }

        tasksPanel.revalidate();
        tasksPanel.repaint();
    }

    private void loadCompletedTasks() {
        tasksPanel.removeAll();

        // 작업 리스트 가져오기 및 필터링
        java.util.List<Task> tasks = taskService.getAllTasks();
        tasks.stream()
                .filter(Task::isCompleted) // 완료된 작업만 필터링
                .sorted(Comparator.comparing(Task::getDueDate)) // Due Date 기준 정렬
                .forEach(task -> tasksPanel.add(createTaskBox(task))); // 작업 추가

        tasksPanel.revalidate();
        tasksPanel.repaint();
    }



    private void loadActiveTasks() {
        tasksPanel.removeAll();

        // 작업 리스트 가져오기 및 필터링
        java.util.List<Task> tasks = taskService.getAllTasks();
        tasks.stream()
                .filter(task -> !task.isCompleted()) // 완료되지 않은 작업만 필터링
                .sorted(Comparator.comparing(Task::getDueDate)) // Due Date 기준 정렬
                .forEach(task -> tasksPanel.add(createTaskBox(task))); // 작업 추가

        tasksPanel.revalidate();
        tasksPanel.repaint();
    }


    private JPanel createTaskBox(Task task) {
        JPanel taskPanel = new JPanel(new BorderLayout());

        // 창의 너비에 맞추도록 크기를 동적으로 설정
        taskPanel.setPreferredSize(new Dimension(centerPanel.getWidth(), 100)); // 창의 너비에 맞추기
        taskPanel.setMaximumSize(new Dimension(centerPanel.getWidth(), 100)); // 창의 너비에 맞추기

        taskPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // 기한에 따라 색상 설정
        LocalDate now = LocalDate.now();
        if (task.getDueDate().isBefore(now)) {
            taskPanel.setBackground(new Color(223, 83, 83, 255)); // 기한이 지난 작업 (빨간색)
        } else if (!task.getDueDate().isAfter(now.plusDays(2))) {
            taskPanel.setBackground(new Color(255, 181, 99)); // 기한이 1~2일 남은 작업 (주황색)
        } else {
            taskPanel.setBackground(new Color(245, 245, 245)); // 일반 작업
        }

        // 왼쪽: 체크박스
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(task.isCompleted());
        checkBox.setBackground(taskPanel.getBackground());
        checkBox.setPreferredSize(new Dimension(30, 30));
        checkBox.setFont(new Font("Tahoma", Font.PLAIN, 20));
        checkBox.addActionListener(e -> {
            task.setCompleted(checkBox.isSelected());
            taskService.updateTask(task);
            loadTasks();
        });
        taskPanel.add(checkBox, BorderLayout.WEST);

        // 중앙: 작업 제목, Due Date 및 Priority Level
        JPanel textPanel = new JPanel(new GridLayout(3, 1));
        textPanel.setBackground(taskPanel.getBackground());

        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        JLabel dueDateLabel = new JLabel("Due Date: " + task.getDueDate());
        dueDateLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        dueDateLabel.setForeground(new Color(81, 79, 79));

        String priorityText = switch (task.getPriority()) {
            case 1 -> "High";
            case 2 -> "Medium";
            case 3 -> "Low";
            default -> "Unknown";
        };
        JLabel priorityLabel = new JLabel("Priority: " + priorityText);
        priorityLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        priorityLabel.setForeground(new Color(100, 100, 255));

        textPanel.add(titleLabel);
        textPanel.add(dueDateLabel);
        textPanel.add(priorityLabel);

        taskPanel.add(textPanel, BorderLayout.CENTER);

        // 오른쪽: 수정 및 삭제 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(taskPanel.getBackground());

        // 수정 버튼
        JButton editButton = new JButton("Edit");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        editButton.setFocusPainted(false);
        editButton.setBorderPainted(false);
        editButton.addActionListener(e -> editTask(task));
        buttonPanel.add(editButton);

        // 삭제 버튼
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(255, 99, 71));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> {
            taskService.deleteTask(task.getId());
            loadTasks();
        });
        buttonPanel.add(deleteButton);

        taskPanel.add(buttonPanel, BorderLayout.EAST);

        return taskPanel;
    }



    private void editTask(Task task) {
        JPanel editPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        JTextField titleField = new JTextField(task.getTitle());
        JTextField dueDateField = new JTextField(task.getDueDate().toString());
        JComboBox<String> priorityField = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        priorityField.setSelectedIndex(task.getPriority() - 1);

        editPanel.add(new JLabel("Title:"));
        editPanel.add(titleField);
        editPanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        editPanel.add(dueDateField);
        editPanel.add(new JLabel("Priority:"));
        editPanel.add(priorityField);

        int result = JOptionPane.showConfirmDialog(null, editPanel, "Edit Task",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String newTitle = titleField.getText().trim();
                LocalDate newDueDate = LocalDate.parse(dueDateField.getText().trim());
                int newPriority = priorityField.getSelectedItem().equals("High") ? 1
                        : priorityField.getSelectedItem().equals("Medium") ? 2 : 3;

                if (newTitle.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                task.setTitle(newTitle);
                task.setDueDate(newDueDate);
                task.setPriority(newPriority);

                taskService.updateTask(task); // 업데이트 작업
                loadTasks(); // UI 갱신
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}




