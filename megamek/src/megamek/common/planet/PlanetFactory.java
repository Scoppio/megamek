package megamek.common.planet;

import java.util.Random;

public class PlanetFactory {

    private final Random random;
    private final MoonFactory moonFactory;

    public PlanetFactory(Random random) {
        this.random = random;
        this.moonFactory = new MoonFactory(this.random);
    }

    public Planet createPlanet(PlanetData data) {
        Planet planet = new Planet();

        // Basic fields from data
        planet.setName(data.getName());
        planet.setPlanetType(determinePlanetType(data.getType()));
        planet.setOrbitalDistance(data.getOrbitalDist());
        planet.setSystemPosition(data.getSysPos());
        planet.setIcon(data.getIcon());

        // Pressure, atmosphere, etc. (Convert from string or enum to your enum)
        planet.setPressure(determinePressureType(data.getPressure()));
        planet.setAtmosphere(determineAtmosphereType(data.getAtmosphere()));

        planet.setComposition(data.getComposition());
        planet.setGravity(data.getGravity());
        planet.setDiameter(data.getDiameter());
        planet.setDensity(data.getDensity());
        planet.setDayLength(data.getDayLength());
        planet.setYearLength(data.getYearLength());
        planet.setAverageTemperature(data.getTemperature());
        planet.setWaterPercentage(data.getWater());

        // Seasons: any number between 1 and 4 - you can randomize or base it on data
        int seasons = 1 + random.nextInt(4);
        planet.setSeasons(seasons);

        // Description text if present
        if (data.getDesc() != null) {
            planet.setDescription(data.getDesc());
        }

        // Landmasses
        if (data.getLandmassList() != null) {
            data.getLandmassList().forEach(lm -> {
                Landmass landmass = new Landmass(lm.getName(), lm.getCapital());
                planet.getLandmasses().add(landmass);
            });
        }

        // Satellite / Moons
        if (data.getSatellites() != null) {
            data.getSatellites().forEach(satData -> {
                Moon moon = moonFactory.createMoon(satData);
                planet.getSatellites().add(moon);
            });
        }

        // Planet-specific events (could have population, faction, etc.)
        if (data.getEvents() != null) {
            data.getEvents().forEach(eData -> {
                // If it has population, store in a PopulationEvent
                if (eData.getPopulation() != null) {
                    planet.getPopulationEvents().add(
                        new PopulationEvent(eData.getDate(), eData.getPopulation())
                    );
                }
                // For other events, store them as general "Event"
                Event evt = new Event(eData.getDate(), "");
                evt.setFaction(eData.getFaction());
                evt.setSocioIndustrial(eData.getSocioIndustrial());
                // Possibly set "hiringHall", "hpgLevel" if present
                planet.getEvents().add(evt);
            });
        }

        // You can filter or pick the “current” population for the given date if needed

        return planet;
    }

    // Convert data string to PlanetType
    private PlanetType determinePlanetType(String typeString) {
        // Example naive approach
        if (typeString == null) return PlanetType.TERRESTRIAL;
        return switch (typeString.toLowerCase()) {
            case "dwarf terrestrial" -> PlanetType.DWARF_TERRESTRIAL;
            case "gas giant" -> PlanetType.GAS_GIANT;
            case "asteroid belt" -> PlanetType.ASTEROID_BELT;
            default -> PlanetType.TERRESTRIAL;
        };
    }

    private PressureType determinePressureType(String pressureString) {
        // Convert from data source. E.g. "Vacuum", "Normal", "Very High"
        if (pressureString == null) return PressureType.NORMAL;
        return switch (pressureString.toLowerCase()) {
            case "vacuum" -> PressureType.VACUUM;
            case "normal" -> PressureType.NORMAL;
            case "very high" -> PressureType.VERY_HIGH;
            default -> PressureType.NORMAL;
        };
    }

    private AtmosphereType determineAtmosphereType(String atmString) {
        if (atmString == null) return AtmosphereType.NONE;
        if (atmString.toLowerCase().contains("breathable")) {
            return AtmosphereType.BREATHABLE;
        } else if (atmString.toLowerCase().contains("toxic")) {
            return AtmosphereType.TOXIC;
        } else if (atmString.toLowerCase().contains("none")) {
            return AtmosphereType.NONE;
        }
        return AtmosphereType.NONE;
    }
}
