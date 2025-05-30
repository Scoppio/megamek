/*
 * MegaMek - Copyright (C) 2004, 2005 Ben Mazur (bmazur@sev.org)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package megamek.common.weapons.lasers;

import megamek.common.*;
import megamek.common.Game;
import megamek.common.SimpleTechLevel;
import megamek.common.ToHitData;
import megamek.common.actions.WeaponAttackAction;
import megamek.common.alphaStrike.AlphaStrikeElement;
import megamek.common.weapons.AttackHandler;
import megamek.common.weapons.BombastLaserWeaponHandler;
import megamek.server.totalwarfare.TWGameManager;

/**
 * @author Jason Tighe
 * @since Sep 12, 2004
 */
public class ISBombastLaser extends LaserWeapon {
    private static final long serialVersionUID = 3379805005243042138L;

    public ISBombastLaser() {
        super();
        name = "Bombast Laser";
        setInternalName(name);
        addLookupName("IS Bombast Laser");
        addLookupName("ISBombastLaser");
        String[] modeStrings = { "Damage 12", "Damage 11", "Damage 10",
                "Damage 9", "Damage 8", "Damage 7" };
        setModes(modeStrings);
        heat = 12;
        damage = 12;
        shortRange = 5;
        mediumRange = 10;
        longRange = 15;
        extremeRange = 20;
        waterShortRange = 3;
        waterMediumRange = 6;
        waterLongRange = 9;
        waterExtremeRange = 12;
        tonnage = 7.0;
        criticals = 3;
        bv = 137;
        cost = 200000;
        shortAV = 12;
        medAV = 12;
        maxRange = RANGE_MED;
        flags = flags.or(F_BOMBAST_LASER).andNot(F_PROTO_WEAPON);
        rulesRefs = "319, TO";
        // Tech Progression tweaked to combine IntOps with TRO Prototypes/3145 NTNU RS
        techAdvancement.setTechBase(TechBase.IS).setTechRating(TechRating.D)
                .setAvailability(AvailabilityValue.X, AvailabilityValue.X, AvailabilityValue.E, AvailabilityValue.E)
                .setISAdvancement(3064, 3085).setPrototypeFactions(Faction.LC)
                .setProductionFactions(Faction.LC)
                .setStaticTechLevel(SimpleTechLevel.ADVANCED);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * megamek.common.weapons.Weapon#getCorrectHandler(megamek.common.ToHitData,
     * megamek.common.actions.WeaponAttackAction, megamek.common.Game,
     * megamek.server.Server)
     */
    @Override
    protected AttackHandler getCorrectHandler(ToHitData toHit, WeaponAttackAction waa, Game game,
            TWGameManager manager) {
        return new BombastLaserWeaponHandler(toHit, waa, game, manager);
    }

    @Override
    public double getBattleForceDamage(int range, Mounted<?> fcs) {
        return (range <= AlphaStrikeElement.MEDIUM_RANGE) ? 1.02 : 0;
    }
}
