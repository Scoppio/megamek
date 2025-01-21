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

import java.text.DecimalFormat;

/**
 * @author Deric "Netzilla" Page (deric dot page at usa dot net)
 * @since 12/10/13 3:19 PM
 */
public class EntityEvaluationResponse {
    private double estimatedEnemyDamage;
    private double myEstimatedDamage;
    private double myEstimatedPhysicalDamage;
    private int distance;

    public EntityEvaluationResponse() {
        estimatedEnemyDamage = 0;
        myEstimatedDamage = 0;
        myEstimatedPhysicalDamage = 0;
        distance = 0;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public double getEstimatedEnemyDamage() {
        return estimatedEnemyDamage;
    }

    public void setEstimatedEnemyDamage(double estimatedEnemyDamage) {
        this.estimatedEnemyDamage = estimatedEnemyDamage;
    }

    public void addToEstimatedEnemyDamage(double amount) {
        this.estimatedEnemyDamage += amount;
    }

    public double getMyEstimatedDamage() {
        return myEstimatedDamage;
    }

    public void setMyEstimatedDamage(double myEstimatedDamage) {
        this.myEstimatedDamage = myEstimatedDamage;
    }

    public void addToMyEstimatedDamage(double amount) {
        this.myEstimatedDamage += amount;
    }

    public double getMyEstimatedPhysicalDamage() {
        return myEstimatedPhysicalDamage;
    }

    public void setMyEstimatedPhysicalDamage(double myEstimatedPhysicalDamage) {
        this.myEstimatedPhysicalDamage = myEstimatedPhysicalDamage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityEvaluationResponse)) {
            return false;
        }

        EntityEvaluationResponse that = (EntityEvaluationResponse) o;

        if (Double.compare(that.estimatedEnemyDamage, estimatedEnemyDamage) != 0) {
            return false;
        }
        if (Double.compare(that.myEstimatedDamage, myEstimatedDamage) != 0) {
            return false;
        }
        if (Double.compare(that.myEstimatedPhysicalDamage, myEstimatedPhysicalDamage) != 0) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = Double.hashCode(estimatedEnemyDamage);
        result = 31 * result + Double.hashCode(myEstimatedDamage);
        result = 31 * result + Double.hashCode(myEstimatedPhysicalDamage);
        result = 31 * result + Double.hashCode(distance);
        return result;
    }

    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat("0.000");
        return "Enemy: " + format.format(estimatedEnemyDamage)
               + "\tMe: " + format.format(myEstimatedDamage)
               + "\tPhysical: " + format.format(myEstimatedPhysicalDamage)
               + "\tDistance: " + format.format(distance);
    }
}
