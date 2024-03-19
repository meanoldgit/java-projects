package UF4.media;

public class Ogg extends Music {

    private int version;
    
    public Ogg(String name, int length, String artist, String genre, int version) {
        super(name, length, artist, genre);
        this.version = version;
    }

    // Getters & Setters.
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
