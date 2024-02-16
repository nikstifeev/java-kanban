package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        Task task1 = new Task("Пройти 1ый урок", "Инкапсуляция"); // 1
        manager.createTask(task1);
        Task task2 = new Task("Пройти 2ой урок", "Наследование"); // 2
        manager.createTask(task2);

        Epic epic1 = new Epic("Посетить форум", "В ноябре"); // 3
        manager.createEpic(epic1);
        Epic epic2 = new Epic("Закончить курс", "Java-разаботчик"); // 4
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Подготовить доклад", "На 7 минут", 3); // 5
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подготовить презентацию", "На 9 слайдов", 3); // 6
        manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Написать проект", "Дипломный", 3); // 7
        manager.createSubtask(subtask3);


        manager.getTaskById(1);
        manager.getEpicById(3);
        manager.getSubtaskById(7);
        manager.getEpicById(3);

        FileBackedTasksManager manager2 = loadFromFile(path);
        System.out.println(manager2.getAllTasks());
        System.out.println(manager2.getAllEpics());
        System.out.println(manager2.getAllSubtasks());
        System.out.println();
        System.out.println(manager2.getHistory());
        manager2.createTask(new Task("новый таск", "тест таска"));
        manager2.createEpic(new Epic("новый эпик", "тест эпика"));
        manager2.getEpicById(9);
    }


    private final Path path;

    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(path)))) {
            writer.write("id,type,name,status,description,epic\n");
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
                    if (task.getType() == TypeTask.TASK) {
                        manager.tasks.put(task.getId(), task);
                    } else if (task.getType() == TypeTask.EPIC) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task.getType() == TypeTask.SUBTASK) {
                        manager.subtasks.put(task.getId(), (Subtask) task);
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

            Task.setCount(Collections.max(ids));
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
