package albertperez.healthsites;

import java.util.List;

public class User {

    private String id;
    private String username; // primary key
    private String surname;
    private String name;
    private String email;
    private String password;
    private Long dateOfBirth;
    private String gender;
    private Boolean premium;
    //private List<HealthSite> healthSites;

    public User() { }

    public User(String username, String name, String surname, String email, String password, Long dateOfBirth, String gender, Boolean premium) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.premium = false;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getDateOfBirth(){return dateOfBirth;}

    public void setDateOfBirth(Long dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    /*public List<HealthSite> getHealthSites() {
        return healthSites;
    }

    public void setHealthSites(List<HealthSite> healthSites) {
        this.healthSites = healthSites;
    }*/
}
