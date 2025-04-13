# April 9 7:33pm

a. This is my first addition to the devlog. This project seems like it will be more complex than the first as it uses multithreading and semiphores. I have worked with these concepts a little before in my UNIX class, however not very deeply. Additionally, that was using C but I plan on using Java for this project as I am more familiar with it. 
b. This session I plan on importing the sample code from elearning and trying to just understand how it works. I will also setup my github repo and push this devlog. Additionally, I intend to add the file for this project. 

# April 9 10:42pm
I am now ending this project session. I added all the sample code and looked into it more to understand how it works. I also finished making my github repo and cloning it and I pushed this devlog. I also created the Bank.java file for the project. I was able to accomplish everything I intended. Next session I plan on looking at Project Overview video again and then beginning the code for the project. 


# April 10 4:45pm
a. I am starting a new project session. After watching the Project 2 overview video again and looking through the sample code I understand the project better. We have different threads, some of the Teller class and some of the Customer class. We are going to use signals to tell customers when the teller is ready. This is possibly using semaphores the way SignalDemo did but some comments said the project has too many threads for that so I will have to look more into that. 
b. This session I plan on creating some Tellers and some Customers and just working on the communication there. 


# April 10 6:25pm
I am now ending this project session. I created the Teller and Customer Class and the Transaction Types. Now Threads can start running for both, and the program is randomly choosing a transaction type. Next session I plan on integrating the semaphores to start communication between tellers and customers. 


# April 10 8:36pm
a. I am starting a new project session. Since last project session I have learned how semaphores will act as signals throughout the program to let tellers and customers know when they are ready.
b. This session I plan on working on the communication between the tellers and the customers and getting the line function for customers seeing tellers to be functional. 

# April 10 10:31pm
I am now ended this project session. I was working on the communication between the Teller and Customer and it still isn't working. Currently I'm trying to get the customer to introduce themselves but I'm getting lots of errors. I will try again in tomorrow's project session. 

# April 11 11:30am
a. I am now starting a new project session. I don't have any new thoughts on this project except that I'm a little worried about the complexity and the fact that I'm running into a lot of errors. 
b. This session I plan on continuing the communication between the Teller and the Customer and I want to get the customer to introudce themselves to the Teller. 

# April 11 1:47pm
I am now ending this project session. The communication of the customers introducing themselves seems to be working so I will now push my current code. Next session I plan to flesh out the communication better so it's more back and forth. 

# April 11 11:12pm
a. I am starting a new project session. Since last project session I dont really have any new thoughts, but I am hoping I can get the communication figured out as that seems to be the main aspect of this project. 
b. This project session I plan on fleshing out the communication more. Right now customers can introduce themselves to the Tellers, but tellers cannot respond and don't have access to the customer's information. This is what I want to accomplish. 

# April 12 2:14am
I am now ending this project session. The Tellers now have access to the customer's id and their transaction type. I also ended up running into an issue with the program terminating and the customers being served out of order. I ended up fixing this issue so now customers are being served in order and I also implemented mutual exclusion to access of shared data. I also ended up duplicating ThreadDemo.java to ThreadTest.java and am now working on my code there as Bank.java was giving me lots of isses. 

# April 12 7:14pm
a. I am starting a new project session. I don't really have any new thoughts since the last session, but I do understand how semaphore's work better than before last session.
b. This project session I plan on implementing part of the actual transaction. This includes getting the manager approval and entering the safe. I'm not sure if I will finish all of this during this project session, but I plan to do as much as I can. 


# April 13 12:04am
I am now ending this project session. I actually pivoted from doing manager approval to fully fleshing out the code I currently had. This included making the print statements the correct format and giving each teller access to the customer ID through a shared array. This made it easier to pass information from customer through teller when the customer introduces themselves. To accomplish this I ended up creating more semaphores like doorLock to make sure only 2 customers are entering the door, i chaned customerQueue to lineLock to be a more intuitive name, and I created arrays of semaphores to signal when customers are ready, done, and when tellers are available.  


# April 13 2:43pm
a. I am starting a new project session. Since last session I have thought about how the project will be continued and I think it will be more simple from here on out. Now that I've figured out the communication, sharing of variables, and access to data using semaphores I just need to repeat it for aspects like the manager, and safe. 
b. This project session I plan to implement the transaction aspect including the interaction with the manager and accessing the safe. I will do this using new semaphores to make sure only one teller can talk to the manager at once, and only 2 tellers can acesss the safe at once. 



