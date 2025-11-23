import threads.*;
import functions.*;
import functions.basic.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    
    private static void test1() {
        System.out.println("-".repeat(80));
        System.out.println("Тестирование работы всех написанных классов");
        System.out.println("-".repeat(80));
        
        testBasicFunctionsSinCos();
        testTabulatedAnalogues();
        testSumOfSquares();
        testFileOperationsExponential();
        testFileOperationsLogarithm();
        compareStorageFormats();
    }
    
    private static void test2() {
        System.out.println("=".repeat(80));
        System.out.println("ЗАДАНИЕ 9: ТЕСТИРОВАНИЕ СЕРИАЛИЗАЦИИ");
        System.out.println("=".repeat(80));
        
        testSerializableApproach();
        testExternalizableApproach();
        compareSerializationMethods();
    }
    
    private static void testBasicFunctionsSinCos() {
        System.out.println("\n1. Тестирование базовых функций Sin и Cos");
        System.out.println("-".repeat(50));
        
        Function sin = new Sin();
        Function cos = new Cos();
        
        double from = 0;
        double to = Math.PI;
        double step = 0.1;
        
        System.out.println("Значения sin(x) на [0, π] с шагом 0.1:");
        System.out.printf("%-8s %-10s%n", "x", "sin(x)");
        for (double x = from; x <= to + 1e-10; x += step) {
            System.out.printf("%-8.3f %-10.6f%n", x, sin.getFunctionValue(x));
        }
        
        System.out.println("\nЗначения cos(x) на [0, π] с шагом 0.1:");
        System.out.printf("%-8s %-10s%n", "x", "cos(x)");
        for (double x = from; x <= to + 1e-10; x += step) {
            System.out.printf("%-8.3f %-10.6f%n", x, cos.getFunctionValue(x));
        }
    }
    
    private static void testTabulatedAnalogues() {
        System.out.println("\n\n2. Табулированные аналоги Sin и Cos (10 точек)");
        System.out.println("-".repeat(55));
        
        Function sin = new Sin();
        Function cos = new Cos();
        
        TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        
        double from = 0;
        double to = Math.PI;
        double step = 0.1;
        
        System.out.println("Сравнение sin(x) и табулированного sin(x) (10 точек):");
        System.out.printf("%-8s %-12s %-12s %-12s%n", "x", "sin(x)", "tab_sin(x)", "погрешность");
        for (double x = from; x <= to + 1e-10; x += step) {
            double exact = sin.getFunctionValue(x);
            double approx = tabulatedSin.getFunctionValue(x);
            double error = Math.abs(exact - approx);
            System.out.printf("%-8.3f %-12.6f %-12.6f %-12.6f%n", x, exact, approx, error);
        }
        
        System.out.println("\nСравнение cos(x) и табулированного cos(x) (10 точек):");
        System.out.printf("%-8s %-12s %-12s %-12s%n", "x", "cos(x)", "tab_cos(x)", "погрешность");
        for (double x = from; x <= to + 1e-10; x += step) {
            double exact = cos.getFunctionValue(x);
            double approx = tabulatedCos.getFunctionValue(x);
            double error = Math.abs(exact - approx);
            System.out.printf("%-8.3f %-12.6f %-12.6f %-12.6f%n", x, exact, approx, error);
        }
    }
    
    private static void testSumOfSquares() {
        System.out.println("\n\n3. Сумма квадратов табулированных функций");
        System.out.println("-".repeat(45));
        
        int[] pointsCounts = {5, 10, 20};
        
        for (int pointsCount : pointsCounts) {
            System.out.println("\nКоличество точек в табулированных функциях: " + pointsCount);
            
            TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, pointsCount);
            TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, pointsCount);
            
            Function sumOfSquares = Functions.sum(
                Functions.power(tabulatedSin, 2),
                Functions.power(tabulatedCos, 2)
            );
            
            double from = 0;
            double to = Math.PI;
            double step = 0.1;
            
            System.out.printf("%-8s %-15s%n", "x", "sin²(x)+cos²(x)");
            for (double x = from; x <= to + 1e-10; x += step) {
                double value = sumOfSquares.getFunctionValue(x);
                double deviation = Math.abs(value - 1.0);
                System.out.printf("%-8.3f %-15.8f (отклонение: %.8f)%n", x, value, deviation);
            }
        }
    }
    
    private static void testFileOperationsExponential() {
        System.out.println("\n\n4. Работа с текстовыми файлами (экспонента)");
        System.out.println("-".repeat(50));
        
        String filename = "exponential_function.txt";
        
        try {
            TabulatedFunction expFunction = TabulatedFunctions.tabulate(new Exp(), 0, 10, 11);
            
            try (FileWriter writer = new FileWriter(filename)) {
                TabulatedFunctions.writeTabulatedFunction(expFunction, writer);
            }
            System.out.println("Табулированная экспонента записана в файл: " + filename);
            
            TabulatedFunction readFunction;
            try (FileReader reader = new FileReader(filename)) {
                readFunction = TabulatedFunctions.readTabulatedFunction(reader);
            }
            System.out.println("Функция прочитана из текстового файла");
            
            System.out.println("\nСравнение исходной и прочитанной функции:");
            System.out.printf("%-8s %-15s %-15s %-15s%n", "x", "исходная", "прочитанная", "разница");
            for (int i = 0; i < expFunction.getPointsCount(); i++) {
                double x = expFunction.getPointX(i);
                double original = expFunction.getPointY(i);
                double read = readFunction.getPointY(i);
                double difference = Math.abs(original - read);
                System.out.printf("%-8.1f %-15.8f %-15.8f %-15.8f%n", x, original, read, difference);
            }
            
            System.out.println("\nСодержимое текстового файла:");
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            System.out.println(content);
            
            Files.deleteIfExists(Paths.get(filename));
            System.out.println("Временный файл удален");
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void testFileOperationsLogarithm() {
        System.out.println("\n\n5. Работа с бинарными файлами (логарифм)");
        System.out.println("-".repeat(50));
        
        String filename = "logarithm_function.dat";
        
        try {
            TabulatedFunction logFunction = TabulatedFunctions.tabulate(new Log(Math.E), 1, 10, 11);
            
            try (FileOutputStream out = new FileOutputStream(filename)) {
                TabulatedFunctions.outputTabulatedFunction(logFunction, out);
            }
            System.out.println("Табулированный логарифм записан в файл: " + filename);
            
            TabulatedFunction readFunction;
            try (FileInputStream in = new FileInputStream(filename)) {
                readFunction = TabulatedFunctions.inputTabulatedFunction(in);
            }
            System.out.println("Функция прочитана из бинарного файла");
            
            System.out.println("\nСравнение исходной и прочитанной функции:");
            System.out.printf("%-8s %-15s %-15s %-15s%n", "x", "исходная", "прочитанная", "разница");
            for (int i = 0; i < logFunction.getPointsCount(); i++) {
                double x = logFunction.getPointX(i);
                double original = logFunction.getPointY(i);
                double read = readFunction.getPointY(i);
                double difference = Math.abs(original - read);
                System.out.printf("%-8.1f %-15.8f %-15.8f %-15.8f%n", x, original, read, difference);
            }
            
            File file = new File(filename);
            System.out.println("\nРазмер бинарного файла: " + file.length() + " байт");
            
            Files.deleteIfExists(Paths.get(filename));
            System.out.println("Временный файл удален");
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void compareStorageFormats() {
        System.out.println("\n\n6. Сравнение орматов хранения");
        System.out.println("-".repeat(35));
        
        String textFile = "comparison_text.txt";
        String binaryFile = "comparison_binary.dat";
        
        try {
            TabulatedFunction testFunction = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, 5);
            
            try (FileWriter writer = new FileWriter(textFile)) {
                TabulatedFunctions.writeTabulatedFunction(testFunction, writer);
            }
            
            try (FileOutputStream out = new FileOutputStream(binaryFile)) {
                TabulatedFunctions.outputTabulatedFunction(testFunction, out);
            }
            
            File text = new File(textFile);
            File binary = new File(binaryFile);
            
            System.out.println("Размер текстового файла: " + text.length() + " байт");
            System.out.println("Размер бинарного файла: " + binary.length() + " байт");
            System.out.println("Бинарный файл занимает " + 
                String.format("%.1f", (double)binary.length() / text.length() * 100) + "% от текстового");
            
            System.out.println("\nСодержимое текстового файла:");
            System.out.println(new String(Files.readAllBytes(Paths.get(textFile))));
            
            Files.deleteIfExists(Paths.get(textFile));
            Files.deleteIfExists(Paths.get(binaryFile));
            System.out.println("\nВременные файлы удалены");
            
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void testSerializableApproach() {
    System.out.println("\n1. СЕРИАЛИЗАЦИЯ ЧЕРЕЗ Serializable");
    System.out.println("=".repeat(45));
    
    String filename = "serializable_function.ser";
    
    try {
        // Создаем функцию, которая использует ТОЛЬКО Serializable
        TabulatedFunction originalFunction = createSerializableOnlyFunction();
        
        System.out.println("Функция создана:");
        System.out.println("  Тип: " + originalFunction.getClass().getSimpleName());
        System.out.println("  Количество точек: " + originalFunction.getPointsCount());
        System.out.println("  Область определения: [" + originalFunction.getLeftDomainBorder() + ", " + originalFunction.getRightDomainBorder() + "]");
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(originalFunction);
        }
        System.out.println("✓ Сериализована в файл: " + filename);
        
        TabulatedFunction deserializedFunction;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            deserializedFunction = (TabulatedFunction) in.readObject();
        }
        System.out.println("✓ Десериализована из файла");
        System.out.println("  Тип после десериализации: " + deserializedFunction.getClass().getSimpleName());
        
        System.out.println("\nСравнение исходной и десериализованной функции:");
        System.out.printf("%-8s %-12s %-12s %-10s%n", "x", "исходная", "десериал.", "разница");
        boolean allMatch = true;
        for (int i = 0; i < originalFunction.getPointsCount(); i++) {
            double x = originalFunction.getPointX(i);
            double original = originalFunction.getPointY(i);
            double deserialized = deserializedFunction.getPointY(i);
            double diff = Math.abs(original - deserialized);
            System.out.printf("%-8.1f %-12.6f %-12.6f %-10.6f", x, original, deserialized, diff);
            if (diff > 1e-10) {
                System.out.print(" ✗");
                allMatch = false;
            } else {
                System.out.print(" ✓");
            }
            System.out.println();
        }
        
        File file = new File(filename);
        System.out.println("\nРазмер файла Serializable: " + file.length() + " байт");
        
        Files.deleteIfExists(Paths.get(filename));
        System.out.println("✓ Временный файл удален");
        
    } catch (Exception e) {
        System.out.println("✗ Ошибка: " + e.getMessage());
        e.printStackTrace();
    }
}

private static void testExternalizableApproach() {
    System.out.println("\n\n2. СЕРИАЛИЗАЦИЯ ЧЕРЕЗ Externalizable");
    System.out.println("=".repeat(50));
    
    String filename = "externalizable_function.ser";
    
    try {
        // Создаем функцию, которая использует Externalizable
        TabulatedFunction originalFunction = createExternalizableFunction();
        
        System.out.println("Функция создана:");
        System.out.println("  Тип: " + originalFunction.getClass().getSimpleName());
        System.out.println("  Количество точек: " + originalFunction.getPointsCount());
        System.out.println("  Область определения: [" + originalFunction.getLeftDomainBorder() + ", " + originalFunction.getRightDomainBorder() + "]");
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(originalFunction);
        }
        System.out.println("✓ Сериализована в файл: " + filename);
        
        TabulatedFunction deserializedFunction;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            deserializedFunction = (TabulatedFunction) in.readObject();
        }
        System.out.println("✓ Десериализована из файла");
        System.out.println("  Тип после десериализации: " + deserializedFunction.getClass().getSimpleName());
        
        System.out.println("\nСравнение исходной и десериализованной функции:");
        System.out.printf("%-8s %-12s %-12s %-10s%n", "x", "исходная", "десериал.", "разница");
        boolean allMatch = true;
        for (int i = 0; i < originalFunction.getPointsCount(); i++) {
            double x = originalFunction.getPointX(i);
            double original = originalFunction.getPointY(i);
            double deserialized = deserializedFunction.getPointY(i);
            double diff = Math.abs(original - deserialized);
            System.out.printf("%-8.1f %-12.6f %-12.6f %-10.6f", x, original, deserialized, diff);
            if (diff > 1e-10) {
                System.out.print(" ✗");
                allMatch = false;
            } else {
                System.out.print(" ✓");
            }
            System.out.println();
        }
        
        File file = new File(filename);
        System.out.println("\nРазмер файла Externalizable: " + file.length() + " байт");
        
        Files.deleteIfExists(Paths.get(filename));
        System.out.println("✓ Временный файл удален");
        
    } catch (Exception e) {
        System.out.println("✗ Ошибка: " + e.getMessage());
        e.printStackTrace();
    }
}

// Функция, которая использует ТОЛЬКО Serializable (не реализует Externalizable)
private static TabulatedFunction createSerializableOnlyFunction() {
    // Создаем LinkedListTabulatedFunction через массив FunctionPoint
    FunctionPoint[] points = new FunctionPoint[11];
    Function composition = Functions.composition(new Exp(), new Log(Math.E));
    
    for (int i = 0; i < 11; i++) {
        double x = i;
        double y = composition.getFunctionValue(x);
        points[i] = new FunctionPoint(x, y);
    }
    
    return new LinkedListTabulatedFunction(points);
}

// Функция, которая использует Externalizable
private static TabulatedFunction createExternalizableFunction() {
    // Создаем ArrayTabulatedFunction через два массива
    double[] xValues = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    double[] yValues = new double[11];
    Function composition = Functions.composition(new Exp(), new Log(Math.E));
    
    for (int i = 0; i < 11; i++) {
        yValues[i] = composition.getFunctionValue(xValues[i]);
    }
    
    return new ArrayTabulatedFunction(xValues, yValues);
}

private static void compareSerializationMethods() {
    System.out.println("\n\n3. СРАВНЕНИЕ МЕТОДОВ СЕРИАЛИЗАЦИИ");
    System.out.println("=".repeat(45));
    
    String serializableFile = "comparison_serializable.ser";
    String externalizableFile = "comparison_externalizable.ser";
    
    try {
        // Serializable-only функция (LinkedListTabulatedFunction)
        TabulatedFunction serializableFunc = createSerializableOnlyFunction();
        // Externalizable функция (ArrayTabulatedFunction)
        TabulatedFunction externalizableFunc = createExternalizableFunction();
        
        // Сериализация через стандартный Serializable
        try (ObjectOutputStream out1 = new ObjectOutputStream(new FileOutputStream(serializableFile))) {
            out1.writeObject(serializableFunc);
        }
        
        // Сериализация через Externalizable
        try (ObjectOutputStream out2 = new ObjectOutputStream(new FileOutputStream(externalizableFile))) {
            out2.writeObject(externalizableFunc);
        }
        
        File serializable = new File(serializableFile);
        File externalizable = new File(externalizableFile);
        
        long serializableSize = serializable.length();
        long externalizableSize = externalizable.length();
        long difference = Math.abs(serializableSize - externalizableSize);
        double percent = (1 - (double)externalizableSize / serializableSize) * 100;
        
        System.out.println("Размеры файлов:");
        System.out.println("  Serializable (LinkedList): " + serializableSize + " байт");
        System.out.println("  Externalizable (Array): " + externalizableSize + " байт");
        System.out.println("  Разница: " + difference + " байт");
        System.out.printf("  Экономия: %.1f%%\n", Math.abs(percent));
        
        System.out.println("\nАнализ подходов:");
        System.out.println("  Serializable: автоматическая сериализация всех полей");
        System.out.println("  Externalizable: ручное управление, только нужные данные");
        
        Files.deleteIfExists(Paths.get(serializableFile));
        Files.deleteIfExists(Paths.get(externalizableFile));
        System.out.println("\n✓ Временные файлы удалены");
        
    } catch (Exception e) {
        System.out.println("✗ Ошибка: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    private static void test3() {
        System.out.println("=".repeat(80));
        System.out.println("ЗАДАНИЕ 5: ТЕСТИРОВАНИЕ toString(), equals(), hashCode(), clone()");
        System.out.println("=".repeat(80));
        
        testToString();
        testEquals();
        testHashCode();
        testClone();
    }
    
    private static void testToString() {
        System.out.println("\n1. ТЕСТИРОВАНИЕ toString()");
        System.out.println("-".repeat(40));
        
        // Создаем тестовые функции
        FunctionPoint[] points = {
            new FunctionPoint(0.0, 1.0),
            new FunctionPoint(1.0, 3.0),
            new FunctionPoint(2.0, 5.0),
            new FunctionPoint(3.0, 7.0)
        };
        
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(points);
        LinkedListTabulatedFunction listFunc = new LinkedListTabulatedFunction(points);
        
        System.out.println("ArrayTabulatedFunction.toString():");
        System.out.println("  " + arrayFunc.toString());
        
        System.out.println("LinkedListTabulatedFunction.toString():");
        System.out.println("  " + listFunc.toString());
        
        // Тестирование с разным количеством точек
        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};
        ArrayTabulatedFunction shortArrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        
        System.out.println("Короткая функция (3 точки):");
        System.out.println("  " + shortArrayFunc.toString());
    }
    
    private static void testEquals() {
        System.out.println("\n\n2. ТЕСТИРОВАНИЕ equals()");
        System.out.println("-".repeat(40));
        
        // Создаем идентичные функции
        FunctionPoint[] points1 = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0)
        };
        
        FunctionPoint[] points2 = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0)
        };
        
        FunctionPoint[] differentPoints = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 2.0), // Разное значение Y
            new FunctionPoint(2.0, 4.0)
        };
        
        ArrayTabulatedFunction array1 = new ArrayTabulatedFunction(points1);
        ArrayTabulatedFunction array2 = new ArrayTabulatedFunction(points2);
        ArrayTabulatedFunction arrayDifferent = new ArrayTabulatedFunction(differentPoints);
        
        LinkedListTabulatedFunction list1 = new LinkedListTabulatedFunction(points1);
        LinkedListTabulatedFunction list2 = new LinkedListTabulatedFunction(points2);
        LinkedListTabulatedFunction listDifferent = new LinkedListTabulatedFunction(differentPoints);
        
        System.out.println("Сравнение одинаковых ArrayTabulatedFunction:");
        System.out.println("  array1.equals(array2): " + array1.equals(array2));
        System.out.println("  array2.equals(array1): " + array2.equals(array1));
        
        System.out.println("Сравнение одинаковых LinkedListTabulatedFunction:");
        System.out.println("  list1.equals(list2): " + list1.equals(list2));
        System.out.println("  list2.equals(list1): " + list2.equals(list1));
        
        System.out.println("Сравнение разных ArrayTabulatedFunction:");
        System.out.println("  array1.equals(arrayDifferent): " + array1.equals(arrayDifferent));
        
        System.out.println("Сравнение ArrayTabulatedFunction и LinkedListTabulatedFunction:");
        System.out.println("  array1.equals(list1): " + array1.equals(list1));
        System.out.println("  list1.equals(array1): " + list1.equals(array1));
        
        System.out.println("Сравнение с разным количеством точек:");
        double[] shortX = {0.0, 2.0};
        double[] shortY = {0.0, 4.0};
        ArrayTabulatedFunction shortArray = new ArrayTabulatedFunction(shortX, shortY);
        System.out.println("  array1.equals(shortArray): " + array1.equals(shortArray));
        
        System.out.println("Сравнение с null:");
        System.out.println("  array1.equals(null): " + array1.equals(null));
        
        System.out.println("Сравнение с другим типом объекта:");
        System.out.println("  array1.equals(\"строка\"): " + array1.equals("строка"));
    }
    
    private static void testHashCode() {
        System.out.println("\n\n3. ТЕСТИРОВАНИЕ hashCode()");
        System.out.println("-".repeat(40));
        
        // Создаем идентичные функции
        FunctionPoint[] points = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0),
            new FunctionPoint(3.0, 9.0)
        };
        
        ArrayTabulatedFunction array1 = new ArrayTabulatedFunction(points);
        ArrayTabulatedFunction array2 = new ArrayTabulatedFunction(points);
        LinkedListTabulatedFunction list1 = new LinkedListTabulatedFunction(points);
        LinkedListTabulatedFunction list2 = new LinkedListTabulatedFunction(points);
        
        System.out.println("Хэш-коды одинаковых ArrayTabulatedFunction:");
        int hashArray1 = array1.hashCode();
        int hashArray2 = array2.hashCode();
        System.out.println("  array1.hashCode(): " + hashArray1);
        System.out.println("  array2.hashCode(): " + hashArray2);
        System.out.println("  Совпадают: " + (hashArray1 == hashArray2));
        
        System.out.println("Хэш-коды одинаковых LinkedListTabulatedFunction:");
        int hashList1 = list1.hashCode();
        int hashList2 = list2.hashCode();
        System.out.println("  list1.hashCode(): " + hashList1);
        System.out.println("  list2.hashCode(): " + hashList2);
        System.out.println("  Совпадают: " + (hashList1 == hashList2));
        
        System.out.println("Хэш-коды Array и LinkedList с одинаковыми точками:");
        System.out.println("  array1.hashCode(): " + hashArray1);
        System.out.println("  list1.hashCode(): " + hashList1);
        System.out.println("  Совпадают: " + (hashArray1 == hashList1));
        
        // Тестирование изменения объекта
        System.out.println("\nТестирование изменения объекта:");
        ArrayTabulatedFunction original = new ArrayTabulatedFunction(points);
        int originalHash = original.hashCode();
        System.out.println("  Исходный hashCode: " + originalHash);
        
        // Незначительно изменяем одну координату
        try {
            original.setPointY(1, 1.001); // Изменяем Y на 0.001
            int modifiedHash = original.hashCode();
            System.out.println("  После изменения Y[1] на 0.001: " + modifiedHash);
            System.out.println("  Хэш-код изменился: " + (originalHash != modifiedHash));
            System.out.println("  Разница: " + Math.abs(originalHash - modifiedHash));
        } catch (Exception e) {
            System.out.println("  Ошибка при изменении: " + e.getMessage());
        }
        
        // Тестирование с разным количеством точек
        System.out.println("\nТестирование с разным количеством точек:");
        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction func3points = new ArrayTabulatedFunction(xValues1, yValues1);
        
        double[] xValues2 = {0.0, 1.0};
        double[] yValues2 = {0.0, 1.0};
        ArrayTabulatedFunction func2points = new ArrayTabulatedFunction(xValues2, yValues2);
        
        System.out.println("  func3points.hashCode(): " + func3points.hashCode());
        System.out.println("  func2points.hashCode(): " + func2points.hashCode());
        System.out.println("  Совпадают: " + (func3points.hashCode() == func2points.hashCode()));
    }
    
    private static void testClone() {
        System.out.println("\n\n4. ТЕСТИРОВАНИЕ clone()");
        System.out.println("-".repeat(40));
        
        testArrayTabulatedFunctionClone();
        testLinkedListTabulatedFunctionClone();
        testCrossClassClone();
    }
    
    private static void testArrayTabulatedFunctionClone() {
        System.out.println("ArrayTabulatedFunction.clone():");
        
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};
        ArrayTabulatedFunction original = new ArrayTabulatedFunction(xValues, yValues);
        
        ArrayTabulatedFunction clone = (ArrayTabulatedFunction) original.clone();
        
        System.out.println("  Исходная функция: " + original.toString());
        System.out.println("  Клонированная функция: " + clone.toString());
        System.out.println("  equals(): " + original.equals(clone));
        System.out.println("  == : " + (original == clone));
        
        // Изменяем исходный объект
        try {
            original.setPointY(1, 999.0); // Меняем Y во второй точке
            original.setPointX(2, 2.5);   // Меняем X в третьей точке
            
            System.out.println("  После изменения исходной функции:");
            System.out.println("    Исходная: " + original.toString());
            System.out.println("    Клон: " + clone.toString());
            System.out.println("    Клон не изменился: " + 
                (clone.getPointY(1) == 1.0 && clone.getPointX(2) == 2.0));
        } catch (Exception e) {
            System.out.println("  Ошибка при изменении: " + e.getMessage());
        }
    }
    
    private static void testLinkedListTabulatedFunctionClone() {
        System.out.println("\nLinkedListTabulatedFunction.clone():");
        
        FunctionPoint[] points = {
            new FunctionPoint(0.0, 0.0),
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0),
            new FunctionPoint(3.0, 9.0)
        };
        
        LinkedListTabulatedFunction original = new LinkedListTabulatedFunction(points);
        LinkedListTabulatedFunction clone = (LinkedListTabulatedFunction) original.clone();
        
        System.out.println("  Исходная функция: " + original.toString());
        System.out.println("  Клонированная функция: " + clone.toString());
        System.out.println("  equals(): " + original.equals(clone));
        System.out.println("  == : " + (original == clone));
        
        // Изменяем исходный объект
        try {
            original.setPointY(1, 888.0); // Меняем Y во второй точке
            original.deletePoint(2);       // Удаляем третью точку
            
            System.out.println("  После изменения исходной функции:");
            System.out.println("    Исходная: " + original.toString());
            System.out.println("    Клон: " + clone.toString());
            System.out.println("    Клон не изменился: " + 
                (clone.getPointY(1) == 1.0 && clone.getPointsCount() == 4));
        } catch (Exception e) {
            System.out.println("  Ошибка при изменении: " + e.getMessage());
        }
    }
    
    private static void testCrossClassClone() {
        System.out.println("\nКлонирование через интерфейс TabulatedFunction:");
        
        FunctionPoint[] points = {
            new FunctionPoint(0.0, 10.0),
            new FunctionPoint(1.0, 20.0),
            new FunctionPoint(2.0, 30.0)
        };
        
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(points);
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(points);
        
        TabulatedFunction arrayClone = (TabulatedFunction) arrayFunc.clone();
        TabulatedFunction listClone = (TabulatedFunction) listFunc.clone();
        
        System.out.println("  ArrayTabulatedFunction.clone() тип: " + arrayClone.getClass().getSimpleName());
        System.out.println("  LinkedListTabulatedFunction.clone() тип: " + listClone.getClass().getSimpleName());
        System.out.println("  Array clone equals original: " + arrayFunc.equals(arrayClone));
        System.out.println("  List clone equals original: " + listFunc.equals(listClone));
        
        // Проверка глубокого клонирования через интерфейс
        try {
            arrayFunc.setPointY(0, 100.0);
            listFunc.setPointY(0, 200.0);
            
            System.out.println("  После изменения исходных функций:");
            System.out.println("    Array clone Y[0]: " + arrayClone.getPointY(0) + " (должно быть 10.0)");
            System.out.println("    List clone Y[0]: " + listClone.getPointY(0) + " (должно быть 10.0)");
            System.out.println("    Глубокое клонирование работает: " + 
                (arrayClone.getPointY(0) == 10.0 && listClone.getPointY(0) == 10.0));
        } catch (Exception e) {
            System.out.println("  Ошибка при проверке глубокого клонирования: " + e.getMessage());
        }
    }
    
    private static void testIntegration() {
    System.out.println("=".repeat(80));
    System.out.println("Вычисление интеграла методом трапеций");
    System.out.println("=".repeat(80));
    
    testExponentialIntegration();
    findPrecisionForExponential();
    testIntegrationExceptions();
}

