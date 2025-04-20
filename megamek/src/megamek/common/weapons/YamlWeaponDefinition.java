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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data class to hold weapon definitions loaded from YAML files.
 * This is used by the YamlWeaponLoader to create Weapon instances.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class YamlWeaponDefinition {
    
    // Weapon type identifiers
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("subtype")
    private String subtype;
    
    // Basic identification
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("internalName")
    private String internalName;
    
    @JsonProperty("lookupNames")
    private List<String> lookupNames = new ArrayList<>();
    
    // Basic attributes
    @JsonProperty("heat")
    private int heat;
    
    @JsonProperty("damage")
    private int damage = -1; // DAMAGE_VARIABLE by default
    
    @JsonProperty("rackSize")
    private int rackSize;
    
    // Range values
    @JsonProperty("minimumRange")
    private int minimumRange = 0;
    
    @JsonProperty("shortRange")
    private int shortRange;
    
    @JsonProperty("mediumRange")
    private int mediumRange;
    
    @JsonProperty("longRange")
    private int longRange;
    
    @JsonProperty("extremeRange")
    private int extremeRange;
    
    @JsonProperty("waterShortRange")
    private int waterShortRange = -1; // Not specified by default
    
    @JsonProperty("waterMediumRange")
    private int waterMediumRange = -1; // Not specified by default
    
    @JsonProperty("waterLongRange")
    private int waterLongRange = -1; // Not specified by default
    
    @JsonProperty("waterExtremeRange")
    private int waterExtremeRange = -1; // Not specified by default
    
    // Physical characteristics
    @JsonProperty("tonnage")
    private double tonnage;
    
    @JsonProperty("criticals")
    private int criticals;
    
    // Game values
    @JsonProperty("bv")
    private int bv;
    
    @JsonProperty("cost")
    private int cost;
    
    // Weapon flags (processed separately due to complexity)
    @JsonProperty("flags")
    private List<String> flags = new ArrayList<>();
    
    // Attack values
    @JsonProperty("shortAV")
    private int shortAV = -1; // Not specified by default
    
    @JsonProperty("medAV")
    private int medAV = -1; // Not specified by default
    
    @JsonProperty("longAV")
    private int longAV = -1; // Not specified by default
    
    @JsonProperty("extAV")
    private int extAV = -1; // Not specified by default
    
    @JsonProperty("maxRange")
    private String maxRange;
    
    // Rules references
    @JsonProperty("rulesRefs")
    private String rulesRefs;
    
    // Tech Advancement details
    @JsonProperty("techBase")
    private String techBase;
    
    @JsonProperty("techRating")
    private String techRating;
    
    @JsonProperty("introLevel")
    private boolean introLevel;
    
    @JsonProperty("unofficial")
    private boolean unofficial;
    
    @JsonProperty("availability")
    private List<String> availability = new ArrayList<>();
    
    @JsonProperty("isAdvancement")
    private TechAdvancementYear isAdvancement = new TechAdvancementYear();
    
    @JsonProperty("clanAdvancement")
    private TechAdvancementYear clanAdvancement = new TechAdvancementYear();
    
    @JsonProperty("prototypeFactions")
    private List<String> prototypeFactions = new ArrayList<>();
    
    @JsonProperty("productionFactions")
    private List<String> productionFactions = new ArrayList<>();
    
    // Specialized fields for various weapon types
    @JsonProperty("ammoType")
    private int ammoType = -1; // Not specified by default
    
    @JsonProperty("atClass")
    private int atClass = -1; // Not specified by default
    
    @JsonProperty("explosive")
    private boolean explosive;
    
    @JsonProperty("sortingName")
    private String sortingName;
    
    // Getters and setters

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    public List<String> getLookupNames() {
        return lookupNames;
    }

    public int getHeat() {
        return heat;
    }

    public int getDamage() {
        return damage;
    }

    public int getRackSize() {
        return rackSize;
    }

    public int getMinimumRange() {
        return minimumRange;
    }

    public int getShortRange() {
        return shortRange;
    }

    public int getMediumRange() {
        return mediumRange;
    }

    public int getLongRange() {
        return longRange;
    }

    public int getExtremeRange() {
        return extremeRange;
    }

    public int getWaterShortRange() {
        return waterShortRange;
    }

    public int getWaterMediumRange() {
        return waterMediumRange;
    }

    public int getWaterLongRange() {
        return waterLongRange;
    }

    public int getWaterExtremeRange() {
        return waterExtremeRange;
    }

    public double getTonnage() {
        return tonnage;
    }

    public int getCriticals() {
        return criticals;
    }

    public int getBv() {
        return bv;
    }

    public int getCost() {
        return cost;
    }

    public List<String> getFlags() {
        return flags;
    }

    public int getShortAV() {
        return shortAV;
    }

    public int getMedAV() {
        return medAV;
    }

    public int getLongAV() {
        return longAV;
    }

    public int getExtAV() {
        return extAV;
    }

    public String getMaxRange() {
        return maxRange;
    }

    public String getRulesRefs() {
        return rulesRefs;
    }

    public String getTechBase() {
        return techBase;
    }

    public String getTechRating() {
        return techRating;
    }

    public boolean isIntroLevel() {
        return introLevel;
    }

    public boolean isUnofficial() {
        return unofficial;
    }

    public List<String> getAvailability() {
        return availability;
    }

    public TechAdvancementYear getIsAdvancement() {
        return isAdvancement;
    }

    public TechAdvancementYear getClanAdvancement() {
        return clanAdvancement;
    }

    public List<String> getPrototypeFactions() {
        return prototypeFactions;
    }

    public List<String> getProductionFactions() {
        return productionFactions;
    }

    public int getAmmoType() {
        return ammoType;
    }

    public int getAtClass() {
        return atClass;
    }

    public boolean isExplosive() {
        return explosive;
    }

    public String getSortingName() {
        return sortingName;
    }

    /**
     * Inner class to hold tech advancement years
     */
    public static class TechAdvancementYear {
        @JsonProperty("introDate")
        private int introDate;
        
        @JsonProperty("productionDate")
        private int productionDate;
        
        @JsonProperty("commonDate")
        private int commonDate;
        
        @JsonProperty("extinctionDate")
        private int extinctionDate = -1; // DATE_NONE by default
        
        @JsonProperty("reintroductionDate")
        private int reintroductionDate = -1; // DATE_NONE by default
        
        @JsonProperty("approximate")
        private boolean approximate;

        public int getIntroDate() {
            return introDate;
        }

        public int getProductionDate() {
            return productionDate;
        }

        public int getCommonDate() {
            return commonDate;
        }

        public int getExtinctionDate() {
            return extinctionDate;
        }

        public int getReintroductionDate() {
            return reintroductionDate;
        }

        public boolean isApproximate() {
            return approximate;
        }
    }
}
