package eu.tutorials.myappkusa;

public class Event {
    private String name;
    private String date;
    private String disciplines;
    private String venue;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String name, String date, String disciplines, String venue) {
        this.name = name;
        this.date = date;
        this.disciplines = disciplines;
        this.venue = venue;
    }


    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getDisciplines() {
        return disciplines;
    }

    public String getVenue() {
        return venue;
    }
}
