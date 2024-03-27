package editor;

import javax.swing.JFrame;
import java.awt.Color;

public class Editor {
    public static void main(String[] args) {
        String fileName;
        Key key;
        JFrame frame = new JFrame();
        
        if (args.length > 0) fileName = args[0];
        else fileName = null;
        key = new Key(fileName);

        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.addKeyListener(key);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(new Color(0, 0, 0, 1));
        frame.setFocusTraversalKeysEnabled(false);
    }
}

