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

import net.runelite.api.Client;
import net.runelite.api.FriendsChatRank;
import net.runelite.api.IconID;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.game.SpriteManager;
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
import net.runelite.client.hiscore.HiscoreSkillType;

import java.time.Duration;
import java.util.concurrent.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.killcountviewer.KillCountViewerConfig.IconSetting;

@Singleton
public class KillCountViewerOverlay extends Overlay
{
	private static final Logger log = LoggerFactory.getLogger(KillCountViewerOverlay.class);

	@Inject
	private Client client;

	@Inject
	private HiscoreClient hiscoreClient;

	@Inject
	private ConfigManager configManager;

	private static final int ACTOR_OVERHEAD_TEXT_MARGIN = 25;
	private static final int ACTOR_HORIZONTAL_TEXT_MARGIN = 10;

	private final KillCountViewerService killcountService;
	private final KillCountViewerConfig config;
	private final ChatIconManager chatIconManager;
	private final SpriteManager spriteManager;
	private final Map<String, CachedKC> kcCache = new ConcurrentHashMap<>();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final Queue<String> kcLookupQueue = new ConcurrentLinkedQueue<>();

	private static final HiscoreSkill[] SCORES = {
		// Skills
		HiscoreSkill.WOODCUTTING,                      // Done
		HiscoreSkill.FISHING,                          // Done
		HiscoreSkill.RANGED,                           // Done
		HiscoreSkill.MINING,                           // Done
		HiscoreSkill.CRAFTING,                         // Done
		HiscoreSkill.COOKING,                          // Done
		HiscoreSkill.FARMING,                          // Done
		HiscoreSkill.MAGIC,                            // Done
		HiscoreSkill.HUNTER,                           // Done
		HiscoreSkill.SMITHING,                         // Done
		HiscoreSkill.HERBLORE,                         // Done
		HiscoreSkill.SLAYER,                           // Done
		HiscoreSkill.AGILITY,                          // Done (probably some rooftops and courses missing)
		HiscoreSkill.CONSTRUCTION,                     // Done
		HiscoreSkill.STRENGTH,                         // Done
		HiscoreSkill.PRAYER,                           // Done
		HiscoreSkill.FLETCHING,                        // Done
		HiscoreSkill.THIEVING,                         // Done
		HiscoreSkill.RUNECRAFT,                        // Done
		// Activities
		HiscoreSkill.SOUL_WARS_ZEAL,                    // Done (test in matchmaking)
		HiscoreSkill.LAST_MAN_STANDING,                 // Done (not in matchmaking)
		HiscoreSkill.RIFTS_CLOSED,                      // Done
		// Bosses
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
		HiscoreSkill.KALPHITE_QUEEN,                    // Done
		HiscoreSkill.KING_BLACK_DRAGON,                 // Done
		HiscoreSkill.KRAKEN,                            // Done
		HiscoreSkill.KREEARRA,                          // Done
		HiscoreSkill.KRIL_TSUTSAROTH,                   // Done
		HiscoreSkill.LUNAR_CHESTS,                      // Done
		HiscoreSkill.MIMIC,                             // Done
		HiscoreSkill.NEX,
		HiscoreSkill.NIGHTMARE,                         // Done (to test)
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
		HiscoreSkill.THERMONUCLEAR_SMOKE_DEVIL,
		HiscoreSkill.TZKAL_ZUK,                         // Done
		HiscoreSkill.TZTOK_JAD,                         // Done
		HiscoreSkill.VARDORVIS,
		HiscoreSkill.VENENATIS,                         // Done
		HiscoreSkill.VETION,                            // Done
		HiscoreSkill.VORKATH,                           // Done
		HiscoreSkill.WINTERTODT,                        // Done
		HiscoreSkill.YAMA,                              // Done
		HiscoreSkill.ZALCANO,                           // Done
		HiscoreSkill.ZULRAH,                            // Done
		// Raids
		HiscoreSkill.CHAMBERS_OF_XERIC,                 // Done
		HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE,  // Done
		HiscoreSkill.THEATRE_OF_BLOOD,                  // Done
		HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE,        // Done
		HiscoreSkill.TOMBS_OF_AMASCUT,                  // Done
		HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT,           // Done
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
	private KillCountViewerOverlay(KillCountViewerConfig config, KillCountViewerService killCountService, ChatIconManager chatIconManager, SpriteManager spriteManager)
	{
		this.config = config;
		this.killcountService = killCountService;
		this.chatIconManager = chatIconManager;
		this.spriteManager = spriteManager;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(PRIORITY_MED);
		// Log any missing activities/bosses from the SCORES array
		HiscoreSkill[] allSkills = HiscoreSkill.values();
		java.util.Set<HiscoreSkill> scoresSet = new java.util.HashSet<>();
		java.util.Collections.addAll(scoresSet, SCORES);
		for (HiscoreSkill skill : allSkills) {
			if (!scoresSet.contains(skill)) {
				log.info("HiscoreSkill missing from SCORES: {}", skill);
			}
		}
	}

	private int lookupCounter = 0;

	public void processKcQueue() {
		if (kcLookupQueue.isEmpty()) {
			// If the queue is empty, reset the counter so it does a look up next tick when a player is added
			lookupCounter = config.lookupCooldown() - 1;
			return;
		}

		if (killcountService.currentBoss == null) {
			// If we don't have a boss zone, clear the queue and return
			kcLookupQueue.clear();
			return;
		}
		
		// Only run every x seconds to avoid spamming the hiscore API
		lookupCounter = (lookupCounter + 1) % config.lookupCooldown();

		if (lookupCounter != 0) {
			return;
		}

		String playerName = kcLookupQueue.poll();
		Map<HiscoreSkill, Integer> data = kcCache.get(playerName) != null ? kcCache.get(playerName).kcMap : null;
		// Update the cache with current time so we don't re-add players that we are currently fetching
		kcCache.put(playerName, new CachedKC(data, Instant.now()));

		executor.submit(() ->
		{
			Map<HiscoreSkill, Integer> kcMap = fetchPlayerKC(playerName);
			// If the lookup failed, use the previous data and set the fetchedAt to 2 minutes less than our cache duration
			if (kcMap.isEmpty()) {
				kcCache.put(playerName, new CachedKC(data, Instant.now().minus(Duration.ofMinutes((long)config.cacheDuration() - 2))));
			} else {
				kcCache.put(playerName, new CachedKC(kcMap, Instant.now()));
			}
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

		// Re-fetch if it's been more than x minutes and not already in the queue or never fetched
		if (!kcLookupQueue.contains(playerName) && (cached == null || Duration.between(cached.fetchedAt, Instant.now()).toMinutes() >= config.cacheDuration()))
		{
			kcLookupQueue.offer(playerName);
		}

		if (playerName.equalsIgnoreCase(client.getLocalPlayer().getName())) {
			kc = getLocalPlayerKc(killcountService.currentBoss);
		}

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
		String killCountText = kc == 0 ? "" : kc + "";
		if (killCountText == "" && !kcLookupQueue.contains(playerName))
		{
			killCountText = "...";
		}
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

		// Should we be using the skill or boss settings for the icon
		IconSetting configIconSetting = boss.getType() == HiscoreSkillType.SKILL ? config.levelRankIcon() : config.bossRankIcon();
		// If the icon setting is disabled, we don't need to draw anything
		Image rankIcon = configIconSetting == IconSetting.BOTH || configIconSetting == IconSetting.RANK ? this.calculateRankImage(boss, kc) : null;
		Image skillIcon = configIconSetting == IconSetting.BOTH || configIconSetting == IconSetting.ICON ? getSkillIcon(boss) : null;

		// If we have a skill icon it should be on the left, otherwise rank icon, or none
		Image leftIcon = skillIcon != null ? skillIcon : rankIcon != null ? rankIcon : null;
		// If we have a skill icon and rank icon, the rank icon should be on the right, otherwise none
		Image rightIcon = skillIcon != null && rankIcon != null ? rankIcon : null;


		if (leftIcon != null && kc > 0)
		{

			final int textHeight = graphics.getFontMetrics().getHeight() - graphics.getFontMetrics().getMaxDescent();
			final int textWidth = graphics.getFontMetrics().stringWidth(killCountText);
			final int imageMargin = 5;

			// If we only have a left icon, center the whole text block
			if (rightIcon == null)
			{
				textLocation = new Point(textLocation.getX() + (imageMargin + leftIcon.getWidth(null)) / 2, textLocation.getY());
			}

			if (leftIcon != null)
			{
				Point iconLoc = new Point(
					textLocation.getX() - imageMargin - leftIcon.getWidth(null),
					textLocation.getY() - textHeight / 2 - leftIcon.getHeight(null) / 2
				);
				graphics.drawImage(leftIcon, iconLoc.getX(), iconLoc.getY(), null);
			}

			if (rightIcon != null)
			{

				Point iconLoc = new Point(
					textLocation.getX() + textWidth + imageMargin,
					textLocation.getY() - textHeight / 2 - rightIcon.getHeight(null) / 2
				);
				graphics.drawImage(rightIcon, iconLoc.getX(), iconLoc.getY(), null);
			}
		}

		OverlayUtil.renderTextLocation(graphics, textLocation, killCountText, color);
	}

	private BufferedImage calculateRankImage(HiscoreSkill skill, int kc)
	{
		FriendsChatRank rank = FriendsChatRank.UNRANKED;
		if (skill.getType() == HiscoreSkillType.SKILL) {
			if (kc >= 99) {
				rank = FriendsChatRank.JMOD;
			} else if (kc >= 95) {
				rank = FriendsChatRank.GENERAL;
			} else if (kc >= 90) {
				rank = FriendsChatRank.CAPTAIN;
			} else if (kc >= 80) {
				rank = FriendsChatRank.LIEUTENANT;
			} else if (kc >= 70) {
				rank = FriendsChatRank.SERGEANT;
			} else if (kc >= 60) {
				rank = FriendsChatRank.CORPORAL;
			} else {
				rank = FriendsChatRank.RECRUIT;
			}
		} else {
			if (kc >= 1000) {
				rank = FriendsChatRank.JMOD;
			} else if (kc >= 700) {
				rank = FriendsChatRank.GENERAL;
			} else if (kc >= 500) {
				rank = FriendsChatRank.CAPTAIN;
			} else if (kc >= 300) {
				rank = FriendsChatRank.LIEUTENANT;
			} else if (kc >= 200) {
				rank = FriendsChatRank.SERGEANT;
			} else if (kc >= 100) {
				rank = FriendsChatRank.CORPORAL;
			} else {
				rank = FriendsChatRank.RECRUIT;
			}
		}
		return chatIconManager.getRankImage(rank);
	}

	private Image getSkillIcon(HiscoreSkill boss)
	{
			BufferedImage skillIcon = spriteManager.getSprite(boss.getSpriteId(), 0);


			if (skillIcon != null)
			{
				// How much we want to scale down the icon
				final int scalePercent = 55;
				int scaledWidth = skillIcon.getWidth() * scalePercent / 100;
				int scaledHeight = skillIcon.getHeight() * scalePercent / 100;

				return skillIcon.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
			}
			return null;
	}

	public void clearCache()
	{
		kcCache.clear();
		kcLookupQueue.clear();
	}

	private Map<HiscoreSkill, Integer> fetchPlayerKC(String playerName)
	{
		Map<HiscoreSkill, Integer> results = new HashMap<>();

		try {
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
			log.warn("Failed to fetch kill count for {}", playerName);
		}
		return results;
	}

	private String getBossString(HiscoreSkill boss) {
		switch (boss) {
			case CALVARION:
				return "Calvar'ion";
			case KREEARRA:
				return "Kree'arra";
			case KRIL_TSUTSAROTH:
				return "K'ril Tsutsaroth";
			case PHOSANIS_NIGHTMARE:
				return "Phosani's Nightmare";
			case THE_CORRUPTED_GAUNTLET:
				return "Corrupted Gauntlet";
			case THE_HUEYCOATL:
				return "Hueycoatl";
			case THE_LEVIATHAN:
				return "Leviathan";
			case THE_ROYAL_TITANS:
				return "Royal Titans";
			case THE_WHISPERER:
				return "Whisperer";
			case TZKAL_ZUK:
				return "TzKal-Zuk";
			case TZTOK_JAD:
				return "TzTok-Jad";
			case VETION:
				return "Vet'ion";
			case CHAMBERS_OF_XERIC:
				return "Chambers of Xeric";
			case CHAMBERS_OF_XERIC_CHALLENGE_MODE:
				return "Chambers of Xeric Challenge Mode";
			case THEATRE_OF_BLOOD:
				return "Theatre of Blood";
			case THEATRE_OF_BLOOD_HARD_MODE:
				return "Theatre of Blood Hard Mode";
			case TOMBS_OF_AMASCUT:
				return "Tombs of Amascut";
			case TOMBS_OF_AMASCUT_EXPERT:
				return "Tombs of Amascut Expert Mode";
			default:
				return Text.titleCase(boss);
		}

	}

	private int localKc = 0;
	private int lastTick = 0;

	private int getLocalPlayerKc(HiscoreSkill boss)
	{
		// Return cached value if we are on the same tick
		if (lastTick == client.getTickCount())
		{
			return localKc;
		}
		// Update last tick
		lastTick = client.getTickCount();

		// If it's a skill, return the real skill level
		if (boss.getType() == HiscoreSkillType.SKILL)
		{
			int skillLevel = client.getRealSkillLevel(Skill.valueOf(boss.name()));
			if (skillLevel > 0)
			{
				localKc = skillLevel;
				return localKc;
			}
		}
		
		// If it's a boss, check the config for the kill count
		if (boss.getType() == HiscoreSkillType.BOSS)
		{
			String bossNameFormatted = getBossString(boss);
			Integer killCount = configManager.getRSProfileConfiguration("killcount", bossNameFormatted.toLowerCase(), int.class);
			if (killCount != null && killCount >= 0)
			{
				localKc = killCount;
				return localKc;
			}
		}

		// If we couldn't find the kill count, use highscore value if available
		CachedKC cached = kcCache.get(client.getLocalPlayer().getName());
		if (cached != null && cached.kcMap != null && cached.kcMap.containsKey(boss)) {
			localKc = cached.kcMap.get(boss);
			return localKc;
		}

		return 0;
	}
}