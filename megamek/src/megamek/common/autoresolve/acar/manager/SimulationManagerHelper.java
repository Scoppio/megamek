/*
 * Copyright (c) 2025 - The MegaMek Team. All Rights Reserved.
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
package megamek.common.autoresolve.acar.manager;

import megamek.common.autoresolve.acar.SimulationContext;
import megamek.common.autoresolve.acar.SimulationManager;
import megamek.common.autoresolve.acar.report.PublicReportEntry;

/**
 * Helper interface for classes that need to interact with the {@link SimulationManager}.
 * @author Luana Coppio
 */
public interface SimulationManagerHelper {

    SimulationManager simulationManager();

    default SimulationContext game() {
        return simulationManager().getGame();
    }

}