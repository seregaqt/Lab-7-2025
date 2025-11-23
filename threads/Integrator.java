package threads;

import functions.Functions;

public class Integrator extends Thread {
    private final Task task;
    private final ReadWriteSemaphore semaphore;
    
    public Integrator(Task task, ReadWriteSemaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }
    
    @Override
    public void run() {
        int processedCount = 0;
        try {
            for (int i = 0; i < task.getTasksCount(); i++) {
                // Проверяем прерывание
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                
                double left, right, step, result;
                
                // Используем семафор для чтения
                semaphore.beginRead();
                try {
                    // Проверяем, что функция установлена
                    if (task.getFunction() == null) {
                        System.out.println("Integrator: Function is null, skipping...");
                        continue;
                    }
                    
                    left = task.getLeftBorder();
                    right = task.getRightBorder();
                    step = task.getStep();
                    
                    // Вычисляем интеграл
                    result = Functions.integrate(task.getFunction(), left, right, step);
                    processedCount++;
                } finally {
                    semaphore.endRead();
                }
                
                System.out.printf("Integrator[%d]: Result %.4f %.4f %.4f %.8f%n", 
                                processedCount, left, right, step, result);
                
                // Небольшая пауза для наглядности
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            System.out.println("Integrator was interrupted - stopping work");
        } catch (IllegalArgumentException e) {
            System.out.printf("Integrator ERROR: %s%n", e.getMessage());
        } finally {
            System.out.println("Integrator finished work. Processed: " + processedCount + " tasks");
        }
    }
}