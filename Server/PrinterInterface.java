package Server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface PrinterInterface extends Remote {
    String echo(String input) throws RemoteException;

    Boolean isPrinterOnline() throws RemoteException;
    String print(String filename, String printer, String token) throws RemoteException;   
    String queue(String printer, String token) throws RemoteException;  
    String topQueue(String printer, int job, String token) throws RemoteException;  
    String start(String token) throws RemoteException; 
    String stop(String token) throws RemoteException;  
    String restart(String printer, String token) throws RemoteException;   
    String status(String printer, String token) throws RemoteException;  
    String readConfig(String parameter, String token) throws RemoteException;  
    String setConfig(String parameter, String value, String token) throws RemoteException;
    String Login(String username, String password) throws RemoteException;
    String AddUser(String username, String password, String token) throws IOException;
}