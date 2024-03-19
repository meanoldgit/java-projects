package UF4.media;

public class MP3 extends Music {

    private int mode;
    
    public MP3(String name, int length, String artist, String genre, int mode) {
        super(name, length, artist, genre);
        this.mode = mode;
    }

    // Getters & Setters.
    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
