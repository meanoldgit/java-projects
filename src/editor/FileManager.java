package editor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {
    File file;
    String fileName;
    ArrayList<ArrayList<Character>> lines;

    public FileManager(String fileName, ArrayList<ArrayList<Character>> lines) {
        this.fileName = fileName;
        this.lines = lines;
    }

    public void openFile() {
        file = new File(fileName);

        if (file.exists()) {
            System.out.println("file exists");
            readFile();
        }
        else {
            lines.add(new ArrayList<>());
        }
    }

    public void readFile() {
        try {
            Scanner fscan = new Scanner(file);
            String data;
            int line;

            while (fscan.hasNextLine()) {
                line = lines.size();
                data = fscan.nextLine();
                lines.add(new ArrayList<>());

                for (int i = 0; i < data.length(); i++) {
                    lines.get(line).add(data.charAt(i));
                }
            }

            fscan.close();
        }
        catch (IOException e) {
            System.out.println("\nCouldn't scan file.");
            e.printStackTrace();
        }
    }

    public void writeFile() {
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                }
                catch (IOException e) {
                    System.out.println("\nCouldn't create file.");
                    e.printStackTrace();
                }
            }

            FileWriter fwriter = new FileWriter(file);
            // write.write(0);
            fwriter.close();
        }
        catch (IOException e) {
            System.out.println("\nCouldn't write file.");
            e.printStackTrace();
        }
    }
}
