package demo.bytecode.javassist;

import javassist.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HelloWorldClassGenerator {

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // 创建 ClassPool，它是一个基于HashMap实现的 CtClass 对象容器
        ClassPool pool = ClassPool.getDefault();

        // 创建类：类全限定名
        CtClass targetClass = pool.makeClass("demo.bytecode.javassist.HelloWorld");

        // 创建无参构造器
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, targetClass);
        constructor.setBody("{}");
        targetClass.addConstructor(constructor);

        // 添加 main 方法
        CtMethod mainMethod = new CtMethod(CtClass.voidType, "main",
                new CtClass[]{pool.get(String[].class.getName())}, targetClass);
        mainMethod.setModifiers(Modifier.PUBLIC + Modifier.STATIC);
        mainMethod.setBody("{System.out.println(\"Hello Javassist!!!\");}");
        targetClass.addMethod(mainMethod);

        // 输出类文件
        targetClass.writeFile();

        Class<?> klass = targetClass.toClass();
        Object instance = klass.newInstance();

        Method main = klass.getMethod("main", String[].class);
        main.invoke(instance, (Object) new String[1]);

    }

}
