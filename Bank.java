
import java.util.Random;

public class Bank {

    static Random random = new Random();


    // Transaction types
    enum TransactionType {
        DEPOSIT, WITHDRAW, CHECK_BALANCE
    }

    static class Teller extends Thread {
        int tellerId;

        public Teller(int id) {
            this.tellerId = id;
        }

        @Override
        public void run() {
            System.out.println("Teller " + tellerId + " has started their shift");
        }


    }

    static class Customer extends Thread {
        int customerId;
        TransactionType transactionType;

        public Customer(int id, TransactionType type) {
            this.customerId = id;
            this.transactionType = type;
        }

        @Override
        public void run() {
            System.out.println("Customer " + customerId + " enters the bank for transaction " + transactionType);
        }


    }

    public static void main(String args[]) { 
        Teller[] tellers = new Teller[2];
        for (int i = 0; i < 2; i++) {
            tellers[i] = new Teller(i + 1);
            tellers[i].start();
        }


        Customer[] customers = new Customer[5];
        for (int i = 0; i < 5; i++) {
            TransactionType type = TransactionType.values()[random.nextInt(TransactionType.values().length)];
            customers[i] = new Customer(i + 1, type);
            customers[i].start();
        }


    }



}






