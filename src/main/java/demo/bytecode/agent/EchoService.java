package demo.bytecode.agent;

public class EchoService {

    public static void main(String[] args) {
        new EchoService().echo("hello");
    }

    public void echo(String message) {
        System.out.println(message);
    }

}
