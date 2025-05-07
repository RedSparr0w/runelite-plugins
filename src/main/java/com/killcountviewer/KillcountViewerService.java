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

import lombok.Value;
import net.runelite.api.*;
import net.runelite.api.clan.*;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.party.PartyService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Singleton
class KillcountViewerService
{
	private final Client client;
	private final KillcountViewerConfig config;

	@Inject
	private KillcountViewerService(Client client, KillcountViewerConfig config, PartyService partyService)
	{
		this.config = config;
		this.client = client;
	}

	void forEachPlayer(final BiConsumer<Player, Decorations> consumer)
	{

		HiscoreSkill boss = getBossZone(client.getLocalPlayer());

		if (boss == null)	return;

		for (Player player : client.getPlayers())
		{
			if (player == null || player.getName() == null)
			{
				continue;
			}

			Decorations decorations = getDecorations(player);

			if (decorations != null && decorations.getColor() != null)
			{
				consumer.accept(player, decorations);
			}
		}
	}

	HiscoreSkill getBossZone(Player player)
	{
		if (player == null || player.getName() == null) return null;
		int region = client.getLocalPlayer().getWorldLocation().getRegionID();

		if (region == 12127) return HiscoreSkill.THE_CORRUPTED_GAUNTLET;
		if (region == 13250) return HiscoreSkill.ZALCANO;

		return null;
	}

	Decorations getDecorations(Player player)
	{
		if (player.getName() == null)
		{
			return null;
		}

		if (player == client.getLocalPlayer())
		{
			if (config.highlightOwnPlayer() == KillcountViewerConfig.HighlightSetting.ENABLED)
			{
				return new Decorations(null, null, config.getOwnPlayerColor());
			}
		}
		else if (config.highlightOthers() == KillcountViewerConfig.HighlightSetting.ENABLED)
		{
			return new Decorations(null, null, config.getOthersColor());
		}

		
		return null;
	}

	@Value
	static class Decorations
	{
		FriendsChatRank friendsChatRank;
		ClanTitle clanTitle;
		Color color;
	}
}
