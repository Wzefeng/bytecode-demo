package demo.bytecode.bytebuddy.gateway;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.io.IOException;

public class AnnotationGenerateTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        new ByteBuddy()
                .subclass(TypeDescription.Generic.Builder.parameterizedType(Repository.class, String.class).build())
                .name(Repository.class.getPackage().getName().concat(".").concat("MyUserRepository"))
                .method(ElementMatchers.named("queryData"))
                .intercept(MethodDelegation.to(UserRepositoryInterceptor.class))
                .annotateMethod(AnnotationDescription.Builder.ofType(RpcGatewayMethod.class)
                        .define("methodName", "用户查询")
                        .define("methodDesc", "通过用户ID查询用户信息")
                        .build())
                .annotateType(AnnotationDescription.Builder.ofType(RpcGatewayClazz.class)
                        .define("clazzDesc", "用户Repository")
                        .define("alias", "UserRepositoryAPI")
                        .define("timeOut", 500L)
                        .build())
                .make()
                .saveIn(new File(AnnotationGenerateTest.class.getResource("/").getPath()));

        Class<?> klass = Class.forName("demo.bytecode.bytebuddy.gateway.MyUserRepository");
        Repository<String> repository = (Repository<String>) klass.newInstance();
        String result = repository.queryData(1001);
        System.out.println(result);
    }

}
