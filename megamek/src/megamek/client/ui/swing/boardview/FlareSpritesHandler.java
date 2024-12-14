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
package megamek.client.ui.swing.boardview;

import megamek.common.Flare;
import megamek.common.TWGame;
import megamek.common.event.GameBoardChangeEvent;

import java.util.Collection;

public class FlareSpritesHandler extends BoardViewSpriteHandler {

    private final TWGame twGame;

    public FlareSpritesHandler(BoardView boardView, TWGame twGame) {
        super(boardView);
        this.twGame = twGame;
    }

    public void renewSprites(Collection<Flare> flares) {
        clear();
        flares.stream().map(flare -> new FlareSprite(boardView, flare)).forEach(currentSprites::add);
        boardView.addSprites(currentSprites);
    }

    @Override
    public void initialize() {
        twGame.addGameListener(this);
    }

    @Override
    public void dispose() {
        clear();
        twGame.removeGameListener(this);
    }

    @Override
    public void gameBoardChanged(GameBoardChangeEvent e) {
        renewSprites(twGame.getFlares());
    }
}
