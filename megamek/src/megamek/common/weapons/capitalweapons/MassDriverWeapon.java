/**
 * MegaMek - Copyright (C) 2004,2005 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */
/*
 * Created on Jan 25, 2014
 *
 */
package megamek.common.weapons.capitalweapons;

import megamek.common.weapons.gaussrifles.GaussWeapon;

/**
 * @author Dave Nawton
 */
public abstract class MassDriverWeapon extends GaussWeapon {

    /**
     *
     */
    private static final long serialVersionUID = -2800123131421584210L;

    public MassDriverWeapon() {
        super();
        this.atClass = CLASS_CAPITAL_MD;
        this.capital = true;
        flags = flags.or(F_MASS_DRIVER).andNot(F_PROTO_WEAPON).andNot(F_MEK_WEAPON).andNot(F_TANK_WEAPON);;
        this.maxRange = RANGE_LONG;
    }

    @Override
    public int getBattleForceClass() {
        return BFCLASS_CAPITAL;
    }
}
