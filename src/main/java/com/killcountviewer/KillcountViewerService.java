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

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.CommandExecuted;
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.party.PartyService;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.eventbus.Subscribe;
import com.killcountviewer.KillcountViewerConfig.HighlightAlwaysSetting;
import com.killcountviewer.KillcountViewerConfig.HighlightSetting;

import java.awt.*;
import java.util.function.BiConsumer;

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

	void forEachPlayer(final BiConsumer<Player, Color> consumer)
	{

		HiscoreSkill boss = getBossZone(client.getLocalPlayer());

		if (boss == null)	return;

		for (Player player : client.getPlayers())
		{
			if (player == null || player.getName() == null)
			{
				continue;
			}

			consumer.accept(player, config.killCountColor());
		}
	}

	
	private boolean isInArea(Player player, int x1, int y1, int x2, int y2) {
		return isInArea(player, x1, y1, x2, y2, 0);
	}
	private boolean isInArea(Player player, int x1, int y1, int x2, int y2, int z)
	{
		if (player == null || player.getWorldLocation() == null)
		{
			return false;
		}

		WorldPoint location = WorldPoint.fromLocalInstance(client, player.getLocalLocation());

		int minX = Math.min(x1, x2);
		int maxX = Math.max(x1, x2);
		int x = location.getX();
		int minY = Math.min(y1, y2);
		int maxY = Math.max(y1, y2);
		int y = location.getY();

		return x >= minX && x <= maxX && y >= minY && y <= maxY && location.getPlane() == z;
	}

	boolean enabledAlways(HighlightAlwaysSetting setting)
	{
		return setting == HighlightAlwaysSetting.ALWAYS;
	}

	boolean enabledLobby(HighlightAlwaysSetting setting)
	{
		return setting == HighlightAlwaysSetting.LOBBY || setting == HighlightAlwaysSetting.ALWAYS;
	}

	boolean enabledLobby(HighlightSetting setting)
	{
		return setting == HighlightSetting.LOBBY;
	}

	HiscoreSkill getBossZone(Player player)
	{
		if (player == null || player.getWorldLocation() == null) return null;

		int region = WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID();

		// The corrupted gauntlet
		if (region == 12127) { // Lobby
			return enabledLobby(config.bossEnabledCorruptedGauntlet()) ? HiscoreSkill.THE_CORRUPTED_GAUNTLET : null;
		}

		// Royal titans
		if (isInArea(player, 2948, 9571, 2958, 9583)) { // Lobby
			return enabledLobby(config.bossEnabledRoyalTitans()) ? HiscoreSkill.THE_ROYAL_TITANS : null;
		}
		if (region == 11669) { // Boss room
			return enabledAlways(config.bossEnabledRoyalTitans()) ? HiscoreSkill.THE_ROYAL_TITANS : null;
		}

		// Wintertodt
		if (region == 6461) { // Lobby
			return enabledLobby(config.bossEnabledWintertodt()) ? HiscoreSkill.WINTERTODT : null;
		}
		if (region == 6462) { // Boss room
			return enabledAlways(config.bossEnabledWintertodt()) ? HiscoreSkill.WINTERTODT : null;
		}

		// Zalcano
		if (isInArea(player, 3028, 6063, 3039, 6071)) { // Lobby
			return enabledLobby(config.bossEnabledZalcano()) ? HiscoreSkill.ZALCANO : null;
		}
		if (region == 12126) { // Lobby + boss room
			return enabledAlways(config.bossEnabledZalcano()) ? HiscoreSkill.ZALCANO : null;
		}


		return null;
	}
}
