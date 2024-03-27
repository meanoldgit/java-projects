package editor;

import java.util.ArrayList;

class HotKeys {
    Cursor cursor = new Cursor();

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
