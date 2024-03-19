package UF4.media;

public class Media {
    private String name;
    private int length;

    public Media(String name, int length) {
        this.name = name;
        this.length = length;
    }

    // Getters & Setters.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public static void main(String[] args) {
        MP3 lakeOfFire = new MP3("Lake of fire", 300, "Nirvana", "Grunge", 2);
        Ogg chopSuey = new Ogg("Chop Suey", 300, "SOAD", "Metal", 1);
        System.out.println(lakeOfFire.getName());
        System.out.println(chopSuey.getName());
    }
}
