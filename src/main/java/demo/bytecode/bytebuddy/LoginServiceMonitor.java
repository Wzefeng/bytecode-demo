package demo.bytecode.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;

public class LoginServiceMonitor {

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
        LoginService login = new ByteBuddy()
                .subclass(LoginService.class)
                .method(ElementMatchers.named("login"))
                .intercept(MethodDelegation.to(LoginServiceInterceptor.class))
                .make()
                .load(LoginServiceMonitor.class.getClassLoader())
                .getLoaded()
                .newInstance();
        String result = login.login("zhangsan", "123456");

        System.out.println("===>" + result);
    }


}
