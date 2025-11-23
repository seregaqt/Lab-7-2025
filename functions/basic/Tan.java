package functions.basic;

public class Tan extends TrigonometricFunction {
    
    public double getFunctionValue(double x) {
        // Тангенс не определен, когда косинус равен 0
        double cosValue = Math.cos(x);
        if (Math.abs(cosValue) < 1e-10) {
            return Double.NaN;
        }
        return Math.tan(x);
    }
}