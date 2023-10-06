import manager.Manager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("Пройти 1ый урок", "Инкапсуляция", "NEW");
        manager.createTask(task1);
        Task task2 = new Task("Пройти 2ой урок", "Наследование", "NEW");
        manager.createTask(task2);

        Epic epic1 = new Epic("Посетить форум", "В ноябре");
        manager.createEpic(epic1);
        Epic epic2 = new Epic("Закончить курс", "Java-разаботчик");
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Подготовить доклад", "На 7 минут", "NEW", 3);
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подготовить презентацию", "На 9 слайдов", "NEW", 3);
        manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Написать проект", "Дипломный", "NEW", 4);
        manager.createSubtask(subtask3);

        manager.updateTask(new Task("Пройти 1ый урок", "Циклы", "DONE", 1));
        manager.updateSubtask(new Subtask("Подготовить доклад", "На 7 минут", "DONE", 3, 5));
        manager.updateSubtask(new Subtask("Подготовить презентацию", "На 9 слайдов", "IN_PROGRESS", 3, 6));
        manager.updateSubtask(new Subtask("Написать проект", "Дипломный", "DONE", 4, 7));
        manager.updateEpic(new Epic("Посетить форум", "В декабре", 3));

//        manager.deleteTaskById(2);
//        manager.deleteEpicById(4);
//        manager.deleteSubtaskById(5);

//        manager.deleteAllTasks();
//        manager.deleteAllEpics();
//        manager.deleteAllSubtasks();

        System.out.println("Все таски: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Все сабтаски: " + manager.getAllSubtasks());
        System.out.println();
        System.out.println("Таск по id: " + manager.getTaskById(1));
        System.out.println("Эпик по id: " + manager.getEpicById(3));
        System.out.println("Сабтаск по id: " + manager.getSubtaskById(7));
        System.out.println();
        System.out.println("Сабтаски по эпику: " + manager.getAllSubtasksByEpic(epic1));

    }
}
