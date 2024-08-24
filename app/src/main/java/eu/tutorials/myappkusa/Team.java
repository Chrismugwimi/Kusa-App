package eu.tutorials.myappkusa;

public class Team {
    private String name; // This is the student's name
    private boolean verified;

    // Default constructor required for Firebase
    public Team() {
    }

    public Team(String name, boolean verified) {
        this.name = name;
        this.verified = verified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return name + (verified ? " (Verified)" : " (Not Verified)");
    }
}
