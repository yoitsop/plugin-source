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


import net.runelite.client.config.*;

@ConfigGroup("spammer")
public interface SpammerConfig extends Config
{
	@ConfigSection(
			name = "Pile Spammer",
			description = "",
			position = 0,
			keyName = "pileSpammerSection"
	)
	String pileSpammerSection = "pileSpammerSection";

	@ConfigSection(
			name = "Keybind Spammer",
			description = "",
			position = 1,
			keyName = "keybindSpammer"
	)
	String keybindSpammer = "keybindSpammer";

	@ConfigItem(
			keyName = "messageConfig1",
			position = 0,
			name = "Message One",
			description = "A message you would like to spam upon keypress",
			section = keybindSpammer
	)
	default String message()
	{
		return "";
	}

	@ConfigItem(
			keyName = "keybind1",
			name = "Message Keybind",
			description = "Configure what button to press to spam",
			position = 1,
			section = keybindSpammer
	)
	default Keybind msgkeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "clanPrefix",
			position = 1,
			name = "Clan Prefix",
			description = "The clan prefix you would like to spam before your targets name",
			section = pileSpammerSection
	)
	default String clanPrefix()
	{
		return "";
	}

	@ConfigItem(
			keyName = "pileKeybind",
			name = "Pile Keybind",
			description = "Configure what button to press to spam your current target",
			position = 3,
			section = pileSpammerSection
	)
	default Keybind pilekeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "useFirstFourChars",
			name = "Smaller Pile Names",
			description = "Spam the first four characters of a piles name opposed to the full name",
			position = 2,
			section = pileSpammerSection
	)
	default boolean smallerPileNames()
	{
		return true;
	}
}
