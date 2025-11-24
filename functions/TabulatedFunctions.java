package functions;

import java.io.*;
import java.lang.reflect.Constructor;

public class TabulatedFunctions {
    
    private TabulatedFunctions() {
        throw new AssertionError("Нельзя создать объект класса TabulatedFunctions");
    }
    
    private static TabulatedFunctionFactory factory = new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }

    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }
    
    // Методы с рефлексией
public static TabulatedFunction createTabulatedFunction(Class<?> functionClass, double leftX, double rightX, int pointsCount) {
    if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
        throw new IllegalArgumentException("Класс " + functionClass.getName() + " не реализует интерфейс TabulatedFunction");
    }
    
    try {
        Constructor<?> constructor = functionClass.getConstructor(double.class, double.class, int.class);
        return (TabulatedFunction) constructor.newInstance(leftX, rightX, pointsCount);
    } catch (Exception e) {
        throw new IllegalArgumentException("Ошибка при создании объекта через рефлексию", e);
    }
}

public static TabulatedFunction createTabulatedFunction(Class<?> functionClass, double leftX, double rightX, double[] values) {
    if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
        throw new IllegalArgumentException("Класс " + functionClass.getName() + " не реализует интерфейс TabulatedFunction");
    }
    
    try {
        Constructor<?> constructor = functionClass.getConstructor(double.class, double.class, double[].class);
        return (TabulatedFunction) constructor.newInstance(leftX, rightX, values);
    } catch (Exception e) {
        throw new IllegalArgumentException("Ошибка при создании объекта через рефлексию", e);
    }
}

public static TabulatedFunction createTabulatedFunction(Class<?> functionClass, FunctionPoint[] points) {
    if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
        throw new IllegalArgumentException("Класс " + functionClass.getName() + " не реализует интерфейс TabulatedFunction");
    }
    
    try {
        Constructor<?> constructor = functionClass.getConstructor(FunctionPoint[].class);
        return (TabulatedFunction) constructor.newInstance((Object) points);
    } catch (Exception e) {
        throw new IllegalArgumentException("Ошибка при создании объекта через рефлексию", e);
    }
}

public static TabulatedFunction createTabulatedFunction(Class<?> functionClass, double[] xValues, double[] yValues) {
    if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
        throw new IllegalArgumentException("Класс " + functionClass.getName() + " не реализует интерфейс TabulatedFunction");
    }
    
    try {
        Constructor<?> constructor = functionClass.getConstructor(double[].class, double[].class);
        return (TabulatedFunction) constructor.newInstance(xValues, yValues);
    } catch (Exception e) {
        throw new IllegalArgumentException("Ошибка при создании объекта через рефлексию", e);
    }
}

// Перегруженные методы tabulate с рефлексией
public static TabulatedFunction tabulate(Class<?> functionClass, Function function, double leftX, double rightX, int pointsCount) {
    if (pointsCount < 2) {
        throw new IllegalArgumentException("Количество точек табулирования не может быть меньше 2");
    }
    
    if (leftX >= rightX) {
        throw new IllegalArgumentException("Левая граница табулирования должна быть меньше правой");
    }
    
    if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
        throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
    }
    
    double[] values = new double[pointsCount];
    double step = (rightX - leftX) / (pointsCount - 1);
    
    for (int i = 0; i < pointsCount; i++) {
        double x = leftX + i * step;
        values[i] = function.getFunctionValue(x);
    }
    
    // Используем рефлексию для создания объекта
    return createTabulatedFunction(functionClass, leftX, rightX, values);
}

