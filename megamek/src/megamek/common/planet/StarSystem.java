package megamek.common.planet;

import java.util.ArrayList;
import java.util.List;

public class StarSystem {
    private String id;
    private int sucsId;
    private double xCoord;
    private double yCoord;

    private List<Star> stars;
    private final List<Event> events = new ArrayList<>();
    private final List<Planet> planets = new ArrayList<>();

    public String getId() {
        return id;
    }

    public int getSucsId() {
        return sucsId;
    }

    public double getxCoord() {
        return xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }

    public List<Star> getStars() {
        return stars;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSucsId(int sucsId) {
        this.sucsId = sucsId;
    }

    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    public void setStars(List<Star> stars) {
        this.stars = stars;
    }
}
