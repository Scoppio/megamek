/*
 * Copyright (c) 2025 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMek.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MegaMek. If not, see <http://www.gnu.org/licenses/>.
 */
package megamek.common.weapons;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import megamek.common.EquipmentFlag;
import megamek.common.TechAdvancement;
import megamek.common.WeaponType;
import megamek.common.WeaponTypeFlag;
import megamek.common.weapons.ppc.PPCWeapon;
import megamek.common.weapons.srms.SRMWeapon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Loads weapon definitions from YAML files and creates Weapon instances
 */
public class YamlWeaponLoader {
    private static final Logger logger = LogManager.getLogger(YamlWeaponLoader.class);
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private static final String DEFAULT_WEAPONS_DIR = "data/weapons";
    
    /**
     * Get the weapons directory, allowing override via system property
     * 
     * @return The path to the weapons directory
     */
    private static String getWeaponsDir() {
        return System.getProperty("megamek.weapons.dir", DEFAULT_WEAPONS_DIR);
    }
    
    // Map of weapon type to base class
    private static final Map<String, Class<? extends Weapon>> WEAPON_BASE_CLASSES = new HashMap<>();
    
    static {
        // Initialize the map with supported weapon types
        WEAPON_BASE_CLASSES.put("srm", SRMWeapon.class);
        WEAPON_BASE_CLASSES.put("ppc", PPCWeapon.class);
        // Add more weapon types as needed
    }
    
    /**
     * Load all weapon definitions found in the weapons directory
     * 
     * @return A map of internal name to weapon instance
     */
    public static Map<String, Weapon> loadAllWeapons() {
        Path weaponsDir = Paths.get(getWeaponsDir());
        if (!Files.exists(weaponsDir)) {
            logger.info("Weapons directory not found: " + weaponsDir.toAbsolutePath());
            return Collections.emptyMap();
        }
        
        Map<String, Weapon> weapons = new HashMap<>();
        
        try (Stream<Path> walk = Files.walk(weaponsDir)) {
            List<Path> files = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".yaml") || path.toString().endsWith(".yml"))
                    .toList();
            
