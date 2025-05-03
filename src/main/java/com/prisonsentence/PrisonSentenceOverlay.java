/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * Copyright (c) 2019, Jordan Atwood <nightfirecat@protonmail.com>
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
package com.prisonsentence;

import net.runelite.api.FriendsChatRank;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;
import java.time.Instant;
import java.util.Map;
import net.runelite.client.hiscore.HiscoreClient;
import net.runelite.client.hiscore.HiscoreResult;
import net.runelite.client.hiscore.HiscoreSkill;
import java.time.Duration;
import java.util.concurrent.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Singleton
public class PrisonSentenceOverlay extends Overlay
{
	@Inject
	private HiscoreClient hiscoreClient;

	private static final int ACTOR_OVERHEAD_TEXT_MARGIN = 25;
	private static final int ACTOR_HORIZONTAL_TEXT_MARGIN = 10;

	private final PrisonSentenceService prisonSentenceService;
	private final PrisonSentenceConfig config;
	private final ChatIconManager chatIconManager;
	private final Map<String, CachedKC> kcCache = new ConcurrentHashMap<>();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private static class CachedKC
	{
		final int kc;
		final Instant fetchedAt;

		CachedKC(int kc, Instant fetchedAt)
		{
			this.kc = kc;
			this.fetchedAt = fetchedAt;
		}
	}

	@Inject
	private PrisonSentenceOverlay(PrisonSentenceConfig config, PrisonSentenceService prisonSentenceService, ChatIconManager chatIconManager)
	{
		this.config = config;
		this.prisonSentenceService = prisonSentenceService;
		this.chatIconManager = chatIconManager;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(PRIORITY_MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		prisonSentenceService.forEachPlayer((player, decorations) -> renderPlayerOverlay(graphics, player, decorations));
		return null;
	}

	private void renderPlayerOverlay(Graphics2D graphics, Player actor, PrisonSentenceService.Decorations decorations)
	{

		final PlayerNameLocation drawPlayerNamesConfig = config.playerNamePosition();
		if (drawPlayerNamesConfig == PlayerNameLocation.DISABLED)
		{
			return;
		}

		final int zOffset;
		switch (drawPlayerNamesConfig)
		{
			case MODEL_CENTER, MODEL_RIGHT:
				zOffset = actor.getLogicalHeight() / 2;
				break;
			default:
				zOffset = actor.getLogicalHeight() + ACTOR_OVERHEAD_TEXT_MARGIN;
		}

		String playerName = Text.removeTags(actor.getName());

		CachedKC cached = kcCache.get(playerName);
		int kc = cached != null ? cached.kc : 0;

		// Don't show anything if no KC
		if (kc < 0) {
			return;
		}

		// Re-fetch if it's been more than 30 minutes
		if (cached == null || Duration.between(cached.fetchedAt, Instant.now()).toMinutes() > 30)
		{
			// Log a cached KC now so we don't spam the server
			kcCache.put(playerName, new CachedKC(kc, Instant.now()));
			executor.submit(() ->
			{
				int newKc = fetchCorruptedGauntletKC(playerName);
				kcCache.put(playerName, new CachedKC(newKc, Instant.now()));
			});
		}

		String killCount = kc == 0 ? "--": kc + "";
		Point textLocation = actor.getCanvasTextLocation(graphics, killCount, zOffset);

		// math-based rank
		FriendsChatRank[] ranks = {
				FriendsChatRank.RECRUIT, // 0-99
				FriendsChatRank.CORPORAL, // 100-199
				FriendsChatRank.SERGEANT, // 200-299
				FriendsChatRank.LIEUTENANT, // 300-399
				FriendsChatRank.LIEUTENANT, // 400-499
				FriendsChatRank.CAPTAIN, // 500-599
				FriendsChatRank.CAPTAIN, // 600-699
				FriendsChatRank.GENERAL, // 700-799
				FriendsChatRank.GENERAL, // 800-899
				FriendsChatRank.GENERAL, // 900-999
				FriendsChatRank.JMOD, // 1000+
		};
		int tierSize = 100;
		int tier = Math.min(kc / tierSize, ranks.length - 1);
		BufferedImage rankImage = config.killcountRankIcon() ? chatIconManager.getRankImage(ranks[tier]) : null;

		if (drawPlayerNamesConfig == PlayerNameLocation.MODEL_RIGHT)
		{
			textLocation = actor.getCanvasTextLocation(graphics, "", zOffset);

			if (textLocation == null)
			{
				return;
			}

			textLocation = new Point(textLocation.getX() + ACTOR_HORIZONTAL_TEXT_MARGIN, textLocation.getY());
		}

		if (textLocation == null)
		{
			return;
		}

		if (rankImage != null && kc > 0)
		{
			final int imageWidth = rankImage.getWidth();
			final int imageTextMargin;
			final int imageNegativeMargin;

			if (drawPlayerNamesConfig == com.prisonsentence.PlayerNameLocation.MODEL_RIGHT)
			{
				imageTextMargin = imageWidth;
				imageNegativeMargin = 0;
			}
			else
			{
				imageTextMargin = imageWidth / 2;
				imageNegativeMargin = imageWidth / 2;
			}

			final int textHeight = graphics.getFontMetrics().getHeight() - graphics.getFontMetrics().getMaxDescent();
			final Point imageLocation = new Point(textLocation.getX() - imageNegativeMargin - 1, textLocation.getY() - textHeight / 2 - rankImage.getHeight() / 2);
			OverlayUtil.renderImageLocation(graphics, imageLocation, rankImage);

			// move text
			textLocation = new Point(textLocation.getX() + imageTextMargin, textLocation.getY());
		}

		OverlayUtil.renderTextLocation(graphics, textLocation, killCount, decorations.getColor());
	}

	private int fetchCorruptedGauntletKC(String playerName)
	{
		try
		{
			HiscoreResult result = hiscoreClient.lookup(playerName);
			return result.getSkill(HiscoreSkill.THE_CORRUPTED_GAUNTLET).getLevel();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
}