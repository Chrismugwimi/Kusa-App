package eu.tutorials.myappkusa;

public class UserHelperClass {
    private String name;
    private String institution;
    private String sportPlayed;
    private int yearOfStudy;

    // Required default constructor for Firebase
    public UserHelperClass() {
    }

    // Constructor to initialize user details
    public UserHelperClass(String name, String institution, String sportPlayed, int yearOfStudy) {
        this.name = name;
        this.institution = institution;
        this.sportPlayed = sportPlayed;
        this.yearOfStudy = yearOfStudy;
    }

    // Getters and setters for user details
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getSportPlayed() {
        return sportPlayed;
    }

    public void setSportPlayed(String sportPlayed) {
        this.sportPlayed = sportPlayed;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }
}
