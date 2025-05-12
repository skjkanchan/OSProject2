# The Bank Simulation
This is a program that runs a simulation of the bank. It includes 3 bank tellers that work at the bank and process customer transactions. It also includes 50 customers that come through the bank. This program simulations the opening of the bank when the tellers are ready, the customers entering and lining up at the bank, the customers visiting a teller, the transactions getting processed, and the customers leaving the bank. In this simulation, the bank closes once all customers have been served. 
This program is implemented using Threads and Semaphores. The threads represent the tellers and customers and the semaphores control access to certain resources like shared data, the amount of poeple entering the bank or safe, and the communication between the tellers and the customers.

# The Teller 
In the Bank Simulation, Tellers are represented by threads. There are a total of 3 tellers at the bank. Once the tellers are created they send out a signal that they are ready to serve the customers. When all tellers are ready, the bank is open. When tellers are ready they are visited by one customer at a time. For each visit they get the customer's id and transaction and process it. When the transaction is a withdrawal they must get permission from the manager, and after they visit the safe to perform the transaction. If the transaction is a deposit, they can go straight to the safe. After the transaction is complete, they signal the customers that they can leave and then send a signal to all customers that they are available and ready to serve again. This repeats until all customers are served.

# The Customer
In the Bank Simulation, Customers are represented by threads. There are a total of 50 customers that come through the bank. Once the customers are created they randomly choose their transaction as either a withdrawal or deposit. Next, the go to the bank and attempt to enter. Only 2 customers can pass through the door though so they may have to wait. Once inside they enter a line and once at the front of the line they wait for an available teller. Once a teller signals that they're available the customer selects that teller and introduces themselves. After giving their transaction it is processed and the customer leaves the bank. Once all customers are served, the bank closes.

# How to run/compile the program
To Compile: Do the following for the Bank file:
- javac filename (Example: javac Bank.java)

To Run: Run the Bank Program with the name of the class which would be the same as the filename but without the .java:
- java classname (Example: java Bank)