package functions;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable  {
    private FunctionPoint[] points; 
    private int pointsCount;
    private static final double EPSILON = 1e-10;
    
    public ArrayTabulatedFunction() {
        this.pointsCount = 0;
        this.points = new FunctionPoint[10];
    }
    
    private boolean doubleEquals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения не может быть больше или равна правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount];
        
        double step = (rightX - leftX) / (pointsCount - 1);
       
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0.0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения не может быть больше или равна правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount]; 
        
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }
    
    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
    if (xValues.length < 2) {
        throw new IllegalArgumentException("Количество точек не может быть меньше двух");
    }
    if (xValues.length != yValues.length) {
        throw new IllegalArgumentException("Количество x и y координат должно совпадать");
    }
    
    // Проверка упорядоченности точек по x
    for (int i = 0; i < xValues.length - 1; i++) {
        if (xValues[i] >= xValues[i + 1]) {
            throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию x");
        }
    }
    
    this.pointsCount = xValues.length;
    this.points = new FunctionPoint[pointsCount];
    
    // Создаем точки из массивов координат
    for (int i = 0; i < pointsCount; i++) {
        this.points[i] = new FunctionPoint(xValues[i], yValues[i]);
    }
}
    
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }
    
        // Проверка упорядоченности точек по x
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].getX() >= points[i + 1].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию x");
            }
        }
        
        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount];
    
        // Создаем копии точек для обеспечения инкапсуляции
        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pointsCount = in.readInt();
        points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }
    
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();
            
            if (x >= x1 && x <= x2) {
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();
                
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }  
        return 0;
    }

    public int getPointsCount() {
        return pointsCount;
    }
 
    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }
        return new FunctionPoint(points[index]);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }
        
        double newX = point.getX();
        
        if (index == 0) {
            if (newX >= points[1].getX() && !doubleEquals(newX, points[1].getX())) {
                throw new InappropriateFunctionPointException(
                    "Новая координата x=" + newX + " должна быть меньше " + points[1].getX()
                );
            }
        }
        else if (index == pointsCount - 1) {
            if (newX <= points[pointsCount - 2].getX() && !doubleEquals(newX, points[pointsCount - 2].getX())) {
                throw new InappropriateFunctionPointException(
                    "Новая координата x=" + newX + " должна быть больше " + points[pointsCount - 2].getX()
                );
            }
        }
        else {
            if ((newX <= points[index - 1].getX() && !doubleEquals(newX, points[index - 1].getX())) || 
                (newX >= points[index + 1].getX() && !doubleEquals(newX, points[index + 1].getX()))) {
                throw new InappropriateFunctionPointException(
                    "Новая координата x=" + newX + " должна быть в интервале (" + 
                    points[index - 1].getX() + ", " + points[index + 1].getX() + ")"
                );
            }
        }
        
        points[index] = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }
        return points[index].getX();
    }
    
    public double getPointY(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }
        return points[index].getY();
    }
    
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }
        
        if (index == 0) {
            if (x >= points[1].getX()) {
                throw new InappropriateFunctionPointException(
                    "Новая координата x=" + x + " должна быть меньше " + points[1].getX()
                );
            }
        }
        else if (index == pointsCount - 1) {
            if (x <= points[pointsCount - 2].getX()) {
                throw new InappropriateFunctionPointException(
                    "Новая координата x=" + x + " должна быть больше " + points[pointsCount - 2].getX()
                );
            }
        }
        else {
            if (x <= points[index - 1].getX() || x >= points[index + 1].getX()) {
                throw new InappropriateFunctionPointException(
                    "Новая координата x=" + x + " должна быть в интервале (" + 
                    points[index - 1].getX() + ", " + points[index + 1].getX() + ")"
                );
            }
        }
        
        points[index].setX(x);
    }

    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }
        
        points[index].setY(y);
    }

    public void deletePoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }
        
        if (pointsCount < 3) {
            throw new IllegalStateException("Невозможно удалить точку: количество точек не может быть меньше двух");
        }
        
        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        for (int i = 0; i < pointsCount; i++) {
            if (doubleEquals(point.getX(), points[i].getX())) {
                throw new InappropriateFunctionPointException(
                    "Точка с x=" + point.getX() + " уже существует"
                );
            }
        }
        
        if (pointsCount == points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length + points.length / 2 + 1];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }

        int insertIndex = 0;
        while (insertIndex < pointsCount && point.getX() > points[insertIndex].getX()) {
            insertIndex++;
        }

        System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pointsCount; i++) {
            if (i > 0) sb.append(", ");
            sb.append("(").append(points[i].getX())
              .append("; ").append(points[i].getY()).append(")");
        }
        sb.append("}");
        return sb.toString();
    }
    
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    
    // Если объект является ArrayTabulatedFunction, используем оптимизированное сравнение
    if (o instanceof ArrayTabulatedFunction) {
        ArrayTabulatedFunction other = (ArrayTabulatedFunction) o;
        
        if (this.pointsCount != other.pointsCount) {
            return false;
        }
        
        // Прямое сравнение массивов точек
        for (int i = 0; i < pointsCount; i++) {
            if (!this.points[i].equals(other.points[i])) {
                return false;
            }
        }
        return true;
    }
    
    // Если объект реализует TabulatedFunction, но не ArrayTabulatedFunction
    if (o instanceof TabulatedFunction) {
        TabulatedFunction other = (TabulatedFunction) o;
        
        if (this.getPointsCount() != other.getPointsCount()) {
            return false;
        }
        
        // Сравнение через методы интерфейса
        for (int i = 0; i < pointsCount; i++) {
            FunctionPoint thisPoint = this.getPoint(i);
            FunctionPoint otherPoint = other.getPoint(i);
            
            if (!thisPoint.equals(otherPoint)) {
                return false;
            }
        }
        return true;
    }
    
    return false;
}
@Override
public int hashCode() {
    int hash = pointsCount; // Включаем количество точек в хэш
    
    // Комбинируем хэш-коды всех точек
    for (int i = 0; i < pointsCount; i++) {
        hash ^= points[i].hashCode();
    }
    
    return hash;
}

@Override
public Object clone() {
    try {
        ArrayTabulatedFunction cloned = (ArrayTabulatedFunction) super.clone();
        
        // Глубокое копирование массива точек
        cloned.points = new FunctionPoint[this.points.length];
        for (int i = 0; i < this.pointsCount; i++) {
            cloned.points[i] = (FunctionPoint) this.points[i].clone();
        }
        cloned.pointsCount = this.pointsCount;
        
        return cloned;
    } catch (CloneNotSupportedException e) {
        // Этот случай не должен произойти, но на всякий случай
        // создаем копию через конструктор
        return new ArrayTabulatedFunction(this.points);
    }
}

@Override
public Iterator<FunctionPoint> iterator() {
    return new Iterator<FunctionPoint>() {
        private int currentIndex = 0;
        
        @Override
        public boolean hasNext() {
            return currentIndex < pointsCount;
        }
        
        @Override
        public FunctionPoint next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more points available");
            }
            // Создаем копию точки для защиты инкапсуляции
            FunctionPoint point = points[currentIndex];
            FunctionPoint copy = new FunctionPoint(point.getX(), point.getY());
            currentIndex++;
            return copy;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not supported");
        }
    };
}

public static class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
    @Override
    public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
    }
    
    @Override
    public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return new ArrayTabulatedFunction(leftX, rightX, values);
    }
    
    @Override
    public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return new ArrayTabulatedFunction(points);
    }
}

}