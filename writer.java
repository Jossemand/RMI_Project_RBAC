import java.io.FileWriter;
import java.io.IOException;

public class writer {

    public static void main(String[] args){
        try {
            System.out.println("Adding User to File");
            FileWriter writer = new FileWriter("Textfile.txt", true);
            //  writer.write(un+":"+pw+"\r\n");
            writer.write("Writing");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
