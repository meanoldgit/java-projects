package Tetris;

import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Tetris extends Variables {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        long startTimer;
        long stopTimer;
        boolean isFalling = true;

        Thread thread = new Thread(() -> {
            while (loop) {
                drawGame();

                if (leftPressed && canMove) {
                    move(LEFT);
                    if (leftJustPressed) {
                        leftJustPressed = false;
                        timer(150);
                    }
                    else {
                        timer(50);
                    }
                }
                else if (rightPressed && canMove) {
                    move(RIGHT);
                    if (rightJustPressed) {
                        rightJustPressed = false;
                        timer(150);
                    }
                    else {
                        timer(50);
                    }
                }
            }
        });

        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.addKeyListener(new Key());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(new java.awt.Color(0, 0, 0, 1));
        
        showTitleScreen();
        setValuesToDefault();
        thread.start();
        playSoundLoop();


        // MAIN LOOP

        while (loop) {
            castShadow();
            drawShape(DRAW_BLOCK);
            
            // Loop timer.
            startTimer = System.nanoTime();
            stopTimer = System.nanoTime() + 1;
            while (stopTimer < (startTimer + millis * 1_000_000) && !downPressed && !spacePressed && isFalling) {
                stopTimer = System.nanoTime();
            }

            if (downPressed && !spacePressed) timer(75);

            if (isFalling && !touchingFloor()) {
                drawShape(ERASE_BLOCK);

                // Make the shape fall.
                if (canMove) shapeY++;

                if (pushBack()) {
                    if ((!leftPressed && !rightPressed) || spacePressed) {
                        canMove = false;
                        isFalling = false;
                        // TODO: Make a timer to avoid infinite time to place shape.
                    }

                    shapeY--;
                }
            }
            else {
                // Small frame to place a shape to a side right before landing.
                if (touchingFloor()) {
                    if (leftPressed) move(LEFT);
                    else if (rightPressed) move(RIGHT);
                }

                // Set to false to avoid shapes falling too fast after the previous shape.
                if (spacePressed) spacePressed = false;

                canMove = false;
                
                playSound(LANDED);
                checkLine();
                if (level < 9) nextLevel();
                goToStartPosition();

                direction = 0;
                shape = nextShape;
                nextShape = SHAPES[rand.nextInt(0, SHAPES.length)];

                // Randomly assign a color until it's different from the prev color.
                while (shapeColor.equals(prevShapeColor)) {
                    shapeColor = COLORS[rand.nextInt(0, COLORS.length)];
                }

                prevShapeColor = shapeColor;

                // Since these shapes have an empty space above, make them appear one block higher.
                if (shape == T || shape == J || shape == I) shapeY--;

                // Check if there are any blocks where the new shape is getting placed.
                if (pushBack()) gameOver();

                if (!paused) canMove = true;
                isFalling = true;
            }
        }
    }


    // DRAW SHAPE

    static void drawShape(char action) {
        char[][][] shp;
        int blockX;
        int blockY;
        int height;
        int width;
        int dir;
        int x;
        int y;

        if (action == DRAW_NEXT_SHAPE) {
            dir = 0;
            shp = nextShape;
        }
        else {
            dir = direction;
            shp = shape;
        }

        x = shapeX;
        y = shapeY;
        height = shp[dir].length;


        //? Maybe ogX, ogY and ogDir should be set here...


        for (int i = 0; i < height; i++) {
            blockY = y + i;
            width = shp[dir][i].length;

            for (int j = 0; j < width; j++) {
                blockX = x + j;

                if (shp[dir][i][j] == BLOCK) {
                    if (action == DRAW_NEXT_SHAPE) {
                        if (nextShape == O) nextShapeGrid[i + 1][j + 1] = BLOCK;
                        else if (!(nextShape == T || nextShape == J || nextShape == I)) nextShapeGrid[i + 1][j] = BLOCK;
                        else nextShapeGrid[i][j] = BLOCK;
                    }
                    else {
                        grid[blockY][blockX] = action;
                        colorGrid[blockY][blockX] = shapeColor;
                    }
                }
            }
        }
    }


    // PUSH BACK

    static boolean pushBack() {
        int blockX;
        int blockY;
        int height = shape[direction].length;
        int width;

        for (int i = 0; i < height; i++) {
            blockY = shapeY + i;
            width = shape[direction][i].length;
            for (int j = 0; j < width; j++) {
                blockX = shapeX + j;
                if (shape[direction][i][j] == BLOCK) {
                    if (grid[blockY][blockX] == BLOCK) return true;
                }
            }
        }

        return false;
    }


    // CHECK LINE

    static void checkLine() {
        int blockCounter;
        boolean isLine;
        boolean soundPlayed;
        char upperCell;
        String colorUpperCell;
        checkingLine = true;
        
        for (int rowToCheck = gridHeight; rowToCheck > 0; rowToCheck--) {
            isLine = true;
            blockCounter = 0;
            soundPlayed = false;

            // Check line.
            for (int col = 0; col < grid[rowToCheck].length && isLine; col++) {
                if (grid[rowToCheck][col] == BLOCK) {
                    blockCounter++;
                }
                else {
                    isLine = false;
                }
            }

            // Check if there's a full line of blocks.
            if (blockCounter == COLS) {
                // Increase lines counter.
                lines++;

                // "Animation".
                for (int i = 0; i < COLS; i++) {
                    // Remove line.
                    grid[rowToCheck][i] = EMPTY;
                    if (i % 3 == 1) playSound(LANDED);
                    timer(30);
                }

                for (int i = rowToCheck; i > 0; i--) {
                    for (int j = 0; j < grid[rowToCheck].length; j++) {
                        if (!soundPlayed) {
                            soundPlayed = true;
                            playSound(LINE_CLEAR);
                        }
                        
                        // Make the blocks fall by placing the upper blocks in the bottom cells.
                        upperCell = grid[i - 1][j];
                        grid[i][j] = upperCell;
                        
                        // And make the same for the color grid.
                        colorUpperCell = colorGrid[i - 1][j];
                        colorGrid[i][j] = colorUpperCell;
                    }
                }

                // Reset the line checker to not miss any line.
                // Set to ROWS (20) instead of gridHeight (ROWS - 1)
                // bc the for loop will decrease it.
                rowToCheck = ROWS;

                timer(200);

                // Clear upper line.
                for (int i = 0; i < COLS; i++) grid[0][i] = EMPTY;
            }
        }

        checkingLine = false;
    }


    // KEY LISTENER

    static class Key implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            // pass
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                // LEFT
                case KeyEvent.VK_A:
                leftPressed = true;
                if (leftJustPressed && canMove) {
                    playSound(MOVE);
                }
                break;
                
                // RIGHT
                case KeyEvent.VK_D:
                rightPressed = true;
                if (rightJustPressed && canMove) {
                    playSound(MOVE);
                }
                break;

                // ROTATE RIGHT
                case KeyEvent.VK_W, KeyEvent.VK_RIGHT:
                upPressed = true;
                rotate(RIGHT);
                break;

                // ROTATE LEFT
                case KeyEvent.VK_SHIFT, KeyEvent.VK_LEFT:
                rotate(LEFT);
                break;

                // DOWN
                case KeyEvent.VK_S:
                downPressed = true;
                break;

                // SPACE
                case KeyEvent.VK_SPACE:
                spacePressed = true;
                break;

                // PAUSE
                case KeyEvent.VK_ENTER, KeyEvent.VK_E:
                if (!titleScreen) {
                    pause();
                    playSound(PAUSE);
                }
                break;

                // QUIT
                case KeyEvent.VK_Q:
                if (paused || titleScreen) gameClose();
                break;

                default:
                break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_A) {
                leftPressed = false;
                leftJustPressed = true;
            }
            else if (e.getKeyCode() == KeyEvent.VK_D) {
                rightPressed = false;
                rightJustPressed = true;
            }
            else if (e.getKeyCode() == KeyEvent.VK_W) {
                upPressed = false;
            }
            else if (e.getKeyCode() == KeyEvent.VK_S) {
                downPressed = false;
            }
            else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                spacePressed = false;
            }
        }
    }


    // DRAW GAME

    static void drawGame() {
        String lineToPrint = "";

        clearNextShapeGrid();
        drawShape(DRAW_NEXT_SHAPE);

        if (!paused) System.out.println(" ".repeat(13) + "- TETRIS -" + " ".repeat(13));
        else System.out.println(" ".repeat(13) + "- PAUSED -" + " ".repeat(13));
        
        System.out.println("╔" + "═".repeat(COLS * 2 + 1) + "╦" + "═".repeat(12) + "╗");
        
        for (int row = 0; row < grid.length; row++) {
            System.out.print("║ ");

            // Draw grid.
            for (int col = 0; col < grid[row].length; col++) {
                // Take the color from the color grid and display the block, if any, from the grid.
                System.out.print(colorGrid[row][col] + grid[row][col] + " ");
            }

            // Reset the color.
            System.out.print(color);
            
            if (row < 11) {
                if (row == 0) System.out.println("║ Next:" + " ".repeat(6) + "║");
                else if (row == 1) {
                    System.out.print("║" + " ".repeat(2));
                    for (int i = 0; i < 4; i++) System.out.print(" " + nextShapeGrid[0][i]);
                    System.out.println("  ║");
                }
                else if (row == 2) {
                    System.out.print("║" + " ".repeat(2));
                    for (int i = 0; i < 4; i++) System.out.print(" " + nextShapeGrid[1][i]);
                    System.out.println("  ║");
                }
                else if (row == 3) {
                    System.out.print("║" + " ".repeat(2));
                    for (int i = 0; i < 4; i++) System.out.print(" " + nextShapeGrid[2][i]);
                    System.out.println("  ║");
                }
                else if (row == 4) {
                    System.out.print("║" + " ".repeat(2));
                    for (int i = 0; i < 4; i++) System.out.print(" " + nextShapeGrid[3][i]);
                    System.out.println("  ║");
                }
                else {
                    if (row == 5) lineToPrint = "╠" + "═".repeat(12) + "╣";
                    else if (row == 6) lineToPrint = "║ Score:" + " ".repeat(5) + "║";
                    else if (row == 7) lineToPrint = "║ " + " ".repeat(10 - Long.toString(lines).length()) + lines + " ║";
                    else if (row == 8) lineToPrint = "╠" + "═".repeat(12) + "╣";
                    else if (row == 9) lineToPrint = "║ Level:" + " ".repeat(3) + level + " ║";
                    else if (row == 10) lineToPrint = "╠" + "═".repeat(12) + "╝";
                    
                    System.out.println(lineToPrint);
                }
            }
            else {
                System.out.println("║");
            }
        }

        System.out.println("╚" + "═".repeat(COLS * 2 + 1) + "╝");
        System.out.print(CURSOR_TOP_LEFT);
    }


    // SHADOW THE EDGELORD

    static void castShadow() {
        char[][][] shadow = shape;
        int shadowX = shapeX;
        int shadowY = shapeY;
        int blockX;
        int blockY;
        boolean blockFound = false;

        // Erase the shadows from the grid.
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == SHADOW) grid[i][j] = EMPTY;
            }
        }

        // Place the shadow.
        while (!blockFound && shadowY + shadow[direction].length - 1 < gridHeight) {
            // Make the shadow fall.
            shadowY++;

            for (int i = 0; i < shadow[direction].length && !blockFound; i++) {
                blockY = shadowY + i;
                for (int j = 0; j < shadow[direction][i].length && !blockFound; j++) {
                    blockX = shadowX + j;
                    if (shadow[direction][i][j] == BLOCK) {
                        if (grid[blockY][blockX] == BLOCK) {
                            blockFound = true;
                            
                            // Push back up.
                            shadowY--;
                        }
                    }
                    
                }
            }
        }

        // Draw the shadow.
        for (int i = 0; i < shadow[direction].length; i++) {
            blockY = shadowY + i;

            for (int j = 0; j < shadow[direction][i].length; j++) {
                blockX = shadowX + j;
                
                if (shadow[direction][i][j] == BLOCK && grid[blockY][blockX] != BLOCK) {
                    grid[blockY][blockX] = SHADOW;
                    colorGrid[blockY][blockX] = shapeColor;
                }
            }
        }
    }


    // MOVE

    static void move(char dir) {
        drawShape(ERASE_BLOCK);

        if (dir == LEFT) {
            if (leftClear()) shapeX--;
            if (pushBack()) shapeX++;
        }
        else if (dir == RIGHT) {
            if (rightClear()) shapeX++;
            if (pushBack()) shapeX--;
        }

        castShadow();
        drawShape(DRAW_BLOCK);
    }


    // LEFT/RIGHT CLEAR

    static boolean leftClear() {
        int blockX;
        for (int i = 0; i < shape[direction].length; i++) {
            for (int j = 0; j < shape[direction][i].length; j++) {
                blockX = shapeX + j;

                // If the block X is at the left boundary (0) return false.
                if (shape[direction][i][j] == BLOCK) {
                    if (blockX == 0) return false;
                }
            }
        }

        return true;
    }

    static boolean rightClear() {
        boolean rightClear = shapeX + shape[direction][0].length - 1 < gridWidth;
        if (rightClear) return true;
        else return false;
    }


    // ROTATE

    static void rotate(char dir) {
        if (canMove) {
            drawShape(ERASE_BLOCK);
            getOriginalPosition();

            if (dir == LEFT) {
                if (direction > 0) direction--;
                else direction = shape.length - 1;
            }
            else if (dir == RIGHT) {
                if (direction < shape.length - 1) direction++;
                else direction = 0;
            }

            rotationFix();

            if (pushBack()) recoverOriginalPosition();
            
            castShadow();
            drawShape(DRAW_BLOCK);
            playSound(ROTATE);
        }
    }


    // ROTATION FIX

    static void rotationFix() {
        int blockX;
        int blockY;

        // Left wall.
        for (int i = 0; i < shape[direction].length; i++) {
            for (int j = 0; j < shape[direction][i].length; j++) {
                blockX = shapeX + j;
                if (shape[direction][i][j] == BLOCK) {
                    if (blockX < 0) shapeX++;
                }
            }
        }
        
        // Right wall.
        while (shapeX + shape[direction][0].length - 1 > gridWidth) {
            shapeX--;
        }

        // Ceiling.
        for (int i = 0; i < shape[direction].length; i++) {
            blockY = shapeY + i;
            for (int j = 0; j < shape[direction][i].length; j++) {
                if (shape[direction][i][j] == BLOCK) {
                    if (blockY < 0) shapeY++;
                }
            }
        }

        // Floor.
        while (shapeY + shape[direction].length - 1 > gridHeight) {
            shapeY--;
        }
    }


    // ORIGINAL POSITION
    
    static void getOriginalPosition() {
        ogX = shapeX;
        ogY = shapeY;
        ogDir = direction;
    }

    static void recoverOriginalPosition() {
        shapeX = ogX;
        shapeY = ogY;
        direction = ogDir;
    }


    // GOTO START POSITION

    static void goToStartPosition() {
        shapeX = START_POSITION_X;
        shapeY = START_POSITION_Y;
    }


    // TOUCHING FLOOR

    static boolean touchingFloor() {
        boolean notTouchingFloor = (shapeY + shape[direction].length - 1) < gridHeight;
        if (notTouchingFloor) return false;
        else return true;
    }


    // NEXT LEVEL

    static void nextLevel() {
        if (lines >= linesForNextLevel) {
            linesForNextLevel += 10;
            level++;
            
            if (millis > 400) millis -= 200;
            else if (millis > 300) millis -= 100;
            else millis -= 50;

            changeColor();

            if (level <= 9) playSound(LEVEL_UP);
        }
    }


    // CHANGE COLOR

    static void changeColor() {
        if (level < 8) {
            while (color.equals(prevColor)) {
                color = COLORS[rand.nextInt(0, COLORS.length - 1)];
            }
        }
        else if (level < 9) {
            color = COLOR_RED;
        }
        else {
            color = COLOR_DARK_RED;
        }

        prevColor = color;
    }
    

    // TITLE SCREEN

    static void showTitleScreen() {
        clearScreen();
        formatSongNames();
        System.out.print(HIDE_CURSOR);

        while (titleScreen) {
            System.out.println(color);
            figlet("Tetris");
            System.out.println(" Music:\n");
            System.out.print(COLOR_WHITE);

            if (selectorY < 0) selectorY = themes.length - 1;
            else if (selectorY > themes.length - 1) selectorY = 0;

            for (int i = 0; i < themes.length; i++) {
                System.out.print(color);
                System.out.print(COLOR_WHITE);
                if (selectorY == i) System.out.print(color);
                System.out.println(themes[i]);
                System.out.print(COLOR_WHITE);
            }
            
            //! Busy wait loop (bad practice).
            while (!upPressed && !downPressed && !spacePressed) doNothing();

            if (upPressed) selectorY--;
            else if (downPressed) selectorY++;
            else if (spacePressed) titleScreen = false;

            playSound(MOVE);
            timer(50);
            System.out.print(CURSOR_TOP_LEFT);
            System.out.print(color);
        }

        selectedSong = THEMES[selectorY];
        canMove = true;
        spacePressed = false;
    }

    static void doNothing() {
        System.out.print("");
    }


    // FORMAT SONG NAMES

    static void formatSongNames() {
        char[] letters;
        String name;
        String firstHalf;
        String secondHalf;
        boolean slashFound;
        boolean dotFound;
        boolean capitalizeLetter;

        for (int i = 0; i < THEMES.length; i++) {
            letters = THEMES[i].toCharArray();
            name = "";
            firstHalf = "";
            secondHalf = "";
            slashFound = false;
            dotFound = false;
            capitalizeLetter = true;
            firstHalf += " " + (i + 1) + ".";

            // Apply changes to the letters.
            for (char letter : letters) {
                if (letter == '.') dotFound = true;

                if (slashFound && !dotFound) {
                    if (Character.isDigit(letter)) secondHalf += " ";

                    if (capitalizeLetter) {
                        secondHalf += Character.toUpperCase(letter);
                        capitalizeLetter = false;
                    }
                    else if (letter == '_') {
                        secondHalf += " ";
                        capitalizeLetter = true;
                    }
                    else {
                        secondHalf += letter;
                    }
                }

                if (letter == '/') slashFound = true;
            }

            name += firstHalf + secondHalf;
            themes[i] = name;
        }
    }


    // GAME OVER

    static void gameOver() {
        int colorPicker = 0;

        drawShape(DRAW_BLOCK);
        timer(100);
        playSound(GAME_OVER);
        
        for (int i = gridHeight; i >= 0; i--) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j] = BLOCK;
                
                // Picks the colors from the array to display the rainbow.
                colorGrid[i][j] = COLORS[colorPicker];
                timer(4);
            }
            
            colorPicker++;
            if (colorPicker == COLORS.length) colorPicker = 0;
        }
        
        for (int i = gridHeight; i >= 0; i--) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j] = EMPTY;
                timer(4);
            }
        }
        
        setValuesToDefault();
        changeColor();
    }


    // SET VALUES TO DEFAULT

    static void setValuesToDefault() {
        level = 1;
        lines = 0;
        millis = 1000;
        linesForNextLevel = 10;
    }


    // PAUSE

    static void pause() {
        if (paused) {
            paused = false;
            if (!checkingLine) canMove = true;
        }
        else {
            paused = true;
            canMove = false;
        }
    }
    
    
    // TIMER

    static void timer(long ms) {
        long startTimer = System.nanoTime();
        long stopTimer = System.nanoTime() + 1;
        while (stopTimer < (startTimer + ms * 1_000_000)) {
            stopTimer = System.nanoTime();
        }
    }


    // CLEAR NEXT SHAPE GRID

    static void clearNextShapeGrid() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextShapeGrid[i][j] = EMPTY;
            }
        }
    }
    

    // COMMANDS

    static void clearScreen() {
        System.out.print("\u001b[H\u001b[2J");
        System.out.flush();
    }

    static void clearCommand() {
        try {
            new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void figlet(String str) {
        try {
            new ProcessBuilder("bash", "-c", "figlet " + str).inheritIO().start().waitFor();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    // PLAY SOUND

    static void playSoundLoop() {
        String filePath;

        if (firstLoop) {
            filePath = selectedSong;
            firstLoop = false;
        }
        else {
            filePath = prevSong;
        }

        while (filePath.equals(prevSong)) filePath = THEMES[rand.nextInt(0, THEMES.length)];
        prevSong = filePath;

        try {
            File soundFile = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    playSoundLoop();
                }
            });

            clip.start();
        }
        catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    static void playSound(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        }
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    // GAME CLOSE

    static void gameClose() {
        loop = false;
        clearCommand();
        System.out.print(SHOW_CURSOR);
        System.exit(0);
    }
}


// GAME VARIABLES

class Variables
{
    static Random rand = new Random();
    static final int ROWS = 20;
    static final int COLS = 10;
    static final int START_POSITION_X = 4;
    static final int START_POSITION_Y = 0;

    static final char BLOCK = '▄';
    static final char EMPTY = ' ';
    static final char SHADOW = '.';
    static final char DRAW_BLOCK = BLOCK;
    static final char ERASE_BLOCK = EMPTY;
    
    static final char LEFT = 'L';
    static final char RIGHT = 'R';
    static final char DRAW_NEXT_SHAPE = 'N';

    static final String RANDOM = "Music/random.wav";
    static final String THEME1 = "Music/theme1.wav";
    static final String THEME2 = "Music/theme2.wav";
    static final String KATYUSHA = "Music/katyusha.wav";
    static final String KALINKA = "Music/kalinka.wav";
    static final String COOL_THEME = "Music/cool_theme.wav";
    static final String COOLER_THEME = "Music/cooler_theme.wav";

    static final String LINE_CLEAR = "Sound/line_clear.wav";
    static final String MOVE = "Sound/move.wav";
    static final String LANDED = "Sound/landed.wav";
    static final String ROTATE = "Sound/rotate.wav";
    static final String GAME_OVER = "Sound/game_over.wav";
    static final String LEVEL_UP = "Sound/level_up.wav";
    static final String PAUSE = "Sound/pause.wav";

    static final String COLOR_RED = "\u001B[91m";
    static final String COLOR_DARK_RED = "\u001B[31m";
    static final String COLOR_GREEN = "\u001B[92m";
    static final String COLOR_MAGENTA = "\u001B[95m";
    static final String COLOR_CYAN = "\u001B[36m";
    static final String COLOR_ORANGE = "\u001B[33m";
    static final String COLOR_WHITE = "\033[37m";
    static final String CURSOR_TOP_LEFT = "\033[H";
    static final String HIDE_CURSOR = "\033[?25l";
    static final String SHOW_CURSOR = "\033[?25h";

    static final String[] THEMES = {
        RANDOM,
        THEME1,
        THEME2,
        KATYUSHA,
        KALINKA,
        COOL_THEME,
        COOLER_THEME,
    };

    static final String[] COLORS = {
        COLOR_ORANGE,
        COLOR_GREEN,
        COLOR_CYAN,
        COLOR_MAGENTA,
        COLOR_DARK_RED
    };

    // Instead of full length, subtract one to avoid picking red.
    static String color = COLORS[rand.nextInt(0, COLORS.length - 1)];
    static String shapeColor = COLORS[rand.nextInt(0, COLORS.length)];
    static String prevColor = color;
    static String prevShapeColor = shapeColor;
    static String selectedSong;
    static String prevSong = "";

    static String[] themes = new String[THEMES.length];
    static char[][] grid = createGrid(ROWS, COLS);
    static String[][] colorGrid = createColorGrid();
    static char[][] nextShapeGrid = createGrid(4, 4);

    static int gridHeight = ROWS - 1;
    static int gridWidth = COLS - 1;

    static int shapeX = START_POSITION_X;
    static int shapeY = START_POSITION_Y;
    static int direction = 0;
    static int selectorY = 0;

    static int ogX;
    static int ogY;
    static int ogDir;

    static int level;
    static int lines;
    static int linesForNextLevel;
    static long millis;

    static boolean upPressed = false;
    static boolean downPressed = false;
    static boolean leftPressed = false;
    static boolean rightPressed = false;
    static boolean spacePressed = false;
    static boolean leftJustPressed = true;
    static boolean rightJustPressed = true;

    static boolean checkingLine = false;
    static boolean titleScreen = true;
    static boolean firstLoop = true;
    static boolean canMove = false;
    static boolean paused = false;
    static boolean loop = true;

    static final char[][][] T = {
        {
            {' ',' ',' '},
            {'▄','▄','▄'},
            {' ','▄',' '}
        },
        {
            {' ','▄'},
            {'▄','▄'},
            {' ','▄'}
        },
        {
            {' ','▄',' '},
            {'▄','▄','▄'}
        },
        {
            {' ','▄',' '},
            {' ','▄','▄'},
            {' ','▄',' '}
        }
    };

    static final char[][][] L = {
        {
            {' ',' ','▄'},
            {'▄','▄','▄'}
        },
        {
            {' ','▄',' '},
            {' ','▄',' '},
            {' ','▄','▄'}
        },
        {
            {' ',' ',' '},
            {'▄','▄','▄'},
            {'▄',' ',' '}
        },
        {
            {'▄','▄'},
            {' ','▄'},
            {' ','▄'}
        }
    };

    static final char[][][] J = {
        {
            {' ',' ',' '},
            {'▄','▄','▄'},
            {' ',' ','▄'}
        },
        {
            {' ','▄'},
            {' ','▄'},
            {'▄','▄'}
        },
        {
            {'▄',' ',' '},
            {'▄','▄','▄'}
        },
        {
            {' ','▄','▄'},
            {' ','▄',' '},
            {' ','▄',' '}
        }
    };

    static final char[][][] Z = {
        {
            {'▄','▄',' '},
            {' ','▄','▄'}
        },
        {
            {' ','▄'},
            {'▄','▄'},
            {'▄',' '}
        }
    };

    static final char[][][] S = {
        {
            {' ','▄','▄'},
            {'▄','▄',' '}
        },
        {
            {' ','▄',' '},
            {' ','▄','▄'},
            {' ',' ','▄'}
        },
    };

    static final char[][][] I = {
        {
            {' ',' ',' ',' '},
            {'▄','▄','▄','▄'}
        },
        {
            {' ','▄'},
            {' ','▄'},
            {' ','▄'},
            {' ','▄'}
        }
    };

    static final char[][][] O = {
        {
            {'▄','▄'},
            {'▄','▄'}
        }
    };

    static final char[][][][] SHAPES = {T, L, J, Z, S, I, O};
    static char[][][] shape = SHAPES[rand.nextInt(0, SHAPES.length)];
    static char[][][] nextShape = SHAPES[rand.nextInt(0, SHAPES.length)];

    private static char[][] createGrid(int rows, int cols) {
        char[][] grid = new char[ROWS][COLS];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col] = EMPTY;
            }
        }

        return grid;
    }

    private static String[][] createColorGrid() {
        String[][] grid = new String[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = "";
            }
        }

        return grid;
    }
}
