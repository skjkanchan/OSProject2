
public class Bank {

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

    public static void main(String args[]) { 
        Teller[] tellers = new Teller[2];
        for (int i = 0; i < 2; i++) {
            tellers[i] = new Teller(i + 1);
            tellers[i].start();
        }

    }



}






