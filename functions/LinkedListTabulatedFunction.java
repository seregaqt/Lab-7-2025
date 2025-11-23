package functions;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable{
    
    private FunctionNode head;
    
    private int pointsCount;
    
    private FunctionNode lastAccessedNode;
    private int lastAccessedIndex;
    
    private static final double EPSILON = 1e-10;
    
    private boolean doubleEquals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }
    
    protected static class FunctionNode implements Serializable {
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;
        
        public FunctionNode(FunctionPoint point, FunctionNode prev, FunctionNode next) {
            this.point = point;
            this.prev = prev;
            this.next = next;
        }
        
        public FunctionNode(FunctionNode node) {
            this.point = new FunctionPoint(node.point);
            this.prev = node.prev;
            this.next = node.next;
        }
        
        FunctionPoint getPoint() {
            return point;
        }
        
        void setPoint(FunctionPoint point) {
            this.point = point;
        }
        
        FunctionNode getPrev() {
            return prev;
        }
        
        void setPrev(FunctionNode prev) {
            this.prev = prev;
        }
        
        FunctionNode getNext() {
            return next;
        }
        
        void setNext(FunctionNode next) {
            this.next = next;
        }
    }
    
    private void initializeList() {
        head = new FunctionNode(null, null, null);
        head.setPrev(head);
        head.setNext(head);
        pointsCount = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }
    
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null, head.getPrev(), head);
        head.getPrev().setNext(newNode);
        head.setPrev(newNode);
        pointsCount++;
        lastAccessedIndex = -1;
        return newNode;
    }
    
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения не может быть больше или равна правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }
        
        initializeList();
        
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail().setPoint(new FunctionPoint(x, 0.0));
        }
    }
    
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения не может быть больше или равна правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }
        
        initializeList();
        
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            addNodeToTail().setPoint(new FunctionPoint(x, values[i]));
        }
    }
    
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }
    
        // Проверка упорядоченности точек по x
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].getX() >= points[i + 1].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию x");
            }
        }
    
        initializeList();
    
        // Добавляем точки в список, создавая копии для инкапсуляции
        for (int i = 0; i < points.length; i++) {
            addNodeToTail().setPoint(new FunctionPoint(points[i]));
        }
    }
    
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }
        
        if (lastAccessedIndex != -1) {
            int diff = index - lastAccessedIndex;
            if (Math.abs(diff) == 1) {
                lastAccessedNode = (diff > 0) ? lastAccessedNode.getNext() : lastAccessedNode.getPrev();
                lastAccessedIndex = index;
                return lastAccessedNode;
            } else if (Math.abs(diff) < index && Math.abs(diff) < pointsCount - index - 1) {
                FunctionNode currentNode = lastAccessedNode;
                int currentIndex = lastAccessedIndex;
                
                while (currentIndex != index) {
                    currentNode = (index > currentIndex) ? currentNode.getNext() : currentNode.getPrev();
                    currentIndex += (index > currentIndex) ? 1 : -1;
                }
                
                lastAccessedNode = currentNode;
                lastAccessedIndex = index;
                return currentNode;
            }
        }
        
        FunctionNode currentNode;
        int currentIndex;
        
        if (index < pointsCount - index) {
            currentNode = head.getNext();
            currentIndex = 0;
            while (currentIndex < index) {
                currentNode = currentNode.getNext();
                currentIndex++;
            }
        } else {
            currentNode = head.getPrev();
            currentIndex = pointsCount - 1;
            while (currentIndex > index) {
                currentNode = currentNode.getPrev();
                currentIndex--;
            }
        }
        
        lastAccessedNode = currentNode;
        lastAccessedIndex = index;
        return currentNode;
    }
    
    
    
    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                "Индекс " + index + " вне границ [0, " + pointsCount + "]"
            );
        }
        
        if (index == pointsCount) {
            return addNodeToTail();
        }
        
        FunctionNode nextNode = getNodeByIndex(index);
        FunctionNode prevNode = nextNode.getPrev();
        FunctionNode newNode = new FunctionNode(null, prevNode, nextNode);
        
        prevNode.setNext(newNode);
        nextNode.setPrev(newNode);
        
        pointsCount++;
        lastAccessedIndex = -1;
        return newNode;
    }
    
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(
                "Индекс " + index + " вне границ [0, " + (pointsCount - 1) + "]"
            );
        }
        
        if (pointsCount < 3) {
            throw new IllegalStateException("Невозможно удалить точку: количество точек не может быть меньше двух");
        }
        
        FunctionNode nodeToDelete = getNodeByIndex(index);
        FunctionNode prevNode = nodeToDelete.getPrev();
        FunctionNode nextNode = nodeToDelete.getNext();
        
        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);
        
        pointsCount--;
        
        if (lastAccessedIndex == index) {
            lastAccessedIndex = -1;
            lastAccessedNode = head;
        } else if (lastAccessedIndex > index) {
            lastAccessedIndex--;
        }
        
        return nodeToDelete;
    }
    
    public double getLeftDomainBorder() {
        if (pointsCount == 0) {
            throw new IllegalStateException("Функция не содержит точек");
        }
        return head.getNext().getPoint().getX();
    }
    
    public double getRightDomainBorder() {
        if (pointsCount == 0) {
            throw new IllegalStateException("Функция не содержит точек");
        }
        return head.getPrev().getPoint().getX();
    }
    
    private int findNodeIndex(FunctionNode node) {
    FunctionNode current = head.getNext();
    for (int i = 0; i < pointsCount; i++) {
        if (current == node) {
            return i;
        }
        current = current.getNext();
    }
    return -1;
}
    
    public double getFunctionValue(double x) {
        if (pointsCount == 0) return Double.NaN;
    
        FunctionNode startNode = (lastAccessedIndex != -1) ? lastAccessedNode : head.getNext();
        int startIndex = (lastAccessedIndex != -1) ? lastAccessedIndex : 0;
    
        FunctionNode currentNode = startNode;
        int checkedNodes = 0;
    
        while (checkedNodes < pointsCount) {
            FunctionPoint point1 = currentNode.getPoint();
            FunctionPoint point2 = currentNode.getNext().getPoint();
        
            if (point1 == null || point2 == null) {
                currentNode = currentNode.getNext();
                checkedNodes++;
                continue;
            }
        
            double x1 = point1.getX();
            double x2 = point2.getX();
        
            if (x >= x1 && x <= x2) {
                double y1 = point1.getY();
                double y2 = point2.getY();
            
                lastAccessedNode = currentNode;
                lastAccessedIndex = findNodeIndex(currentNode);
            
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        
            currentNode = currentNode.getNext();
            checkedNodes++;
        
            if (currentNode == head) {
                currentNode = head.getNext();
            }
        }
    
        return Double.NaN; 
    }
    
    public int getPointsCount() {
        return pointsCount;
    }
    
    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).getPoint());
    }
    
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        double newX = point.getX();
        
        FunctionNode prevNode = node.getPrev();
        FunctionNode nextNode = node.getNext();
        
        if (prevNode == head) {
            if (newX >= nextNode.getPoint().getX() && !doubleEquals(newX, nextNode.getPoint().getX())) {
                throw new InappropriateFunctionPointException(
                    "Новая координата x=" + newX + " должна быть меньше " + nextNode.getPoint().getX()
                );
            }
        } else if (nextNode == head) {
            if (newX <= prevNode.getPoint().getX() && !doubleEquals(newX, prevNode.getPoint().getX())) {
                throw new InappropriateFunctionPointException(
                    "Новая координата x=" + newX + " должна быть больше " + prevNode.getPoint().getX()
                );
            }
        } else {
            if ((newX <= prevNode.getPoint().getX() && !doubleEquals(newX, prevNode.getPoint().getX())) || 
                (newX >= nextNode.getPoint().getX() && !doubleEquals(newX, nextNode.getPoint().getX()))) {
                throw new InappropriateFunctionPointException(
                    "Новая координата x=" + newX + " должна быть в интервале (" + 
                    prevNode.getPoint().getX() + ", " + nextNode.getPoint().getX() + ")"
                );
            }
        }
        
        node.setPoint(new FunctionPoint(point));
    }
    
    public double getPointX(int index) {
        return getNodeByIndex(index).getPoint().getX();
    }
    
    public double getPointY(int index) {
        return getNodeByIndex(index).getPoint().getY();
    }
    
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionPoint currentPoint = getNodeByIndex(index).getPoint();
        setPoint(index, new FunctionPoint(x, currentPoint.getY()));
    }
    
    public void setPointY(int index, double y) {
        FunctionNode node = getNodeByIndex(index);
        FunctionPoint currentPoint = node.getPoint();
        node.setPoint(new FunctionPoint(currentPoint.getX(), y));
    }
    
    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }
    
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode currentNode = head.getNext();
        for (int i = 0; i < pointsCount; i++) {
            if (doubleEquals(point.getX(), currentNode.getPoint().getX())) {
                throw new InappropriateFunctionPointException(
                    "Точка с x=" + point.getX() + " уже существует"
                );
            }
            currentNode = currentNode.getNext();
        }
        
        int insertIndex = 0;
        currentNode = head.getNext();
        while (insertIndex < pointsCount && point.getX() > currentNode.getPoint().getX()) {
            currentNode = currentNode.getNext();
            insertIndex++;
        }
        
        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.setPoint(new FunctionPoint(point));
        
        FunctionNode prevNode = newNode.getPrev();
        FunctionNode nextNode = newNode.getNext();
        
        if ((prevNode != head && newNode.getPoint().getX() <= prevNode.getPoint().getX()) ||
            (nextNode != head && newNode.getPoint().getX() >= nextNode.getPoint().getX())) {
            deleteNodeByIndex(insertIndex);
            throw new InappropriateFunctionPointException(
                "Новая точка нарушает упорядоченность функции"
            );
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        FunctionNode currentNode = head.getNext();
        for (int i = 0; i < pointsCount; i++) {
            if (i > 0) sb.append(", ");
            FunctionPoint point = currentNode.getPoint();
            sb.append("(").append(point.getX())
              .append("; ").append(point.getY()).append(")");
            currentNode = currentNode.getNext();
        }
        sb.append("}");
        return sb.toString();
    }
    
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    
    // Если объект является LinkedListTabulatedFunction, используем оптимизированное сравнение
    if (o instanceof LinkedListTabulatedFunction) {
        LinkedListTabulatedFunction other = (LinkedListTabulatedFunction) o;
        
        if (this.pointsCount != other.pointsCount) {
            return false;
        }
        
        // Прямое сравнение узлов списка
        FunctionNode thisNode = this.head.getNext();
        FunctionNode otherNode = other.head.getNext();
        
        for (int i = 0; i < pointsCount; i++) {
            if (!thisNode.getPoint().equals(otherNode.getPoint())) {
                return false;
            }
            thisNode = thisNode.getNext();
            otherNode = otherNode.getNext();
        }
        return true;
    }
    
    // Если объект реализует TabulatedFunction, но не LinkedListTabulatedFunction
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
    FunctionNode currentNode = head.getNext();
    for (int i = 0; i < pointsCount; i++) {
        hash ^= currentNode.getPoint().hashCode();
        currentNode = currentNode.getNext();
    }
    
    return hash;
}

