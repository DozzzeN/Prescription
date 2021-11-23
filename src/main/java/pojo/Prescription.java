package pojo;

public class Prescription {
    private String pkid;
    private String id;
    private String name;
    private String medicine;
    private String specification;
    private String unit;
    private String price;
    private String number;
    private String amount;

    public Prescription(String pkid, String id, String name, String medicine, String specification, String unit, String price, String number, String amount) {
        this.pkid = pkid;
        this.id = id;
        this.name = name;
        this.medicine = medicine;
        this.specification = specification;
        this.unit = unit;
        this.price = price;
        this.number = number;
        this.amount = amount;
    }

    public Prescription() {
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "pkid='" + pkid + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", medicine='" + medicine + '\'' +
                ", specification='" + specification + '\'' +
                ", unit='" + unit + '\'' +
                ", price='" + price + '\'' +
                ", number='" + number + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }

    public String getPkid() {
        return pkid;
    }

    public void setPkid(String pkid) {
        this.pkid = pkid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
