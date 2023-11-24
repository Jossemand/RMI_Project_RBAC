package Server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PrinterServer {
    public static void main(String[] args) throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(5099); // Default RMI registry port
        registry.rebind("hello", new PrinterImplementation());
        System.out.println("Print Server is running.");
    }
}