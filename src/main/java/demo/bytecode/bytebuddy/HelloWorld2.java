package demo.bytecode.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class HelloWorld2 {

    public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        DynamicType.Unloaded<Object> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .name("net.bytebuddy.ByteBuddy.HelloWorld")
                .defineMethod("main", void.class, Modifier.PUBLIC + Modifier.STATIC)
                .withParameter(String[].class, "args")
                .intercept(MethodDelegation.to(HelloWorld2Delegate.class)) // MethodDelegation 需要是 public 类
                // 被委托的方法与需要与原方法有着一样的入参、出参、方法名，否则不能映射上
                .make();

        outputClass(dynamicType.getBytes(), "HelloWorld.class");

        Class<?> generateClass = dynamicType.load(HelloWorld2.class.getClassLoader()).getLoaded();
        Method main = generateClass.getMethod("main", String[].class);
        main.invoke(generateClass.newInstance(), (Object) new String[1]);
    }

    private static void outputClass(byte[] bytes, String className) {
        String fileName = HelloWorld2.class.getResource("/").getPath() + className;
        File file = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
