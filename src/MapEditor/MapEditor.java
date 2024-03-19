package MapEditor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JFrame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MapEditor extends Variables
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        System.out.print("Map name: ");
        String mapName = "";
        mapName = "Maps/" + mapName + ".txt";

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new Key());
        frame.setFocusable(true);
        frame.setVisible(true);

        try
        {
            File file = new File(mapName);
            FileWriter write = new FileWriter(file);
            Scanner read = new Scanner(file);
            String mapData = "";

            // Fill map.
            for (int i = 0; i < 10; i++)
            {
                for (int j = 0; j < 10; j++) mapData += BLOCK + " ";
                mapData += "\n";
            }

            if (file.createNewFile()) System.out.println("Created.");
            else System.out.println("Name taken.");

            write.write(mapData);
            write.close();
            System.out.println("Done.");

            while (read.hasNextLine())
            {
                String data = read.nextLine();
                System.out.println(data);
            }

            read.close();
        }
        catch (IOException e)
        {
            System.out.println("Something bad happened.");
            e.printStackTrace();
        }
    }

    static void drawMap()
    {
        for (int i = 0; i < map.length; i++)
        {
            for (int j = 0; j < map[i].length; j++)
            {
                System.out.print(map[i][j] + " ");
            }
            
            System.out.println();
        }
    }
}

class Key implements KeyListener
{
    Variables var = new Variables();
    int pointer = 0;
    char letter;
    char action;
    char empty = 0;
    char newLine = '¶';
    boolean isCtrlPressed = false;
    boolean c_pressed = false;

    @Override
    public void keyTyped(KeyEvent event)
    {
        letter = event.getKeyChar();
        if (Character.isLetterOrDigit(letter))
        {
            System.out.print(letter);
            var.txtSpace[pointer] = letter;
            pointer++;
        }
        else
        {
            switch (letter)
            {
                case ' ', '.', ',', ':', ';', '-', '_', '\n':
                System.out.print(letter);
                var.txtSpace[pointer] = newLine;
                pointer++;
                break;
            }
            
            if (isCtrlPressed && c_pressed)
            {
                System.out.println("\nbye");

                // Print file name.
                for (int i = 0; i < var.txtSpace.length; i++)
                {
                    if (var.txtSpace[i] == newLine)
                    {
                        System.out.println();
                    }
                    else
                    {
                        System.out.print(var.txtSpace[i]);
                    }
                }
                
                System.out.println();
                System.exit(0);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent event)
    {
        action = event.getKeyChar();
        switch (event.getKeyCode())
        {
            case KeyEvent.VK_CONTROL:
            isCtrlPressed = true;
            break;

            case KeyEvent.VK_C:
            c_pressed = true;
            break;
            
            case KeyEvent.VK_BACK_SPACE:
            backSpace();
            break;

            case KeyEvent.VK_LEFT:
            pointer--;
            System.out.print('\b');
            break;

            case KeyEvent.VK_TAB:
            System.out.print("tab");
            break;

            default:
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        switch (event.getKeyCode())
        {
            case KeyEvent.VK_CONTROL:
            isCtrlPressed = false;
            break;

            case KeyEvent.VK_C:
            c_pressed = false;
            break;
            
            default:
            break;
        }
    }

    public void backSpace()
    {
        pointer--;
        System.out.print(action);
        System.out.print(' ');
        System.out.print(action);
        var.txtSpace[pointer] = empty;
    }

    public void clearCommand()
    {
        try
        {
            new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}


class Variables
{
    char[] txtSpace = new char[100];
    Scanner scan = new Scanner(System.in);
    static char[][] map = createGrid();
    static final char BLOCK = '▄';
    static boolean isCtrlPressed;
    static boolean c_pressed;

    static char[][] createGrid()
    {
        char[][] grid = new char[10][10];
        for (int i = 0; i < grid.length; i++)
        {
            for (int j = 0; j < grid[i].length; j++)
            {
                grid[i][j] = '.';
            }
        }

        return grid;
    }
}
