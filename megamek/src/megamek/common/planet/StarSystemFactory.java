package megamek.common.planet;

import java.time.LocalDate;
import java.util.Random;

public class StarSystemFactory {

    private final Random random;
    private final StarFactory starFactory;
    private final PlanetFactory planetFactory;

    public StarSystemFactory(long seed) {
        this.random = new Random(seed);
        // We can share the same random among sub-factories
        this.starFactory = new StarFactory(this.random);
        this.planetFactory = new PlanetFactory(this.random);
    }

    /**
     * Create a StarSystem from some data object (parsed from YAML or similar).
     * The date argument can be used for deciding population or other time-sensitive data.
     */
    public StarSystem createStarSystem(StarSystemData data) {
        StarSystem starSystem = new StarSystem();

        starSystem.setId(data.getId());
        starSystem.setSucsId(data.getSucsId());
        starSystem.setxCoord(data.getXcoord());
        starSystem.setyCoord(data.getYcoord());
        starSystem.setSpectralType(data.getSpectralType());

        // Create the star
        Star star = starFactory.createStar(data.getSpectralType());
        starSystem.setStar(star);

        // Process top-level events in the star system
        if (data.getEvents() != null) {
            data.getEvents().forEach(e -> {
                Event event = new Event(e.getDate(), e.getDescription());
                // set other details (faction, etc.) as needed
                starSystem.addEvent(event);
            });
        }

        // Create each planet using PlanetFactory
        if (data.getPlanets() != null) {
            data.getPlanets().forEach(planetData -> {
                Planet planet = planetFactory.createPlanet(planetData);
                starSystem.addPlanet(planet);
            });
        }

        return starSystem;
    }
}
