package ATM;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;

public class OptionMenu {
	Scanner menuInput = new Scanner(System.in);
	DecimalFormat moneyFormat = new DecimalFormat("'$'###,##0.00");
	HashMap<Integer, ArrayList<Account>> data = new HashMap<>();
	// maybe two is necessary in the future but right now it's not really
	StringBuilder logTransactions = new StringBuilder();
	StringBuilder curAccTransactions = new StringBuilder();
	private final String savedAccountInfo = "acc_info.txt";

	public void getLogin() throws IOException {
		boolean end = false;
		int customerNumber = 0;
		int pinNumber = 0;
		while (!end) {
			try {
				System.out.print("\nEnter your customer number: ");
				customerNumber = menuInput.nextInt();
				System.out.print("\nEnter your PIN number: ");
				pinNumber = menuInput.nextInt();
				Iterator it = data.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					ArrayList<Account> acc = (ArrayList<Account>) pair.getValue();
					if (data.containsKey(customerNumber) && pinNumber == acc.get(0).getPinNumber()) {
						getTransactionType(acc);
						end = true;
						break;
					}
				}
				if (!end) {
					System.out.println("\nWrong Customer Number or Pin Number");
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Character(s). Only Numbers.");
			}
		}
	}

	// modifying this so it can take an Array List
	public void getTransactionType(ArrayList<Account> accounts) {
		boolean end = false;
		while (!end) {
			try {
				System.out.println("\nSelect the transaction you would like to do: ");
				System.out.println(" Type 1 - Access Individual Accounts");
				System.out.println(" Type 2 - Check All Account Balances");
				System.out.println(" Type 3 - View All Transactions");
				System.out.println(" Type 4 - Exit");
				System.out.print("\nChoice: ");

				int selection = menuInput.nextInt();

				switch (selection) {
				case 1:
					pickAccount(accounts);
					break;
				case 2:
					// Iterate through the ArrayList
					for(Account acc : accounts){
						System.out.println("\nChecking Account Balance: " + moneyFormat.format(acc.getCheckingBalance()));
						System.out.println("Savings Account Balance: " + moneyFormat.format(acc.getSavingBalance()));
					}
					// log the current action
					logAction(curAccTransactions, "Viewed All Account Balances for %d.", accounts.get(0).getCustomerNumber());
					break;
				case 3:
					System.out.println("\nTransactions since login: \n");
					if(curAccTransactions.toString().equals("")){
						System.out.println("None yet!");
					}
					else{
						System.out.print(curAccTransactions.toString());
					}
					break;
				case 4:
					end = true;
					break;
				default:
					System.out.println("\nInvalid Choice.");
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
	}

	public void pickAccount(ArrayList<Account> accounts){
		boolean end = false;
		while (!end) {
			try {
				System.out.println("\nSelect the transaction you would like to do: ");
				System.out.println(" Type 0 - Exit");
				System.out.printf(" Type 1 up until %d for which specific account you'd like to access", accounts.size());
				System.out.print("\n\nChoice: ");

				int selection = menuInput.nextInt();

				if(selection == 0){
					end = true;
				}
				else if (1 <= selection && selection <= accounts.size()) {
					chooseCheckingOrSaving(accounts.get(selection - 1));
				}
				else{
					System.out.println("\nInvalid Choice.");
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
	}

	public void chooseCheckingOrSaving(Account acc) {
		boolean end = false;
		while (!end) {
			try {
				System.out.println("\nSelect the sub-account you want to access: ");
				System.out.println(" Type 1 - Checking Account");
				System.out.println(" Type 2 - Savings Account");
				System.out.println(" Type 3 - Exit");
				System.out.print("\nChoice: ");

				int selection = menuInput.nextInt();

				switch (selection) {
					case 1:
						getChecking(acc);
						break;
					case 2:
						getSaving(acc);
						break;
					case 3:
						end = true;
						break;
					default:
						System.out.println("\nInvalid Choice.");
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
	}

	public void getChecking(Account acc) {
		boolean end = false;
		while (!end) {
			try {
				System.out.println("\nChecking Account: ");
				System.out.println(" Type 1 - View Balance");
				System.out.println(" Type 2 - Withdraw Funds");
				System.out.println(" Type 3 - Deposit Funds");
				System.out.println(" Type 4 - Transfer Funds");
				System.out.println(" Type 5 - Exit");
				System.out.print("\nChoice: ");

				int selection = menuInput.nextInt();
				// setting of the message for logging
				String message = null;

				switch (selection) {
				case 1:
					System.out.println("\nChecking Account Balance: " + moneyFormat.format(acc.getCheckingBalance()));
					message = "Viewed Checking Account %d balance for %d.";
					break;
				case 2:
					acc.getCheckingWithdrawInput();
					message = "Withdrew from Checking Account %d for %d.";
					break;
				case 3:
					acc.getCheckingDepositInput();
					message = "Deposit into Checking Account %d for %d.";
					break;
				case 4:
					acc.getTransferInput("Checking");
					message = "Transfer from Savings to Checking Account %d for %d.";
					break;
				case 5:
					end = true;
					break;
				default:
					System.out.println("\nInvalid Choice.");
				}
				if(message != null) {
					logAction(curAccTransactions, message, acc.getAccountNumber(), acc.getCustomerNumber());
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
	}

	public void getSaving(Account acc) {
		boolean end = false;
		while (!end) {
			try {
				System.out.println("\nSavings Account: ");
				System.out.println(" Type 1 - View Balance");
				System.out.println(" Type 2 - Withdraw Funds");
				System.out.println(" Type 3 - Deposit Funds");
				System.out.println(" Type 4 - Transfer Funds");
				System.out.println(" Type 5 - Exit");
				System.out.print("\nChoice: ");

				int selection = menuInput.nextInt();
				// setting the message to write to the log file
				String message = null;

				switch (selection) {
				case 1:
					System.out.println("\nSavings Account Balance: " + moneyFormat.format(acc.getSavingBalance()));
					message = "Viewed Savings Account %d balance for %d.";
					break;
				case 2:
					acc.getsavingWithdrawInput();
					message = "Withdrew from Savings Account %d for %d.";
					break;
				case 3:
					acc.getSavingDepositInput();
					message = "Deposit into Savings Account %d for %d.";
					break;
				case 4:
					acc.getTransferInput("Savings");
					message = "Transfer from Savings to Checking Account %d for %d.";
					break;
				case 5:
					end = true;
					break;
				default:
					System.out.println("\nInvalid Choice.");
				}
				if(message != null) {
					// log the current action if there is one
					logAction(curAccTransactions, message, acc.getAccountNumber(), acc.getCustomerNumber());
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
	}

	public void createAccount() throws IOException {
		int cst_no = 0;
		boolean newAccount = false;
		boolean end = false;
		while (!end) {
			try {
				System.out.println("\nEnter your customer number ");
				cst_no = menuInput.nextInt();
				Iterator it = data.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					if (!data.containsKey(cst_no)) {
						newAccount = true;
						end = true;
					}
				}
				if (!end) {
					System.out.println("\nThis customer number is already registered");
					System.out.println("Would you like a new account? (y/n)");

					// throwing away the new line
					menuInput.nextLine();
					String response = menuInput.nextLine();

					if(response.charAt(0) == 'y'){
						end = true;
					}
				}
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
		int pin;
		int accNum;
		// do different things for new users versus old users
		if(newAccount) {
			System.out.println("\nEnter PIN to be registered");
			pin = menuInput.nextInt();
			data.put(cst_no, new ArrayList<>());
			accNum = 1;
		}
		else {
			pin = data.get(cst_no).get(0).getPinNumber();
			accNum = data.get(cst_no).size() + 1;
		}
		data.get(cst_no).add(new Account(cst_no, pin, accNum));
		System.out.println("\nYour new account has been successfully registered!");

		// log the current action
		logAction(logTransactions, "Created account number %d for %d.", accNum, cst_no);

		System.out.println("\nRedirecting to login.............");
		getLogin();
	}

	public void mainMenu() throws IOException {
		readAccountData();

		boolean end = false;
		while (!end) {
			try {
				System.out.println("\n Type 1 - Login");
				System.out.println(" Type 2 - Create Account");
				System.out.print("\nChoice: ");
				int choice = menuInput.nextInt();
				switch (choice) {
				case 1:
					getLogin();
					end = true;
					break;
				case 2:
					createAccount();
					end = true;
					break;
				default:
					System.out.println("\nInvalid Choice.");
				}
				// if you make it here, you've probably logged out and we should move that over to the other string builder
				logTransactions.append(curAccTransactions);
			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice.");
				menuInput.next();
			}
		}
		System.out.println("\nThank You for using this ATM.\n");
		menuInput.close();
		
		saveAccountData();
		printLogFile();
		
		System.exit(0);
	}

	private void saveAccountData() {
		try{
			PrintWriter fileOut = new PrintWriter(savedAccountInfo);
			StringBuilder sb = new StringBuilder();
			// Print the info to the file
			for(int accountNumber : data.keySet()){
				ArrayList<Account> accs = data.get(accountNumber);
				sb.append(accountNumber);
				sb.append(',');
				sb.append(accs.get(0).getPinNumber());
				sb.append(',');
				// iterate through all accounts on the array list
				for(Account a : accs){
					sb.append(a.getCheckingBalance());
					sb.append(',');
					sb.append(a.getSavingBalance());
					sb.append(',');
				}
				sb.deleteCharAt(sb.lastIndexOf(","));
				sb.append('\n');
			}
			fileOut.print(sb.toString());
			fileOut.close();
		}
		catch (IOException e) {
			System.out.println("File not found");
		}
	}

	private void readAccountData() {
		try {
			Scanner fileIn = new Scanner(new File(savedAccountInfo));

			while (fileIn.hasNext()) {
				// Reads the current line
				String[] csvFileLine = fileIn.nextLine().split(",");
				if(csvFileLine.length != 4){
					// every line should always have 4 values
					continue;
				}
				double[] accountInfo = new double[csvFileLine.length];
				// Parse the strings as integers
				for(int i = 0; i < csvFileLine.length; i++){
					accountInfo[i] = Double.valueOf(csvFileLine[i]);
				}
				int cst_no = (int) accountInfo[0];
				int pin = (int) accountInfo[1];
				data.put(cst_no, new ArrayList<>());
				// iterate through all accounts on the array list
				for(int i = 2; i < accountInfo.length; i += 2){
					data.get(cst_no).add(new Account(cst_no, pin, accountInfo[i], accountInfo[i+1], i / 2));
				}
			}
			fileIn.close();
		}
		catch (IOException e) {
			System.out.println("File not found");
		}

		// just in case we don't have any data in there
		if(data.isEmpty()){
			System.out.println("Reading in default accounts");

			data.put(952141, new ArrayList<>());
			data.put(123, new ArrayList<>());
			data.get(952141).add(new Account(952141, 191904, 1000, 5000));
			data.get(123).add(new Account(123, 123, 20000, 50000));
		}
	}

	private void logAction(StringBuilder sb, String taskMessage, int customerNumber) {
		sb.append(String.format(taskMessage, customerNumber));
		sb.append("\n");
	}

	private void logAction(StringBuilder sb, String taskMessage, int customerNumber, int accNumber) {
		sb.append(String.format(taskMessage, customerNumber, accNumber));
		sb.append("\n");
	}

	private void printLogFile() {
		PrintWriter fileOut = null;
		try {
			fileOut = new PrintWriter("logfiles.txt");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		fileOut.print(logTransactions.toString());
		// Close out file
		fileOut.close();
	}
}
