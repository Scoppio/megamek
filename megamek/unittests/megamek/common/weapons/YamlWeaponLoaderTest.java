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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import megamek.common.EquipmentType;
import megamek.common.RangeType;
import megamek.common.TechAdvancement;
import megamek.common.weapons.ppc.ISPPC;
import megamek.common.weapons.ppc.PPCWeapon;
import megamek.common.weapons.srms.ISSRM2;
import megamek.common.weapons.srms.SRMWeapon;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for the YAML weapon loading system
 */
public class YamlWeaponLoaderTest {
    
    @TempDir
    Path tempDir;
    
    private Map<String, Class<? extends Weapon>> originalWeaponBaseClasses;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Reset the EquipmentType state to ensure tests are isolated
        EquipmentType.setAllTypes(null);
        EquipmentType.setLookupHash(null);
        
        // Save original and override WEAPON_BASE_CLASSES for testing
        Field weaponBaseClassesField = YamlWeaponLoader.class.getDeclaredField("WEAPON_BASE_CLASSES");
        weaponBaseClassesField.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        Map<String, Class<? extends Weapon>> baseClasses = 
            (Map<String, Class<? extends Weapon>>) weaponBaseClassesField.get(null);
        
        // Save original map
        originalWeaponBaseClasses = new HashMap<>(baseClasses);
        
