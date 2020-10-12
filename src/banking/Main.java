package banking;

public class Main {

    public static void main(String[] args) {

        UI userInterface = new UI();
        if (args[0].equals("-fileName") && args[1] != null) {
            userInterface.start(args[1]);
        } else {
            System.out.println("Error! Command line arguments don't contain -filename, exiting...");
        }

    }
}