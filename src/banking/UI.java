package banking;

import org.sqlite.SQLiteDataSource;

import java.util.Random;
import java.util.Scanner;

public class UI {
    private static Database db;
    private Account activeAccount;

    public void start(String databaseName) {
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl("jdbc:sqlite:" + databaseName);
        db = new Database(sqLiteDataSource);
        db.init();
        printMenu();
    }

    private void printMenu() {
        boolean loggedIn = false;
        Scanner scan = new Scanner(System.in);

        loop:
        while (true) {
            // Client is not logged in, so he can only create new acc or log in
            if (!loggedIn) {
                System.out.println("1. Create account\n" +
                        "2. Log into account\n" +
                        "0. Exit");
                int choice = scan.nextInt();
                scan.nextLine();
                switch (choice) {
                    case 0:
                        break loop;
                    case 1:
                        createNewAccount();
                        break;
                    case 2:
                        loggedIn = login();
                        break;
                    default:
                        System.out.println("Wrong input, try again!");
                        break;
                }
                // User logged in, he can check balance, add income, make a transfer,
                // log out or close his account
            } else {
                System.out.println("\n" +
                        "1. Balance\n" +
                        "2. Add income\n" +
                        "3. Do transfer\n" +
                        "4. Close account\n" +
                        "5. Log Out\n" +
                        "0. Exit");
                int choice = scan.nextInt();
                scan.nextLine();
                switch (choice) {
                    case 0:
                        break loop;
                    case 1:
                        getBalance(activeAccount);
                        break;
                    case 2:
                        addIncome(activeAccount);
                        break;
                    case 3:
                        transferMoney(activeAccount);
                        break;
                    case 4:
                        closeAccount(activeAccount);
                        loggedIn = false;
                        activeAccount = null;
                        break;
                    case 5:
                        loggedIn = false;
                        activeAccount = null;
                        System.out.println("You have successfully logged out!");
                        break;
                    default:
                        System.out.println("Wrong input, try again!");
                        break;
                }
            }

        }
        System.out.println("Bye!");
    }

    // Returns true if the card number and pin code are valid, false otherwise
    private boolean login() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your card number:");
        String cardNumber = scan.nextLine();
        System.out.println("Enter your PIN:");
        int pinCode = scan.nextInt();
        scan.nextLine();

        activeAccount = db.findByNumber(cardNumber);
        if (activeAccount != null && activeAccount.getPin() == pinCode) {
            System.out.println("\nYou have successfully logged in!\n");
            return true;
        } else {
            System.out.println("\nWrong card number or PIN!\n");
            return false;
        }
    }

    // Generates new account and adds it to the database
    // Card number always starts with "400000", last number is checked by luhn algorithm
    private void createNewAccount() {
        Random random = new Random();
        int pin = random.nextInt(9000) + 1000;
        StringBuilder name = new StringBuilder("400000");

        for (int i = 0; i < 9; i++) {
            int randomNumber = random.nextInt(10);
            name.append(randomNumber);
        }
        int lastDigit = luhnAlgorithm(name.toString());
        name.append(lastDigit);
        Account newAccount = new Account(name.toString(), pin);
        db.addNewAccount(newAccount);

        System.out.println("Your card have been created\n" +
                "Your card number:");
        System.out.println(name.toString());
        System.out.println("Your card PIN:");
        System.out.println(pin);
        System.out.println();
    }

    // Generates last digit of card number using luhn algorithm
    private int luhnAlgorithm(String cardNumber) {
        int[] array = new int[cardNumber.length()];
        int sum = 0;
        for (int i = 0; i < cardNumber.length(); i++) {
            array[i] = Character.getNumericValue(cardNumber.charAt(i));
            if (i % 2 == 0) {
                array[i] = 2 * array[i];
            }
            if (array[i] > 9) {
                array[i] -= 9;
            }
            sum += array[i];
        }
        int lastDigit = sum % 10;
        return lastDigit == 0 ? 0 : Math.abs(10 - lastDigit);
    }

    // Add income to the account
    private void addIncome(Account account) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter income:");
        int income = scan.nextInt();
        scan.nextLine();
        db.addIncomeToAccount(income, account.getCardNumber());
        System.out.println("Income was added!");
    }

    // Get balance of the account
    private void getBalance(Account account) {
        int balance = db.getBalance(account.getCardNumber());
        System.out.println("Balance: " + balance);
    }

    // Close the account
    private void closeAccount(Account account) {
        db.closeAccount(account.getCardNumber());
        System.out.println("The account has been closed!");
    }

    // Makes a transfer from one account to another one
    private void transferMoney(Account account) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Transfer \n Enter card number:");
        String cardNumberToTransfer = scan.nextLine();
        if (cardNumberToTransfer.equals(account.getCardNumber())) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!checkCardNumber(cardNumberToTransfer)) {
            System.out.println("Probably you made mistake in the card number. Please try again!");
        } else if (db.getBalance(cardNumberToTransfer) == -1) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int amount = scan.nextInt();
            scan.nextLine();
            if (amount > db.getBalance(account.getCardNumber())) {
                System.out.println("Not enough money!");
            } else {
                db.addIncomeToAccount(amount, cardNumberToTransfer);
                db.withdrawMoney(amount, account.getCardNumber());
                System.out.println("Success!");
            }
        }
    }

    // Checks if the card number is valid using luhn algorithm
    private boolean checkCardNumber(String cardNumber) {
        if (cardNumber.length() != 16) {
            return false;
        } else {
            String first_15 = cardNumber.substring(0, 15);
            int lastDigit = Integer.parseInt(cardNumber.substring(15));
            return luhnAlgorithm(first_15) == lastDigit;
        }
    }
}