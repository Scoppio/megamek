# YAML Weapon System for MegaMek

## Overview

The YAML Weapon System allows modders to create and modify weapons in MegaMek without having to modify the game's Java code. This document explains how to use this system.

## How It Works

1. MegaMek loads weapon definitions from YAML files in the `data/weapons/` directory
2. Each weapon type has its own subdirectory (e.g., `data/weapons/srms/` for SRM weapons)
3. Weapons are loaded during game startup and made available alongside the built-in weapons

## Usage

### Creating Custom Weapons

To create a custom weapon:

1. Choose the appropriate subdirectory for your weapon type (e.g., `ppcs` for PPC weapons)
2. Create a YAML file with a unique name (e.g., `ISPPC-MyCustom.yaml`)
3. Define your weapon using the YAML format (see example below)
4. Start MegaMek, and your custom weapon will be available

### Converting Existing Weapons to YAML

To convert existing hardcoded weapons to YAML:

1. Run the utility: `./gradlew createWeaponYamlFiles`
2. This will create YAML files for all existing weapons in `data/weapons/`
3. You can then modify these files to create custom variants

### Example Weapon Definition

```yaml
type: srm                # Weapon type (srm, ppc, etc.)
name: SRM 2 (Custom)     # Display name
internalName: ISSRM2-Custom  # Unique internal name
lookupNames:             # Alternative names for lookups
  - IS SRM-2 (Custom)
  - ISSRM2-Custom
  - IS SRM 2 (Custom)
heat: 2                  # Heat generated
rackSize: 2              # For missile weapons
shortRange: 3            # Range brackets
mediumRange: 6
longRange: 9
extremeRange: 12
tonnage: 1.0             # Weight
criticals: 1             # Critical slots
bv: 21                   # Battle value
cost: 10000              # C-bill cost
shortAV: 2               # Attack values
maxRange: RANGE_SHORT    # Maximum range
rulesRefs: "229, TM"     # Rules reference
flags:                   # Special flags
  - NO_FIRES
techBase: ALL            # Technology base (ALL, IS, CLAN)
techRating: C            # Tech rating (A-F)
introLevel: true         # Is this an intro-level technology?
unofficial: false        # Is this unofficial?
availability:            # Availability ratings by era
  - C                    # Succession Wars
  - C                    # Clan Invasion
  - C                    # Civil War
  - C                    # Jihad
isAdvancement:           # IS tech advancement years
  introDate: 2365
  productionDate: 2370
  commonDate: 2400
  extinctionDate: -1     # -1 means DATE_NONE
  reintroductionDate: -1
  approximate: false     # Are these dates approximate?
clanAdvancement:         # Clan tech advancement years
  introDate: 2365
  productionDate: 2370
  commonDate: 2400
  extinctionDate: 2836
  reintroductionDate: -1
  approximate: false
prototypeFactions:       # Factions for prototype
  - TH
productionFactions:      # Factions for production
  - TH
```

## Currently Supported Weapon Types

- `srm` - Short-Range Missiles (SRMWeapon)
- `ppc` - Particle Projection Cannons (PPCWeapon)

## Adding Support for More Weapon Types

To add support for additional weapon types:

1. Locate the base class for the weapon type in `megamek/common/weapons/`
2. Modify the `WEAPON_BASE_CLASSES` map in `YamlWeaponLoader.java`
3. Add a new entry mapping the type string to the base class
4. Restart MegaMek for the changes to take effect

## Testing Custom Weapons

1. Place your YAML weapon files in the appropriate subdirectory
2. Start MegaMek
3. Create a new unit and look for your custom weapons in the weapons list

## Troubleshooting

- Check the MegaMek log file for errors
- Ensure your YAML syntax is correct
- Make sure your weapon has a unique `internalName`
- Verify that all required fields are included in your definition

## Developer Information

- Main classes:
  - `YamlWeaponDefinition.java`: Data model for weapon definitions
  - `YamlWeaponLoader.java`: Loads weapons from YAML files
  - `WeaponRegistry.java`: Registers custom weapons with the game
  - `CreateWeaponYamlFiles.java`: Utility to export existing weapons to YAML

- Testing:
  - Run `./gradlew test --tests "*YamlWeaponLoaderTest"` to run the tests