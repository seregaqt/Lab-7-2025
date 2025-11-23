package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private final Task task;
    
    public SimpleIntegrator(Task task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTasksCount(); i++) {
                // Локальные переменные для хранения параметров задания
                double left, right, step;
                double result;
                
                // Синхронизированное чтение параметров из задания
                synchronized (task) {
                    // Проверяем, что функция установлена (защита от NullPointerException)
                    if (task.getFunction() == null) {
                        System.out.println("Integrator: Function is null, waiting...");
                        Thread.sleep(50);
                        continue;
                    }
                    
                    left = task.getLeftBorder();
                    right = task.getRightBorder();
                    step = task.getStep();
                    
                    // Вычисляем интеграл внутри синхронизированного блока
                    result = Functions.integrate(task.getFunction(), left, right, step);
                }
                
                // Вывод результата (вне синхронизированного блока)
                System.out.printf("Integrator: Result %.4f %.4f %.4f %.8f%n", 
                                left, right, step, result);
                
                // Небольшая пауза для наглядности
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            System.out.println("Integrator was interrupted");
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException e) {
            System.out.printf("Integrator ERROR: %s%n", e.getMessage());
        } catch (Exception e) {
            System.out.printf("Integrator UNEXPECTED ERROR: %s%n", e.getMessage());
        }
    }
}