            for (Path file : files) {
                try {
                    loadWeaponsFromFile(file.toFile(), weapons);
                } catch (Exception e) {
                    logger.error("Error loading weapons from file: " + file, e);
                }
            }
        } catch (IOException e) {
            logger.error("Error walking weapons directory", e);
        }
        
        return weapons;
    }
    
    /**
     * Load weapons from a single YAML file
     * 
     * @param file The YAML file to load
     * @param weaponsMap The map to add the loaded weapons to
     * @throws IOException If there is an error reading the file
     */
    private static void loadWeaponsFromFile(File file, Map<String, Weapon> weaponsMap) throws IOException {
        logger.debug("Loading weapons from file: " + file);
        
        YamlWeaponDefinition definition = mapper.readValue(file, YamlWeaponDefinition.class);
        Weapon weapon = createWeaponFromDefinition(definition);
        
        if (weapon != null) {
            weaponsMap.put(weapon.getInternalName(), weapon);
            logger.debug("Loaded weapon: " + weapon.getInternalName());
        }
    }
    
    /**
     * Create a weapon instance from a definition
     * 
     * @param definition The weapon definition
     * @return The weapon instance, or null if creation failed
     */
    private static Weapon createWeaponFromDefinition(YamlWeaponDefinition definition) {
        String type = definition.getType();
        
        if (type == null || !WEAPON_BASE_CLASSES.containsKey(type.toLowerCase())) {
            logger.error("Unknown weapon type: " + type);
            return null;
        }
        
        Class<? extends Weapon> baseClass = WEAPON_BASE_CLASSES.get(type.toLowerCase());
        
        try {
            // Create a dynamically named class for the weapon
            String className = definition.getInternalName().replaceAll("[^a-zA-Z0-9]", "");
            Weapon weapon = instantiateWeapon(baseClass, className);
            
            if (weapon != null) {
                configureWeapon(weapon, definition);
            }
            
            return weapon;
        } catch (Exception e) {
            logger.error("Error creating weapon from definition: " + definition.getInternalName(), e);
            return null;
        }
    }
    
    /**
     * Instantiate a weapon of the given base class
     * 
     * @param baseClass The base class of the weapon
     * @param className The desired class name (for debugging)
     * @return The instantiated weapon
     */
    private static Weapon instantiateWeapon(Class<? extends Weapon> baseClass, String className) {
        try {
            Constructor<? extends Weapon> constructor = baseClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.error("Error instantiating weapon: " + className, e);
            return null;
        }
    }
    
    /**
     * Configure a weapon instance from a definition
     * 
     * @param weapon The weapon to configure
     * @param definition The definition to use
     */
    private static void configureWeapon(Weapon weapon, YamlWeaponDefinition definition) {
        // Set basic properties
        weapon.setName(definition.getName());
        weapon.setInternalName(definition.getInternalName());
        
        // Add lookup names
        for (String lookupName : definition.getLookupNames()) {
            weapon.addLookupName(lookupName);
        }
        
        // Set weapon properties
        weapon.setHeat(definition.getHeat());
        
        if (definition.getDamage() != -1) {
            weapon.setDamage(definition.getDamage());
        }
        
        weapon.rackSize = definition.getRackSize();
        weapon.minimumRange = definition.getMinimumRange();
        weapon.shortRange = definition.getShortRange();
        weapon.mediumRange = definition.getMediumRange();
        weapon.longRange = definition.getLongRange();
        weapon.extremeRange = definition.getExtremeRange();
        
        // Set water ranges if specified
        if (definition.getWaterShortRange() != -1) {
            weapon.waterShortRange = definition.getWaterShortRange();
        }
        if (definition.getWaterMediumRange() != -1) {
            weapon.waterMediumRange = definition.getWaterMediumRange();
        }
        if (definition.getWaterLongRange() != -1) {
            weapon.waterLongRange = definition.getWaterLongRange();
        }
        if (definition.getWaterExtremeRange() != -1) {
            weapon.waterExtremeRange = definition.getWaterExtremeRange();
        }
        
        // Set physical characteristics
        weapon.setTonnage(definition.getTonnage());
        weapon.setCriticals(definition.getCriticals());
        
        // Set game values
        weapon.setBv(definition.getBv());
        weapon.setCost(definition.getCost());
        
        // Set attack values if specified
        if (definition.getShortAV() != -1) {
            weapon.setShortAV(definition.getShortAV());
        }
        if (definition.getMedAV() != -1) {
            weapon.setMedAV(definition.getMedAV());
        }
        if (definition.getLongAV() != -1) {
            weapon.setLongAV(definition.getLongAV());
        }
        if (definition.getExtAV() != -1) {
            weapon.setExtAV(definition.getExtAV());
        }
        
        // Set max range
        if (definition.getMaxRange() != null) {
            switch (definition.getMaxRange().toUpperCase()) {
                case "RANGE_SHORT":
                    weapon.setMaxRange(WeaponType.RANGE_SHORT);
                    break;
                case "RANGE_MED":
                    weapon.setMaxRange(WeaponType.RANGE_MED);
                    break;
                case "RANGE_LONG":
                    weapon.setMaxRange(WeaponType.RANGE_LONG);
                    break;
                case "RANGE_EXT":
                    weapon.setMaxRange(WeaponType.RANGE_EXT);
                    break;
                default:
                    logger.warn("Unknown max range: {}", definition.getMaxRange());
            }
        }
        
        // Set rules references
        weapon.rulesRefs = definition.getRulesRefs();
        
        // Set flags
        for (String flagName : definition.getFlags()) {
            String flagNameFixed = flagName.toUpperCase();
            try {
                if (!flagNameFixed.startsWith("F_")) {
                    flagNameFixed = "F_" + flagNameFixed;
                }
                EquipmentFlag flag = WeaponTypeFlag.valueOf(flagNameFixed);
                weapon.getFlags().set(flag);
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown flag: {} ({})", flagName, flagNameFixed, e);
            }
        }
        
        // Handle tech advancement
        configureTechAdvancement(weapon, definition);
        
        // Set specialized fields
        if (definition.getAmmoType() != -1) {
            weapon.ammoType = definition.getAmmoType();
        }
        
        if (definition.getAtClass() != -1) {
            weapon.setAtClass(definition.getAtClass());
        }
        
        weapon.setExplosive(definition.isExplosive());
        
        if (definition.getSortingName() != null) {
            weapon.setSortingName(definition.getSortingName());
        }
    }
    
    /**
     * Configure the tech advancement of a weapon
     * 
     * @param weapon The weapon to configure
     * @param definition The definition containing tech advancement info
     */
    private static void configureTechAdvancement(Weapon weapon, YamlWeaponDefinition definition) {
        TechAdvancement techAdvancement = weapon.getTechAdvancement();
        
        // Set tech base
        if ("ALL".equals(definition.getTechBase())) {
            techAdvancement.setTechBase(TechAdvancement.TECH_BASE_ALL);
        } else if ("IS".equals(definition.getTechBase())) {
            techAdvancement.setTechBase(TechAdvancement.TECH_BASE_IS);
        } else if ("CLAN".equals(definition.getTechBase())) {
            techAdvancement.setTechBase(TechAdvancement.TECH_BASE_CLAN);
        }
        
        // Set tech rating
        if (definition.getTechRating() != null) {
            try {
                int techRating = (int) TechAdvancement.class.getField("RATING_" + definition.getTechRating()).get(null);
                techAdvancement.setTechRating(techRating);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.warn("Unknown tech rating: " + definition.getTechRating(), e);
            }
        }
        
        // Set intro level and official status
        techAdvancement.setIntroLevel(definition.isIntroLevel());
        techAdvancement.setUnofficial(definition.isUnofficial());
        
        // Set availability
        if (definition.getAvailability().size() == 4) {
            try {
                int[] availabilities = new int[4];
                for (int i = 0; i < 4; i++) {
                    availabilities[i] = (int) TechAdvancement.class.getField("RATING_" + definition.getAvailability().get(i)).get(null);
                }
                techAdvancement.setAvailability(availabilities[0], availabilities[1], availabilities[2], availabilities[3]);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.warn("Error setting availability", e);
            }
        }
        
        // Set IS advancement
        YamlWeaponDefinition.TechAdvancementYear isAdvancement = definition.getIsAdvancement();
        techAdvancement.setISAdvancement(
            isAdvancement.getIntroDate(),
            isAdvancement.getProductionDate(),
            isAdvancement.getCommonDate(),
            isAdvancement.getExtinctionDate(),
            isAdvancement.getReintroductionDate()
        );
        techAdvancement.setISApproximate(isAdvancement.isApproximate(), isAdvancement.isApproximate(), isAdvancement.isApproximate(), isAdvancement.isApproximate(), isAdvancement.isApproximate());
        
        // Set Clan advancement
        YamlWeaponDefinition.TechAdvancementYear clanAdvancement = definition.getClanAdvancement();
        techAdvancement.setClanAdvancement(
            clanAdvancement.getIntroDate(),
            clanAdvancement.getProductionDate(),
            clanAdvancement.getCommonDate(),
            clanAdvancement.getExtinctionDate(),
            clanAdvancement.getReintroductionDate()
        );
        techAdvancement.setClanApproximate(clanAdvancement.isApproximate(), clanAdvancement.isApproximate(), clanAdvancement.isApproximate(), clanAdvancement.isApproximate(), clanAdvancement.isApproximate());
        
        // Set factions
        if (!definition.getPrototypeFactions().isEmpty()) {
            int[] factions = new int[definition.getPrototypeFactions().size()];
            for (int i = 0; i < definition.getPrototypeFactions().size(); i++) {
                try {
                    factions[i] = (int) TechAdvancement.class.getField("F_" + definition.getPrototypeFactions().get(i)).get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    logger.warn("Unknown prototype faction: " + definition.getPrototypeFactions().get(i), e);
                }
            }
            techAdvancement.setPrototypeFactions(factions);
        }
        
        if (!definition.getProductionFactions().isEmpty()) {
            int[] factions = new int[definition.getProductionFactions().size()];
            for (int i = 0; i < definition.getProductionFactions().size(); i++) {
                try {
                    factions[i] = (int) TechAdvancement.class.getField("F_" + definition.getProductionFactions().get(i)).get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    logger.warn("Unknown production faction: " + definition.getProductionFactions().get(i), e);
                }
            }
            techAdvancement.setProductionFactions(factions);
        }
    }
}
