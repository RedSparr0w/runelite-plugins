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
package com.killcountviewer;

import net.runelite.api.FriendsChatRank;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

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
public class KillCountViewerOverlay extends Overlay
{
	@Inject
	private HiscoreClient hiscoreClient;

	private static final int ACTOR_OVERHEAD_TEXT_MARGIN = 25;
	private static final int ACTOR_HORIZONTAL_TEXT_MARGIN = 10;

	private final KillCountViewerService killcountService;
	private final KillCountViewerConfig config;
	private final ChatIconManager chatIconManager;
	private final Map<String, CachedKC> kcCache = new ConcurrentHashMap<>();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final Queue<String> kcLookupQueue = new ConcurrentLinkedQueue<>();

	private static final HiscoreSkill[] SCORES = {
		HiscoreSkill.SOUL_WARS_ZEAL,                    // Done (test in matchmaking)
		HiscoreSkill.LAST_MAN_STANDING,                 // Done (not in matchmaking)
		HiscoreSkill.RIFTS_CLOSED,                      // Done
		HiscoreSkill.ABYSSAL_SIRE,                      // Done
		HiscoreSkill.ALCHEMICAL_HYDRA,                  // Done
		HiscoreSkill.AMOXLIATL,                         // Done
		HiscoreSkill.ARAXXOR,                           // Done
		HiscoreSkill.ARTIO,                             // Done
		HiscoreSkill.BARROWS_CHESTS,                    // Done
		HiscoreSkill.BRYOPHYTA,                         // Done
		HiscoreSkill.CALLISTO,                          // Done
		HiscoreSkill.CALVARION,                         // Done
		HiscoreSkill.CERBERUS,                          // Done
		HiscoreSkill.CHAMBERS_OF_XERIC,                 // Done
		HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE,  // Done
		HiscoreSkill.CHAOS_ELEMENTAL,                   // Done
		HiscoreSkill.CHAOS_FANATIC,                     // Done
		HiscoreSkill.COMMANDER_ZILYANA,                 // Done
		HiscoreSkill.CORPOREAL_BEAST,                   // Done
		HiscoreSkill.CRAZY_ARCHAEOLOGIST,               // Done
		HiscoreSkill.DAGANNOTH_PRIME,
		HiscoreSkill.DAGANNOTH_REX,
		HiscoreSkill.DAGANNOTH_SUPREME,
		HiscoreSkill.DERANGED_ARCHAEOLOGIST,            // Done
		HiscoreSkill.DUKE_SUCELLUS,
		HiscoreSkill.GENERAL_GRAARDOR,                  // Done
		HiscoreSkill.GIANT_MOLE,                        // Done
		HiscoreSkill.GROTESQUE_GUARDIANS,               // Done
		HiscoreSkill.HESPORI,                           // Done
		HiscoreSkill.KALPHITE_QUEEN,                    // Done (to test)
		HiscoreSkill.KING_BLACK_DRAGON,                 // Done
		HiscoreSkill.KRAKEN,                            // Done (to test)
		HiscoreSkill.KREEARRA,                          // Done
		HiscoreSkill.KRIL_TSUTSAROTH,                   // Done
		HiscoreSkill.LUNAR_CHESTS,                      // Done
		HiscoreSkill.MIMIC,                             // Done
		HiscoreSkill.NEX,
		HiscoreSkill.NIGHTMARE,
		HiscoreSkill.PHOSANIS_NIGHTMARE,
		HiscoreSkill.OBOR,                              // Done
		HiscoreSkill.PHANTOM_MUSPAH,                    // Done
		HiscoreSkill.SARACHNIS,                         // Done
		HiscoreSkill.SCORPIA,                           // Done
		HiscoreSkill.SCURRIUS,                          // Done
		HiscoreSkill.SKOTIZO,                           // Done
		HiscoreSkill.SOL_HEREDIT,                       // Done
		HiscoreSkill.SPINDEL,                           // Done
		HiscoreSkill.TEMPOROSS,                         // Done
		HiscoreSkill.THE_CORRUPTED_GAUNTLET,            // Done
		HiscoreSkill.THE_HUEYCOATL,                     // Done
		HiscoreSkill.THE_LEVIATHAN,
		HiscoreSkill.THE_ROYAL_TITANS,                  // Done
		HiscoreSkill.THE_WHISPERER,
		HiscoreSkill.THEATRE_OF_BLOOD,                  // Done
		HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE,        // Done
		HiscoreSkill.THERMONUCLEAR_SMOKE_DEVIL,
		HiscoreSkill.TOMBS_OF_AMASCUT,                  // Done
		HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT,           // Done
		HiscoreSkill.TZKAL_ZUK,                         // Done
		HiscoreSkill.TZTOK_JAD,                         // Done
		HiscoreSkill.VARDORVIS,
		HiscoreSkill.VENENATIS,                         // Done
		HiscoreSkill.VETION,                            // Done
		HiscoreSkill.VORKATH,                           // Done
		HiscoreSkill.WINTERTODT,                        // Done
		HiscoreSkill.ZALCANO,                           // Done
		HiscoreSkill.ZULRAH,                            // Done
	};

