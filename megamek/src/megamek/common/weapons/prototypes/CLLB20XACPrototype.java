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
package megamek.common.weapons.prototypes;

import megamek.common.Mounted;
import megamek.common.SimpleTechLevel;
import megamek.common.alphaStrike.AlphaStrikeElement;

/**
 * @author Andrew Hunter
 * @since Oct 15, 2004
 */
public class CLLB20XACPrototype extends CLLBXACPrototypeWeapon {
    private static final long serialVersionUID = -4257248228202258750L;

    public CLLB20XACPrototype() {
        super();
        name = "Prototype LB 20-X Autocannon";
        setInternalName("CLLBXAC20Prototype");
        shortName = "LB 20-X (P)";
        heat = 6;
        damage = 20;
        rackSize = 20;
        shortRange = 4;
        mediumRange = 8;
        longRange = 12;
        extremeRange = 16;
        tonnage = 14.0;
        criticals = 12;
        bv = 237;
        cost = 600000;
        shortAV = 20;
        medAV = 20;
        maxRange = RANGE_MED;
        rulesRefs = "97, IO";
        flags = flags.or(F_PROTOTYPE).andNot(F_PROTO_WEAPON);
        techAdvancement.setTechBase(TechBase.CLAN)
                .setIntroLevel(false)
                .setTechRating(TechRating.F)
                .setAvailability(AvailabilityValue.X, AvailabilityValue.D, AvailabilityValue.X, AvailabilityValue.X)
                .setClanAdvancement(2820, DATE_NONE, DATE_NONE, 2826, DATE_NONE)
                .setClanApproximate(true, false, false, true, false)
                .setPrototypeFactions(Faction.CHH)
                .setStaticTechLevel(SimpleTechLevel.EXPERIMENTAL);
    }

    @Override
    public double getBattleForceDamage(int range, Mounted<?> fcs) {
        return (range <= AlphaStrikeElement.MEDIUM_RANGE) ? 1.26 : 0;
    }
}
