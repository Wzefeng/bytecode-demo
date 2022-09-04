package demo.bytecode.bytebuddy;

public class LoginService {

    public String login(String username, String password) {

        System.out.println("do login...");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return username + "Login Success";
    }

}
