package demo.bytecode.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class MyAgent {

    public static void premain(String arg, Instrumentation instrumentation) {

        System.out.println("====== this is my agent " + arg + " =======");

        ClassFileTransformer transformer = new MyMonitorTransformer();

        instrumentation.addTransformer(transformer);
    }

}
