package megamek.common.planet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Planet {
    private String name;
    private PlanetType planetType;
    private double orbitalDistance;
    private int systemPosition;
    private String icon;

    private PressureType pressure;
    private AtmosphereType atmosphere;
    private String composition;

    private double gravity;
    private double diameter;
    private double density;
    private int dayLength;
    private double yearLength;
    private double averageTemperature;
    private double waterPercentage;

    // Seasons: any number between 1 and 4
    private int seasons;
    private final List<Landmass> landmasses = new ArrayList<>();
    private String description;

    // Moons / satellites
    private final List<Moon> satellites = new ArrayList<>();

    // Additional metadata / events of other kinds
    private final List<Event> events = new ArrayList<>();

    public Planet() {
    }

    public double getPopulationAtDate(LocalDate date) {
        double population = 0.0;
        for (Event pe : events) {
            if (!pe.getDate().isAfter(date)) {
                population = pe.getPopulation();
            } else {
                break;
            }
        }
        return population;
    }

}
