package UF4.media;

public class Video extends Media {

    private String director;
    private String language;

    public Video(String name, int length, String director, String language) {
        super(name, length);
        this.director = director;
        this.language = language;
    }

    // Getters & Setters.
    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
