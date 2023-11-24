package Server;

import Database.RBACManager;
import Database.DatabaseManager;
import Database.TokenManager;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class PrinterImplementation extends UnicastRemoteObject implements PrinterInterface {

    private boolean printerOnline = false;

    public Boolean isPrinterOnline() {
        return printerOnline;
    }
    private Map<String, Deque<String>> mapOfPrinters;
    private Map<String, String> config = new HashMap<>();
    private TokenManager tokenManager;
    private DatabaseManager dbm;

    public PrinterImplementation() throws RemoteException {
        super();
        this.mapOfPrinters = new HashMap<>();
        this.tokenManager = new TokenManager();
        this.dbm = new DatabaseManager();
    }

    public String echo(String input) throws RemoteException {
        return null;
    }

    public String print(String filename, String printer, String token) throws RemoteException {
        if (!tokenManager.ConfirmToken(token)) {
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("hello" + token_username);
            System.out.println("A user has NOT been authenticated to use the print method");
            return "The user is not authenticated";
        }

        if (RBACManager.findUserAccess(tokenManager.getUsernameFromToken(token), "print")) {
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("User with user name: " + token_username + " has been authenticated to use the print method");

            if (printerOnline) {
                mapOfPrinters.computeIfAbsent(printer, k -> new LinkedList<>()).add(filename);
                return filename + " has been added to " + (mapOfPrinters.containsKey(printer) ? "existing" : "new") + " printer " + printer;
            } else {
                return "The printer has not been started yet";
            }
        } else{
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("The role of: " + token_username + " does NOT have permission to use the print method");
            return "You are not authorized for this action";
        }
    }

    public String queue(String printer, String token) throws RemoteException {
        if (!tokenManager.ConfirmToken(token)) {
            System.out.println("The session timed out.");

            return "Please log in again";
        }
        if (RBACManager.findUserAccess(tokenManager.getUsernameFromToken(token), "queue")) {
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("User with user name: " + token_username + " has been authenticated to use the queue method");

            if (printerOnline) {
                Deque<String> printerQueue = mapOfPrinters.get(printer);
                if (printerQueue != null) {
                    StringBuilder jobs = new StringBuilder("Job Number:\n");
                    int jobNumber = 1;
                    for (String job : printerQueue) {
                        jobs.append(jobNumber).append(" - ").append(job).append("\n");
                        jobNumber++;
                    }
                    return "Printer: " + printer + " jobs: " + jobs.toString();
                } else {
                    return "The printer: " + printer + " is not avaliable";
                }
            } else {
                return "The printer has not been started yet";
            }
        } else{
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("The role of: " + token_username + " does NOT have permission to use the queue method");
            return "You are not authorized for this action";
        }

    }

    public String topQueue(String printer, int job, String token) throws RemoteException {
        if (!tokenManager.ConfirmToken(token)) {
            System.out.println("A user has NOT been authenticated to use the topQueue method");
            return "The user is not authenticated";
        }

        if (RBACManager.findUserAccess(tokenManager.getUsernameFromToken(token), "topQueue")) {
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("User with user name: " + token_username + " has been authenticated to use the topQueue method");
            if (printerOnline) {
                Deque<String> printerQueue = mapOfPrinters.get(printer);
                if (printerQueue != null && job >= 1 && job <= printerQueue.size()) {
                    List<String> jobs = new ArrayList<>(printerQueue);
                    String removedJob = jobs.remove(job - 1);
                    jobs.add(0, removedJob);
                    printerQueue.clear();
                    printerQueue.addAll(jobs);
                    return "The job with number "+ job + " has been shifted to the top position in the printer queue of " + printer;
                } else {
                    return "The job with number " + job + " cannot be prioritized at the top of the queue because either the printer " + printer + " does not exist or the job number is invalid";
                }
            } else {
                return "The job with number " + job + " is offline, and so it cant be placed at the top of the queue";
            }
        } else{
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("The role of: " + token_username + " does NOT have permission to use the topQueue method");
            return "You are not authorized for this action";
        }
    }


    public String start(String token) throws RemoteException { // Start skal kun kunne udføres af hvisse brugere....?
        if (!tokenManager.ConfirmToken(token)) {
            System.out.println("A user has NOT been authenticated to use the start method");

            return "The user is not authenticated";
        }
        String token_username = tokenManager.getUsernameFromToken(token);
        System.out.println("User with user name: " + token_username + " has been authenticated to use the start method");
        printerOnline = true;
        return "Printer has been turned on";
    }

    public String stop(String token) throws RemoteException {
        if (!tokenManager.ConfirmToken(token)) {
            System.out.println("A user has NOT been authenticated to use the stop method");

            return "The user is not authenticated";
        }

        if (RBACManager.findUserAccess(tokenManager.getUsernameFromToken(token), "stop")) {
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("User with user name: " + token_username + " has been authenticated to use the stop method");
            printerOnline = false;
            return "Printer has been turned off";
        } else{
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("The role of: " + token_username + " does NOT have permission to use the AddUser method");
            return "You are not authorized for this action";
        }
    }

    public String restart(String printer, String token) throws RemoteException {
        if (!tokenManager.ConfirmToken(token)) {
            System.out.println("A user has NOT been authenticated to use the restart method");

            return "The user is not authenticated";
        }
        if (RBACManager.findUserAccess(tokenManager.getUsernameFromToken(token), "restart")) {
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("User with user name: " + token_username + " has been authenticated to use the restart method");
            if (printerOnline) {
                System.out.println("Restarting...");
                System.out.println("Printer " + printer + " has been stopped.");
                mapOfPrinters.clear();
                System.out.println("Printer " + printer + " queue has been cleared");
                mapOfPrinters.computeIfAbsent(printer, k -> new LinkedList<>());
                this.printerOnline = true;
                System.out.println("Printer " + printer + " has been restarted");
                return "Restart has been successful";
            } else {
                return "Printer is off, cannot restart.";
            }
        } else{
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("The role of: " + token_username + " does NOT have permission to use the restart method");
            return "You are not authorized for this action";
        }
    }

    public String status(String printer, String token) throws RemoteException {
        if (!tokenManager.ConfirmToken(token)) {
            System.out.println("A user has NOT been authenticated to use the status method");
            return "The user is not authenticated";
        }

        if (RBACManager.findUserAccess(tokenManager.getUsernameFromToken(token), "status")) {
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("User with user name: " + token_username + " has been authenticated to use the status method");
            if (printerOnline) {
                if (mapOfPrinters.containsKey(printer)) {
                    int size = mapOfPrinters.get(printer).size();
                    return "The printer " + printer + " currently has a queue containing " + size + " jobs.";
                } else {
                    return "No online printer named " + printer;
                }
            } else {
                return "Printer is offline";
            }
        } else{
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("The role of: " + token_username + " does NOT have permission to use the status method");
            return "You are not authorized for this action";
        }
    }

    public String readConfig(String parameter, String token) throws RemoteException {
        if (!tokenManager.ConfirmToken(token)) {
            System.out.println("A user has NOT been authenticated to use the readConfig method");
            return "The user is not authenticated";
        }
        if (RBACManager.findUserAccess(tokenManager.getUsernameFromToken(token), "readConfig")) {
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("User with user name: " + token_username + " has been authenticated to use the readConfig method");
            if (printerOnline) {
                if (config.containsKey(parameter)) {
                    return "Parameter " + parameter + " = " + config.get(parameter);
                } else {
                    return "Parameter " + parameter + " not found";
                }
            } else {
                return "Printer is offline, could not retrieve the parameters";
            }
        } else{
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("The role of: " + token_username + " does NOT have permission to use the readConfig method");
            return "You are not authorized for this action";
        }
    }

    public String setConfig(String parameter, String value, String token) throws RemoteException {
        if (!tokenManager.ConfirmToken(token)) {
            System.out.println("A user has NOT been authenticated to use the sefConfig method");

            return "The user is not authenticated";
        }
        if (RBACManager.findUserAccess(tokenManager.getUsernameFromToken(token), "addUser")) {
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("User with user name: " + token_username + " has been authenticated to use the setConfig method");
            if (printerOnline) {

                config.put(parameter, value);
                return "The configuration has been updated: " + parameter + " is now set to " + value;
            } else {
                return "Printer is offline, could not set the parameter";
            }
        }   else{
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("The role of: " + token_username + " does NOT have permission to use the setConfig method");
            return "You are not authorized for this action";
        }
    }

    public String Login(String username, String password) throws RemoteException {
        if (dbm.Login(username, password)) {
            String token = tokenManager.GenerateToken();
            tokenManager.AddToken(token, username);
            return token;

        } else {
            return "The user you are looking for could not be found";
        }
    }

    public String AddUser(String username, String password, String token) throws IOException {
        if (!tokenManager.ConfirmToken(token)) {
            System.out.println("A user has NOT been authenticated to use the AddUser method");

            return "The user is not authenticated";
        }
        if (RBACManager.findUserAccess(tokenManager.getUsernameFromToken(token), "addUser")){
            if(dbm.AddUserToFile(username, password)){
                return "User " + username + " has been registered.";
            } else {
                return "User already exists";
            }
        }    else{
            String token_username = tokenManager.getUsernameFromToken(token);
            System.out.println("The role of: " + token_username + " does NOT have permission to use the AddUser method");
            return "You are not authorized for this action";
        }
    }
}