	private static class CachedKC
	{
		final Map<HiscoreSkill, Integer> kcMap;
		final Instant fetchedAt;

		CachedKC(Map<HiscoreSkill, Integer> kcMap, Instant fetchedAt)
		{
			this.kcMap = kcMap;
			this.fetchedAt = fetchedAt;
		}
	}

	@Inject
	private KillCountViewerOverlay(KillCountViewerConfig config, KillCountViewerService KillCountService, ChatIconManager chatIconManager)
	{
		this.config = config;
		this.killcountService = KillCountService;
		this.chatIconManager = chatIconManager;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(PRIORITY_MED);
	}

	private int lookupCounter = 0;

	public void processKcQueue() {
		if (kcLookupQueue.isEmpty()) {
			lookupCounter = config.lookupCooldown() - 1;
			return;
		}
		
		// Only run every x seconds to avoid spamming the hiscore API
		lookupCounter = (lookupCounter + 1) % config.lookupCooldown();

		if (lookupCounter != 0) {
			return;
		}

		if (killcountService.currentBoss == null) {
			// If we don't have a boss zone, clear the queue and return
			kcLookupQueue.clear();
			return;
		}

		String playerName = kcLookupQueue.poll();
		Map<HiscoreSkill, Integer> data = kcCache.get(playerName) != null ? kcCache.get(playerName).kcMap : null;
		kcCache.put(playerName, new CachedKC(data, Instant.now()));

		executor.submit(() ->
		{
			Map<HiscoreSkill, Integer> kcMap = fetchPlayerKC(playerName);
			kcCache.put(playerName, new CachedKC(kcMap, Instant.now()));
		});
	}

	public void gameTick()
	{
		killcountService.getBossZone();
		this.processKcQueue();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		killcountService.forEachPlayer((player, color) -> renderPlayerOverlay(graphics, player, color));
		return null;
	}

	private void renderPlayerOverlay(Graphics2D graphics, Player player, Color color)
	{

		final PlayerNameLocation drawPlayerNamesConfig = config.killcountPosition();
		if (drawPlayerNamesConfig == PlayerNameLocation.DISABLED)
		{
			return;
		}

		String playerName = Text.removeTags(player.getName());

		CachedKC cached = kcCache.get(playerName);
		HiscoreSkill boss = killcountService.currentBoss;
		int kc = cached != null && cached.kcMap != null && cached.kcMap.containsKey(boss) ? cached.kcMap.get(boss) : 0;

		// Don't show anything if no KC
		if (kc < 0) {
			return;
		}

		// Re-fetch if it's been more than 30 minutes and not already in the queue or never fetched
		if (!kcLookupQueue.contains(playerName) && (cached == null || Duration.between(cached.fetchedAt, Instant.now()).toMinutes() > 30))
		{
			kcLookupQueue.offer(playerName);
		}

		// Caclulate our rank image if enabled
		BufferedImage rankImage = config.killcountRankIcon() ? this.calculateRankImage(kc) : null;

		// Draw the kill count
		final int zOffset;
		switch (drawPlayerNamesConfig)
		{
			case MODEL_CENTER:
			case MODEL_RIGHT:
				zOffset = player.getLogicalHeight() / 2;
				break;
			default:
				zOffset = player.getLogicalHeight() + ACTOR_OVERHEAD_TEXT_MARGIN;
		}

		String killCountText = kc == 0 ? "..." : kc + "";
		Point textLocation = player.getCanvasTextLocation(graphics, killCountText, zOffset);

		if (drawPlayerNamesConfig == PlayerNameLocation.MODEL_RIGHT)
		{
			textLocation = player.getCanvasTextLocation(graphics, "", zOffset);

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

			if (drawPlayerNamesConfig == com.killcountviewer.PlayerNameLocation.MODEL_RIGHT)
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

		OverlayUtil.renderTextLocation(graphics, textLocation, killCountText, color);
	}

	private BufferedImage calculateRankImage(int kc)
	{
		
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
		return chatIconManager.getRankImage(ranks[tier]);
	}

	private Map<HiscoreSkill, Integer> fetchPlayerKC(String playerName)
	{
		Map<HiscoreSkill, Integer> results = new HashMap<>();

		try
		{
			HiscoreResult result = hiscoreClient.lookup(playerName);
			for (HiscoreSkill boss : SCORES)
			{
				if (boss != null)
				{
					results.put(boss, result.getSkill(boss).getLevel());
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return results;
	}
}