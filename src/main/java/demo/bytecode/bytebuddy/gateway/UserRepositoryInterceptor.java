package demo.bytecode.bytebuddy.gateway;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.Method;

public class UserRepositoryInterceptor {

    @RuntimeType
    public static Object intercept(@Origin Method method, @AllArguments Object[] args) {
        return "查询错误" + args[0];
    }


}
