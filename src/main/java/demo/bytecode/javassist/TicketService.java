package demo.bytecode.javassist;

import java.util.Random;

public class TicketService {

    public String queryRemainingTickets(String trainNumber) {
        return "The Train " + trainNumber + " remaining tickets is: " + new Random().nextInt(1000);
    }

}
