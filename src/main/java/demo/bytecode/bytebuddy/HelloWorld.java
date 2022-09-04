package demo.bytecode.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public class HelloWorld {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        String helloWorld = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .load(HelloWorld.class.getClassLoader())
                .getLoaded()
                .newInstance()
                .toString();

        System.out.println(helloWorld);
    }

}
