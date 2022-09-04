package demo.bytecode.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SumOfTwoNumbers extends ClassLoader {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        byte[] bytes = generate();

        Class<?> klass = new SumOfTwoNumbers().defineClass("demo.bytecode.asm.AsmSumOfTwoNumbers", bytes, 0, bytes.length);

        Method sum = klass.getMethod("sum", int.class, int.class);

        System.out.println(sum.invoke(klass.newInstance(), 3, 4));
    }

    private static byte[] generate() {
        ClassWriter classWriter = new ClassWriter(0);

        // 定义对象头；版本号、修饰符、全类名、类名、父类、实现的接口 -> public class HelloWorld
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "demo/bytecode/asm/AsmSumOfTwoNumbers",
                null, "java/lang/Object", null);

        {
            // 生成空参构造方法
            MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }

        {
            // 生成两数之和方法
            MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "sum", "(II)I", null, null);
            methodVisitor.visitVarInsn(Opcodes.ILOAD, 1);
            methodVisitor.visitVarInsn(Opcodes.ILOAD, 2);
            methodVisitor.visitInsn(Opcodes.IADD);
            methodVisitor.visitInsn(Opcodes.IRETURN);
            methodVisitor.visitMaxs(2, 3);
            methodVisitor.visitEnd();
        }

        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
