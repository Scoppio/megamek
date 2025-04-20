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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import megamek.common.EquipmentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registry for all weapon types in MegaMek.
 * This class is responsible for loading weapons from YAML definitions and
 * providing them to the rest of the system.
 */
public class WeaponRegistry {
    private static final Logger logger = LogManager.getLogger(WeaponRegistry.class);
    
    private static final String DEFAULT_WEAPONS_DIR = "data/weapons";
    private static final Map<String, Weapon> customWeapons = new HashMap<>();
    private static boolean weaponsLoaded = false;
    
    /**
     * Get the weapons directory, allowing override via system property
     * 
     * @return The path to the weapons directory
     */
    private static String getWeaponsDir() {
        return System.getProperty("megamek.weapons.dir", DEFAULT_WEAPONS_DIR);
    }
    
    /**
     * Initialize the registry by loading all custom weapon definitions.
     * This is called by EquipmentType.initializeTypes() to ensure custom
     * weapons are available during equipment initialization.
     */
    public static void initialize() {
        if (weaponsLoaded) {
            return;
        }
        
        // Check if the weapons directory exists
        Path weaponsDirPath = Paths.get(getWeaponsDir());
        if (!Files.exists(weaponsDirPath)) {
            // Create the directory if it doesn't exist
            try {
                Files.createDirectories(weaponsDirPath);
                logger.info("Created weapons directory: " + weaponsDirPath.toAbsolutePath());
                
                // Create example SRM and PPC weapon files
                createExampleWeaponFiles(weaponsDirPath);
            } catch (Exception e) {
                logger.error("Error creating weapons directory", e);
            }
        }
        
        // Load all weapons from YAML files
        customWeapons.putAll(YamlWeaponLoader.loadAllWeapons());
        logger.info("Loaded " + customWeapons.size() + " custom weapons from YAML files");
        
        // Add the custom weapons to EquipmentType.allTypes for lookup
        for (Weapon weapon : customWeapons.values()) {
            EquipmentType.addType(weapon);
        }
        
        weaponsLoaded = true;
    }
    
    /**
     * Get a custom weapon by its internal name
     * 
     * @param internalName The internal name of the weapon
     * @return The weapon, or null if not found
     */
    public static Weapon getCustomWeapon(String internalName) {
        return customWeapons.get(internalName);
    }
    
    /**
     * Check if a custom weapon exists
     * 
     * @param internalName The internal name of the weapon
     * @return True if the weapon exists
     */
    public static boolean hasCustomWeapon(String internalName) {
        return customWeapons.containsKey(internalName);
    }
    
    /**
     * Create example SRM and PPC weapon files in the weapons directory
     * 
     * @param weaponsDir The path to the weapons directory
     */
    private static void createExampleWeaponFiles(Path weaponsDir) {
        try {
            // Create example SRM weapon
            Path srmDir = weaponsDir.resolve("srms");
            Files.createDirectories(srmDir);
            
            String srmExample = """
                  type: srm
                  name: SRM 2
                  internalName: ISSRM2-YAML
                  lookupNames:
                    - IS SRM-2 (YAML)
                    - ISSRM2-YAML
                    - IS SRM 2 (YAML)
                  heat: 2
                  rackSize: 2
                  shortRange: 3
                  mediumRange: 6
                  longRange: 9
                  extremeRange: 12
                  tonnage: 1.0
                  criticals: 1
                  bv: 21
                  cost: 10000
                  shortAV: 2
                  maxRange: RANGE_SHORT
                  rulesRefs: "229, TM"
                  flags:
                    - NO_FIRES
                  techBase: ALL
                  techRating: C
                  introLevel: true
                  unofficial: false
                  availability:
                    - C
                    - C
                    - C
                    - C
                  isAdvancement:
                    introDate: 2365
                    productionDate: 2370
                    commonDate: 2400
                    extinctionDate: -1
                    reintroductionDate: -1
                    approximate: false
                  clanAdvancement:
                    introDate: 2365
                    productionDate: 2370
                    commonDate: 2400
                    extinctionDate: 2836
                    reintroductionDate: -1
                    approximate: false
                  prototypeFactions:
                    - TH
                  productionFactions:
                    - TH""";
            
            Files.write(srmDir.resolve("ISSRM2-YAML.yaml"), srmExample.getBytes());
            
            // Create example PPC weapon
            Path ppcDir = weaponsDir.resolve("ppcs");
            Files.createDirectories(ppcDir);
            
            String ppcExample = """
                  type: ppc
                  name: PPC
                  internalName: ISPPC-YAML
                  lookupNames:
                    - Particle Cannon (YAML)
                    - IS PPC (YAML)
                    - ISPPC-YAML
                  sortingName: PPC C
                  heat: 10
                  damage: 10
                  minimumRange: 3
                  shortRange: 6
                  mediumRange: 12
                  longRange: 18
                  extremeRange: 24
                  waterShortRange: 4
                  waterMediumRange: 7
                  waterLongRange: 10
                  waterExtremeRange: 15
                  tonnage: 7.0
                  criticals: 3
                  bv: 176
                  cost: 200000
                  shortAV: 10
                  medAV: 10
                  maxRange: RANGE_MED
                  explosive: true
                  rulesRefs: "234, TM"
                  techBase: ALL
                  techRating: D
                  introLevel: true
                  unofficial: false
                  availability:
                    - C
                    - C
                    - C
                    - C
                  isAdvancement:
                    introDate: 2440
                    productionDate: 2460
                    commonDate: 2500
                    extinctionDate: -1
                    reintroductionDate: -1
                    approximate: true
                  clanAdvancement:
                    introDate: 2440
                    productionDate: 2460
                    commonDate: 2500
                    extinctionDate: 2825
                    reintroductionDate: -1
                    approximate: true
                  prototypeFactions:
                    - TH
                  productionFactions:
                    - TH""";
            
            Files.write(ppcDir.resolve("ISPPC-YAML.yaml"), ppcExample.getBytes());
            
            logger.info("Created example weapon files");
        } catch (Exception e) {
            logger.error("Error creating example weapon files", e);
        }
    }
}
