/*
 * MegaMek - Copyright (C) 2007 Ben Mazur (bmazur@sev.org)
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
package megamek.common.weapons;

import java.util.Vector;

import megamek.common.AmmoType;
import megamek.common.Compute;
import megamek.common.Coords;
import megamek.common.Game;
import megamek.common.Mounted;
import megamek.common.Report;
import megamek.common.TargetRoll;
import megamek.common.ToHitData;
import megamek.common.actions.WeaponAttackAction;
import megamek.common.enums.GamePhase;
import megamek.logging.MMLogger;
import megamek.server.totalwarfare.TWGameManager;

/**
 * @author arlith
 */
public class MekMortarFlareHandler extends AmmoWeaponHandler {
    private static final MMLogger logger = MMLogger.create(MekMortarFlareHandler.class);

    private static final long serialVersionUID = -2073773899108954657L;

    public MekMortarFlareHandler(ToHitData t, WeaponAttackAction w, Game g, TWGameManager m) {
        super(t, w, g, m);
    }

    @Override
    protected int calcDamagePerHit() {
        return 0;
    }

    @Override
    public boolean handle(GamePhase phase, Vector<Report> vPhaseReport) {
        if (!cares(phase)) {
            return true;
        }

        Coords targetPos = target.getPosition();

        Mounted<?> ammoUsed = ae.getEquipment(waa.getAmmoId());
        final AmmoType ammoType = (ammoUsed == null) ? null : (AmmoType) ammoUsed.getType();
        if ((ammoType == null) || !ammoType.getMunitionType().contains(AmmoType.Munitions.M_FLARE)) {
            logger.error("Trying to use a Mek Mortar Flare with non-flare ammo");
            return true;
        }

        // Report weapon attack and its to-hit value.
        Report r = new Report(3120);
        r.indent();
        r.newlines = 0;
        r.subject = subjectId;
        if (wtype != null) {
            r.add(wtype.getName() + ' ' + ammoType.getSubMunitionName());
        } else {
            r.add("Error: From Nowhere");
        }

        r.add(target.getDisplayName(), true);
        vPhaseReport.addElement(r);
        if (toHit.getValue() == TargetRoll.IMPOSSIBLE) {
            r = new Report(3135);
            r.subject = subjectId;
            r.add(toHit.getDesc());
            vPhaseReport.addElement(r);
            return false;
        } else if (toHit.getValue() == TargetRoll.AUTOMATIC_FAIL) {
            r = new Report(3140);
            r.newlines = 0;
            r.subject = subjectId;
            r.add(toHit.getDesc());
            vPhaseReport.addElement(r);
        } else if (toHit.getValue() == TargetRoll.AUTOMATIC_SUCCESS) {
            r = new Report(3145);
            r.newlines = 0;
            r.subject = subjectId;
            r.add(toHit.getDesc());
            vPhaseReport.addElement(r);
        } else {
            // roll to hit
            r = new Report(3150);
            r.newlines = 0;
            r.subject = subjectId;
            r.add(toHit);
            vPhaseReport.addElement(r);
        }

        // dice have been rolled, thanks
        r = new Report(3155);
        r.newlines = 0;
        r.subject = subjectId;
        r.add(roll);
        vPhaseReport.addElement(r);

        // do we hit?
        bMissed = roll.getIntValue() < toHit.getValue();
        // Set Margin of Success/Failure.
        toHit.setMoS(roll.getIntValue() - Math.max(2, toHit.getValue()));
        int duration = wtype.getRackSize() * 2;

        if (!bMissed) {
            r = new Report(3190);
            r.subject = subjectId;
            r.add(targetPos.getBoardNum());
            vPhaseReport.addElement(r);
        } else {
            // only scatters by one d6
            targetPos = Compute.scatter(targetPos, 1);
            if (game.hasBoardLocation(targetPos, target.getBoardId())) {
                // misses and scatters to another hex
                r = new Report(3195);
                r.subject = subjectId;
                r.add(targetPos.getBoardNum());
                vPhaseReport.addElement(r);
            } else {
                // misses and scatters off-board
                r = new Report(3200);
                r.subject = subjectId;
                vPhaseReport.addElement(r);
                return !bMissed;
            }
        }

        gameManager.deliverMortarFlare(targetPos, target.getBoardId(), duration);
        return false;
    }
}
