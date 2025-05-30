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
import megamek.client.ui.clientGUI.boardview.sprite.MovementModifierEnvelopeSprite;
import megamek.common.Game;
import megamek.common.moves.MovePath;
import megamek.common.event.GamePhaseChangeEvent;

import java.util.Collection;

public class MovementModifierSpriteHandler extends BoardViewSpriteHandler {

    private final Game game;

    public MovementModifierSpriteHandler(AbstractClientGUI clientGUI, Game game) {
        super(clientGUI);
        this.game = game;
    }

    @Override
    public void initialize() {
        game.addGameListener(this);
    }

    @Override
    public void dispose() {
        clear();
        game.removeGameListener(this);
    }

    public void renewSprites(Collection<MovePath> movePaths) {
        clear();
        if (clientGUI.boardViews().isEmpty()) {
            return;
        }
        BoardView boardView = (BoardView) clientGUI.boardViews().get(0);
        movePaths.stream().map(path -> new MovementModifierEnvelopeSprite(boardView, path)).forEach(currentSprites::add);
        movePaths.stream()
              .filter(MovePath::isMoveLegal)
              .map(path -> new MovementModifierEnvelopeSprite(boardView, path))
              .forEach(currentSprites::add);
        boardView.addSprites(currentSprites);
    }

    @Override
    public void gamePhaseChange(GamePhaseChangeEvent e) {
        clear();
    }
}
