/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
 *
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
package com.redprisonsentence;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup(RedPrisonSentenceConfig.GROUP)
public interface RedPrisonSentenceConfig extends Config
{
	String GROUP = "prisonsentence";

	@ConfigItem(
		position = 0,
		keyName = "playerNamePosition",
		name = "Name position",
		description = "Configures the position of drawn player names, or if they should be disabled."
	)
	default PlayerNameLocation playerNamePosition()
	{
		return PlayerNameLocation.ABOVE_HEAD;
	}

	@ConfigItem(
		position = 1,
		keyName = "killcountRankIcon",
		name = "Show rank icon",
		description = "Show rank icon next to kill count."
	)
	default boolean killcountRankIcon()
	{
		return true;
	}

	@ConfigSection(
		name = "Highlight options",
		description = "Toggle highlighted players by type (self, others) and choose their highlight colors.",
		position = 99
	)
	String highlightSection = "section";

	enum HighlightSetting
	{
		DISABLED,
		ENABLED,
		GAUNTLET_LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "highlightSelf",
		name = "Highlight own player",
		description = "Configures whether your own player should be highlighted.",
		section = highlightSection
	)
	default HighlightSetting highlightOwnPlayer()
	{
		return HighlightSetting.GAUNTLET_LOBBY;
	}


	@ConfigItem(
			position = 1,
			keyName = "ownNameColor",
			name = "Own player",
			description = "Color of your own player.",
			section = highlightSection
	)
	default Color getOwnPlayerColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		position = 2,
		keyName = "highlightOthers",
		name = "Highlight others",
		description = "Configures whether other players should be highlighted.",
		section = highlightSection
	)
	default HighlightSetting highlightOthers()
	{
		return HighlightSetting.GAUNTLET_LOBBY;
	}

	@ConfigItem(
		position = 3,
		keyName = "otherPlayerColor",
		name = "Others",
		description = "Color of other players names.",
		section = highlightSection
	)
	default Color getOthersColor()
	{
		return Color.RED;
	}
}
