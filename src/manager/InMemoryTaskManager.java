package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizetedTasks = new TreeSet<>();

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        for (Integer id : tasks.keySet()) {
            allTasks.add(tasks.get(id));
        }
        return allTasks;
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizetedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void createTask(Task task) {
        validateTimeTask(task);
        tasks.put(task.getId(), task);
        prioritizetedTasks.add(task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            validateTimeTask(task);
            prioritizetedTasks.remove(tasks.get(task.getId()));
            prioritizetedTasks.add(task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        prioritizetedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> allEpics = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            allEpics.add(epics.get(id));
        }
        return allEpics;
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            prioritizetedTasks.remove(epic);
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            prioritizetedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void createEpic(Epic epic) {
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic oldEpic = epics.get(epic.getId());
            epic.setStatus(oldEpic.getStatus());
            epic.setIdSubtasks(oldEpic.getIdSubtasks());
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.get(id) != null) {
            for (Integer idSubtask : epics.get(id).getIdSubtasks()) {
                prioritizetedTasks.remove(subtasks.get(idSubtask));
                historyManager.remove(idSubtask);
                subtasks.remove(idSubtask);
            }
            prioritizetedTasks.remove(epics.get(id));
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> allSubtasks = new ArrayList<>();
        for (Integer id : subtasks.keySet()) {
            allSubtasks.add(subtasks.get(id));
        }
        return allSubtasks;
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            prioritizetedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getIdSubtasks().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        validateTimeTask(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtask.getId(), subtask);
            epic.getIdSubtasks().add(subtask.getId());
            prioritizetedTasks.add(subtask);
            updateEpicTime(epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        validateTimeTask(subtask);
        if (subtasks.containsKey(subtask.getId())) {
            prioritizetedTasks.remove(subtasks.get(subtask.getId()));
            prioritizetedTasks.add(subtask);
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epics.get(subtask.getEpicId()));
            updateEpicTime(getEpicById(subtask.getEpicId()));
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.getIdSubtasks().remove(Integer.valueOf(id));
            updateEpicStatus(epic);
            prioritizetedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
            updateEpicTime(epic);
        }
    }

    @Override
    public List<Subtask> getAllSubtasksByEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existEpic = epics.get(epic.getId());
            List<Subtask> allSubtasksByEpic = new ArrayList<>();
            for (Integer idSubtask : existEpic.getIdSubtasks()) {
                allSubtasksByEpic.add(subtasks.get(idSubtask));
            }
            return allSubtasksByEpic;
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizetedTasks;
    }


    private void updateEpicStatus(Epic epic) {
        if (epic.getIdSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        int countDone = 0;
        for (Integer idSubtask : epic.getIdSubtasks()) {
            Subtask subtask = subtasks.get(idSubtask);
            if (subtask.getStatus().equals(Status.IN_PROGRESS)) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            } else {
                if (subtask.getStatus().equals(Status.DONE)) {
                    countDone++;
                }
            }
        }
        if (countDone == epic.getIdSubtasks().size()) {
            epic.setStatus(Status.DONE);
        } else if (countDone == 0) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected void updateEpicTime(Epic epic) {
        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;
        long duration = 0;

        for (Subtask subtask : getAllSubtasksByEpic(epic)) {
            LocalDateTime subTaskStartTime = subtask.getStartTime();
            LocalDateTime subTaskEndTime = subtask.getEndTime();
            if (subTaskStartTime != null) {
                if (earliestStartTime == null || subTaskStartTime.isBefore(earliestStartTime)) {
                    earliestStartTime = subTaskStartTime;
                }
            }
            if (subTaskEndTime != null) {
                if (latestEndTime == null || subTaskEndTime.isAfter(latestEndTime)) {
                    latestEndTime = subTaskEndTime;
                }
            }
            if (subtask.getDuration() != null) {
                duration += subtask.getDuration();
            }
        }

        epic.setStartTime(earliestStartTime);
        epic.setEndTime(latestEndTime);
        epic.setDuration(duration);
    }

    private void validateTimeTask(Task newTask) {
        Set<Task> prioritizedTasks = getPrioritizedTasks();
        for (Task task : prioritizedTasks) {
            if (task.getId() == newTask.getId()) {
                continue;
            }
            if (hasTimeOverlap(newTask, task)) {
                throw new TaskTimeOverlapException("Новая задача с id " + newTask.getId() + " по времени пересекается с существующей задачей c id " + task.getId());
            }
        }
    }

    private boolean hasTimeOverlap(Task newTask, Task oldTask) {
        LocalDateTime newTaskStartTime = newTask.getStartTime();
        LocalDateTime oldTaskTaskStartTime = oldTask.getStartTime();
        if (newTaskStartTime == null || oldTaskTaskStartTime == null) {
            return false;
        }
        LocalDateTime newTaskEndTime = newTaskStartTime.plusMinutes(newTask.getDuration());
        LocalDateTime oldTaskTaskEndTime = oldTaskTaskStartTime.plusMinutes(oldTask.getDuration());
        return newTaskStartTime.isBefore(oldTaskTaskEndTime) && oldTaskTaskStartTime.isBefore(newTaskEndTime);
    }

}