private static void testExponentialIntegration() {
    System.out.println("\n1. Вычисление интеграла экспоненты на отрезке [0, 1]");
    System.out.println("-".repeat(60));
    
    Function exp = new Exp();
    double a = 0.0;
    double b = 1.0;
    double theoretical = Math.E - 1; // ∫e^x dx от 0 до 1 = e - 1
    
    double[] steps = {0.1, 0.01, 0.001, 0.0001};
    
    System.out.printf("%-12s %-15s %-15s %-15s%n", "Шаг", "Численный", "Теоретический", "Погрешность");
    for (double step : steps) {
        double numerical = Functions.integrate(exp, a, b, step);
        double error = Math.abs(numerical - theoretical);
        System.out.printf("%-12.4f %-15.10f %-15.10f %-15.10f%n", 
                         step, numerical, theoretical, error);
    }
}

private static void findPrecisionForExponential() {
    System.out.println("\n\n2. Поиск шага для точности 1e-7");
    System.out.println("-".repeat(50));
    
    Function exp = new Exp();
    double a = 0.0;
    double b = 1.0;
    double theoretical = Math.E - 1;
    
    double targetPrecision = 1e-7;
    double step = 0.1;
    double numerical = 0;
    double error = Double.MAX_VALUE;
    int iterations = 0;
    
    System.out.printf("%-6s %-12s %-15s %-15s%n", "Итер.", "Шаг", "Численный", "Погрешность");
    
    while (error > targetPrecision && iterations < 20) {
        numerical = Functions.integrate(exp, a, b, step);
        error = Math.abs(numerical - theoretical);
        
        System.out.printf("%-6d %-12.8f %-15.10f %-15.10f", 
                         iterations, step, numerical, error);
        
        if (error <= targetPrecision) {
            System.out.print(" ✓ ДОСТИГНУТО");
        }
        System.out.println();
        
        step /= 2.0;
        iterations++;
    }
    
    if (error <= targetPrecision) {
        System.out.println("\nТребуемая точность достигнута при шаге: " + step * 2);
    } else {
        System.out.println("\nТребуемая точность не достигнута за " + iterations + " итераций");
    }
    
    // Дополнительная проверка с очень малым шагом
    System.out.println("\n3. Проверка с очень малым шагом");
    double fineStep = 1e-6;
    numerical = Functions.integrate(exp, a, b, fineStep);
    error = Math.abs(numerical - theoretical);
    System.out.printf("Шаг: %.2e, Погрешность: %.2e%n", fineStep, error);
}