@Override
public Object clone() {
    try {
        LinkedListTabulatedFunction cloned = (LinkedListTabulatedFunction) super.clone();
        
        // Создаем новую голову для клонированного списка
        cloned.head = new FunctionNode(null, null, null);
        cloned.head.setPrev(cloned.head);
        cloned.head.setNext(cloned.head);
        cloned.pointsCount = 0;
        cloned.lastAccessedNode = cloned.head;
        cloned.lastAccessedIndex = -1;
        
        // Пересобираем список, создавая копии точек
        FunctionNode currentNode = this.head.getNext();
        for (int i = 0; i < this.pointsCount; i++) {
            FunctionPoint pointCopy = (FunctionPoint) currentNode.getPoint().clone();
            FunctionNode newNode = cloned.addNodeToTail();
            newNode.setPoint(pointCopy);
            currentNode = currentNode.getNext();
        }
        
        return cloned;
    } catch (CloneNotSupportedException e) {
        // Этот случай не должен произойти, но на всякий случай
        // создаем копию через конструктор с массивом точек
        FunctionPoint[] pointsArray = new FunctionPoint[pointsCount];
        FunctionNode currentNode = head.getNext();
        for (int i = 0; i < pointsCount; i++) {
            pointsArray[i] = (FunctionPoint) currentNode.getPoint().clone();
            currentNode = currentNode.getNext();
        }
        return new LinkedListTabulatedFunction(pointsArray);
    }
}

@Override
public Iterator<FunctionPoint> iterator() {
    return new Iterator<FunctionPoint>() {
        private FunctionNode currentNode = head.getNext();
        private int iterations = 0;
        
        @Override
        public boolean hasNext() {
            return iterations < pointsCount;
        }
        
        @Override
        public FunctionPoint next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more points available");
            }
            // Создаем копию точки для защиты инкапсуляции
            FunctionPoint point = currentNode.getPoint();
            FunctionPoint copy = new FunctionPoint(point.getX(), point.getY());
            currentNode = currentNode.getNext();
            iterations++;
            return copy;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not supported");
        }
    };
}

public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
    @Override
    public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
    }
    
    @Override
    public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return new LinkedListTabulatedFunction(leftX, rightX, values);
    }
    
    @Override
    public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return new LinkedListTabulatedFunction(points);
    }
}

}