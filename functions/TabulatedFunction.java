package functions;

import java.util.Iterator;

public interface TabulatedFunction extends Function, Iterable<FunctionPoint> {
    
    int getPointsCount();
    
    FunctionPoint getPoint(int index);
    
    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException;

    double getPointX(int index);

    double getPointY(int index);

    void setPointX(int index, double x) throws InappropriateFunctionPointException;

    void setPointY(int index, double y);

    void deletePoint(int index);

    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
    
    Object clone();
}
