package eu.tutorials.myappkusa;

public class MainModel {

    String Date, Sports, Venue;
    MainModel()
    {

    }

    public MainModel(String date, String sports, String venue) {
        Date = date;
        Sports = sports;
        Venue = venue;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getSports() {
        return Sports;
    }

    public void setSports(String sports) {
        Sports = sports;
    }

    public String getVenue() {
        return Venue;
    }

    public void setVenue(String venue) {
        Venue = venue;
    }
}
