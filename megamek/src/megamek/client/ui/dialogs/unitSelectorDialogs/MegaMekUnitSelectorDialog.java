/*
 * Copyright (c) 2020 - The MegaMek Team. All rights reserved.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MegaMek.  If not, see <http://www.gnu.org/licenses/>.
 */
package megamek.client.ui.dialogs.unitSelectorDialogs;

import megamek.client.AbstractClient;
import megamek.client.Client;
import megamek.client.generator.RandomGenderGenerator;
import megamek.client.generator.RandomNameGenerator;
import megamek.client.ui.Messages;
import megamek.client.ui.clientGUI.ClientGUI;
import megamek.client.ui.dialogs.UnitFailureDialog;
import megamek.client.ui.dialogs.UnitLoadingDialog;
import megamek.common.Entity;
import megamek.common.MekSummaryCache;
import megamek.common.Player;
import megamek.common.TechConstants;
import megamek.common.enums.Gender;
import megamek.common.options.OptionsConstants;
import megamek.common.preference.ClientPreferences;
import megamek.common.preference.PreferenceManager;
import megamek.logging.MMLogger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class MegaMekUnitSelectorDialog extends AbstractUnitSelectorDialog {

    private static final long serialVersionUID = -5717009055093904636L;
    MMLogger LOGGER = MMLogger.create(MegaMekUnitSelectorDialog.class);
    //region Variable Declarations
    private ClientGUI clientGUI;
    private JComboBox<String> comboPlayer = new JComboBox<>();
    //endregion Variable Declarations

    public MegaMekUnitSelectorDialog(ClientGUI clientGUI, UnitLoadingDialog unitLoadingDialog) {
        super(clientGUI.getFrame(), unitLoadingDialog, true);
        this.clientGUI = clientGUI;

        updateOptionValues();

        initialize();
    }

    @Override
    public void updateOptionValues() {
        gameOptions = clientGUI.getClient().getGame().getOptions();
        enableYearLimits = true;
        allowedYear = gameOptions.intOption(OptionsConstants.ALLOWED_YEAR);
        canonOnly = gameOptions.booleanOption(OptionsConstants.ALLOWED_CANON_ONLY);
        allowInvalid = gameOptions.booleanOption(OptionsConstants.ALLOWED_ALLOW_ILLEGAL_UNITS);
        gameTechLevel = TechConstants.getSimpleLevel(gameOptions.stringOption("techlevel"));
        eraBasedTechLevel = gameOptions.booleanOption(OptionsConstants.ALLOWED_ERA_BASED);
    }

    //region Button Methods
    @Override
    protected JPanel createButtonsPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panelButtons = new JPanel(new GridBagLayout());

        buttonSelect = new JButton(Messages.getString("MekSelectorDialog.m_bPick"));
        buttonSelect.addActionListener(this);
        panelButtons.add(buttonSelect, gbc);

        buttonSelectClose = new JButton(Messages.getString("MekSelectorDialog.m_bPickClose"));
        buttonSelectClose.addActionListener(this);
        panelButtons.add(buttonSelectClose, gbc);

        buttonClose = new JButton(Messages.getString("Close"));
        buttonClose.addActionListener(this);
        panelButtons.add(buttonClose, gbc);

        JLabel labelPlayer = new JLabel(Messages.getString("MekSelectorDialog.m_labelPlayer"),
                SwingConstants.RIGHT);
        panelButtons.add(labelPlayer, gbc);

        panelButtons.add(comboPlayer, gbc);

        buttonShowBV = new JButton(Messages.getString("MekSelectorDialog.BV"));
        buttonShowBV.addActionListener(this);
        panelButtons.add(buttonShowBV, gbc);

        return panelButtons;
    }

    @Override
    protected void select(boolean close) {
        ArrayList<Entity> entities = getSelectedEntities();
        if (!entities.isEmpty()) {
            Client client = null;
            String name = (String) comboPlayer.getSelectedItem();

            if (comboPlayer.getSelectedIndex() > 0) {
                client = (Client) clientGUI.getLocalBots().get(name);
            }

            if (client == null) {
                client = clientGUI.getClient();
            }


            for (var e : entities) {
                autoSetSkillsAndName(e, client.getLocalPlayer());
                e.setOwner(client.getLocalPlayer());
            }
            client.sendAddEntity(entities);

            String msg = clientGUI.getClient().getLocalPlayer() + " selected " + (entities.size() == 1 ? "a unit" : entities.size() + " units") + " for player: " + name;
            clientGUI.getClient().sendServerChat(Player.PLAYER_NONE, msg);
        }

        if (close) {
            setVisible(false);
        }
    }

    private void autoSetSkillsAndName(Entity e, Player player) {
        ClientPreferences cs = PreferenceManager.getClientPreferences();

        Arrays.fill(e.getCrew().getClanPilots(), e.isClan());
        if (cs.useAverageSkills()) {
            clientGUI.getClient().getSkillGenerator().setRandomSkills(e);
        }

        for (int i = 0; i < e.getCrew().getSlotCount(); i++) {
            if (cs.generateNames()) {
                Gender gender = RandomGenderGenerator.generate();
                e.getCrew().setGender(gender, i);
                e.getCrew().setName((player != null)
                        ? RandomNameGenerator.getInstance().generate(gender, e.getCrew().isClanPilot(i), player.getName())
                        : RandomNameGenerator.getInstance().generate(gender, e.getCrew().isClanPilot(i)), i);
            }
        }
    }

    private void updatePlayerChoice(String selectionName) {
        String clientName = clientGUI.getClient().getName();
        comboPlayer.setEnabled(false);
        comboPlayer.removeAllItems();
        comboPlayer.addItem(clientName);

        for (AbstractClient client : clientGUI.getLocalBots().values()) {
            comboPlayer.addItem(client.getName());
        }
        comboPlayer.setSelectedItem(selectionName);
        if (comboPlayer.getSelectedIndex() < 0) {
            comboPlayer.setSelectedIndex(0);
        }
        if (comboPlayer.getItemCount() > 1) {
            comboPlayer.setEnabled(true);
        }
    }

    private void updatePlayerChoice() {
        String lastChoice = (String) comboPlayer.getSelectedItem();
        updatePlayerChoice(lastChoice);
    }

    public void setPlayerFromClient(Client c) {
        if (c != null) {
            updatePlayerChoice(c.getName());
        } else {
            updatePlayerChoice();
        }
    }
    //endregion Button Methods

    @Override
    protected Entity refreshUnitView() {
        Entity selectedEntity = super.refreshUnitView(); //we first want it to run through the same code as its parent
        if (selectedEntity != null) {
            clientGUI.loadPreviewImage(labelImage, selectedEntity);
        }
        return selectedEntity;
    }

    @Override
    public void run() {
        super.run();
        // In some cases, it's possible to get here without an initialized
        // instance (loading a saved game without a cache).  In these cases,
        // we don't care about the failed loads.
        if (mscInstance.isInitialized()) {
            final Map<String, String> hFailedFiles = MekSummaryCache.getInstance().getFailedFiles();
            if ((hFailedFiles != null) && !hFailedFiles.isEmpty()) {
                LOGGER.warn("Unit loading errors: " + hFailedFiles);
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        updatePlayerChoice();
        comboPlayer.grabFocus();
        super.setVisible(visible);
    }
}
