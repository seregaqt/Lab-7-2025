package functions.meta;

import functions.Function;

public class Scale implements Function {
    private Function f;
    private double scaleX;
    private double scaleY;
    
    public Scale(Function f, double scaleX, double scaleY) {
        this.f = f;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    public double getLeftDomainBorder() {
        if (scaleX > 0) {
            return f.getLeftDomainBorder() / scaleX;
        } else if (scaleX < 0) {
            return f.getRightDomainBorder() / scaleX;
        } else {
            return Double.NaN; // При scaleX = 0 функция вырождена
        }
    }
    
    public double getRightDomainBorder() {
        if (scaleX > 0) {
            return f.getRightDomainBorder() / scaleX;
        } else if (scaleX < 0) {
            return f.getLeftDomainBorder() / scaleX;
        } else {
            return Double.NaN; // При scaleX = 0 функция вырождена
        }
    }
    
    public double getFunctionValue(double x) {
        double scaledX = x * scaleX;
        // Проверяем, что scaledX принадлежит области определения исходной функции
        if (scaledX < f.getLeftDomainBorder() || scaledX > f.getRightDomainBorder()) {
            return Double.NaN;
        }
        return f.getFunctionValue(scaledX) * scaleY;
    }
}