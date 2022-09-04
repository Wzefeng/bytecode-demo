package demo.bytecode.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.util.HotSwapper;

public class HotSwapperTest {

    public static void main(String[] args) throws Exception {
        TicketService ticketService = new TicketService();
        System.out.println("Remaining Ticket Querying...");

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println(ticketService.queryRemainingTickets("D2313"));
            }
        }).start();

        // javassist.tools.HotSwapper，是 javassist 的包中提供的热加载替换类操作。在执行时需要启用 JPDA（Java平台调试器体系结构）。
        // 监听 8000 端口,在启动参数里设置
        // java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8001
        // HotSwapper hotSwapper = new HotSwapper(8001);

        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get(TicketService.class.getName());

        CtMethod ctMethod = ctClass.getDeclaredMethod("queryRemainingTickets");
        // 重写方法
        ctMethod.setBody("{return \"The Train \" + $1 + \" remaining tickets is: \" + 0;}");

        // 加载新的类
        System.out.println("::执行 HotSwapper 热插拔...");
        // hotSwapper.reload(TicketService.class.getName(), ctClass.toBytecode());

    }

}
