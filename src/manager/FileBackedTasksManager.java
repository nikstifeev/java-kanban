package manager;

import manager.exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main(String[] args) throws IOException {

        Path path = Paths.get("tasks_file.csv");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        FileBackedTasksManager manager = new FileBackedTasksManager(path);

        manager.createTask(new Task("Пройти 1ый урок", "Инкапсуляция")); // 1
        manager.createTask(new Task("Пройти 2ой урок", "Наследование", 90, LocalDateTime.of(2024, 2, 23, 13, 30, 0))); // 2

        manager.createEpic(new Epic("Посетить форум", "В ноябре")); // 3
        manager.createEpic(new Epic("Закончить курс", "Java-разаботчик")); // 4

        manager.createSubtask(new Subtask("Подготовить доклад", "На 7 минут", 3)); // 5
        manager.createSubtask(new Subtask("Подготовить презентацию", "На 9 слайдов", 3, 80, LocalDateTime.of(2024, 2, 22, 18, 0, 0))); // 6
        manager.createSubtask(new Subtask("Написать проект", "Дипломный", 3, 77, LocalDateTime.of(2024, 2, 22, 10, 30, 0))); // 7

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getSubtaskById(5);
        manager.getEpicById(3);
        manager.getEpicById(4);

        System.out.println("___список задач в порядке приоритета___");
        for (Task prioritizedTask : manager.getPrioritizedTasks()) {
            System.out.println(prioritizedTask);
        }
        System.out.println();

        FileBackedTasksManager manager2 = loadFromFile(path);

        System.out.println("___вывод всех задач после прочтения файла___");
        System.out.println(manager2.getAllTasks());
        System.out.println(manager2.getAllEpics());
        System.out.println(manager2.getAllSubtasks());
        System.out.println();

        manager2.createTask(new Task("новый таск", "тест таска", 15, LocalDateTime.of(2024, 2, 22, 13, 45, 0))); // 8
        manager2.createEpic(new Epic("новый эпик", "тест эпика")); // 9
        manager2.createSubtask(new Subtask("новый сабтаск", "тест сабтаска", 9, 12, LocalDateTime.of(2024, 2, 20, 22, 22, 0))); // 10

        manager2.getEpicById(9);

        System.out.println("___вывод истории после прочтения файла___");
        System.out.println(manager2.getHistory());
        System.out.println();

        System.out.println("___список задач в порядке приоритета после прочтения файла___");
        for (Task prioritizedTask : manager2.getPrioritizedTasks()) {
            System.out.println(prioritizedTask);
        }
        System.out.println();

        System.out.println("___время старта и завершения всех эпиков___");
        for (Epic epic : manager2.epics.values()) {
            System.out.println(epic + ". Время завершения эпика: " + epic.getEndTime());
        }
    }


    private final Path path;

    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(path)))) {
            writer.write("id,type,name,status,description,duration,startTime,epic\n");
            List<String> totalList = new ArrayList<>();
            for (Task task : super.getAllTasks()) {
                totalList.add(task.toString() + "\n");
            }
            for (Epic epic : super.getAllEpics()) {
                totalList.add(epic.toString() + "\n");
            }
            for (Subtask subtask : super.getAllSubtasks()) {
                totalList.add(subtask.toString() + "\n");
            }
            totalList.add("\n");
            totalList.add(CSVFormat.historyToString(historyManager));
            for (String line : totalList) {
                writer.write(line);
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка записи файла");
        }
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager manager = new FileBackedTasksManager(path);
        try (BufferedReader br = new BufferedReader(new FileReader(String.valueOf(path), StandardCharsets.UTF_8))) {
            br.readLine();
            List<Integer> ids = new ArrayList<>();

            while (br.ready()) {
                String line = br.readLine();
                if (!line.isBlank()) {
                    Task task = CSVFormat.fromString(line);
                    switch (task.getType()) {
                        case TASK:
                            manager.tasks.put(task.getId(), task);
                            manager.prioritizetedTasks.add(task);
                            break;
                        case EPIC:
                            manager.epics.put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            Subtask subtask = (Subtask) task;
                            Epic epic = manager.epics.get(subtask.getEpicId());
                            manager.subtasks.put(subtask.getId(), subtask);
                            manager.prioritizetedTasks.add(subtask);
                            if (epic != null) {
                                epic.getIdSubtasks().add(subtask.getId());
                                manager.updateEpicTime(epic);
                            }
                            break;
                    }
                    ids.add(task.getId());
                } else {
                    break;
                }
            }
            List<Integer> history = CSVFormat.historyFromString(br.readLine());
            Collections.reverse(history);
            for (Integer id : history) {
                if (manager.tasks.containsKey(id)) {
                    manager.historyManager.add(manager.tasks.get(id));
                } else if (manager.epics.containsKey(id)) {
                    manager.historyManager.add(manager.epics.get(id));
                } else if (manager.subtasks.containsKey(id)) {
                    manager.historyManager.add(manager.subtasks.get(id));
                }
            }

            if (!ids.isEmpty()) {
                Task.setCount(Collections.max(ids));
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("Ошибка загрузки файла");
        }
        return manager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }
}
