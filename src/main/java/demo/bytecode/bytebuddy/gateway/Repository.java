package demo.bytecode.bytebuddy.gateway;

public abstract class Repository<T> {

    public abstract T queryData(int id);

}
