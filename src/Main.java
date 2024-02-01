import manager.Managers;
import manager.TaskManager;
import tasks.Status;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

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
        manager.getEpicById(4);
        manager.getTaskById(2);
        manager.getTaskById(1);
        manager.getSubtaskById(5);
        manager.getSubtaskById(7);
        manager.getSubtaskById(7);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);
        manager.getSubtaskById(6);
        manager.getSubtaskById(6);
        manager.getSubtaskById(6);
        System.out.println(manager.getHistory());

        manager.deleteTaskById(1);
        manager.deleteEpicById(4);
        manager.deleteSubtaskById(7);
        System.out.println(manager.getHistory());

        manager.getEpicById(3);
        System.out.println(manager.getHistory());

    }
}
