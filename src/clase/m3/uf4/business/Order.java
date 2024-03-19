package clase.m3.uf4.business;

public class Order {
    
    private int day;
    private int month;
    private int hour;
    private int minutes;

    public Order(int day, int month, int hour, int minutes) {
        this.day = day;
        this.month = month;
        this.hour = hour;
        this.minutes = minutes;
    }

    public void addProduct(Petition petition) {}

    public void cancelOrder(Petition petition) {}

    // Getters & Setters.
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
