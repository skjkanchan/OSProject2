import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ThreadTest {
    static Random random = new Random();

    //Semaphores
    static public Semaphore dataLock = new Semaphore(1); //to protect access to shared data
    static public Semaphore doorLock = new Semaphore(2); //door allows 2 customers at a time
    static public Semaphore lineLock = new Semaphore(1); //for accessing the line safely
    static public Semaphore[] customerReady = null; //array of semaphores signalling when customers are ready
    static public Semaphore[] tellerAvailable = null; //arry of semaphores signalling when tellers are available
    static public Semaphore[] customerDone = null; //array of semaphores signalling that customer is done with transaction
    
    //Shared Data
    static public int customerCount = 0;
    static public int customersServed = 0;
    static public int numTellers = 1;
    static public int numCustomers = 2;
    static public String[] customerTransactions = new String[numCustomers]; //to keep track of customers and their transactions
    static public ArrayList<Integer> waitingCustomers = new ArrayList<>(); //line of customers
    static public int[] customerToTeller = new int[numCustomers]; //to keep track of which tellers are serving which customers


    static public class Teller extends Thread {
        int id;

        Teller(int id) {
            this.id = id;
        }

        public void run() {
            try {
                System.out.println("Teller " + id + " has started their shift.");
                
                while (true) {
                    //Wait until customer signals this teller
                    tellerAvailable[id].acquire();
                    
                    //Check if all customers served - end condition
                    lineLock.acquire();
                    if (waitingCustomers.isEmpty() && allCustomersCreated) {
                        lineLock.release();
                        break;
                    }
                    
                    //If no customers in queue yet, release lock and try again
                    if (waitingCustomers.isEmpty()) {
                        lineLock.release();
                        tellerAvailable[id].release(); // Make teller available again
                        Thread.sleep(50);
                        continue;
                    }
                    
                    //Get next customer
                    int customerIndex = waitingCustomers.remove(0);
                    customerToTeller[customerIndex] = id;
                    lineLock.release();
                    
                    //Signal customer that teller is ready
                    System.out.println("Teller " + id + " [Customer " + customerIndex + "]: serving a customer");
                    customerReady[customerIndex].release();
                    
                    //Wait for customer to introduce themselves
                    customerDone[customerIndex].acquire();
                    
                    //teller is done with customer
                    System.out.println("Teller " + id + " []: finishes transaction");

                }
            } catch(Exception e) {
                System.err.println("Error in Teller " + id + ": " + e);
            }
        }
    }

    static public class Customer extends Thread {
        int id;
        String transactionType;

        Customer(int id, String type) {
            this.id = id;
            this.transactionType = type;
        }

        public void run() {
            try {
                //Decide transaction (already done in constructor)
                if (transactionType.equals("Deposit")) {
                    System.out.println("Customer " + id + " []: wants to perform a deposit transaction");
                } else {
                    System.out.println("Customer " + id + " []: wants to perform a withdrawal transaction");
                }
                
                //Wait between 0-100ms
                Thread.sleep(random.nextInt(101));

                //Go to bank
                System.out.println("Customer " + id + " []: going to bank.");

                //Wait at door (only 2 at a time)
                doorLock.acquire();
                
                //Enter bank
                System.out.println("Customer " + id + " []: entering bank.");

                //Get in line
                System.out.println("Customer " + id + " []: getting in line.");
                
                //Get in line
                lineLock.acquire();
                waitingCustomers.add(id);
                lineLock.release();

                //Wait for teller
                System.out.println("Customer " + id + " []: selecting a teller.");

                //Signal all tellers that there's a customer waiting
                for (int i = 0; i < numTellers; i++) {
                    tellerAvailable[i].release();
                }
                
                //Wait until a teller is ready
                customerReady[id].acquire();

                //Get assigned teller
                int myTeller = customerToTeller[id];

                //Select teller
                System.out.println("Customer " + id + " [Teller " + myTeller + "]: selects teller");
                
                //Introduce self
                System.out.println("Customer " + id + " [Teller " + myTeller + "]: introduces itself");
                customerDone[id].release();

                //Leave bank
                System.out.println("Customer " + id + " []: goes to door");
                System.out.println("Customer " + id + " []: leaves the bank");
                doorLock.release();
                
            } catch(Exception e) {
                System.err.println("Error in Customer " + id + ": " + e);
                doorLock.release();
            }
        }
    }

    static public boolean allCustomersCreated = false;

    static public void main(String[] args) {
        ArrayList<String> transactions = new ArrayList<>();
        transactions.add("Withdrawal");
        transactions.add("Deposit");
        
        //Initialize semaphores
        customerReady = new Semaphore[numCustomers];
        customerDone = new Semaphore[numCustomers];
        tellerAvailable = new Semaphore[numTellers];

        for (int i = 0; i < numCustomers; i++) {
            customerReady[i] = new Semaphore(0);
            customerDone[i] = new Semaphore(0);
        }

        for (int i = 0; i < numTellers; i++) {
            tellerAvailable[i] = new Semaphore(0);
        }

        Customer[] customers = new Customer[numCustomers];
        Teller[] tellers = new Teller[numTellers];

        //Create Tellers
        for (int i = 0; i < numTellers; i++) {
            tellers[i] = new Teller(i);
            tellers[i].start();
        }

        //Create Customers
        for (int i = 0; i < numCustomers; i++) {
            String transactionType = transactions.get(random.nextInt(transactions.size()));
            customerTransactions[i] = transactionType;
            customers[i] = new Customer(i, transactionType);
            customers[i].start();
        }

        allCustomersCreated = true;


        //Wait for customer threads to exit
        for (int i = 0; i < numCustomers; i++) {
            try {
                customers[i].join();
            } catch (InterruptedException e) {
                System.err.println("Error joining with Customer " + i + ": " + e);
            }
        }

        for (int i = 0; i < numTellers; i++) {
            tellerAvailable[i].release();
        }
        
        System.out.println("All customers have been processed.");

        //Wait for teller threads to exit
        for (int i = 0; i < numTellers; i++) {
            try {
                tellers[i].join();
            } catch (InterruptedException e) {
                System.err.println("Error joining with Teller " + i + ": " + e);
            }
        }
        
        System.out.println("All tellers have finished their shifts. Bank is closed.");
    }
}