        // Clear and set up for testing
        baseClasses.clear();
        baseClasses.put("srm", ISSRM2.class);
        baseClasses.put("ppc", ISPPC.class);
    }
    
    @Test
    public void testLoadSRMWeapon() throws IOException {
        // Create test weapon directory
        Path weaponsDir = tempDir.resolve("weapons");
        Path srmsDir = weaponsDir.resolve("srms");
        Files.createDirectories(srmsDir);
        
        // Create YAML weapon definition
        String srmYaml = 
            "type: srm\n" +
            "name: SRM 2 (Test)\n" +
            "internalName: ISSRM2-Test\n" +
            "lookupNames:\n" +
            "  - IS SRM-2 (Test)\n" +
            "  - ISSRM2-Test\n" +
            "heat: 2\n" +
            "rackSize: 2\n" +
            "shortRange: 3\n" +
            "mediumRange: 6\n" +
            "longRange: 9\n" +
            "extremeRange: 12\n" +
            "tonnage: 1.0\n" +
            "criticals: 1\n" +
            "bv: 21\n" +
            "cost: 10000\n" +
            "shortAV: 2\n" +
            "maxRange: RANGE_SHORT\n" +
            "flags:\n" +
            "  - NO_FIRES\n" +
            "techBase: ALL\n" +
            "techRating: C";
        
        Files.write(srmsDir.resolve("ISSRM2-Test.yaml"), srmYaml.getBytes());
        
        // Set the system property to use our test directory
        String originalDir = System.getProperty("megamek.weapons.dir");
        System.setProperty("megamek.weapons.dir", weaponsDir.toString());
        
        try {
            // Load the weapons
            Map<String, Weapon> weapons = YamlWeaponLoader.loadAllWeapons();
            
            // Verify the SRM weapon was loaded
            assertNotNull(weapons);
            assertEquals(1, weapons.size());
            
            Weapon srm = weapons.get("ISSRM2-Test");
            assertNotNull(srm);
            assertTrue(srm instanceof SRMWeapon);
            
            assertEquals("SRM 2 (Test)", srm.getName());
            assertEquals("ISSRM2-Test", srm.getInternalName());
            assertEquals(2, srm.getHeat());
            assertEquals(2, srm.getRackSize());
            assertEquals(3, srm.getShortRange());
            assertEquals(6, srm.getMediumRange());
            assertEquals(9, srm.getLongRange());
            assertEquals(12, srm.getExtremeRange());
            assertEquals(1.0, srm.getTonnage(null), 0.01);
            assertEquals(1, srm.getCriticals(null));
            assertEquals(21, srm.getBV(null));
            assertEquals(10000, srm.getCost(null, false, 0));
            assertEquals(2, srm.getShortAV());
            assertEquals(RangeType.RANGE_SHORT, srm.getMaxRange());
            
            // Verify tech advancement
            TechAdvancement techAdvancement = srm.getTechAdvancement();
            assertEquals(TechAdvancement.TECH_BASE_ALL, techAdvancement.getTechBase());
            assertEquals(TechAdvancement.RATING_C, techAdvancement.getTechRating());
        } finally {
            // Restore the original directory
            if (originalDir != null) {
                System.setProperty("megamek.weapons.dir", originalDir);
            } else {
                System.clearProperty("megamek.weapons.dir");
            }
        }
    }
    
    @Test
    public void testLoadPPCWeapon() throws IOException {
        // Create test weapon directory
        Path weaponsDir = tempDir.resolve("weapons");
        Path ppcsDir = weaponsDir.resolve("ppcs");
        Files.createDirectories(ppcsDir);
        
        // Create YAML weapon definition
        String ppcYaml = 
            "type: ppc\n" +
            "name: PPC (Test)\n" +
            "internalName: ISPPC-Test\n" +
            "lookupNames:\n" +
            "  - Particle Cannon (Test)\n" +
            "  - IS PPC (Test)\n" +
            "heat: 10\n" +
            "damage: 10\n" +
            "minimumRange: 3\n" +
            "shortRange: 6\n" +
            "mediumRange: 12\n" +
            "longRange: 18\n" +
            "extremeRange: 24\n" +
            "tonnage: 7.0\n" +
            "criticals: 3\n" +
            "bv: 176\n" +
            "cost: 200000\n" +
            "shortAV: 10\n" +
            "medAV: 10\n" +
            "maxRange: RANGE_MED\n" +
            "explosive: true\n" +
            "techBase: ALL\n" +
            "techRating: D";
        
        Files.write(ppcsDir.resolve("ISPPC-Test.yaml"), ppcYaml.getBytes());
        
        // Set the system property to use our test directory
        String originalDir = System.getProperty("megamek.weapons.dir");
        System.setProperty("megamek.weapons.dir", weaponsDir.toString());
        
        try {
            // Load the weapons
            Map<String, Weapon> weapons = YamlWeaponLoader.loadAllWeapons();
            
            // Verify the PPC weapon was loaded
            assertNotNull(weapons);
            assertEquals(1, weapons.size());
            
            Weapon ppc = weapons.get("ISPPC-Test");
            assertNotNull(ppc);
            assertTrue(ppc instanceof PPCWeapon);
            
            assertEquals("PPC (Test)", ppc.getName());
            assertEquals("ISPPC-Test", ppc.getInternalName());
            assertEquals(10, ppc.getHeat());
            assertEquals(10, ppc.getDamage());
            assertEquals(3, ppc.getMinimumRange());
            assertEquals(6, ppc.getShortRange());
            assertEquals(12, ppc.getMediumRange());
            assertEquals(18, ppc.getLongRange());
            assertEquals(24, ppc.getExtremeRange());
            assertEquals(7.0, ppc.getTonnage(null), 0.01);
            assertEquals(3, ppc.getCriticals(null));
            assertEquals(176, ppc.getBV(null));
            assertEquals(200000, ppc.getCost(null, false, 0));
            assertEquals(10, ppc.getShortAV());
            assertEquals(10, ppc.getMedAV());
            assertEquals(RangeType.RANGE_MEDIUM, ppc.getMaxRange());
            assertTrue(ppc.isExplosive(null));
            
            // Verify tech advancement
            TechAdvancement techAdvancement = ppc.getTechAdvancement();
            assertEquals(TechAdvancement.TECH_BASE_ALL, techAdvancement.getTechBase());
            assertEquals(TechAdvancement.RATING_D, techAdvancement.getTechRating());
        } finally {
            // Restore the original directory
            if (originalDir != null) {
                System.setProperty("megamek.weapons.dir", originalDir);
            } else {
                System.clearProperty("megamek.weapons.dir");
            }
        }
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        // Restore original WEAPON_BASE_CLASSES
        if (originalWeaponBaseClasses != null) {
            Field weaponBaseClassesField = YamlWeaponLoader.class.getDeclaredField("WEAPON_BASE_CLASSES");
            weaponBaseClassesField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Map<String, Class<? extends Weapon>> baseClasses = 
                (Map<String, Class<? extends Weapon>>) weaponBaseClassesField.get(null);
            
            baseClasses.clear();
            baseClasses.putAll(originalWeaponBaseClasses);
        }
    }
}
