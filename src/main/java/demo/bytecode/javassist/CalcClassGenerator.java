package demo.bytecode.javassist;

import javassist.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CalcClassGenerator {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        ClassPool pool = ClassPool.getDefault();

        CtClass targetClass = pool.makeClass("demo.bytecode.javassist.CalcClass");

        // 定义字段 private double π
        CtField paiField = new CtField(CtClass.doubleType, "pai", targetClass);
        paiField.setModifiers(Modifier.PRIVATE + Modifier.STATIC + Modifier.FINAL);
        targetClass.addField(paiField, "3.14d");

        // 定义方法: 求圆的面积 public double calcCircuitArea(double r)
        CtMethod calcCircuitArea = new CtMethod(CtClass.doubleType,
                "calcCircuitArea", new CtClass[]{pool.get(double.class.getName())}, targetClass);
        calcCircuitArea.setModifiers(Modifier.PUBLIC);
        // 通过符号 $+ 数字，来获取入参
        calcCircuitArea.setBody("{return pai * $1 * $1;}");
        targetClass.addMethod(calcCircuitArea);

        // 定义方法：求两数之和 public int sumOfTwoNumbers(int a, int b)
        CtMethod sumOfTwoNumbers = new CtMethod(CtClass.intType,
                "sumOfTwoNumbers",
                new CtClass[]{pool.get(int.class.getName()), pool.get(int.class.getName())},
                targetClass);
        sumOfTwoNumbers.setModifiers(Modifier.PUBLIC);
        sumOfTwoNumbers.setBody("{return $1 + $2;}");
        targetClass.addMethod(sumOfTwoNumbers);

        // 输出类文件
        targetClass.writeFile();

        // 反射生成实例
        Class<?> klass = targetClass.toClass();
        Object instance = klass.newInstance();

        // test methods
        Method calcCircuitAreaMethod = klass.getMethod("calcCircuitArea", double.class);
        double circuitArea = (Double) calcCircuitAreaMethod.invoke(instance, 2);
        System.out.println("所求的圆的面积为：" + circuitArea);

        Method sumOfTwoNumbersMethod = klass.getMethod("sumOfTwoNumbers", int.class, int.class);
        int sum = (Integer) sumOfTwoNumbersMethod.invoke(instance, 8, 5);
        System.out.println("所求两数之和为：" + sum);
    }

}
