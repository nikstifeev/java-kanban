import manager.Managers;
import manager.TaskManager;
import tasks.Status;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Пройти 1ый урок", "Инкапсуляция");
        manager.createTask(task1);
        Task task2 = new Task("Пройти 2ой урок", "Наследование");
        manager.createTask(task2);

        Epic epic1 = new Epic("Посетить форум", "В ноябре");
        manager.createEpic(epic1);
        Epic epic2 = new Epic("Закончить курс", "Java-разаботчик");
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Подготовить доклад", "На 7 минут", 3);
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подготовить презентацию", "На 9 слайдов", 3);
        manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Написать проект", "Дипломный", 4);
        manager.createSubtask(subtask3);

        Task updTask1 = new Task(task1);
        updTask1.setName("Повторить 1ый урок");
        updTask1.setDescription("Git");
        updTask1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(updTask1);

        Subtask updSubtask1 = new Subtask(subtask1);
        updSubtask1.setStatus(Status.DONE);
        manager.updateSubtask(updSubtask1);

        Subtask updSubtask2 = new Subtask(subtask2);
        updSubtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(updSubtask2);

        Subtask updSubtask3 = new Subtask(subtask3);
        updSubtask3.setStatus(Status.DONE);
        manager.updateSubtask(updSubtask3);

        Epic updEpic1 = new Epic(epic1);
        updEpic1.setDescription("В декабре");
        manager.updateEpic(updEpic1);

//        manager.deleteTaskById(2);
//        manager.deleteEpicById(4);
//        manager.deleteSubtaskById(5);

//        manager.deleteAllTasks();
//        manager.deleteAllEpics();
//        manager.deleteAllSubtasks();

        System.out.println("Все таски: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Все сабтаски: " + manager.getAllSubtasks());
        System.out.println("Сабтаски по эпику: " + manager.getAllSubtasksByEpic(epic1));
        System.out.println();
        System.out.println("Таск по id: " + manager.getTaskById(1));
        System.out.println("История: " + manager.getHistory());
        System.out.println("Эпик по id: " + manager.getEpicById(3));
        System.out.println("История: " + manager.getHistory());
        System.out.println("Сабтаск по id: " + manager.getSubtaskById(7));
        System.out.println("История: " + manager.getHistory());
        System.out.println("Сабтаск по id: " + manager.getSubtaskById(7));
        System.out.println("История: " + manager.getHistory());

    }
}
