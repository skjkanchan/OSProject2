import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;


public class ThreadTest
{
    // Create a new semaphore initialized to 1
    // Operations:
    //   gLock.acquire()  -- This is the same thing as sem_wait
    //        - Decrements the value, and blocks it value is less than zero
    //        - A initial value of 1, means the first thread to call acquire will not block
    //   gLock.release() -- This is the same thing as sem_signal
    //        - Increments the value, and unblocks a blocked thread if the new value is zero or less
    static public Semaphore dataLock = new Semaphore(1);
    static public Semaphore customerQueue = new Semaphore(0);
    static public int customerCount = 0; // Data shared amongst all the threads
    static public int nextCustomer = 0;
    static public int customersServed = 0; 

    static public int numTellers = 3;
    static public int numCustomers = 10;
    static Random random = new Random();
    static public String[] customerTransactions = new String[numCustomers];

    
    
    


    // Class Inherits from the Thread class will be a specific thread
    // On the project you will need one of these per type of thread
    static public class Teller extends Thread {
        int id; //store the id of the entity in the simulation

        Teller(int id) {
            this.id = id;
        }

        // You need to implement run() to customise the behavior of this thread type. 
        // This will be called by the code when you call the start method that comes from the Thread class
        public void run() {

            try {
                while (true) {
                    //wait for customer to arrive
                    customerQueue.acquire();

                    //get lock for shared data
                    dataLock.acquire();

                    // Check if all customers have been served
                    if (customersServed >= numCustomers) {
                        // Release semaphore for other waiting tellers
                        customerQueue.release();
                        dataLock.release();
                        System.out.println("Teller " + id + " is ending their shift.");
                        break;  // Exit if all customers are served
                    }

                    //serve next customer
                    int currCustomer = nextCustomer++;
                    System.out.println("Teller " + id + " is serving customer " + currCustomer + " with transaction " + customerTransactions[currCustomer]);
                    customersServed++;

                    //release lock
                    dataLock.release();



                }
                // gLock.acquire(); //The first thread can continue, everyone else must block
                // // The above guarantees only one thread can execute the next three lines, at any one time
                // System.out.println("Teller " + id + " has started their shift with customer " + customer);
                // //System.out.println("Teller " + id + " " + gCount " has started their shift");
                // //customer++;
                // gLock.release(); //Allows one of the blocked threads to continue
            }
            catch(Exception e)
            {
                System.err.println("Error in Teller " + id + ": " + e);
            }
        }
    }

    static public class Customer extends Thread {
        int id; //store the id of the entity in the simulation
        String transactionType;

        Customer(int id, String type) {
            this.id = id;
            this.transactionType = type;
        }

        // You need to implement run() to customise the behavior of this thread type. 
        // This will be called by the code when you call the start method that comes from the Thread class
        public void run() {
            try {

                //get lock for shared data
                dataLock.acquire();

                //customer enters bank
                int myNum = customerCount++;
                System.out.println("Customer " + myNum + " has entered the bank for transaction " + transactionType);

                //release lock
                dataLock.release();

                //signal that customer is ready
                customerQueue.release();


                // cLock.acquire(); //The first thread can continue, everyone else must block
                // // The above guarantees only one thread can execute the next three lines, at any one time
                // System.out.println("Customer " + customer + " has entered the bank.");
                // //System.out.println("Teller " + id + " " + gCount " has started their shift");
                // customer++;
                // gLock.release(); //Allows one of the blocked threads to continue
            }
            catch(Exception e)
            {
                System.err.println("Error in Customer " + id + ": " + e);
            }
        }
    }

    static public void main(String[] args) {
        ArrayList<String> transactions = new ArrayList<String>();
        transactions.add("Withdrawal");
        transactions.add("Deposit");

        Customer[] customers = new Customer[numCustomers];
        Teller[] tellers = new Teller[numTellers];

        //Create Tellers
        for(int i=0; i<numTellers; i++) {
            // Create instances of your custom thread class
            tellers[i] = new Teller(i);
            // Create and run the thread
            tellers[i].start(); //At this point, thread i is running concurrently
        }


        //Create Customers
        
        for(int i=0; i<numCustomers; i++) {
            String transactionType = transactions.get(random.nextInt(transactions.size()));
            customerTransactions[i] = transactionType;
            // Create instances of your custom thread class
            customers[i] = new Customer(i, transactionType);
            // Create and run the thread
            customers[i].start(); //At this point, thread i is running concurrently
        }

        // Wait for customer threads to exit first
        for(int i=0; i<numCustomers; i++) {
            try {
                customers[i].join();
            } catch (InterruptedException e) {
                System.err.println("Error joining with Customer " + i + ": " + e);
            }
        }
        System.out.println("All customers have been processed.");

        // Release the customerQueue semaphore enough times to wake up all waiting tellers
        for(int i=0; i<numTellers; i++) {
            customerQueue.release();
        }

        // Wait for teller threads to exit
        for(int i=0; i<numTellers; i++) {
            try {
                tellers[i].join();
            } catch (InterruptedException e) {
                System.err.println("Error joining with Teller " + i + ": " + e);
            }
        }
        System.out.println("All tellers have finished their shifts. Bank is closed.");
    }
}
