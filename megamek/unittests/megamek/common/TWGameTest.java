/*
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
package megamek.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import megamek.server.victory.VictoryResult;

class TWGameTest {

    @Test
    void testCancelVictory() {
        // Default test
        TWGame twGame = new TWGame();
        twGame.cancelVictory();
        assertFalse(twGame.isForceVictory());
        assertSame(Player.PLAYER_NONE, twGame.getVictoryPlayerId());
        assertSame(Player.TEAM_NONE, twGame.getVictoryTeam());

        // Test with members set to specific values
        TWGame twGame2 = new TWGame();
        TWGame2.setVictoryPlayerId(10);
        TWGame2.setVictoryTeam(10);
        TWGame2.setForceVictory(true);

        TWGame2.cancelVictory();
        assertFalse(twGame.isForceVictory());
        assertSame(Player.PLAYER_NONE, twGame.getVictoryPlayerId());
        assertSame(Player.TEAM_NONE, twGame.getVictoryTeam());
    }

    @Test
    void testGetVictoryReport() {
        TWGame twGame = new TWGame();
        twGame.createVictoryConditions();
        VictoryResult victoryResult = twGame.getVictoryResult();
        assertNotNull(victoryResult);

        // Note: this accessors are tested in VictoryResultTest
        assertSame(Player.PLAYER_NONE, victoryResult.getWinningPlayer());
        assertSame(Player.TEAM_NONE, victoryResult.getWinningTeam());

        int winningPlayer = 2;
        int winningTeam = 5;

        // Test an actual scenario
        TWGame twGame2 = new TWGame();
        TWGame2.setVictoryTeam(winningTeam);
        TWGame2.setVictoryPlayerId(winningPlayer);
        TWGame2.setForceVictory(true);
        TWGame2.createVictoryConditions();
        VictoryResult victoryResult2 = TWGame2.getVictoryResult();

        assertSame(winningPlayer, victoryResult2.getWinningPlayer());
        assertSame(winningTeam, victoryResult2.getWinningTeam());
    }
}
