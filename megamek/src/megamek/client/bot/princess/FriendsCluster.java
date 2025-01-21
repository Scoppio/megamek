/*
 * Copyright (c) 2025 - The MegaMek Team. All Rights Reserved.
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
 *
 */

package megamek.client.bot.princess;

import megamek.codeUtilities.MathUtility;
import megamek.common.Coords;

public record FriendsCluster(Coords midpoint, int units, int formationRunMP, int unitSeparation) {
    private final static FriendsCluster EMPTY = new FriendsCluster(null, -1, 0, 0);
    public static FriendsCluster empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    public double distance(Coords coords) {
        if (midpoint != null) {
            return midpoint.distance(coords);
        }
        return 0d;
    }

    public double finalUnitSeparation(Coords coords) {
        var dist = distance(coords);
        return MathUtility.clamp(dist - (unitSeparation + formationRunMP), 0, dist);
    }
}

