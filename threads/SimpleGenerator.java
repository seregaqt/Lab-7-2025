package threads;

import functions.Function;
import functions.basic.Log;

public class SimpleGenerator implements Runnable {
    private final Task task;
    
    public SimpleGenerator(Task task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTasksCount(); i++) {
                // Генерируем случайные параметры
                double base = 1 + Math.random() * 9; // [1, 10)
                double left = Math.random() * 100;   // [0, 100)
                double right = 100 + Math.random() * 100; // [100, 200)
                double step = Math.random();         // [0, 1)
                
                // Создаем функцию
                Function logFunction = new Log(base);
                
                // Синхронизированная установка параметров в задание
                synchronized (task) {
                    task.setFunction(logFunction);
                    task.setLeftBorder(left);
                    task.setRightBorder(right);
                    task.setStep(step);
                }
                
                // Вывод сообщения
                System.out.printf("Generator: Source %.4f %.4f %.4f (log base=%.4f)%n", 
                                left, right, step, base);
                
                // Небольшая пауза для наглядности
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            System.out.println("Generator was interrupted");
            Thread.currentThread().interrupt();
        }
    }
}
