package functions;

import functions.meta.*;

public class Functions {

    private Functions() {
        throw new AssertionError("Нельзя создать объект класса Functions");
    }

    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }
    
    public static double integrate(Function f, double a, double b, double step) {
        if (a < f.getLeftDomainBorder() || b > f.getRightDomainBorder()) {
            throw new IllegalArgumentException("Интервал интегрирования выходит за границы области определения функции");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("Шаг дискретизации должен быть положительным");
        }
        if (a > b) {
            // Меняем пределы интегрирования местами
            double temp = a;
            a = b;
            b = temp;
        }

        double integral = 0.0;
        double x = a;
        
        // Проходим по всем полным шагам
        while (x + step <= b) {
            double y1 = f.getFunctionValue(x);
            double y2 = f.getFunctionValue(x + step);
            integral += (y1 + y2) * step / 2.0;
            x += step;
        }
        
        // Обрабатываем последний неполный шаг (если есть)
        if (x < b) {
            double lastStep = b - x;
            double y1 = f.getFunctionValue(x);
            double y2 = f.getFunctionValue(b);
            integral += (y1 + y2) * lastStep / 2.0;
        }
        
        return integral;
    }
}