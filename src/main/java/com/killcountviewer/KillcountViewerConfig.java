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
package com.killcountviewer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup(KillcountViewerConfig.GROUP)
public interface KillcountViewerConfig extends Config
{
	String GROUP = "killcountviewer";

	@ConfigItem(
		position = 0,
		keyName = "killcountPosition",
		name = "Kill count position",
		description = "Configures the position of drawn player names, or if they should be disabled."
	)
	default PlayerNameLocation killcountPosition()
	{
		return PlayerNameLocation.ABOVE_HEAD;
	}


	@ConfigItem(
			position = 1,
			keyName = "killcountColor",
			name = "Kill count color",
			description = "Color of the kill count text."
	)
	default Color killCountColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		position = 2,
		keyName = "killcountRankIcon",
		name = "Show rank icon",
		description = "Show rank icon next to kill count."
	)
	default boolean killcountRankIcon()
	{
		return true;
	}

	@ConfigSection(
		name = "Boss options",
		description = "Toggle which bosses to show kill count for.",
		position = 99
	)
	String highlightSection = "section";

	enum HighlightSetting
	{
		DISABLED,
		ENABLED,
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCorruptedGauntlet",
		name = "Corrupted Gauntlet",
		description = "Configures whether kill count is displayed within the Corrupted Gauntlet.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledCorruptedGauntlet()
	{
		return HighlightSetting.ENABLED;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossZalcano",
		name = "Zalcano",
		description = "Configures whether kill count is displayed within Zalcano.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledZalcano()
	{
		return HighlightSetting.ENABLED;
	}
}
