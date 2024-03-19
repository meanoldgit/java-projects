package asciir.editor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MapEditor
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        frame.setUndecorated(true);
        frame.setBackground(new java.awt.Color(0, 0, 0, 0)); // Transparent background color
        frame.getContentPane().setBackground(new java.awt.Color(0, 0, 0, 0)); // Transparent content pane
        panel.setBackground(new java.awt.Color(0, 0, 0, 1));
        frame.add(panel);
        frame.addKeyListener(new Key());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setFocusable(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clearCommand();
        System.out.print("Map name: ");
    }

    public static void clearCommand()
    {
        try
        {
            ProcessBuilder clear = new ProcessBuilder("bash", "-c", "clear").inheritIO();
            clear.start().waitFor();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

class Key implements KeyListener
{
    ArrayList<Character> txtSpace = new ArrayList<>();
    int pointer = 0;
    char letter;
    char action;
    char empty = 0;
    String regex = "[.,:;_+-/\\*!\"$&()=?<>]";
    boolean isCtrlPressed = false;
    boolean c_pressed = false;
    boolean selectMode = false;
    String path = "";

    final String CURSOR_UP = "\033[A";
    final String CURSOR_DOWN = "\033[B";
    final String CURSOR_FORWARD = "\033[C";
    final String CURSOR_BACKWARD = "\033[D";

    @Override
    public void keyTyped(KeyEvent event)
    {
        letter = event.getKeyChar();
        
        if ((Character.isLetterOrDigit(letter)
        || String.valueOf(letter).matches(regex))
        && !selectMode)
        {
            System.out.print(letter);
            txtSpace.add(pointer, letter);
            pointer++;
        }
        else
        {
            if (isCtrlPressed && c_pressed)
            {
                System.out.println("\nbye");

                // Print file name.
                for (int i = 0; i < txtSpace.size(); i++)
                {
                    System.out.print(txtSpace.get(i));
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
            case KeyEvent.VK_ENTER:
                String fileName = "";
                for (int i = 0; i < txtSpace.size(); i++)
                {
                    fileName += txtSpace.get(i);
                }

                path = fileName;

                confirmName();
            break;

            case KeyEvent.VK_CONTROL:
            isCtrlPressed = true;
            break;

            case KeyEvent.VK_C:
            c_pressed = true;
            break;

            case KeyEvent.VK_SPACE:
            System.out.print(' ');
            txtSpace.add(pointer, '-');
            pointer++;
            break;
            
            case KeyEvent.VK_BACK_SPACE:
            backSpace();
            break;

            case KeyEvent.VK_J:
            if (selectMode)
            {
                pointer--;
                System.out.print(CURSOR_BACKWARD);
            }
            break;

            case KeyEvent.VK_L:
            if (selectMode)
            {
                pointer++;
                System.out.print(CURSOR_FORWARD);
            }
            break;

            case KeyEvent.VK_K:
            if (selectMode)
            {
                System.out.print(CURSOR_DOWN);
            }
            break;

            case KeyEvent.VK_I:
                if (isCtrlPressed)
                {
                    if (!selectMode) selectMode = true;
                    else selectMode = false;
                }
                
                if (selectMode)
                {
                    System.out.print(CURSOR_UP);
                }
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
        txtSpace.remove(pointer);
        // Backspace, print empty space, backspace again.
        System.out.print(action + " " + action);
    }

    public void confirmName()
    {
        try
        {
            path = "asciir/maps/" + path + ".txt";

            File file = new File(path);
            FileWriter write = new FileWriter(file);
            Scanner read = new Scanner(file);
            String mapData = "";

            if (file.createNewFile()) System.out.println("\nCreated.");
            else System.out.println("\nName taken.");

            write.write(mapData);
            write.close();
            System.out.println("\nDone.");

            while (read.hasNextLine())
            {
                String data = read.nextLine();
                System.out.println(data);
            }

            read.close();
        }
        catch (IOException e)
        {
            System.out.println("\nSomething bad happened.");
            e.printStackTrace();
        }
    }
}