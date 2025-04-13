import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Bank {
    static Random random = new Random();
    
    //semaphores
    static public Semaphore dataLock = new Semaphore(1); // For controlling accesss to shared data
    static public Semaphore doorLock = new Semaphore(2); // Door allows 2 customers at a time
    static public Semaphore lineLock = new Semaphore(1); // For accessing the line safely
    static public Semaphore safeLock = new Semaphore(2); // Safe allows 2 tellers at a time
    static public Semaphore bankOpen = new Semaphore(0); // For controlling when customers can enter bank
    static public Semaphore bankClosedLock = new Semaphore(1); // To make sure program termiantes correctly
    static public Semaphore[] customerReady = null; // Array of semaphores indicating customer readiness
    static public Semaphore[] tellerAvailable = null; // Array of semaphores indicating teller availability
    static public Semaphore[] customerDone = null; // Signals that customer is done with transaction
    static public Semaphore managerLock = new Semaphore(1); // Only one teller can talk to manager at a time
    

    //shared data
    static public boolean bankClosed = false;
    static public int customersServed = 0;
    static public int numTellers = 3;
    static public int numCustomers = 50;
    static public int tellersReady = 0;
    static public String[] customerTransactions = new String[numCustomers]; //keeps track of customer transactions
    static public ArrayList<Integer> waitingCustomers = new ArrayList<>(); //line of customers
    static public int[] customerToTeller = new int[numCustomers]; //keeps track of customers and their tellers


    static public class Teller extends Thread {
        int id;

        Teller(int id) {
            this.id = id;
        }

        public void run() {
            try {
                while (true) {

                    dataLock.acquire();
                    if (customersServed < numCustomers) {
                        System.out.println("Teller " + id + " []: ready to serve");
                        System.out.println("Teller " + id + " []: waiting for customer");
                    }
                    dataLock.release();
                    
                    
                    
                    //make sure all tellers are ready before opening bank
                    dataLock.acquire();
                    tellersReady++;
                    if (tellersReady == numTellers) {
                        //open the bank
                        bankOpen.release();
                    }

                    dataLock.release(); 

                    //wait for customer to signal they are ready
                    tellerAvailable[id].acquire();

                    //check if all customers have been served, if so end the loop
                    lineLock.acquire();
                    bankClosedLock.acquire();
                    if (bankClosed || (waitingCustomers.isEmpty() && allCustomersCreated)) {
                        bankClosedLock.release();
                        lineLock.release();
                        break;
                    }
                    bankClosedLock.release();
                    
                    //If no customers in queue yet, release lock and signal for customers again
                    if (waitingCustomers.isEmpty()) {
                        lineLock.release();
                        tellerAvailable[id].release(); 
                        Thread.sleep(50); //wait a little before checking for customers again
                        continue;
                    }
                    
                    //get first customer from the line
                    int customerIndex = waitingCustomers.remove(0);
                    customerToTeller[customerIndex] = id;
                    lineLock.release();
                    
                    //signal to customer that teller is ready to interact with customer
                    customerReady[customerIndex].release();
                    
                    //wait for customer to introduce themselves
                    customerDone[customerIndex].acquire();
                    System.out.println("Teller " + id + " [Customer " + customerIndex + "]: serving a customer");

                    
                    //ask customer for transaction
                    System.out.println("Teller " + id + " [Customer " + customerIndex + "]: asks for transaction");
                    customerReady[customerIndex].release();
                    
                    //wait for customer to give transaction
                    customerDone[customerIndex].acquire();
                    
                    //handle the transaction
                    String transaction = customerTransactions[customerIndex];
                    System.out.println("Teller " + id + " [Customer " + customerIndex + "]: handling " + transaction + " transaction");

                    
                    //for withdrawals, get manager permission
                    if (transaction.equals("withdrawal")) {
                        System.out.println("Teller " + id + " [Customer " + customerIndex + "]: going to manager");
                        
                        //only one teller can talk to the manager at a time
                        managerLock.acquire();

                        System.out.println("Teller " + id + " [Customer " + customerIndex + "]: getting manager's permission");

                        //wait for teller to get permission (5-30ms)
                        Thread.sleep(random.nextInt(26) + 5);
                        
                        System.out.println("Teller " + id + " [Customer " + customerIndex + "]: got manager's permission");
                        
                        // Done with manager
                        managerLock.release();
                    }
                    
                    //go to safe
                    System.out.println("Teller " + id + " [Customer " + customerIndex + "]: going to safe");

                    //enter safe, 2 at a time
                    safeLock.acquire();
                    System.out.println("Teller " + id + " [Customer " + customerIndex + "]: enter safe");

                    //random transaction time from 10 to 50 ms
                    Thread.sleep(random.nextInt(41) + 10);

                    //leave safe
                    System.out.println("Teller " + id + " [Customer " + customerIndex + "]: leaving safe");
                    safeLock.release();

                    //finishes transaction
                    System.out.println("Teller " + id + " [Customer " + customerIndex + "]: finishes " + transaction + " transaction");
                    
                    //signal to customer that transaction is done
                    customerReady[customerIndex].release();

                    //wait for customer to leave
                    System.out.println("Teller " + id + " [Customer " + customerIndex + "]: waiting for customer to leave");
                    customerDone[customerIndex].acquire();

                    //track that another customer has been served
                    dataLock.acquire();
                    customersServed++;
                    dataLock.release();
                    
                    //wait for next customer
                    tellerAvailable[id].release();
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
                //customer announces transaction (randomly chosen in constructor)
                if (transactionType.equals("deposit")) {
                    System.out.println("Customer " + id + " []: wants to perform a deposit transaction");
                } else {
                    System.out.println("Customer " + id + " []: wants to perform a withdrawal transaction");
                }

                //custimer goes to bank
                System.out.println("Customer " + id + " []: going to bank.");
                
                //wait 0-100ms before entering bank
                Thread.sleep(random.nextInt(101));
                

                //wait at door (only 2 at a time)
                doorLock.acquire();
                
                //customer enter bank
                System.out.println("Customer " + id + " []: entering bank.");

                //customer gets in line
                System.out.println("Customer " + id + " []: getting in line.");

                lineLock.acquire();
                waitingCustomers.add(id);
                lineLock.release();

                //customer waits in line for until teller is available
                System.out.println("Customer " + id + " []: selecting a teller.");

                //signal all tellers that a customer is waiting
                for (int i = 0; i < numTellers; i++) {
                    tellerAvailable[i].release();
                }
                
                //wait until a teller is ready
                customerReady[id].acquire();

                //get available teller
                int myTeller = customerToTeller[id];

                System.out.println("Customer " + id + " [Teller " + myTeller + "]: selects teller");
                
                //customer introduces themselves to teller
                System.out.println("Customer " + id + " [Teller " + myTeller + "]: introduces itself");
                customerDone[id].release();

                //customer waits for teller to ask for transaction
                customerReady[id].acquire();

                //customer asks for transaction
                if (transactionType.equals("deposit")) {
                    System.out.println("Customer " + id + " [Teller " + myTeller + "]: asks for deposit transaction");
                } else {
                    System.out.println("Customer " + id + " [Teller " + myTeller + "]: asks for withdrawal transaction");
                }
                customerDone[id].release();

                //customer waits for transaction to complete
                customerReady[id].acquire();

                //customer leaves teller
                System.out.println("Customer " + id + " [Teller " + myTeller + "]: leaves teller");
                customerDone[id].release();

                //customer goes to door and leaves bank
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
        transactions.add("withdrawal");
        transactions.add("deposit");
        
        //initialize semaphores
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
        
        //create Tellers
        Teller[] tellers = new Teller[numTellers];
        for (int i = 0; i < numTellers; i++) {
            tellers[i] = new Teller(i);
            tellers[i].start();
        }
        
        try {
            //wait for all tellers to be ready before opening bank
            bankOpen.acquire();
        } catch (InterruptedException ex) {
            System.err.println("Error with Semaphore bankOpen: " + ex);
        }

       
        //create Customers
        Customer[] customers = new Customer[numCustomers];
        for (int i = 0; i < numCustomers; i++) {
            String transactionType = transactions.get(random.nextInt(transactions.size()));
            customerTransactions[i] = transactionType;
            customers[i] = new Customer(i, transactionType);
            customers[i].start();
        }

        allCustomersCreated = true;

        //wait for customer threads to exit
        for (int i = 0; i < numCustomers; i++) {
            try {
                customers[i].join();
            } catch (InterruptedException e) {
                System.err.println("Error joining with Customer " + i + ": " + e);
            }
        }

        System.out.println("All customers have been served. Bank is closed.");

        try {
            bankClosedLock.acquire();
            bankClosed = true;
            bankClosedLock.release();
        } catch (InterruptedException ex) {
        }
        
        //signal all tellers to check if they should terminate
        for (int i = 0; i < numTellers; i++) {
            tellerAvailable[i].release();
        }

        //wait for teller threads to exit
        for (int i = 0; i < numTellers; i++) {
            try {
                tellers[i].join();
            } catch (InterruptedException e) {
                System.err.println("Error joining with Teller " + i + ": " + e);
            }
        }




    
    }
}