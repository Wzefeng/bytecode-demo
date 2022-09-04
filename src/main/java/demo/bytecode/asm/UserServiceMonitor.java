package demo.bytecode.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UserServiceMonitor extends ClassLoader {

    public static void main(String[] args) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        byte[] bytes = generate();

        Class<?> klass = new UserServiceMonitor().defineClass("demo.bytecode.asm.UserService", bytes, 0, bytes.length);

        Method queryUserInfo = klass.getMethod("queryUserInfo", String.class);

        Object user = queryUserInfo.invoke(klass.newInstance(), "u10001");

        System.out.println("User query result: " + user);
    }


    private static byte[] generate() throws IOException {
        ClassReader classReader = new ClassReader(UserService.class.getName());

        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);

        ClassVisitor enhancingClassVisitor = new EnhancingClassVisitor(classWriter);
        classReader.accept(enhancingClassVisitor, ClassReader.EXPAND_FRAMES);

        classWriter.visitEnd();

        return classWriter.toByteArray();
    }


    static class EnhancingClassVisitor extends ClassVisitor {

        protected EnhancingClassVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM9, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

            if (!"queryUserInfo".equals(name)) {
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }

            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);

            return new EnhancingMethodVisitor(mv, access, name, descriptor);
        }
    }

    static class EnhancingMethodVisitor extends AdviceAdapter {
        private final String methodName;

        protected EnhancingMethodVisitor(MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(Opcodes.ASM9, methodVisitor, access, name, descriptor);
            this.methodName = name;
        }

        @Override
        protected void onMethodEnter() {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitVarInsn(Opcodes.LSTORE, 2);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
        }

        @Override
        protected void onMethodExit(int opcode) {
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitLdcInsn("[" + methodName + "]" + "方法执行耗时(ns)" + ": ");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                mv.visitVarInsn(LLOAD, 2);
                mv.visitInsn(LSUB);

                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }
        }
    }
}
