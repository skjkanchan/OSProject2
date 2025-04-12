
import java.util.Random;
import java.util.concurrent.Semaphore;


public class Bank {

    static Random random = new Random();

    // Initialize signals for Customers and Tellers
    // static Semaphore[] customerReady = new Semaphore[2];
    // static Semaphore[] tellerReady = new Semaphore[2];
    // static Semaphore[] transactionComplete = new Semaphore[2];
    // static Semaphore[] customerDone = new Semaphore[2];
    static Semaphore tellerReady = new Semaphore(0);
    static Semaphore customerReady = new Semaphore(0);

    static public Semaphore gLock = new Semaphore(1);
    static public int gCount = 0; // Data shared amongst all the threads

    // Transaction types
    enum TransactionType {
        DEPOSIT, WITHDRAW, CHECK_BALANCE
    }

        
    // Teller Class
    static class Teller extends Thread {
        int tellerId;

        public Teller(int id) {
            this.tellerId = id;
        }

        @Override
        public void run() {
            System.out.println("Teller " + tellerId + " has started their shift");

            //while(!isInterrupted()) {
            //repeat until all customers served
            //for (int i = 0; i < 5; i++) {
                try {

                    gLock.acquire(); //The first thread can continue, everyone else must block

                    //Teller sends signal that its ready
                    // System.out.println("Teller " + tellerId + " is ready to serve");
                    // tellerReady.release();

                    System.out.println("Thread " + tellerId + " has count " + gCount);
                    gCount++;
                    gLock.release(); //Allows one of the blocked threads to continue

                    //wait for customer to approach
                    // customerReady.acquire();
                    // System.out.println("Teller " + tellerId + " sees Customer");

                    //Short delay to simulate interaction
                    //Thread.sleep(300);

                } catch (Exception e) {
                    System.out.println("Teller " + tellerId + " is ending their shift");
                    return;
                }
            //}
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


            try {
                System.out.println("Customer " + customerId + " enters the bank for transaction " + transactionType);
                
                //wait for a teller to be available
                tellerReady.acquire();

                //customer introduces themselves
                System.out.println("Customer " + customerId + ": Hello, I'm customer with transaction" + transactionType + ".");


                //signal that customer is ready for service
                customerReady.release();


                Thread.sleep(500);

                
                System.out.println("Customer " + customerId + " has finished the introduction.");


                

                    



                

            

                
            } catch (Exception e) {
                System.out.println("Customer " + customerId + " left the bank unexpectedly");
            }

            




        }


    }

    public static void main(String args[]) throws InterruptedException { 

        Teller[] tellers = new Teller[2];
        for (int i = 0; i < 2; i++) {
            tellers[i] = new Teller(i + 1);
            tellers[i].start();
        }


        // Customer[] customers = new Customer[5];
        // for (int i = 0; i < 5; i++) {
        //     TransactionType type = TransactionType.values()[random.nextInt(TransactionType.values().length)];
        //     customers[i] = new Customer(i + 1, type);
        //     customers[i].start();
        //     //Thread.sleep(random.nextInt(1000) + 500);
        // }


        // // Wait for all customers to finish
        // for (Customer customer : customers) {
        //     customer.join();
        // }
        
        // End teller shifts
        for (Teller teller : tellers) {
            teller.interrupt();
            teller.join();
        }

        System.out.println("All customers have introduced themselves. Bank is closing.");



    }



}






