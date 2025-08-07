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

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
	name = "Kill Count Viewer",
	description = "Display players kill counts at various bosses throughout the game",
	tags = {"highlight", "boss", "overlay", "players", "killcount", "kc"}
)
@Slf4j
public class KillCountViewerPlugin extends Plugin
{
	private static final String PLUGIN_VERSION = "1.1.1";

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private KillCountViewerOverlay killcountOverlay;

	@Inject
	private Client client;

	@Inject
	private KillCountViewerConfig config;

	@Inject
	private ConfigManager configManager;

	private int showUpdateMessage = 0;

	@Provides
	KillCountViewerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(KillCountViewerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(killcountOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		killcountOverlay.clearCache();
		overlayManager.remove(killcountOverlay);
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		killcountOverlay.gameTick();

		// Send update message to the player if the plugin was updated (wait a few ticks so not up the top)
		if (showUpdateMessage > 0)
		{
			if (--showUpdateMessage == 0)
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=c0392b>Kill Count Viewer:</col> Plugin updated to v" + PLUGIN_VERSION, null);
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=c0392b>-</col> Added option for cache duration.", null);
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=c0392b>-</col> Added Thermonuclear Smoke Devil.", null);
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=c0392b>-</col> Use local player kill count instead of high score.", null);
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == net.runelite.api.GameState.LOGGED_IN)
		{
			// Check if plugin was updated
			String lastSeenVersion = config.lastSeenVersion();
			if (!PLUGIN_VERSION.equals(lastSeenVersion))
			{
				log.info("Kill Count Viewer plugin updated to version: " + PLUGIN_VERSION);
				showUpdateMessage = 5; // Reset message counter to show update messages
				configManager.setConfiguration("killcountviewer", "lastSeenVersion", PLUGIN_VERSION);
			}
		}
	}
}
