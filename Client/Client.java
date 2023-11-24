package Client;
import Server.PrinterInterface;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {

    public static void main(String[] args) throws NotBoundException, IOException {

        PrinterInterface printerInt = (PrinterInterface) Naming.lookup("rmi://localhost:5099/hello");
        System.out.println("For demonstration purposes, login with username: admin2 and password: admin2: ");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        String token = printerInt.Login(username, password);
        printerInt.start(token);

        while(printerInt.isPrinterOnline()){
            System.out.println("What would you like to do: ");
            System.out.println("Press 1 to show the printer queue");
            System.out.println("Press 2 to print a document");
            System.out.println("Press 3 to move a docment to the top of the queue");
            System.out.println("Press 4 to restart the printer");
            System.out.println("Press 5 to get a printer status");
            System.out.println("Press 6 to show the printer configuration");
            System.out.println("Press 7 to set the printer configuration");
            System.out.println("Press 8 to add a new user");
            System.out.println("Press 9 to stop the printer");
            int printerCommand = scanner.nextInt();
            switch (printerCommand) {
                case 1 -> System.out.println(printerInt.queue("Printer 1", token));
                case 2 -> System.out.println(printerInt.print("File 1", "Printer 1", token));
                case 3 -> System.out.println(printerInt.topQueue("Printer 1", 2, token));
                case 4 -> System.out.println(printerInt.restart("Printer 1", token));
                case 5 -> System.out.println(printerInt.status("Printer 1", token));
                case 6 -> System.out.println(printerInt.readConfig("Printer 1", token));
                case 7 -> System.out.println(printerInt.setConfig("Printer 1", "Value", token));

                case 8 -> {
                    System.out.println("Choose a username: ");
                    String newUsername = scanner.next();
                    System.out.println("Choose a password: ");
                    String newPassword = scanner.next();
                    System.out.println("--- " + printerInt.AddUser(newUsername, newPassword, token));
                }
                case 9 -> System.out.println(printerInt.stop(token));
                default -> System.out.println("Invalid command");
            }
        }


        // test to see if user without token can get access:

        //System.out.println("New Test");

        //System.out.println("--- " + printerInt.AddUser("admin3", "admin3"));
        //String token2 = printerInt.Login("admin3", "admin3");
        //printerInt.start(token2);
        //System.out.println(printerInt.print("File 1", "Printer 1","wrong_token"));


    }
}