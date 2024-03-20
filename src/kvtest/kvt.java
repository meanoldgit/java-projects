package kvtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class kvt {
    public static void main(String[] args) {

        int i = 0;
        // ProcessBuilder pb = new ProcessBuilder("bash", "-c", "stty -icanon");
        
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", "echo -e \"\033[6n\"");
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            i = br.read();
            System.out.println(i);
            System.out.println("\033[6n");
            BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
            br2.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        char c = (char) i;
        System.out.println(c);
    }
}
