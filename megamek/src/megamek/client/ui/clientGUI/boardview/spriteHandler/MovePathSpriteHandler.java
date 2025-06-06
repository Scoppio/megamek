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

package megamek.client.ui.clientGUI.boardview.spriteHandler;

import megamek.client.ui.clientGUI.AbstractClientGUI;
import megamek.client.ui.clientGUI.boardview.BoardView;
import megamek.client.ui.clientGUI.boardview.sprite.SBFStepSprite;
import megamek.common.annotations.Nullable;
import megamek.common.strategicBattleSystems.SBFMovePath;
import megamek.common.strategicBattleSystems.SBFMoveStep;

public class MovePathSpriteHandler extends BoardViewSpriteHandler {

    public MovePathSpriteHandler(AbstractClientGUI clientGUI) {
        super(clientGUI);
    }

    /**
     * Clears the current sprites and creates new sprites for all formations.
     */
    public void update(@Nullable SBFMovePath movePath) {
        clear();
        if (clientGUI.boardViews().isEmpty()) {
            return;
        }
        if (movePath == null) {
            return;
        }

        for (SBFMoveStep step : movePath.getSteps()) {
            currentSprites.add(new SBFStepSprite((BoardView) clientGUI.boardViews().get(0), step, movePath));
        }

        clientGUI.boardViews().get(0).addSprites(currentSprites);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void dispose() {

    }
}
