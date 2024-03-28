package editor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

class Key implements KeyListener {
    ArrayList<ArrayList<Character>> lines = new ArrayList<>();
    HotKeys hotKeys = new HotKeys();
    Cursor cursor = new Cursor();
    FileManager fileManager;
    Terminal t;
    
    int screenHeight = 10;
    int screenWidth = 70;
    int scroll = 0;
    char action;
    char letter;
    String path = "";

    boolean cursorMode = false;
    boolean altPressed = false;
    boolean shiftPressed = false;
    boolean ctrlPressed = false;
    
    final char EMPTY_SPACE = ' ';
    final String SYMBOLS_REGEX = "[ .,:;_+-/\\*!\"'%$&@#~|()=¿?<>{}\\[\\]]";

    public Key(String fileName) {

        try {
            this.t = TerminalBuilder.builder().build();
            System.out.println(t.getWidth());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        this.fileManager = new FileManager(fileName, lines);
        // clearCommand();

        if (fileName != null) {
            fileManager.openFile();
            cursor.savePosition();
            
            for (int i = 0; i < screenHeight && i < lines.size(); i++) {
                for (int j = 0; j < lines.get(i).size(); j++) {
                    System.out.print(lines.get(i).get(j));
                }

                System.out.println();
            }

            cursor.restorePosition();
        }
        else {
            lines.add(new ArrayList<>());
        }

        // TODO: Remove all '\n' when opening file.
    }

    @Override
    public void keyTyped(KeyEvent event) {
        letter = event.getKeyChar();
        
        if (isKeyPressed()) {
            lines.get(cursor.y).add(cursor.x, letter);
            cursor.x++;
            
            System.out.print(letter);
            cursor.printLineAfterCursor(lines.get(cursor.y));
        }
    }

    public boolean isKeyPressed() {
        return (Character.isLetterOrDigit(letter)
        || String.valueOf(letter).matches(SYMBOLS_REGEX))
        && lines.get(cursor.y).size() < screenWidth
        && !cursorMode && !altPressed && !shiftPressed && !ctrlPressed;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        action = event.getKeyChar();
        
        switch (event.getKeyCode()) {
            case KeyEvent.VK_ENTER:
            newLine();
            break;

            case KeyEvent.VK_TAB:
            tabulate();
            break;

            case KeyEvent.VK_CONTROL:
            ctrlPressed = true;
            break;

            case KeyEvent.VK_ALT:
            altPressed = true;
            break;

            case KeyEvent.VK_SHIFT:
            shiftPressed = true;
            break;

            case KeyEvent.VK_BACK_SPACE:
            backSpace();
            break;

            // MOVE KEYS
            case KeyEvent.VK_J:
            if (cursorMode || (!cursorMode && ctrlPressed)) {
                if (cursorMode && ctrlPressed) {
                    cursor.jumpBackward(lines.get(cursor.y));
                }
                else {
                    cursor.backward();
                }
            }
            break;

            case KeyEvent.VK_L:
            if (cursorMode || (!cursorMode && ctrlPressed)) {
                if (cursorMode && ctrlPressed) {
                    cursor.jumpForward(lines.get(cursor.y));
                }
                else {
                    cursor.forward(lines.get(cursor.y));
                }
            }
            break;

            case KeyEvent.VK_K:
            toggleCursorMode();

            if (cursorMode || (!cursorMode && ctrlPressed)) {
                cursor.down(lines);
            }
            break;

            case KeyEvent.VK_I:
            if (cursorMode || (!cursorMode && ctrlPressed)) {
                cursor.up();
            }
            break;

            default:
            break;
        }

        handleCtrl(event);
        handleCtrlShift(event);
    }

    public void handleCtrl(KeyEvent event) {
        if (ctrlPressed && !shiftPressed) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_C:
                hotKeys.close(lines);
                break;

                case KeyEvent.VK_S:
                fileManager.writeFile();
                break;

                default:
                break;
            }
        }
    }

    public void handleCtrlShift(KeyEvent event) {
        if (ctrlPressed && shiftPressed) {
            switch (event.getKeyCode()) {
                default:
                break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_E:
            if (cursorMode) {
                cursorMode = false;
                cursor.changeColorWhite();
            }
            break;

            case KeyEvent.VK_CONTROL:
            ctrlPressed = false;
            break;

            case KeyEvent.VK_ALT:
            altPressed = false;
            break;

            case KeyEvent.VK_SHIFT:
            shiftPressed = false;
            break;
            
            default:
            break;
        }
    }


    // NEW LINE

    public void newLine() {
        lines.add(cursor.y + 1, new ArrayList<>());
        splitCurrentLine();
        printNewLine();
    }

    public void splitCurrentLine() {
        int size = lines.get(cursor.y).size();
        int newLine = cursor.y + 1;
        if (cursor.x < size) {
            cursor.clearScreenAfterCursor();

            for (int i = cursor.x; i < size; i++) {
                lines.get(newLine).add(lines.get(cursor.y).get(cursor.x));
                lines.get(cursor.y).remove(cursor.x);
            }
        }
    }

    public void printNewLine() {
        cursor.y++;
        cursor.x = 0;
        
        System.out.print(action);
        cursor.clearScreenAfterCursor();

        cursor.savePosition();

        for (int i = cursor.y; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(i).size(); j++) {
                System.out.print(lines.get(i).get(j));
            }

            System.out.println();
        }

        cursor.restorePosition();
    }


    // BACK SPACE

    public void backSpace() {
        if (cursor.x > 0) {
            cursor.x--;
            lines.get(cursor.y).remove(cursor.x);
            
            // Backspace, print empty space, backspace again.
            System.out.print(action + " " + action);
            
            cursor.printLineAfterCursor(lines.get(cursor.y));
        }
    }


    // TABULATION

    public void tabulate() {
        for (int i = 0; i < 4; i++) {
            lines.get(cursor.y).add(cursor.x, EMPTY_SPACE);
            cursor.x++;
            System.out.print(EMPTY_SPACE);
        }
        
        cursor.printLineAfterCursor(lines.get(cursor.y));
    }

    public void reverseTab() {}


    // TOGGLE CURSOR MODE

    public void toggleCursorMode() {
        if (altPressed) {
            if (cursorMode) {
                cursorMode = false;
                cursor.changeColorWhite();
            }
            else {
                cursorMode = true;
                cursor.changeColorRed();
            }
        }
    }


    // COMMANDS

    public static void clearCommand() {
        try {
            ProcessBuilder clear = new ProcessBuilder("bash", "-c", "clear").inheritIO();
            clear.start().waitFor();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