public static TabulatedFunction tabulate(Class<?> functionClass, Function function, double leftX, double rightX) {
    return tabulate(functionClass, function, leftX, rightX, 20);
}
    
   
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек табулирования не может быть меньше 2");
        }
        
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница табулирования должна быть меньше правой");
        }
        
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения функции");
        }
        
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }
        
        return createTabulatedFunction(leftX, rightX, values);
    }
    
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX) {
        return tabulate(function, leftX, rightX, 20);
    }
    
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) {
        DataOutputStream dataOut = new DataOutputStream(out);
        try {
            dataOut.writeInt(function.getPointsCount());
            for (int i = 0; i < function.getPointsCount(); i++) {
                dataOut.writeDouble(function.getPointX(i));
                dataOut.writeDouble(function.getPointY(i));
            }
            dataOut.flush();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при выводе функции в поток", e);
        }
    }
    
    public static TabulatedFunction inputTabulatedFunction(InputStream in) {
    DataInputStream dataIn = new DataInputStream(in);
    try {
        int pointsCount = dataIn.readInt();
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];
        
        for (int i = 0; i < pointsCount; i++) {
            xValues[i] = dataIn.readDouble();
            yValues[i] = dataIn.readDouble();
        }
        
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(xValues[i], yValues[i]);
        }
        return createTabulatedFunction(points);
    } catch (IOException e) {
        throw new RuntimeException("Ошибка при чтении функции из потока", e);
    }
}
    
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        PrintWriter writer = new PrintWriter(out);
        try {
            writer.print(function.getPointsCount());
            writer.print(' ');
            
            for (int i = 0; i < function.getPointsCount(); i++) {
                writer.print(function.getPointX(i));
                writer.print(' ');
                writer.print(function.getPointY(i));
                if (i < function.getPointsCount() - 1) {
                    writer.print(' ');
                }
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при записи функции в символьный поток", e);
        }
    }
    
    public static TabulatedFunction readTabulatedFunction(Reader in) {
    StreamTokenizer tokenizer = new StreamTokenizer(in);
    try {
        tokenizer.resetSyntax();
        tokenizer.wordChars('0', '9');
        tokenizer.wordChars('.', '.');
        tokenizer.wordChars('-', '-');
        tokenizer.wordChars('e', 'e');
        tokenizer.wordChars('E', 'E');
        tokenizer.whitespaceChars(' ', ' ');
        tokenizer.whitespaceChars('\t', '\t');
        tokenizer.whitespaceChars('\n', '\n');
        tokenizer.whitespaceChars('\r', '\r');
        tokenizer.parseNumbers();
        
        if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new RuntimeException("Ожидалось количество точек");
        }
        int pointsCount = (int) tokenizer.nval;
        
        if (pointsCount < 2) {
            throw new RuntimeException("Некорректное количество точек: " + pointsCount);
        }
        
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];
        
        for (int i = 0; i < pointsCount; i++) {
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new RuntimeException("Ожидалась x-координата точки " + i);
            }
            xValues[i] = tokenizer.nval;
            
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new RuntimeException("Ожидалась y-координата точки " + i);
            }
            yValues[i] = tokenizer.nval;
        }
        
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(xValues[i], yValues[i]);
        }
        return createTabulatedFunction(points);
        
    } catch (IOException e) {
        throw new RuntimeException("Ошибка при чтении функции из символьного потока", e);
    }
    }
    
    //Методы чтения через рефлексию
    // Перегруженные методы чтения с рефлексией
public static TabulatedFunction inputTabulatedFunction(Class<?> functionClass, InputStream in) {
    DataInputStream dataIn = new DataInputStream(in);
    try {
        int pointsCount = dataIn.readInt();
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];
        
        for (int i = 0; i < pointsCount; i++) {
            xValues[i] = dataIn.readDouble();
            yValues[i] = dataIn.readDouble();
        }
        
        // Используем рефлексию для создания объекта указанного класса
        return createTabulatedFunction(functionClass, xValues, yValues);
        
    } catch (IOException e) {
        throw new RuntimeException("Ошибка при чтении функции из потока", e);
    }
}

public static TabulatedFunction readTabulatedFunction(Class<?> functionClass, Reader in) {
    StreamTokenizer tokenizer = new StreamTokenizer(in);
    try {
        tokenizer.resetSyntax();
        tokenizer.wordChars('0', '9');
        tokenizer.wordChars('.', '.');
        tokenizer.wordChars('-', '-');
        tokenizer.wordChars('e', 'e');
        tokenizer.wordChars('E', 'E');
        tokenizer.whitespaceChars(' ', ' ');
        tokenizer.whitespaceChars('\t', '\t');
        tokenizer.whitespaceChars('\n', '\n');
        tokenizer.whitespaceChars('\r', '\r');
        tokenizer.parseNumbers();
        
        if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new RuntimeException("Ожидалось количество точек");
        }
        int pointsCount = (int) tokenizer.nval;
        
        if (pointsCount < 2) {
            throw new RuntimeException("Некорректное количество точек: " + pointsCount);
        }
        
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];
        
        for (int i = 0; i < pointsCount; i++) {
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new RuntimeException("Ожидалась x-координата точки " + i);
            }
            xValues[i] = tokenizer.nval;
            
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new RuntimeException("Ожидалась y-координата точки " + i);
            }
            yValues[i] = tokenizer.nval;
        }
        
        // Используем рефлексию для создания объекта указанного класса
        return createTabulatedFunction(functionClass, xValues, yValues);
        
    } catch (IOException e) {
        throw new RuntimeException("Ошибка при чтении функции из символьного потока", e);
    }
}
}