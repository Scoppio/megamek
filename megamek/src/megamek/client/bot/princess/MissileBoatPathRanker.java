/*
 * MegaMek - Copyright (C) 2000-2011 Ben Mazur (bmazur@sev.org)
 * Copyright (c) 2024 - The MegaMek Team. All Rights Reserved.
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
package megamek.client.bot.princess;

import megamek.common.*;
import megamek.common.options.OptionsConstants;
import megamek.common.planetaryconditions.PlanetaryConditions;
import megamek.logging.MMLogger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A very "basic" path ranker
 */
public class MissileBoatPathRanker extends BasicPathRanker {
    private final static MMLogger logger = MMLogger.create(MissileBoatPathRanker.class);

    public MissileBoatPathRanker(Princess owningPrincess, PathEnumerator pathEnumerator) {
        super(owningPrincess, pathEnumerator);
    }

     @Override
    public RankedPath getBestPath(List<RankedPath> ps) {
        if (ps.isEmpty()) {
            return null;
        }
        double maxDifference = 10;
        List<RankedPath> topPaths = new ArrayList<>(5);
        double topRank = ps.get(0).getRank();
        for (var rankedPath : ps) {
            if (Math.abs(rankedPath.getRank() - topRank) < maxDifference) {
                topPaths.add(rankedPath);
                if (topPaths.size() == 5) {
                    break;
                }
            } else {
                break;
            }
        }
        return topPaths.stream().max(Comparator.comparingDouble(a -> a.getPath().getHexesMoved())).orElse(null);
     }

