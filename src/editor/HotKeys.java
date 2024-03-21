package editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

class HotKeys implements KeyListener {
    Cursor cursor = new Cursor();
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_B:
        System.out.println("bbc");
        break;

        default:
        break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    

    // public void KeyPressed(KeyEvent e) {

    //     switch (e.getKeyCode()) {
    //         case KeyEvent.VK_B:
    //         System.out.println("bbc");
    //         break;

    //         default:
    //         break;
    //     }
    // }

    public void close(ArrayList<ArrayList<Character>> lines) {
        cursor.changeColorWhite();
        cursor.clearScreenAfterCursor();
        System.out.println("\nbye");

        for (int i = 0; i < lines.size(); i++) {
            lines.get(i).add('\n');
        }

        // Print lines.
        for (int i = 0; i < lines.size(); i++)
            for (int j = 0; j < lines.get(i).size(); j++)
                System.out.print(lines.get(i).get(j));
        
        System.out.println();
        System.exit(0);
    }
}