private static void testIntegrationExceptions() {
    System.out.println("\n\n3. Тестирование обработки исключений");
    System.out.println("-".repeat(50));
    
    Function log = new Log(Math.E);
    
    try {
        // Попытка интегрирования за пределами области определения
        double result = Functions.integrate(log, -1, 1, 0.1);
        System.out.println("Результат: " + result);
    } catch (IllegalArgumentException e) {
        System.out.println("Поймано исключение: " + e.getMessage());
    }
    
    try {
        // Неверный шаг
        double result = Functions.integrate(log, 1, 2, -0.1);
        System.out.println("Результат: " + result);
    } catch (IllegalArgumentException e) {
        System.out.println("Поймано исключение: " + e.getMessage());
    }
}

    private static void testNonThread() {
    System.out.println("=".repeat(80));
    System.out.println("Последовательная версия (без потоков)");
    System.out.println("=".repeat(80));
    
    nonThread();
}

private static void nonThread() {
    System.out.println("Запуск последовательной версии программы...");
    System.out.println("Создание и выполнение 100 заданий интегрирования");
    System.out.println("-".repeat(80));
    
    // Создаем объект задания
    Task task = new Task();
    task.setTasksCount(100);
    
    long startTime = System.currentTimeMillis();
    int successCount = 0;
    int errorCount = 0;
    
    // Выполняем задания в цикле
    for (int i = 0; i < task.getTasksCount(); i++) {
        try {
            // 1. Создаем логарифмическую функцию со случайным основанием от 1 до 10
            double base = 1 + Math.random() * 9; // [1, 10)
            Function logFunction = new Log(base);
            task.setFunction(logFunction);
            
            // 2. Левая граница от 0 до 100
            double left = Math.random() * 100; // [0, 100)
            task.setLeftBorder(left);
            
            // 3. Правая граница от 100 до 200
            double right = 100 + Math.random() * 100; // [100, 200)
            task.setRightBorder(right);
            
            // 4. Шаг дискретизации от 0 до 1
            double step = Math.random(); // [0, 1)
            task.setStep(step);
            
            // 5. Вывод информации о задании
            System.out.printf("Source %.4f %.4f %.4f (log base=%.4f)%n", 
                            left, right, step, base);
            
            // 6. Вычисление интеграла
            double result = Functions.integrate(task.getFunction(), 
                                              task.getLeftBorder(), 
                                              task.getRightBorder(), 
                                              task.getStep());
            
            // 7. Вывод результата
            System.out.printf("Result %.4f %.4f %.4f %.8f%n", 
                            left, right, step, result);
            successCount++;
            
        } catch (IllegalArgumentException e) {
            System.out.printf("ERROR: %s%n", e.getMessage());
            errorCount++;
        } catch (Exception e) {
            System.out.printf("UNEXPECTED ERROR: %s%n", e.getMessage());
            errorCount++;
        }
        
        // Небольшая пауза для наглядности (можно убрать)
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
    
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    
    System.out.println("-".repeat(80));
    System.out.println("Статистика выполнения:");
    System.out.printf("Успешно выполнено: %d заданий%n", successCount);
    System.out.printf("Завершено с ошибкой: %d заданий%n", errorCount);
    System.out.printf("Общее время выполнения: %d мс%n", duration);
    System.out.printf("Среднее время на задание: %.2f мс%n", (double)duration / task.getTasksCount());
}

private static void executeTasks(int taskCount) {
    Task task = new Task();
    task.setTasksCount(taskCount);
    
    int successCount = 0;
    
    for (int i = 0; i < task.getTasksCount(); i++) {
        try {
            double base = 1 + Math.random() * 9;
            double left = Math.random() * 100;
            double right = 100 + Math.random() * 100;
            double step = Math.random();
            
            Function logFunction = new Log(base);
            double result = Functions.integrate(logFunction, left, right, step);
            
            successCount++;
            
        } catch (IllegalArgumentException e) {
            // Игнорируем ошибки для статистики
        }
    }
    
    System.out.printf("Успешно: %d/%d (%.1f%%)%n", 
                     successCount, taskCount, (double)successCount / taskCount * 100);
}

private static void testSimpleThreads() {
    System.out.println("=".repeat(80));
    System.out.println("Простая многопоточная версия");
    System.out.println("=".repeat(80));
    
    simpleThreads();
}

private static void simpleThreads() {
    System.out.println("Запуск простой многопоточной версии программы...");
    System.out.println("Создание и выполнение 100 заданий интегрирования");
    System.out.println("-".repeat(80));
    
    // Создаем объект задания
    Task task = new Task();
    task.setTasksCount(100);
    
    // Создаем потоки
    Thread generatorThread = new Thread(new SimpleGenerator(task));
    Thread integratorThread = new Thread(new SimpleIntegrator(task));
    
    // Устанавливаем имена потоков для удобства отладки
    generatorThread.setName("GeneratorThread");
    integratorThread.setName("IntegratorThread");
    
    // Тестирование с разными приоритетами (раскомментируйте для тестирования)
    // generatorThread.setPriority(Thread.MAX_PRIORITY);
    // integratorThread.setPriority(Thread.MIN_PRIORITY);
    
    long startTime = System.currentTimeMillis();
    
    // Запускаем потоки
    generatorThread.start();
    integratorThread.start();
    
    // Ожидаем завершения потоков
    try {
        generatorThread.join();
        integratorThread.join();
    } catch (InterruptedException e) {
        System.out.println("Main thread was interrupted");
        Thread.currentThread().interrupt();
    }
    
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    
    System.out.println("-".repeat(80));
    System.out.println("Оба потока завершили работу");
    System.out.printf("Общее время выполнения: %d мс%n", duration);
}

private static void testWithDifferentPriorities() {
    System.out.println("\n\nТестирование с разными приоритетами потоков");
    System.out.println("=".repeat(60));
    
    int[] priorities = {
        Thread.NORM_PRIORITY,  // оба нормальные
        Thread.MAX_PRIORITY,   // генератор высокий, интегратор нормальный  
        Thread.MIN_PRIORITY    // генератор низкий, интегратор нормальный
    };
    
    String[] priorityNames = {"NORMAL", "HIGH", "LOW"};
    
    for (int i = 0; i < priorities.length; i++) {
        System.out.printf("\nТест %d: Приоритет генератора = %s%n", i + 1, priorityNames[i]);
        System.out.println("-".repeat(50));
        
        Task task = new Task();
        task.setTasksCount(50); // Меньше заданий для быстрого тестирования
        
        Thread generatorThread = new Thread(new SimpleGenerator(task));
        Thread integratorThread = new Thread(new SimpleIntegrator(task));
        
        generatorThread.setPriority(priorities[i]);
        integratorThread.setPriority(Thread.NORM_PRIORITY);
        
        long startTime = System.currentTimeMillis();
        
        generatorThread.start();
        integratorThread.start();
        
        try {
            generatorThread.join();
            integratorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.currentTimeMillis();
        System.out.printf("Время выполнения: %d мс%n", endTime - startTime);
    }
}

private static void testComplicatedThreads() {
    System.out.println("=".repeat(80));
    System.out.println("Версия с семафором");
    System.out.println("=".repeat(80));
    
    complicatedThreads();
}

private static void complicatedThreads() {
    System.out.println("Запуск версии с семафором...");
    System.out.println("Создание и выполнение 100 заданий интегрирования");
    System.out.println("-".repeat(80));
    
    // Создаем объект задания и семафор
    Task task = new Task();
    task.setTasksCount(100);
    ReadWriteSemaphore semaphore = new ReadWriteSemaphore();
    
    // Создаем потоки
    Generator generator = new Generator(task, semaphore);
    Integrator integrator = new Integrator(task, semaphore);
    
    // Устанавливаем имена для удобства отладки
    generator.setName("Generator-Thread");
    integrator.setName("Integrator-Thread");
    
    long startTime = System.currentTimeMillis();
    
    // Запускаем потоки
    generator.start();
    integrator.start();
    
    // Ожидаем завершения потоков
    try {
        generator.join();
        integrator.join();
    } catch (InterruptedException e) {
        System.out.println("Main thread was interrupted");
        Thread.currentThread().interrupt();
    }
    
    long endTime = System.currentTimeMillis();
    
    System.out.println("-".repeat(80));
    System.out.println("Оба потока завершили работу");
    System.out.printf("Общее время выполнения: %d мс%n", endTime - startTime);
}

private static void testWithInterruption() {
    System.out.println("\n\nТестирование с прерыванием потоков через 50 мс");
    System.out.println("=".repeat(60));
    
    Task task = new Task();
    task.setTasksCount(100); // Много заданий, но прервем раньше
    ReadWriteSemaphore semaphore = new ReadWriteSemaphore();
    
    Generator generator = new Generator(task, semaphore);
    Integrator integrator = new Integrator(task, semaphore);
    
    System.out.println("Запуск потоков...");
    generator.start();
    integrator.start();
    
    // Ждем 50 мс и прерываем
    try {
        Thread.sleep(50);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    
    System.out.println("Прерывание потоков...");
    generator.interrupt();
    integrator.interrupt();
    
    // Ожидаем завершения
    try {
        generator.join(1000);
        integrator.join(1000);
        
        if (generator.isAlive()) {
            System.out.println("Generator still alive after interruption, forcing stop");
        }
        if (integrator.isAlive()) {
            System.out.println("Integrator still alive after interruption, forcing stop");
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    
    System.out.println("Тестирование прерывания завершено");
}

private static void testSemaphoreWithPriorities() {
    System.out.println("\n\nТестирование семафора с разными приоритетами");
    System.out.println("=".repeat(55));
    
    int[] testCases = {
        Thread.NORM_PRIORITY,  // оба нормальные
        Thread.MAX_PRIORITY,   // генератор высокий
        Thread.MIN_PRIORITY    // генератор низкий
    };
    
    for (int i = 0; i < testCases.length; i++) {
        System.out.printf("\nТест %d: Приоритет генератора = %s%n", 
                         i + 1, getPriorityName(testCases[i]));
        System.out.println("-".repeat(50));
        
        Task task = new Task();
        task.setTasksCount(30);
        ReadWriteSemaphore semaphore = new ReadWriteSemaphore();
        
        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);
        
        generator.setPriority(testCases[i]);
        integrator.setPriority(Thread.NORM_PRIORITY);
        
        long startTime = System.currentTimeMillis();
        
        generator.start();
        integrator.start();
        
        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.currentTimeMillis();
        System.out.printf("Время выполнения: %d мс%n", endTime - startTime);
    }
}

private static String getPriorityName(int priority) {
    switch (priority) {
        case Thread.MIN_PRIORITY: return "MIN";
        case Thread.NORM_PRIORITY: return "NORM";
        case Thread.MAX_PRIORITY: return "MAX";
        default: return "UNKNOWN";
    }
}

private static void testIterators() {
    System.out.println("=".repeat(80));
    System.out.println("Тестирование итераторов");
    System.out.println("=".repeat(80));
    
    testArrayTabulatedFunctionIterator();
    testLinkedListTabulatedFunctionIterator();
    testIteratorExceptions();
}

private static void testArrayTabulatedFunctionIterator() {
    System.out.println("\n1. Итератор для ArrayTabulatedFunction");
    System.out.println("-".repeat(50));
    
    // Создаем тестовую функцию
    double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
    double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
    ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
    
    System.out.println("Функция: " + arrayFunc.toString());
    System.out.println("\nИтерация с помощью for-each:");
    
    // Тестирование for-each
    for (FunctionPoint point : arrayFunc) {
        System.out.println("  " + point);
    }
    
    // Тестирование явного использования итератора
    System.out.println("\nЯвное использование итератора:");
    java.util.Iterator<FunctionPoint> iterator = arrayFunc.iterator();
    while (iterator.hasNext()) {
        FunctionPoint point = iterator.next();
        System.out.println("  " + point);
    }
}

private static void testLinkedListTabulatedFunctionIterator() {
    System.out.println("\n\n2. Итератор для LinkedListTabulatedFunction");
    System.out.println("-".repeat(55));
    
    // Создаем тестовую функцию
    FunctionPoint[] points = {
        new FunctionPoint(0.0, 0.0),
        new FunctionPoint(1.0, 1.0),
        new FunctionPoint(2.0, 4.0),
        new FunctionPoint(3.0, 9.0),
        new FunctionPoint(4.0, 16.0)
    };
    LinkedListTabulatedFunction listFunc = new LinkedListTabulatedFunction(points);
    
    System.out.println("Функция: " + listFunc.toString());
    System.out.println("\nИтерация с помощью for-each:");
    
    // Тестирование for-each
    for (FunctionPoint point : listFunc) {
        System.out.println("  " + point);
    }
    
    // Тестирование явного использования итератора
    System.out.println("\nЯвное использование итератора:");
    java.util.Iterator<FunctionPoint> iterator = listFunc.iterator();
    while (iterator.hasNext()) {
        FunctionPoint point = iterator.next();
        System.out.println("  " + point);
    }
}

private static void testIteratorExceptions() {
    System.out.println("\n\n3. Тестирование исключений итераторов");
    System.out.println("-".repeat(50));
    
    // Создаем небольшую функцию для тестирования
    double[] xValues = {0.0, 1.0};
    double[] yValues = {0.0, 1.0};
    ArrayTabulatedFunction func = new ArrayTabulatedFunction(xValues, yValues);
    
    // Тестирование NoSuchElementException
    System.out.println("Тестирование NoSuchElementException:");
    java.util.Iterator<FunctionPoint> iterator = func.iterator();
    try {
        iterator.next(); // первая точка
        iterator.next(); // вторая точка
        iterator.next(); // должно бросить исключение
    } catch (java.util.NoSuchElementException e) {
        System.out.println("  Поймано NoSuchElementException: " + e.getMessage());
    }
    
    // Тестирование UnsupportedOperationException для remove()
    System.out.println("Тестирование UnsupportedOperationException для remove():");
    iterator = func.iterator();
    iterator.next(); // переходим к первому элементу
    try {
        iterator.remove();
    } catch (UnsupportedOperationException e) {
        System.out.println("  Поймано UnsupportedOperationException: " + e.getMessage());
    }
    
    // Тестирование защиты инкапсуляции
    System.out.println("Тестирование защиты инкапсуляции:");
    iterator = func.iterator();
    FunctionPoint pointFromIterator = iterator.next();
    System.out.println("  Получена точка из итератора: " + pointFromIterator);
    
    // Пытаемся изменить точку - это не должно повлиять на исходную функцию
    pointFromIterator.setX(999.0);
    pointFromIterator.setY(999.0);
    
    System.out.println("  Точка после изменения: " + pointFromIterator);
    System.out.println("  Исходная функция не изменилась: " + func.toString());
    System.out.println("  Защита инкапсуляции работает: " + 
        (func.getPointX(0) != 999.0 && func.getPointY(0) != 999.0));
}

private static void testFactories() {
    System.out.println("=".repeat(80));
    System.out.println("Тестирование фабрик");
    System.out.println("=".repeat(80));
    
    Function f = new Cos();
    TabulatedFunction tf;
    
    // Тестирование фабрики по умолчанию (Array)
    tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
    System.out.println("Фабрика по умолчанию: " + tf.getClass().getSimpleName());
    
    // Тестирование LinkedList фабрики
    TabulatedFunctions.setTabulatedFunctionFactory(new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
    tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
    System.out.println("LinkedList фабрика: " + tf.getClass().getSimpleName());
    
    // Тестирование Array фабрики
    TabulatedFunctions.setTabulatedFunctionFactory(new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
    tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
    System.out.println("Array фабрика: " + tf.getClass().getSimpleName());
    
    // Дополнительное тестирование с разными конструкторами
    System.out.println("\nТестирование разных конструкторов:");
    
    // Через массив значений
    double[] values = {1, 2, 3, 4, 5};
    tf = TabulatedFunctions.createTabulatedFunction(0, 4, values);
    System.out.println("Через массив значений: " + tf.getClass().getSimpleName());
    
    // Через массив точек
    FunctionPoint[] points = {
        new FunctionPoint(0, 0),
        new FunctionPoint(1, 1),
        new FunctionPoint(2, 4)
    };
    tf = TabulatedFunctions.createTabulatedFunction(points);
    System.out.println("Через массив точек: " + tf.getClass().getSimpleName());
}

private static void testReflection() {
    System.out.println("=".repeat(80));
    System.out.println("Тестирование рефлексии");
    System.out.println("=".repeat(80));
    
    TabulatedFunction f;

    System.out.println("1. Создание ArrayTabulatedFunction через рефлексию:");
    f = TabulatedFunctions.createTabulatedFunction(
        ArrayTabulatedFunction.class, 0.0, 10.0, 3);
    System.out.println("   Класс: " + f.getClass());
    System.out.println("   Функция: " + f);

    System.out.println("\n2. Создание ArrayTabulatedFunction с массивом значений:");
    f = TabulatedFunctions.createTabulatedFunction(
        ArrayTabulatedFunction.class, 0.0, 10.0, new double[] {0.0, 50.0, 100.0});
    System.out.println("   Класс: " + f.getClass());
    System.out.println("   Функция: " + f);

    System.out.println("\n3. Создание LinkedListTabulatedFunction с массивом точек:");
    f = TabulatedFunctions.createTabulatedFunction(
        LinkedListTabulatedFunction.class, 
        new FunctionPoint[] {
            new FunctionPoint(0, 0),
            new FunctionPoint(5, 25),
            new FunctionPoint(10, 100)
        }
    );
    System.out.println("   Класс: " + f.getClass());
    System.out.println("   Функция: " + f);

    System.out.println("\n4. Табулирование Sin с использованием LinkedListTabulatedFunction:");
    f = TabulatedFunctions.tabulate(
        LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
    System.out.println("   Класс: " + f.getClass());
    System.out.println("   Функция: " + f);

    // Тестирование создания через массивы x и y
    System.out.println("\n5. Создание через массивы x и y значений:");
    f = TabulatedFunctions.createTabulatedFunction(
        ArrayTabulatedFunction.class, 
        new double[] {0.0, 1.0, 2.0, 3.0}, 
        new double[] {0.0, 1.0, 4.0, 9.0}
    );
    System.out.println("   Класс: " + f.getClass());
    System.out.println("   Функция: " + f);

    // Тестирование обработки ошибок
    System.out.println("\n6. Тестирование обработки ошибок:");
    try {
        f = TabulatedFunctions.createTabulatedFunction(
            String.class, 0.0, 10.0, 3); 
    } catch (IllegalArgumentException e) {
        System.out.println("   Корректно обработана ошибка неправильного класса: " + e.getMessage());
    }

    try {
        // Тестируем ошибку с неправильным количеством точек
        f = TabulatedFunctions.createTabulatedFunction(
            ArrayTabulatedFunction.class, 0.0, 10.0, 1); // Меньше 2 точек
    } catch (IllegalArgumentException e) {
        System.out.println("   Корректно обработана ошибка неправильного количества точек: " + e.getMessage());
    }

    try {
        // Тестируем ошибку с неправильными границами
        f = TabulatedFunctions.createTabulatedFunction(
            ArrayTabulatedFunction.class, 10.0, 0.0, 3); // Левая граница больше правой
    } catch (IllegalArgumentException e) {
        System.out.println("   Корректно обработана ошибка неправильных границ: " + e.getMessage());
    }
}

    public static void main(String[] args) {
//        test1();
//        test2();
//        test3();

//        testIntegration();
//        testNonThread();        
//        testSimpleThreads();            
//        testWithDifferentPriorities();  
//        testComplicatedThreads();       
//        testWithInterruption();         
//        testSemaphoreWithPriorities();  

          testIterators();
          testFactories();
          testReflection();
    }
}