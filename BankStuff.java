
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

/**
 * This program will simulate multiple users trying to access the same bank
 * account
 * 
 * @author Nikolo Sperberg & Sterling Rohlinger Date:
 */
class BankStuff {

	public static int numThreads;
	public static int numTrans;
	public static BankAccount account = new BankAccount();
	public static Semaphore sem = new Semaphore(1);
	static long startTime = System.nanoTime();

	public static void main(String args[]) {
		String variable = "NumThreads";// create string variable
		numThreads = promptUser(variable);// prompt user for numThreads
		variable = "NumTrans";// set string to new value
		numTrans = promptUser(variable);// prompt user for numTrans
		account.setBalance(getRand(numTrans * numThreads));
		BankThread[] threads = new BankThread[numThreads];// create thread array
		threads = makeThreads(threads);// create threads for thread array
		runThreads(threads);

	}

	/**
	 * This method prompts for user input
	 * 
	 * @param variable
	 * @return
	 */
	public static int promptUser(String variable) {
		int num = 0;// initiate variable
		System.out.print("Enter " + variable + ": ");// print message to console
		Scanner scanner = new Scanner(System.in);// create scanner
		String input = scanner.nextLine();// get user input
		if (checkInput(input)) {// check if user input is correct
			num = Integer.parseInt(input);// cast input to int
		} 
		else {
			System.out.println("Please input a number.");
			num = promptUser(variable);// lol recursion yo!
		}
		
		return num;
		
	}
	

	/**
	 * this method makes sure that the user enters numbers
	 * 
	 * @param input
	 * @return
	 */
	public static boolean checkInput(String input) {
		if (input.matches("[0-9]+")) {// if input has any chars besides 0-9
			return true;
		}
		return false;
	}

	/**
	 * this method creates a thread array full of threads. Neat!
	 * 
	 * @param numThreads
	 * @param threads
	 * @return
	 */
	public static BankThread[] makeThreads(BankThread[] threads) {
		for (int i = 0; i < numThreads; i++) {// loop through thread array
			BankThread t = new BankThread(account, numTrans, sem, startTime);// create new thread
			t.setID(t.getId());// hacky way of prinitng the thread ID
			threads[i] = t;// put new thread in thread array
		}
		return threads;// return array
	}

	/**
	 * this method loops through and runs all of the threads
	 * 
	 * @param threads
	 */
	public static void runThreads(BankThread[] threads) {
		for (int i = 0; i < numThreads; i++) {
			threads[i].start();// starts the treads
		}
	}

	/**
	 * Dr. Tan's random number generator
	 * 
	 * @param nonce
	 * @return
	 */
	public static long randNum(long nonce) {
		long a = 48271;
		long m = 2147483647;
		long q = m / a;
		long r = m % a;
		long state = -1;
		long t = a * (state % q) - r * (state / q);
		if (t > 0) {
			state = t;
		} 
		else {
			state = t + m;
		}
		return (long) (((double) state / m) * nonce);
	}

	/**
	 *
	 * this number get a number between 1 and 0 it uses time stamps and randNum to
	 * get this number
	 *
	 */
	public static double getTrans(int y) {
		double x = randNum(y);// get random number from Dr. Tans code
		while (x > 1) {// incase x is too big
			int temp = (int) randNum(y);// get a random number
			temp = temp % 2;// mod random number
			if (temp == 0) {
				x = x / 3;// divide x by 3
			} 
			else {
				x = x / 4;// divide x by 4
			}

		}
		return x;// return x
	}

	/**
	 *
	 * this method get a random number and uses a ranNum to make a random number
	 * this method make sure that the random number generate from randNum is within
	 * our rand of 300 to 1 it also uses getTrans to help
	 * widen the range of numbers
	 */
	public static int getRand(long num) {
		boolean flag = true;// boolean used to make sure number in specifide range
		double x = randNum(num); // get a random number
		double temp = 0;// initiate temp
		while (flag) {
			temp = getTrans((int) (num * x));// get another random number
			x = getTrans((int) x);// get a number between 1 and 0 based off perv gnereated random number
			if (temp > 0.5) {
				x = x * 100;// times number between 1 and 0 by 50
			} 
			else {
				x = x * 100;// times number between 1 and 0 by 100
			}
			if (x > 300) {
				x = x - 50;// minus 50 if too big
			} 
			else if (x <= 0) {
				x = x + 50;// add 50 if too small
			} 
			else {
				flag = false;// if number checks out exit while loop
			}
		}
		num = (long) (num + x);// add x to num
		temp = getTrans((int) num);// get ranom num between 1 and 0
		if (temp > 0.5) {
			num = num + 150;// add 150 to num
		} 
		else {
			num = num - 100;// subtract 100 from num
		}
		flag = true;// reste flag
		while (flag) {
			if (num > 300) {// if num too big
				num = num - 100;// minus 100
			} 
			else if (num <= 0) {// if num too small
				num = num + 100;// add 100
				num = num + randNum(num);// generate new random number
			} 
			else {// number checks out
				flag = false;// exit while loop
			}
		}
		return (int) num;// set num to x
	}
}

/**
 * this class is used to create BankThreads.
 * 
 * @author Nikolo
 *
 */
class BankThread extends Thread {
	public int numTrans;// number of transactions per thread
	public long id;// id of the thread
	public BankAccount account;// bank account object
	public Semaphore semaphore;// semaphore to restrict use of resources in critcal sections
	public long startTime;

	/**
	 * cunstructor for the BankThreads
	 * 
	 * @param a
	 * @param num
	 * @param sem
	 */
	public BankThread(BankAccount a, int num, Semaphore sem, long startT) {
		account = a;
		numTrans = num;
		semaphore = sem;
		startTime = startT;
	}

