package functions;

import java.io.Serializable;
import java.util.Objects;

public class FunctionPoint implements Serializable {
    
    private double x;
    private double y;
    private static final double EPSILON = 1e-10; 

    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    public FunctionPoint() {
        this(0.0, 0.0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
    
        FunctionPoint that = (FunctionPoint) o;
    
        return Math.abs(that.x - x) < EPSILON && 
               Math.abs(that.y - y) < EPSILON;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return new FunctionPoint(this);
        }
    }
}