    /**
     * A path ranking
     */
    @Override
    protected RankedPath rankPath(
        MovePath path, Game game, int maxRange, double fallTolerance, List<Entity> enemies, FriendsCluster friendsCluster) {
        Entity movingUnit = path.getEntity();
        StringBuilder formula = new StringBuilder("Calculation: {");

        if (blackIce == -1) {
            blackIce = ((game.getOptions().booleanOption(OptionsConstants.ADVANCED_BLACK_ICE)
                    && game.getPlanetaryConditions().getTemperature() <= PlanetaryConditions.BLACK_ICE_TEMP)
                    || game.getPlanetaryConditions().getWeather().isIceStorm()) ? 1 : 0;
        }

        // Copy the path to avoid inadvertent changes.
        MovePath pathCopy = path.clone();

        // Worry about failed piloting rolls (weighted by Fall Shame).
        double successProbability = getMovePathSuccessProbability(pathCopy, formula);
        double utility = -calculateFallMod(successProbability, formula);

        // Worry about how badly we can damage ourselves on this path!
        double expectedDamageTaken = calculateMovePathPSRDamage(movingUnit, pathCopy, formula);
        expectedDamageTaken += checkPathForHazards(pathCopy, movingUnit, game);
        expectedDamageTaken += MinefieldUtil.checkPathForMinefieldHazards(pathCopy);

        // look at all of my enemies
        FiringPhysicalDamage damageEstimate = new FiringPhysicalDamage();

        boolean extremeRange = game.getOptions().booleanOption(OptionsConstants.ADVCOMBAT_TACOPS_RANGE);
        boolean losRange = game.getOptions().booleanOption(OptionsConstants.ADVCOMBAT_TACOPS_LOS_RANGE);
        int minDistanceToEnemy = Integer.MAX_VALUE;
        for (Entity enemy : enemies) {
            // Skip ejected pilots.
            if (enemy instanceof MekWarrior) {
                continue;
            }

            // Skip units not actually on the board.
            if (enemy.isOffBoard() || (enemy.getPosition() == null)
                    || !game.getBoard().contains(enemy.getPosition())) {
                continue;
            }

            // Skip broken enemies
            if (getOwner().getHonorUtil().isEnemyBroken(enemy.getId(), enemy.getOwnerId(),
                    getOwner().getForcedWithdrawal())) {
                continue;
            }

            EntityEvaluationResponse eval;

            if (evaluateAsMoved(enemy)) {
                // For units that have already moved
                eval = evaluateMovedEnemy(enemy, pathCopy, game);
            } else {
                // For units that have not moved this round
                eval = evaluateUnmovedEnemy(enemy, path, extremeRange, losRange);
            }

            // if we're not ignoring the enemy, we consider damage that we may do to them;
            // however, just because we're ignoring them doesn't mean they won't shoot at
            // us.
            if (!getOwner().getBehaviorSettings().getIgnoredUnitTargets().contains(enemy.getId())) {
                if (damageEstimate.firingDamage < eval.getMyEstimatedDamage()) {
                    damageEstimate.firingDamage = eval.getMyEstimatedDamage();
                }
                if (damageEstimate.physicalDamage < eval.getMyEstimatedPhysicalDamage()) {
                    damageEstimate.physicalDamage = eval.getMyEstimatedPhysicalDamage();
                }
            }
            minDistanceToEnemy = Math.min(minDistanceToEnemy, eval.getDistance());
            expectedDamageTaken += eval.getEstimatedEnemyDamage();
        }

        // if we're not in the air, we may get hit by friendly artillery
        if (!path.getEntity().isAirborne() && !path.getEntity().isAirborneVTOLorWIGE()) {
            double friendlyArtilleryDamage = 0;
            Map<Coords, Double> artyDamage = getOwner().getPathRankerState().getIncomingFriendlyArtilleryDamage();

            if (!artyDamage.containsKey(path.getFinalCoords())) {
                friendlyArtilleryDamage = ArtilleryTargetingControl
                        .evaluateIncomingArtilleryDamage(path.getFinalCoords(), getOwner());
                artyDamage.put(path.getFinalCoords(), friendlyArtilleryDamage);
            } else {
                friendlyArtilleryDamage = artyDamage.get(path.getFinalCoords());
            }

            expectedDamageTaken += friendlyArtilleryDamage;
        }

        calcDamageToStrategicTargets(pathCopy, game, getOwner().getFireControlState(), damageEstimate);

        // If I cannot kick because I am a clan unit and "No physical attacks for the
        // clans"
        // is enabled, set maximum physical damage for this path to zero.
        if (game.getOptions().booleanOption(OptionsConstants.ALLOWED_NO_CLAN_PHYSICAL)
                && path.getEntity().getCrew().isClanPilot()) {
            damageEstimate.physicalDamage = 0;
        }

        // I can kick a different target than I shoot, so add physical to
        // total damage after I've looked at all enemies
        double maximumDamageDone = damageEstimate.firingDamage + damageEstimate.physicalDamage;

        // My bravery modifier is based on my chance of getting to the
        // firing position (successProbability), how much damage I can do
        // (weighted by bravery), less the damage I might take.
        double braveryValue = getOwner().getBehaviorSettings().getBraveryValue();
        double braveryMod = (successProbability * (maximumDamageDone * braveryValue)) - expectedDamageTaken;
        formula.append(" + braveryMod [")
                .append(LOG_DECIMAL.format(braveryMod)).append(" = ")
                .append(LOG_PERCENT.format(successProbability))
                .append(" * ((")
                .append(LOG_DECIMAL.format(maximumDamageDone)).append(" * ")
                .append(LOG_DECIMAL.format(braveryValue)).append(") - ")
                .append(LOG_DECIMAL.format(expectedDamageTaken)).append("]");
        utility += braveryMod;

        // the only critters not subject to aggression and herding mods are
        // airborne aeros on ground maps, as they move incredibly fast
        if (!path.getEntity().isAirborneAeroOnGroundMap()) {
            // The further I am from a target, the lower this path ranks
            // (weighted by Aggression slider).
            utility -= calculateAggressionMod(movingUnit, pathCopy, game, formula);

            // The further I am from my teammates, the lower this path
            // ranks (weighted by Herd Mentality).
            utility -= calculateHerdingMod(friendsCluster, pathCopy, formula);
        }

        // Try to face the enemy.
        double facingMod = calculateFacingMod(movingUnit, game, pathCopy, formula);
        if (facingMod == -10000) {
            return new RankedPath(facingMod, pathCopy, formula.toString());
        }
        utility -= facingMod;

        // If I need to flee the board, I want to get closer to my home edge.
        utility -= calculateSelfPreservationMod(movingUnit, pathCopy, game, formula);

        // if we're an aircraft, we want to de-value paths that will force us off the
        // board
        // on the subsequent turn.
        utility -= utility * calculateOffBoardMod(pathCopy);
        utility += pathCopy.getHexesMoved();
        formula.append(" + hexes moved[").append(pathCopy.getHexesMoved()).append("]");

        utility += calculateDistanceToTargetMod(minDistanceToEnemy, path.getEntity(), formula);

        RankedPath rankedPath = new RankedPath(utility, pathCopy, formula.toString());
        rankedPath.setExpectedDamage(maximumDamageDone);
        return rankedPath;
    }

    protected double calculateDistanceToTargetMod(int minDistanceToEnemy, Entity self, StringBuilder formula) {
        if (minDistanceToEnemy < 6 || minDistanceToEnemy > self.getMaxWeaponRange()) {
            formula.append(" + DistanceToTargetMod[").append(-10).append("]");
            return -10;
        }
        return 0;
    }

}