	/**
	 * hacky way to be able to print up the transaction ID
	 * 
	 * @param num
	 */
	public void setID(long num) {
		id = num;
	}

	/**
	 * this method runs the thread.
	 */
	public void run() {
		String temp = "";// temp tring used to hold information about deposits and withdrawls
		while (numTrans != 0) {// loop through number of transactions to be preformed
			int ammount = getRandNum();// get random number to be deposited or withdrawn
			double transaction = getTrans(numTrans * account.getBalance());// determine type of transaction
			if (transaction > 0.5) {// if 0 -- deposit
				try {
					semaphore.acquire();
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				} // wait until resource is free
				temp = withdraw(ammount);
				semaphore.release();
			} 
			else {// esle withdraw
				try {
					semaphore.acquire();
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				} // wait until resource is free
				temp = deposit(ammount);
				semaphore.release();
			}
			System.out.println("tID: " + id + " " + temp);
			temp = "";// clear out temp
			numTrans--;// decrement numTrans
		}
	}

	/**
	 * this method removes money from the balance if there are sufficient funds.
	 * 
	 * @param ammount
	 * @return
	 */
	public String withdraw(int ammount) {
		String withdraw = " - balance = " + account.getBalance() + ".00 | " + "withdraw $";// start of string to be returned																						
		if (account.getBalance() - ammount < 0) {// if not sufficient funds
			long endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			withdraw = withdraw + " $" + ammount + ".00 (NSF) | balance = " + account.getBalance() + ".00 | timestamp: "+ timeElapsed;;// print what would have been withdrawn																							
		} 
		else {// sufficient funds
			long endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			account.setBalance(account.getBalance() - ammount);// set new balance
			withdraw = withdraw + " $" + ammount + ".00 | balance = " + account.getBalance() + ".00 | timestamp: "+ timeElapsed;// add text to string
		}
		return withdraw;// return what had happened
	}

	/**
	 * this method adds money to the balance
	 * 
	 * @param ammount
	 * @return
	 */
	public String deposit(int ammount) {
		String deposit = " - balance = " + account.getBalance() + ".00 | " + "deposit $";// start of string to be returned
		account.setBalance(account.getBalance() + ammount);// set new balance
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		deposit = deposit + ammount;// add text to sting
		return deposit + " | balance = " + account.getBalance() + " | timestamp: " + timeElapsed;// return what had happened
	}

	/**
	 * method that gets a random number less the 300
	 * 
	 * @return
	 */
	public int getRandNum() {
		Random random = new Random();// create random object
		int num = random.nextInt(301);// get random number between 0 and 301
		return num;
	}

	/**
	 *
	 * this number get a number between 1 and 0 it uses time stamps and randNum to
	 * get this number
	 *
	 */
	public double getTrans(int y) {
		double x = randNum(y);// get random number from Dr. Tans code
		while (x > 1) {// incase x is too big
			int temp = (int) randNum(y);// get a random number
			temp = temp % 2;// mod random number
			if (temp == 0) {
				x = x / 3;// divide x by 3
			} 
			else {
				x = x / 4;// divide x by 4
			}

		}
		return x;// return x
	}

	/**
	 * 
	 * 
	 * @param nonce
	 * @return
	 */
	public static long randNum(long nonce) {
		long a = 48271;
		long m = 2147483647;
		long q = m / a;
		long r = m % a;
		long state = -1;
		long t = a * (state % q) - r * (state / q);
		if (t > 0) {
			state = t;
		} 
		else {
			state = t + m;
		}
		return (long) (((double) state / m) * nonce);
	}

	/**
	 *
	 * this method get a random number and uses a ranNum to make a random number
	 * this method make sure that the random number generate from randNum is within
	 * our rand of 300 to 1 it also uses time stamps as well as getTrans to help
	 * widen the rand of numbers
	 */
	public int getRand(long num) {
		boolean flag = true;// boolean used to make sure number in specifide range
		double x = randNum(num); // get a random number
		double temp = 0;// initiate temp
		while (flag) {
			temp = getTrans((int) (num * x));// get another random number
			x = getTrans((int) x);// get a number between 1 and 0 based off perv gnereated random number
			if (temp > 0.5) {
				x = x * 100;// times number between 1 and 0 by 50
			} 
			else {
				x = x * 100;// times number between 1 and 0 by 100
			}
			if (x > 300) {
				x = x - 50;// minus 50 if too big
			} 
			else if (x <= 0) {
				x = x + 50;// add 50 if too small
			} 
			else {
				flag = false;// if number checks out exit while loop
			}
		}
		num = (long) (num + x);// add x to num
		temp = getTrans((int) num);// get ranom num between 1 and 0
		if (temp > 0.5) {
			num = num + 150;// add 150 to num
		} 
		else {
			num = num - 100;// subtract 100 from num
		}
		flag = true;// reste flag
		while (flag) {
			if (num > 300) {// if num too big
				num = num - 100;// minus 100
			} 
			else if (num <= 0) {// if num too small
				num = num + 100;// add 100
				num = num + randNum(num);// generate new random number
			} 
			else {// number checks out
				flag = false;// exit while loop
			}
		}
		return (int) num;// set num to x
	}

}

/**
 * this class is used to keep track of the bank account. We made an object for
 * this data so all threads could touch it
 * 
 * @author Nikolo
 *
 */
class BankAccount {
	public int balance;// balance of the bank account

	/**
	 * this method sets the new balance
	 * 
	 * @param x
	 */
	public void setBalance(int x) {
		balance = x;
	}

	/**
	 * this method returns the current balance
	 * 
	 * @return
	 */
	public int getBalance() {
		return balance;
	}
}
