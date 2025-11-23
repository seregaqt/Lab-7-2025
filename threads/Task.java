package threads;

import functions.Function;

public class Task {
    private Function function;
    private double leftBorder;
    private double rightBorder;
    private double step;
    private int tasksCount;

    public Task() {
    }

    public Task(Function function, double leftBorder, double rightBorder, double step, int tasksCount) {
        this.function = function;
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.step = step;
        this.tasksCount = tasksCount;
    }

    // Геттеры
    public Function getFunction() {
        return function;
    }

    public double getLeftBorder() {
        return leftBorder;
    }

    public double getRightBorder() {
        return rightBorder;
    }

    public double getStep() {
        return step;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    // Сеттеры
    public void setFunction(Function function) {
        this.function = function;
    }

    public void setLeftBorder(double leftBorder) {
        this.leftBorder = leftBorder;
    }

    public void setRightBorder(double rightBorder) {
        this.rightBorder = rightBorder;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public void setTasksCount(int tasksCount) {
        this.tasksCount = tasksCount;
    }

    @Override
    public String toString() {
        return String.format("Task{function=%s, left=%.2f, right=%.2f, step=%.4f, tasks=%d}",
                function, leftBorder, rightBorder, step, tasksCount);
    }
}