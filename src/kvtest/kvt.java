package kvtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class kvt {
    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", "stty -icanon");
        int i = 0;

        try {
            i = br.read();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        char c = (char) i;
        System.out.println(c);
    }
}
