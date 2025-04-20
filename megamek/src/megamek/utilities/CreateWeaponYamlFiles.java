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
package megamek.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import megamek.common.EquipmentFlag;
import megamek.common.EquipmentType;
import megamek.common.RangeType;
import megamek.common.TechAdvancement;
import megamek.common.WeaponTypeFlag;
import megamek.common.weapons.Weapon;
import megamek.common.weapons.YamlWeaponDefinition;
import megamek.common.weapons.ppc.ISPPC;
import megamek.common.weapons.ppc.PPCWeapon;
import megamek.common.weapons.srms.ISSRM2;
import megamek.common.weapons.srms.SRMWeapon;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Utility to create YAML definition files from existing weapon classes.
 * This is intended to help game modders convert existing hardcoded weapons
 * into YAML format.
 */
public class CreateWeaponYamlFiles {
    
    private static final Path DEFAULT_OUTPUT_DIR = Paths.get("data", "weapons");
    
    public static void main(String[] args) {
        try {
            // Initialize EquipmentType to load all weapon types
            EquipmentType.initializeTypes();
            
            // Get the output directory
            Path outputDir = DEFAULT_OUTPUT_DIR;
            if (args.length > 0) {
                outputDir = Paths.get(args[0]);
            }
            
            System.out.println("Creating YAML weapon files in: " + outputDir);
            
            // Create the directory if it doesn't exist
            Files.createDirectories(outputDir);
            
            // Extract SRM weapons - use concrete classes
            extractWeaponsOfType(SRMWeapon.class, "srm", outputDir.resolve("srms"), ISSRM2.class);
            
            // Extract PPC weapons
            extractWeaponsOfType(PPCWeapon.class, "ppc", outputDir.resolve("ppcs"), ISPPC.class);
            
            System.out.println("YAML weapon files created successfully!");
            
        } catch (Exception e) {
            System.err.println("Error creating YAML weapon files: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Extract weapons of a specific type and create YAML files
     * 
     * @param baseClass The base class of the weapons
     * @param type The weapon type string
     * @param outputDir The output directory
     * @param concreteClass The concrete class to use for test or empty definitions
     * @throws IOException If there is an error writing the files
     */
    private static void extractWeaponsOfType(Class<? extends Weapon> baseClass, String type, Path outputDir, 
                                             Class<? extends Weapon> concreteClass) throws IOException {
        // Create the directory if it doesn't exist
        Files.createDirectories(outputDir);
        
        // Get all weapons of the specified type
        List<Weapon> weapons = getWeaponsOfType(baseClass);
        
        // Create a YAML mapper with block-style output
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory()
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        
        // Create a YAML file for each weapon
        int count = 0;
        for (Weapon weapon : weapons) {
            try {
                YamlWeaponDefinition definition = createDefinitionFromWeapon(weapon, type);
                mapper.writeValue(outputDir.resolve(weapon.getInternalName() + ".yaml").toFile(), definition);
                count++;
            } catch (Exception e) {
                System.err.println("Error creating YAML for weapon: " + weapon.getInternalName() + " - " + e.getMessage());
            }
        }
        
        // If no weapons were found, create a sample weapon using the concrete class
        if (count == 0 && concreteClass != null) {
            try {
                // Create an instance of the concrete class
                Weapon sampleWeapon = concreteClass.getDeclaredConstructor().newInstance();
                YamlWeaponDefinition definition = createDefinitionFromWeapon(sampleWeapon, type);
                
                // Add a suffix to make it clear this is a sample
                definition.setInternalName(sampleWeapon.getInternalName() + "-Sample");
                definition.setName(sampleWeapon.getName() + " (Sample)");
                
                // Write the sample weapon
                mapper.writeValue(outputDir.resolve(sampleWeapon.getInternalName() + "-Sample.yaml").toFile(), definition);
                count = 1;
                System.out.println("Created a sample weapon file based on " + sampleWeapon.getInternalName());
            } catch (Exception e) {
                System.err.println("Error creating sample weapon: " + e.getMessage());
            }
        }
        
        System.out.println("Created " + count + " " + type + " weapon files in " + outputDir);
    }
    
    /**
     * Get all weapons of a specific type
     * 
     * @param baseClass The base class of the weapons
     * @return A list of weapons
     */
    @SuppressWarnings("unchecked")
    private static List<Weapon> getWeaponsOfType(Class<? extends Weapon> baseClass) {
        List<Weapon> weapons = new ArrayList<>();
        
        if (EquipmentType.getAllTypes() != null) {
            var allTypes = EquipmentType.getAllTypes();
            while (allTypes.hasMoreElements()) {
                EquipmentType type = allTypes.nextElement();
                if (baseClass.isInstance(type)) {
                    weapons.add((Weapon) type);
                }
            }
        }
        
        return weapons;
    }
    
    /**
     * Create a YAML weapon definition from a weapon
     * 
     * @param weapon The weapon
     * @param type The weapon type string
     * @return The weapon definition
     */
    private static YamlWeaponDefinition createDefinitionFromWeapon(Weapon weapon, String type) {
        YamlWeaponDefinition definition = new YamlWeaponDefinition();
        
        // Set type
        try {
            setField(definition, "type", type);
        } catch (Exception e) {
            System.err.println("Error setting type: " + e.getMessage());
        }
        
        // Set basic identification
        try {
            setField(definition, "name", weapon.getName());
            setField(definition, "internalName", weapon.getInternalName());
            setField(definition, "lookupNames", getLookupNames(weapon));
        } catch (Exception e) {
            System.err.println("Error setting identification: " + e.getMessage());
        }
        
        // Set basic attributes
        try {
            setField(definition, "heat", weapon.getHeat());
            
            if (weapon.getDamage() != Weapon.DAMAGE_VARIABLE) {
                setField(definition, "damage", weapon.getDamage());
            }
            
            setField(definition, "rackSize", weapon.getRackSize());
        } catch (Exception e) {
            System.err.println("Error setting attributes: " + e.getMessage());
        }
        
        // Set range values
        try {
            if (weapon.getMinimumRange() != Weapon.WEAPON_NA) {
                setField(definition, "minimumRange", weapon.getMinimumRange());
            }
            
            setField(definition, "shortRange", weapon.getShortRange());
            setField(definition, "mediumRange", weapon.getMediumRange());
            setField(definition, "longRange", weapon.getLongRange());
            setField(definition, "extremeRange", weapon.getExtremeRange());
            
            // Set water ranges if they're different from the default
            if (weapon.getWShortRange() != (int) (weapon.getShortRange() * 0.5)) {
                setField(definition, "waterShortRange", weapon.getWShortRange());
            }
            
            if (weapon.getWMediumRange() != (int) (weapon.getMediumRange() * 0.5)) {
                setField(definition, "waterMediumRange", weapon.getWMediumRange());
            }
            
            if (weapon.getWLongRange() != (int) (weapon.getLongRange() * 0.5)) {
                setField(definition, "waterLongRange", weapon.getWLongRange());
            }
            
            if (weapon.getWExtremeRange() != (int) (weapon.getExtremeRange() * 0.5)) {
                setField(definition, "waterExtremeRange", weapon.getWExtremeRange());
            }
        } catch (Exception e) {
            System.err.println("Error setting ranges: " + e.getMessage());
        }
        
        // Set physical characteristics
        try {
            setField(definition, "tonnage", weapon.getTonnage(null));
            setField(definition, "criticals", weapon.getCriticals(null));
        } catch (Exception e) {
            System.err.println("Error setting physical characteristics: " + e.getMessage());
        }
        
        // Set game values
        try {
            setField(definition, "bv", weapon.getBV(null));
            setField(definition, "cost", weapon.getCost(null, false, 0));
        } catch (Exception e) {
            System.err.println("Error setting game values: " + e.getMessage());
        }
        
        // Set attack values
        try {
            if (weapon.getShortAV() != 0) {
                setField(definition, "shortAV", weapon.getShortAV());
            }
            
            if (weapon.getMedAV() != 0) {
                setField(definition, "medAV", weapon.getMedAV());
            }
            
            if (weapon.getLongAV() != 0) {
                setField(definition, "longAV", weapon.getLongAV());
            }
            
            if (weapon.getExtAV() != 0) {
                setField(definition, "extAV", weapon.getExtAV());
            }
            
            String maxRangeStr = null;
            if (weapon.getMaxRange() == RangeType.RANGE_SHORT) {
                maxRangeStr = "RANGE_SHORT";
            } else if (weapon.getMaxRange() == RangeType.RANGE_MEDIUM) {
                maxRangeStr = "RANGE_MED";
            } else if (weapon.getMaxRange() == RangeType.RANGE_LONG) {
                maxRangeStr = "RANGE_LONG";
            } else if (weapon.getMaxRange() == RangeType.RANGE_EXTREME) {
                maxRangeStr = "RANGE_EXT";
            }
            
            if (maxRangeStr != null) {
                setField(definition, "maxRange", maxRangeStr);
            }
        } catch (Exception e) {
            System.err.println("Error setting attack values: " + e.getMessage());
        }
        
        // Set rules references
        try {
            if (weapon.getRulesRefs() != null) {
                setField(definition, "rulesRefs", weapon.getRulesRefs());
            }
        } catch (Exception e) {
            System.err.println("Error setting rules references: " + e.getMessage());
        }
        
        // Set flags
        try {
            setField(definition, "flags", getFlagNames(weapon));
        } catch (Exception e) {
            System.err.println("Error setting flags: " + e.getMessage());
        }
        
        // Set tech advancement
        try {
            setTechAdvancement(definition, weapon.getTechAdvancement());
        } catch (Exception e) {
            System.err.println("Error setting tech advancement: " + e.getMessage());
        }
        
        // Set specialized fields
        try {
            Field explosiveField = YamlWeaponDefinition.class.getDeclaredField("explosive");
            explosiveField.setAccessible(true);
            explosiveField.set(definition, weapon.isExplosive(null));
            
            if (weapon.getSortingName() != null) {
                Field sortingNameField = YamlWeaponDefinition.class.getDeclaredField("sortingName");
                sortingNameField.setAccessible(true);
                sortingNameField.set(definition, weapon.getSortingName());
            }
        } catch (Exception e) {
            System.err.println("Error setting specialized fields: " + e.getMessage());
        }
        
        return definition;
    }
    
    /**
     * Get the lookup names for a weapon
     * 
     * @param weapon The weapon
     * @return The lookup names
     */
    private static List<String> getLookupNames(Weapon weapon) {
        List<String> lookupNames = new ArrayList<>();
        try {
            Field lookupNamesField = EquipmentType.class.getDeclaredField("names");
            lookupNamesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<String> names = (List<String>) lookupNamesField.get(weapon);
            if (names != null) {
                for (String name : names) {
                    if (!name.equals(weapon.getName()) && !name.equals(weapon.getInternalName())) {
                        lookupNames.add(name);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting lookup names: " + e.getMessage());
        }
        return lookupNames;
    }
    
    /**
     * Get the flag names for a weapon
     * 
     * @param weapon The weapon
     * @return The flag names
     */
    private static List<String> getFlagNames(Weapon weapon) {
        List<String> flagNames = new ArrayList<>();
        try {
            var flags = weapon.getFlags();
            for (WeaponTypeFlag flag : WeaponTypeFlag.values()) {
                if (flags.get(flag)) {
                    flagNames.add(flag.name());
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting flag names: " + e.getMessage());
        }
        return flagNames;
    }
    
    /**
     * Set tech advancement information in a weapon definition
     * 
     * @param definition The weapon definition
     * @param techAdvancement The tech advancement
     */
    private static void setTechAdvancement(YamlWeaponDefinition definition, TechAdvancement techAdvancement) {
        try {
            // Set tech base
            String techBase = "IS";
            if (techAdvancement.getTechBase() == TechAdvancement.TECH_BASE_ALL) {
                techBase = "ALL";
            } else if (techAdvancement.getTechBase() == TechAdvancement.TECH_BASE_CLAN) {
                techBase = "CLAN";
            }
            
            Field techBaseField = YamlWeaponDefinition.class.getDeclaredField("techBase");
            techBaseField.setAccessible(true);
            techBaseField.set(definition, techBase);
            
            // Set tech rating
            String techRating = null;
            int rating = techAdvancement.getTechRating();
            Field[] fields = TechAdvancement.class.getFields();
            for (Field field : fields) {
                if (field.getName().startsWith("RATING_") && field.getType() == int.class) {
                    if (field.getInt(null) == rating) {
                        techRating = field.getName().substring(7);
                        break;
                    }
                }
            }
            
            if (techRating != null) {
                Field techRatingField = YamlWeaponDefinition.class.getDeclaredField("techRating");
                techRatingField.setAccessible(true);
                techRatingField.set(definition, techRating);
            }
            
            // Set intro level and unofficial status
            Field introLevelField = YamlWeaponDefinition.class.getDeclaredField("introLevel");
            introLevelField.setAccessible(true);
            introLevelField.set(definition, techAdvancement.isIntroLevel());
            
            Field unofficialField = YamlWeaponDefinition.class.getDeclaredField("unofficial");
            unofficialField.setAccessible(true);
            unofficialField.set(definition, techAdvancement.isUnofficial());
            
            // Set availability
            List<String> availability = new ArrayList<>();
            int[] avail = techAdvancement.getAvailability();
            for (int j : avail) {
                for (Field field : fields) {
                    if (field.getName().startsWith("RATING_") && field.getType() == int.class) {
                        if (field.getInt(null) == j) {
                            availability.add(field.getName().substring(7));
                            break;
                        }
                    }
                }
            }
            
            if (availability.size() == 4) {
                Field availabilityField = YamlWeaponDefinition.class.getDeclaredField("availability");
                availabilityField.setAccessible(true);
                availabilityField.set(definition, availability);
            }
            
            // Set IS advancement
            YamlWeaponDefinition.TechAdvancementYear isAdvancement = createTechAdvancementYear(
                techAdvancement.getIntroductionDate(false),
                techAdvancement.getProductionDate(false),
                techAdvancement.getCommonDate(false),
                techAdvancement.getExtinctionDate(false),
                techAdvancement.getReintroductionDate(false),
                isApproximate(techAdvancement, false)
            );
            
            Field isAdvancementField = YamlWeaponDefinition.class.getDeclaredField("isAdvancement");
            isAdvancementField.setAccessible(true);
            isAdvancementField.set(definition, isAdvancement);
            
            // Set Clan advancement
            YamlWeaponDefinition.TechAdvancementYear clanAdvancement = createTechAdvancementYear(
                techAdvancement.getIntroductionDate(true),
                techAdvancement.getProductionDate(true),
                techAdvancement.getCommonDate(true),
                techAdvancement.getExtinctionDate(true),
                techAdvancement.getReintroductionDate(true),
                isApproximate(techAdvancement, true)
            );
            
            Field clanAdvancementField = YamlWeaponDefinition.class.getDeclaredField("clanAdvancement");
            clanAdvancementField.setAccessible(true);
            clanAdvancementField.set(definition, clanAdvancement);
            
            // Set prototype factions
            List<String> prototypeFactions = getFactionNames(techAdvancement.getPrototypeFactions());
            if (!prototypeFactions.isEmpty()) {
                Field prototypeFactionsField = YamlWeaponDefinition.class.getDeclaredField("prototypeFactions");
                prototypeFactionsField.setAccessible(true);
                prototypeFactionsField.set(definition, prototypeFactions);
            }
            
            // Set production factions
            List<String> productionFactions = getFactionNames(techAdvancement.getProductionFactions());
            if (!productionFactions.isEmpty()) {
                Field productionFactionsField = YamlWeaponDefinition.class.getDeclaredField("productionFactions");
                productionFactionsField.setAccessible(true);
                productionFactionsField.set(definition, productionFactions);
            }
        } catch (Exception e) {
            System.err.println("Error setting tech advancement: " + e.getMessage());
        }
    }
    
    /**
     * Create a tech advancement year object
     * 
     * @param introDate The introduction date
     * @param productionDate The production date
     * @param commonDate The common date
     * @param extinctionDate The extinction date
     * @param reintroductionDate The reintroduction date
     * @param approximate Whether the dates are approximate
     * @return The tech advancement year object
     */
    private static YamlWeaponDefinition.TechAdvancementYear createTechAdvancementYear(
            int introDate, int productionDate, int commonDate, int extinctionDate, int reintroductionDate,
            boolean approximate) {
        try {
            YamlWeaponDefinition.TechAdvancementYear year = YamlWeaponDefinition.TechAdvancementYear.class.newInstance();
            
            Field introDateField = YamlWeaponDefinition.TechAdvancementYear.class.getDeclaredField("introDate");
            introDateField.setAccessible(true);
            introDateField.set(year, introDate);
            
            Field productionDateField = YamlWeaponDefinition.TechAdvancementYear.class.getDeclaredField("productionDate");
            productionDateField.setAccessible(true);
            productionDateField.set(year, productionDate);
            
            Field commonDateField = YamlWeaponDefinition.TechAdvancementYear.class.getDeclaredField("commonDate");
            commonDateField.setAccessible(true);
            commonDateField.set(year, commonDate);
            
            Field extinctionDateField = YamlWeaponDefinition.TechAdvancementYear.class.getDeclaredField("extinctionDate");
            extinctionDateField.setAccessible(true);
            extinctionDateField.set(year, extinctionDate);
            
            Field reintroductionDateField = YamlWeaponDefinition.TechAdvancementYear.class.getDeclaredField("reintroductionDate");
            reintroductionDateField.setAccessible(true);
            reintroductionDateField.set(year, reintroductionDate);
            
            Field approximateField = YamlWeaponDefinition.TechAdvancementYear.class.getDeclaredField("approximate");
            approximateField.setAccessible(true);
            approximateField.set(year, approximate);
            
            return year;
        } catch (Exception e) {
            System.err.println("Error creating tech advancement year: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get the faction names for an array of faction IDs
     * 
     * @param factionIds The faction IDs
     * @return The faction names
     */
    private static List<String> getFactionNames(int[] factionIds) {
        List<String> factionNames = new ArrayList<>();
        if (factionIds == null) {
            return factionNames;
        }
        
        try {
            for (int factionId : factionIds) {
                Field[] fields = TechAdvancement.class.getFields();
                for (Field field : fields) {
                    if (field.getName().startsWith("F_") && field.getType() == int.class) {
                        if (field.getInt(null) == factionId) {
                            factionNames.add(field.getName().substring(2));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting faction names: " + e.getMessage());
        }
        
        return factionNames;
    }
    
    /**
     * Helper method to set a field value using reflection
     * 
     * @param object The object to set the field on
     * @param fieldName The name of the field
     * @param value The value to set
     * @throws Exception If the field doesn't exist or can't be set
     */
    private static void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
    
    /**
     * Determine if a tech advancement date is approximate
     * 
     * @param techAdvancement The tech advancement
     * @param clan Whether to check for Clan or IS
     * @return True if approximate
     */
    private static boolean isApproximate(TechAdvancement techAdvancement, boolean clan) {
        try {
            // Access the isApproximate or clanApproximate arrays using reflection
            Field field = TechAdvancement.class.getDeclaredField(clan ? "clanApproximate" : "isApproximate");
            field.setAccessible(true);
            boolean[] approximate = (boolean[]) field.get(techAdvancement);
            
            // Check if any of the dates are approximate
            for (boolean isApprox : approximate) {
                if (isApprox) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking if tech advancement is approximate: " + e.getMessage());
            return false;
        }
    }
}
