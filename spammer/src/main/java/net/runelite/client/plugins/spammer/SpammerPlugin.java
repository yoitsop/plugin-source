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

package net.runelite.client.plugins.spammer;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientStr;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.VarClientStrChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.opponentinfo.OpponentInfoPlugin;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Keypress Spammer",
	enabledByDefault = false,
	description = "Spams a message upon a manually set keypress",
	tags = {"Wildy", "Wilderness Location", "location", "loc", "pvp", "pklite"}
)
@Slf4j
public class SpammerPlugin extends Plugin
{
	@Inject
	private Client client;
	@Getter(AccessLevel.PACKAGE)
	@Inject
	private ClientThread clientThread;
	@Inject
	private SpammerConfig config;
	@Inject
	private KeyManager keyManager;


	private String oldChat = "";
	private String smallerName = "";
	private String opponentName = "";

	@Provides
	SpammerConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SpammerConfig.class);
	}

	private final HotkeyListener messagehotkeyListener = new HotkeyListener(() -> config.msgkeybind())
	{
		@Override
		public void hotkeyPressed()
		{
			sendSpam();
		}
	};

	private final HotkeyListener pilehotkeyListener = new HotkeyListener(() -> config.pilekeybind())
	{
		@Override
		public void hotkeyPressed()
		{
			spamPileName();
		}
	};

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(messagehotkeyListener);
		keyManager.registerKeyListener(pilehotkeyListener);
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(messagehotkeyListener);
		keyManager.unregisterKeyListener(pilehotkeyListener);
	}

	@Subscribe
	private void onInteractingChanged(InteractingChanged event)
	{
		final Actor player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}
		final Actor source = event.getSource();
		final Actor target = event.getTarget();
		if (source == player)
		{
			// You have attacked an enemy
			if (target != null)
			{

				opponentName = target.getName();

				if (target.getName().length() >= 4 && config.smallerPileNames())
				{
					//hopefully will fix combined names being spammed
					if (opponentName.substring(0,4).toLowerCase().equals(target.getName().substring(0,4).toLowerCase()))
					{
						opponentName = opponentName.substring(0,4);
					}
				}
			}
			else
			{
				opponentName = "";
			}
		}
	}

	@Subscribe
	private void onVarClientStrChanged(VarClientStrChanged varClient)
	{
		String newChat = client.getVar(VarClientStr.CHATBOX_TYPED_TEXT);
		if (varClient.getIndex() == VarClientStr.CHATBOX_TYPED_TEXT.getIndex() && !newChat.equals(oldChat))
		{
			oldChat = newChat;
		}
	}

	private void sendMessage(String text)
	{
		Runnable r = () ->
		{
			String cached = oldChat;
			client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, text);
			client.runScript(ScriptID.CHATBOX_INPUT, text);
			oldChat = cached;
			client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, oldChat);
		};
		clientThread.invoke(r);
	}

	private void sendSpam()
	{
		String message = config.message();
		if (message.equals(""))
		{
			return;
		}
		sendMessage(message);
	}

	private void spamPileName()
	{

		if (opponentName.equals(""))
		{
			return;
		}
		sendMessage(config.clanPrefix() + " " + opponentName);
	}
}