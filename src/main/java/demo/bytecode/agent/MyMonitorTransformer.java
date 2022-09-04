package demo.bytecode.agent;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

public class MyMonitorTransformer implements ClassFileTransformer {

    private static final Set<String> classNameSet = new HashSet<>();

    static {
        classNameSet.add("demo.bytecode.agent.EchoService");
        classNameSet.add("demo.bytecode.agent.UserService");
        classNameSet.add("demo.bytecode.agent.Main");
    }


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {

            String currentClassName = className.replaceAll("/", ".");
            if (!classNameSet.contains(currentClassName)) {
                return null;
            }

            System.out.println("transform: [" + currentClassName + "]");

            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.get(currentClassName);

            CtBehavior[] behaviors = ctClass.getDeclaredBehaviors();
            for (CtBehavior behavior : behaviors) {
                enhance(behavior);
            }

            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void enhance(CtBehavior behavior) throws CannotCompileException {
        if (behavior.isEmpty()) {
            return;
        }

        String behaviorName = behavior.getName();
        if ("main".equals(behaviorName)) {
            return;
        }

        StringBuilder enhanceBody = new StringBuilder();
        enhanceBody.append("{")
                .append("long start = System.currentTimeMillis(); \n") //前置增强: 打入时间戳
                .append("$_ = $proceed($$);\n") //调用原有代码，类似于method();($$)表示所有的参数
                .append("System.out.println(\"method:[")
                .append(behaviorName)
                .append("]\" + \" cost \" + (System.currentTimeMillis() - start) + \"(ms)\");")
                .append("}");

        ExprEditor editor = new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                m.replace(enhanceBody.toString());
            }
        };

        behavior.instrument(editor);
    }

}
