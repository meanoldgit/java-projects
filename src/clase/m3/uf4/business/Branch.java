package clase.m3.uf4.business;

public class Branch {

    private String name;
    private String address;
    private String phone;
    private String email;

    public Branch(String name, String address, String phone, String email) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public void breakTime(Transporter transporter) {}

    public void workTime(Transporter transporter) {}

    public void subscribe(Client client) {}
    
    public void unsubscribe(Client client) {}

    public void createOrder(Client client, Transporter transporter, Order order) {}

    public void cancelOrder(Order order) {}

    // Getters & Setters.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
