package manager;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Integer id : tasks.keySet()) {
            allTasks.add(tasks.get(id));
        }
        return allTasks;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            allEpics.add(epics.get(id));
        }
        return allEpics;
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void createEpic(Epic epic) {
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            epic.setStatus(oldEpic.getStatus());
            epic.setIdSubtasks(oldEpic.getIdSubtasks());
            epics.put(epic.getId(), epic);
        }
    }

    public void deleteEpicById(int id) {
        if (epics.get(id) != null) {
            for (Integer idSubtask : epics.get(id).getIdSubtasks()) {
                subtasks.remove(idSubtask);
            }
            epics.remove(id);
        }
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Integer id : subtasks.keySet()) {
            allSubtasks.add(subtasks.get(id));
        }
        return allSubtasks;
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubtasks().clear();
            updateEpicStatus(epic);
        }
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtask.getId(), subtask);
            epic.getIdSubtasks().add(subtask.getId());
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epics.get(subtask.getEpicId()));
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getIdSubtasks().remove(Integer.valueOf(id));
            updateEpicStatus(epic);
            subtasks.remove(id);
        }
    }

    public ArrayList<Subtask> getAllSubtasksByEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existEpic = epics.get(epic.getId());
            ArrayList<Subtask> allSubtasksByEpic = new ArrayList<>();
            for (Integer idSubtask : existEpic.getIdSubtasks()) {
                allSubtasksByEpic.add(subtasks.get(idSubtask));
            }
            return allSubtasksByEpic;
        }
        return null;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getIdSubtasks().isEmpty()) {
            epic.setStatus("NEW");
            return;
        }

        int countDone = 0;
        for (Integer idSubtask : epic.getIdSubtasks()) {
            Subtask subtask = subtasks.get(idSubtask);
            if (subtask.getStatus().equals("IN_PROGRESS")) {
                epic.setStatus("IN_PROGRESS");
                return;
            } else {
                if (subtask.getStatus().equals("DONE")) {
                    countDone++;
                }
            }
        }
        if (countDone == epic.getIdSubtasks().size()) {
            epic.setStatus("DONE");
        } else if (countDone == 0) {
            epic.setStatus("NEW");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }

}
