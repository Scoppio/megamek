# MegaMek Custom Weapons System

This directory contains YAML-based weapon definitions that can be loaded by MegaMek at runtime.

## Adding Custom Weapons

You can create new weapons without having to recompile the game by adding YAML files to this directory. 
The file structure should match the weapon type:

```
data/weapons/
  ├── srms/       # Short-Range Missile weapons
  ├── ppcs/       # PPC weapons
  └── ...         # Other weapon types
```

## YAML Weapon Format

Here's a template for creating your own weapons. The parameters match the Java weapon class properties:

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
maxRange: RANGE_SHORT    # Maximum range (RANGE_SHORT, RANGE_MED, RANGE_LONG, RANGE_EXT)
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

## Supported Weapon Types

Currently, the system supports the following weapon types:
- `srm` - Short-Range Missiles
- `ppc` - Particle Projection Cannons

## Adding More Weapon Types

To add support for more weapon types, you will need to modify the `WEAPON_BASE_CLASSES` map in the `YamlWeaponLoader` class.

## Flag Reference

Here are some common weapon flags you can use:
- `NO_FIRES` - Weapon cannot start fires
- `ARTEMIS_COMPATIBLE` - Compatible with Artemis IV FCS
- `PROTOTYPE_WEAPON` - Prototype technology
- `CLAN_TECH` - Clan technology
- `IS_TECH` - Inner Sphere technology

For a complete list of flags, refer to the `WeaponType` class.