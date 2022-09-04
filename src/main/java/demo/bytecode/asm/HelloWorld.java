package demo.bytecode.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * invokevirtual - 调用实例方法
 * invokespecial - 调用超类构造方法、实例初始化方法、私有方法
 * invokestatic - 调用静态方法
 * invokeinterface - 调用接口方法
 * invokedynamic - 调用动态连接方法
 *
 *
 */
public class HelloWorld extends ClassLoader {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        byte[] bytes = generate();

        Class<?> klass = new HelloWorld().defineClass("demo.bytecode.asm.AsmHelloWorld", bytes, 0, bytes.length);

        Method main = klass.getMethod("main", String[].class);
        main.invoke(klass, (Object) null);

    }

    private static byte[] generate() {
        ClassWriter classWriter = new ClassWriter(0);

        // 定义对象头；版本号、修饰符、全类名、类名、父类、实现的接口 -> public class HelloWorld
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "demo/bytecode/asm/AsmHelloWorld",
                null, "java/lang/Object", null);

        // 添加方法; 修饰符、方法名、描述符、签名、异常
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main",
                "([Ljava/lang/String;)V", null, null);

        // 执行命令 获取静态属性
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // 加载常量
        methodVisitor.visitLdcInsn("Hello World");

        // 调用方法
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
                "println", "(Ljava/lang/String;)V", false);

        // 返回
        methodVisitor.visitInsn(Opcodes.RETURN);

        // 设置操作数栈深度和局部变量的大小
        methodVisitor.visitMaxs(2, 1);

        // 方法结束
        methodVisitor.visitEnd();

        classWriter.visitEnd();

        // 生成字节数组
        return classWriter.toByteArray();
    }

}
