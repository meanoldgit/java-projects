package clase.m3.uf4.business;

public class Transporter {
    
    private String name;
    private String phone;
    private int licenceNum;

    public Transporter(String name, String phone, int licenceNum) {
        this.name = name;
        this.phone = phone;
        this.licenceNum = licenceNum;
    }

    public void assignOrder(Order order) {}

    public void cancelOrder(Order order) {}

    // Getters & Setters.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getLicenceNum() {
        return licenceNum;
    }

    public void setLicenceNum(int licenceNum) {
        this.licenceNum = licenceNum;
    }
}
