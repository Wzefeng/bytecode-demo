package demo.bytecode.javassist;

import javassist.*;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonitorTest {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        ClassPool pool = ClassPool.getDefault();

        CtClass ctClass = pool.get("demo.bytecode.javassist.NumberConverter");

        // ctClass.replaceClassName("NumberConverter", "NumberConverter02");

        String className = ctClass.getName();

        CtMethod ctMethod = ctClass.getDeclaredMethod("strToInt");
        String methodName = ctMethod.getName();

        MethodInfo methodInfo = ctMethod.getMethodInfo();

        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        CtClass[] parameterTypes = ctMethod.getParameterTypes();

        boolean isStatic = (methodInfo.getAccessFlags() & AccessFlag.STATIC) != 0;
        int parameterSize = isStatic ? attr.tableLength() : attr.tableLength() - 1;

        List<String> parameterNameList = new ArrayList<>(parameterSize);            // 入参名称
        List<String> parameterTypeList = new ArrayList<>(parameterSize);
        StringBuilder parameters = new StringBuilder();

        for (int i = 0; i < parameterSize; i++) {
            parameterNameList.add(attr.variableName(i + (isStatic ? 0 : 1))); // 静态类型去掉第一个this参数
            parameterTypeList.add(parameterTypes[i].getName());
            if (i + 1 == parameterSize) {
                parameters.append("$").append(i + 1);
            } else {
                parameters.append("$").append(i + 1).append(",");
            }
        }

        CtClass returnType = ctMethod.getReturnType();
        String returnTypeName = returnType.getName();


        int idx = Monitors.generateMethodId(className, methodName, parameterNameList, parameterTypeList, returnTypeName);

        // 定义属性用于记录开始时间
        ctMethod.addLocalVariable("startNanos", CtClass.longType);
        // 方法前加强
        ctMethod.insertBefore("{startNanos = System.nanoTime();}");

        // 定义属性用于记录入参
        ctMethod.addLocalVariable("parameterValues", pool.get(Object[].class.getName()));
        // 方法前加强
        ctMethod.insertBefore("{parameterValues = new Object[]{" + parameters + "}; }");

        // 方法后加强
        // 如果返回类型非对象类型，$_ 需要进行类型转换
        ctMethod.insertAfter("{demo.bytecode.javassist.Monitors.point(" + idx + ", startNanos, parameterValues, $_);}", false);
        // 方法；添加TryCatch
        ctMethod.addCatch("{demo.bytecode.javassist.Monitors.point(" + idx + ", $e); throw $e; }", ClassPool.getDefault().get("java.lang.Exception"));   // 添加异常捕获

        ctClass.writeFile();

        Class<?> klass = ctClass.toClass();
        Object instance = klass.newInstance();

        Method strToInt = klass.getMethod("strToInt", String.class);
        int result = (Integer) strToInt.invoke(instance, "23");

        System.out.println("==>" + result);
    }


}
