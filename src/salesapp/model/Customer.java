package salesapp.model;

public class Customer {

    private int id;
    private String code;
    private String name;
    private String address;
    private String province;
    private String city;
    private String district;
    private String subdistrict;
    private String postalCode;

    public Customer() {
    }

    public Customer(int id, String code, String name, String address, String province,
            String city, String district, String subdistrict, String postalCode) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
        this.province = province;
        this.city = city;
        this.district = district;
        this.subdistrict = subdistrict;
        this.postalCode = postalCode;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSubdistrict() {
        return subdistrict;
    }

    public void setSubdistrict(String subdistrict) {
        this.subdistrict = subdistrict;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return name; 
    }
}
