/*
 * Copyright (c) 2019, St0newall
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.cwa;

import javax.inject.Inject;

import com.google.inject.Provides;
import com.openosrs.client.game.Sound;
import com.openosrs.client.game.SoundManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.kit.KitType;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import java.util.Objects;
import org.pf4j.Extension;



@Extension
@PluginDescriptor(
	name = "Clan Wars Arena",
	enabledByDefault = false,
	description = "plugins for CWA",
	tags = {"Clan Wars", "Wilderness", "location", "CWA", "pvp"}
)
@Slf4j
public class CwaPlugin extends Plugin
{
	private static final String DRAGON_SCIM_MESSAGE = "You've been injured and can't use protection prayers!";
	private static final String PHOENIX_NECKLACE_BREAK = "Your phoenix necklace heals you, but is destroyed in the process.";
	private static final String CWA_CHALLENGE_REQUEST = "wishes to challenge your clan to a Clan War";
	private static final String CWA_CHALLENGER_MESSAGE = "Your clan is being invited to join you...";
	private static final String CWA_CHALLENGE_MESSAGE = "Your clan has initiated a battle! Come to the Clan Wars challenge area to join it.";


	private CwaTimer cwaTimer;
	private DragonScimTimer scimTimer;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Client client;

	@Inject
	private SoundManager soundManager;

	@Inject
	private CwaConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private ItemManager itemManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Provides
	CwaConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CwaConfig.class);
	}


	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if ( chatMessage.getMessage().contains(DRAGON_SCIM_MESSAGE) && config.showScimTimer() )
		{
			createScimTimer();
		}
		if ( chatMessage.getMessage().contains(PHOENIX_NECKLACE_BREAK) && config.pneckBreak() )
		{
			client.playSoundEffect(SoundEffectID.TOWN_CRIER_BELL_DONG, config.neckBreakVolume());
		}
		if ( chatMessage.getMessage().contains(CWA_CHALLENGE_REQUEST) && config.showCwaNotifs() )
		{
			notifier.notify("A player is requesting a challenge against your clan");
		}
		if ( chatMessage.getMessage().contains(CWA_CHALLENGE_MESSAGE) && config.showCwaTimer() && config.showCwaNotifs() )
		{
			notifier.notify("You have two minutes to get into the CWA portal");
			createCWATimer();
		}
		if (chatMessage.getMessage().contains(CWA_CHALLENGER_MESSAGE) && config.showCwaTimer() )
		{
			createCWATimer();
		}
	}


/*
	private void updatePlayers()
	{
		friendlyPlayerCount = 0;
		nRangePlayerCount = 0;
		magePlayerCount = 0;

		for (Player p : client.getPlayers())
		{
			if (Objects.nonNull(p))
			{
				if (p.isFriendsChatMember())
				{
					friendlyPlayerCount++;
					if ( p.getPlayerComposition().getEquipmentId(KitType.CAPE) == ItemID.IMBUED_ZAMORAK_CAPE || p.getPlayerComposition().getEquipmentId(KitType.CAPE) == ItemID.IMBUED_SARADOMIN_CAPE || p.getPlayerComposition().getEquipmentId(KitType.CAPE) == ItemID.IMBUED_GUTHIX_CAPE || p.getPlayerComposition().getEquipmentId(KitType.LEGS) == ItemID.ZAMORAK_MONK_BOTTOM)  {
						magePlayerCount++;
					}
					else if (p.getPlayerComposition().getEquipmentId(KitType.CAPE) == ItemID.FIRE_CAPE || p.getPlayerComposition().getEquipmentId(KitType.CAPE) == ItemID.INFERNAL_CAPE ||  p.getPlayerComposition().getEquipmentId(KitType.LEGS) == ItemID.GREEN_DHIDE_CHAPS ||  p.getPlayerComposition().getEquipmentId(KitType.LEGS) == ItemID.GREEN_DHIDE_CHAPS_G ||  p.getPlayerComposition().getEquipmentId(KitType.LEGS) == ItemID.GREEN_DHIDE_CHAPS_T)
					{
						nRangePlayerCount++;
					}
				}
			}
		}

		//config change ( won't repaint unless config panel is reopened
		configManager.setConfiguration("clanwars", "ccCount", friendlyPlayerCount);
		configManager.setConfiguration("clanwars", "mageCount", magePlayerCount);
		configManager.setConfiguration("clanwars", "nRangeCount", nRangePlayerCount);
	}
	@Subscribe
	private void onPlayerSpawned(PlayerSpawned event)
	{
		updatePlayers();
	}

	@Subscribe
	private void onPlayerDespawned(PlayerDespawned event)
	{
		updatePlayers();
	}*/

	private void createCWATimer()
	{
		cwaTimer = new CwaTimer(this, itemManager);
		infoBoxManager.addInfoBox(cwaTimer);
	}

	private void createScimTimer()
	{
		scimTimer = new DragonScimTimer(this, itemManager);
		infoBoxManager.addInfoBox(scimTimer);
	}

}