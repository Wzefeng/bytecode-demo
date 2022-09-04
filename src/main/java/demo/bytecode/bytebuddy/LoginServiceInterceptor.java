package demo.bytecode.bytebuddy;

import com.alibaba.fastjson.JSON;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class LoginServiceInterceptor {

    // @RuntimeType：定义运行时的目标方法
    // @SuperCall：用于调用父类版本的方法。
    @RuntimeType
    public static Object intercept(@Origin Method method, @AllArguments Object[] args, @SuperCall Callable<?> callable)
            throws Exception {
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = callable.call();
            return result;
        } finally {
            System.out.println("方法名称：" + method.getName());
            System.out.println("入参个数：" + method.getParameterCount());
            System.out.println("入参类型：" + method.getParameterTypes()[0].getTypeName() + "、" + method.getParameterTypes()[1].getTypeName());
            System.out.println("入参内容：" + JSON.toJSONString(args));
            System.out.println("出参类型：" + method.getReturnType().getName());
            System.out.println("出参结果：" + result);
            System.out.println("方法执行耗时: " + (System.currentTimeMillis() - start) + "ms");
        }
    }

}
