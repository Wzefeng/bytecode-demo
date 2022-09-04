package demo.bytecode.bytebuddy.gateway;

@RpcGatewayClazz(clazzDesc = "查询数据信息", alias = "dataApi", timeOut = 350L)
public class UserRepository extends Repository<String> {

    @RpcGatewayMethod(methodName = "queryData", methodDesc = "查询数据")
    public String queryData(int var1) {
        // ...
        return "aaabbb";
    }

}
