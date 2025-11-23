package threads;

import functions.Function;
import functions.basic.Log;

public class Generator extends Thread {
    private final Task task;
    private final ReadWriteSemaphore semaphore;
    
    public Generator(Task task, ReadWriteSemaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }
    
    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTasksCount(); i++) {
                // Проверяем прерывание
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                
                // Генерируем случайные параметры
                double base = 1 + Math.random() * 9;
                double left = Math.random() * 100;
                double right = 100 + Math.random() * 100;
                double step = Math.random();
                
                Function logFunction = new Log(base);
                
                // Используем семафор для записи
                semaphore.beginWrite();
                try {
                    task.setFunction(logFunction);
                    task.setLeftBorder(left);
                    task.setRightBorder(right);
                    task.setStep(step);
                } finally {
                    semaphore.endWrite();
                }
                
                System.out.printf("Generator[%d]: Source %.4f %.4f %.4f (log base=%.4f)%n", 
                                i + 1, left, right, step, base);
                
                // Небольшая пауза для наглядности
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            System.out.println("Generator was interrupted - stopping work");
        } finally {
            System.out.println("Generator finished work");
        }
    }
}