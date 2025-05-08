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
import net.runelite.client.hiscore.HiscoreSkill;
import net.runelite.client.party.PartyService;
import com.killcountviewer.KillcountViewerConfig.HighlightAlwaysSetting;
import com.killcountviewer.KillcountViewerConfig.HighlightSetting;
import java.awt.*;
import java.util.function.BiConsumer;
import javax.inject.Inject;
import javax.inject.Singleton;

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

	// Check if the setting is enabled for the given boss zone
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

	// Check if the player is in a boss zone and return the corresponding HiscoreSkill
	HiscoreSkill getBossZone()
	{
		Player player = client.getLocalPlayer();
		if (player == null || player.getWorldLocation() == null) return null;

		int region = WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID();
		
		if (isSoulWarsZeal(player, region)) return HiscoreSkill.SOUL_WARS_ZEAL;
		if (isLastManStanding(player, region)) return HiscoreSkill.LAST_MAN_STANDING;
		if (isRiftsClosed(player, region)) return HiscoreSkill.RIFTS_CLOSED;
		if (isAbyssalSire(player, region)) return HiscoreSkill.ABYSSAL_SIRE;
		if (isAlchemicalHydra(player, region)) return HiscoreSkill.ALCHEMICAL_HYDRA;
		if (isAmoxliatl(player, region)) return HiscoreSkill.AMOXLIATL;
		if (isAraxxor(player, region)) return HiscoreSkill.ARAXXOR;
		if (isArtio(player, region)) return HiscoreSkill.ARTIO;
		if (isBarrowsChests(player, region)) return HiscoreSkill.BARROWS_CHESTS;
		if (isBryophyta(player, region)) return HiscoreSkill.BRYOPHYTA;
		if (isCallisto(player, region)) return HiscoreSkill.CALLISTO;
		if (isCalvarion(player, region)) return HiscoreSkill.CALVARION;
		if (isCerberus(player, region)) return HiscoreSkill.CERBERUS;
		if (isChambersOfXeric(player, region)) return HiscoreSkill.CHAMBERS_OF_XERIC;
		if (isChambersOfXericChallengeMode(player, region)) return HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE;
		if (isChaosElemental(player, region)) return HiscoreSkill.CHAOS_ELEMENTAL;
		if (isChaosFanatic(player, region)) return HiscoreSkill.CHAOS_FANATIC;
		if (isCommanderZilyana(player, region)) return HiscoreSkill.COMMANDER_ZILYANA;
		if (isCorporealBeast(player, region)) return HiscoreSkill.CORPOREAL_BEAST;
		if (isCrazyArchaeologist(player, region)) return HiscoreSkill.CRAZY_ARCHAEOLOGIST;
		if (isDagannothPrime(player, region)) return HiscoreSkill.DAGANNOTH_PRIME;
		if (isDagannothRex(player, region)) return HiscoreSkill.DAGANNOTH_REX;
		if (isDagannothSupreme(player, region)) return HiscoreSkill.DAGANNOTH_SUPREME;
		if (isDerangedArchaeologist(player, region)) return HiscoreSkill.DERANGED_ARCHAEOLOGIST;
		if (isDukeSucellus(player, region)) return HiscoreSkill.DUKE_SUCELLUS;
		if (isGeneralGraardor(player, region)) return HiscoreSkill.GENERAL_GRAARDOR;
		if (isGiantMole(player, region)) return HiscoreSkill.GIANT_MOLE;
		if (isGrotesqueGuardians(player, region)) return HiscoreSkill.GROTESQUE_GUARDIANS;
		if (isHespori(player, region)) return HiscoreSkill.HESPORI;
		if (isKalphiteQueen(player, region)) return HiscoreSkill.KALPHITE_QUEEN;
		if (isKingBlackDragon(player, region)) return HiscoreSkill.KING_BLACK_DRAGON;
		if (isKraken(player, region)) return HiscoreSkill.KRAKEN;
		if (isKreeArra(player, region)) return HiscoreSkill.KREEARRA;
		if (isKrilTsutsaroth(player, region)) return HiscoreSkill.KRIL_TSUTSAROTH;
		if (isLunarChests(player, region)) return HiscoreSkill.LUNAR_CHESTS;
		if (isMimic(player, region)) return HiscoreSkill.MIMIC;
		if (isNex(player, region)) return HiscoreSkill.NEX;
		if (isNightmare(player, region)) return HiscoreSkill.NIGHTMARE;
		if (isPhosanisNightmare(player, region)) return HiscoreSkill.PHOSANIS_NIGHTMARE;
		if (isObor(player, region)) return HiscoreSkill.OBOR;
		if (isPhantomMuspah(player, region)) return HiscoreSkill.PHANTOM_MUSPAH;
		if (isSarachnis(player, region)) return HiscoreSkill.SARACHNIS;
		if (isScorpia(player, region)) return HiscoreSkill.SCORPIA;
		if (isScurrius(player, region)) return HiscoreSkill.SCURRIUS;
		if (isSkotizo(player, region)) return HiscoreSkill.SKOTIZO;
		if (isSolHeredit(player, region)) return HiscoreSkill.SOL_HEREDIT;
		if (isTempoross(player, region)) return HiscoreSkill.TEMPOROSS;
		if (isTheHueycoatl(player, region)) return HiscoreSkill.THE_HUEYCOATL;
		if (isTheLeviathan(player, region)) return HiscoreSkill.THE_LEVIATHAN;
		if (isTheWhisperer(player, region)) return HiscoreSkill.THE_WHISPERER;
		if (isTheatreOfBlood(player, region)) return HiscoreSkill.THEATRE_OF_BLOOD;
		if (isTheatreOfBloodHardMode(player, region)) return HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE;
		if (isThermonuclearSmokeDevil(player, region)) return HiscoreSkill.THERMONUCLEAR_SMOKE_DEVIL;
		if (isTombsOfAmascut(player, region)) return HiscoreSkill.TOMBS_OF_AMASCUT;
		if (isTombsOfAmascutExpert(player, region)) return HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT;
		if (isVenenatis(player, region)) return HiscoreSkill.VENENATIS;
		if (isVetion(player, region)) return HiscoreSkill.VETION;
		if (isCorruptedGauntlet(player, region)) return HiscoreSkill.THE_CORRUPTED_GAUNTLET;
		if (isRoyalTitans(player, region)) return HiscoreSkill.THE_ROYAL_TITANS;
		if (isTzKalZuk(player, region)) return HiscoreSkill.TZKAL_ZUK;
		if (isTzTokJad(player, region)) return HiscoreSkill.TZTOK_JAD;
		if (isVardorvis(player, region)) return HiscoreSkill.VARDORVIS;
		if (isVorkath(player, region)) return HiscoreSkill.VORKATH;
		if (isWintertodt(player, region)) return HiscoreSkill.WINTERTODT;
		if (isZalcano(player, region)) return HiscoreSkill.ZALCANO;
		if (isZulrah(player, region)) return HiscoreSkill.ZULRAH;

		return null;
	}
	private boolean isSoulWarsZeal(Player player, int region)
	{
		return false;
	}

	private boolean isLastManStanding(Player player, int region)
	{
		return false;
	}

	private boolean isRiftsClosed(Player player, int region)
	{
		return false;
	}

	private boolean isAbyssalSire(Player player, int region)
	{
		return false;
	}

	private boolean isAlchemicalHydra(Player player, int region)
	{
		return false;
	}

	private boolean isAmoxliatl(Player player, int region)
	{
		return false;
	}

	private boolean isAraxxor(Player player, int region)
	{
		return false;
	}

	private boolean isArtio(Player player, int region)
	{
		return false;
	}

	private boolean isBarrowsChests(Player player, int region)
	{
		return false;
	}

	private boolean isBryophyta(Player player, int region)
	{
		return false;
	}

	private boolean isCallisto(Player player, int region)
	{
		return false;
	}

	private boolean isCalvarion(Player player, int region)
	{
		return false;
	}

	private boolean isCerberus(Player player, int region)
	{
		return false;
	}

	private boolean isChambersOfXeric(Player player, int region)
	{
		return false;
	}

	private boolean isChambersOfXericChallengeMode(Player player, int region)
	{
		return false;
	}

	private boolean isChaosElemental(Player player, int region)
	{
		return false;
	}

	private boolean isChaosFanatic(Player player, int region)
	{
		return false;
	}

	private boolean isCommanderZilyana(Player player, int region)
	{
		return false;
	}

	private boolean isCorporealBeast(Player player, int region)
	{
		return false;
	}

	private boolean isCrazyArchaeologist(Player player, int region)
	{
		return false;
	}

	private boolean isDagannothPrime(Player player, int region)
	{
		return false;
	}

	private boolean isDagannothRex(Player player, int region)
	{
		return false;
	}

	private boolean isDagannothSupreme(Player player, int region)
	{
		return false;
	}

	private boolean isDerangedArchaeologist(Player player, int region)
	{
		return false;
	}

	private boolean isDukeSucellus(Player player, int region)
	{
		return false;
	}

	private boolean isGeneralGraardor(Player player, int region)
	{
		return false;
	}

	private boolean isGiantMole(Player player, int region)
	{
		return false;
	}

	private boolean isGrotesqueGuardians(Player player, int region)
	{
		return false;
	}

	private boolean isHespori(Player player, int region)
	{
		return false;
	}

	private boolean isKalphiteQueen(Player player, int region)
	{
		return false;
	}

	private boolean isKingBlackDragon(Player player, int region)
	{
		return false;
	}

	private boolean isKraken(Player player, int region)
	{
		return false;
	}

	private boolean isKreeArra(Player player, int region)
	{
		return false;
	}

	private boolean isKrilTsutsaroth(Player player, int region)
	{
		return false;
	}

	private boolean isLunarChests(Player player, int region)
	{
		return false;
	}

	private boolean isMimic(Player player, int region)
	{
		return false;
	}

	private boolean isNex(Player player, int region)
	{
		return false;
	}

	private boolean isNightmare(Player player, int region)
	{
		return false;
	}

	private boolean isPhosanisNightmare(Player player, int region)
	{
		return false;
	}

	private boolean isObor(Player player, int region)
	{
		return false;
	}

	private boolean isPhantomMuspah(Player player, int region)
	{
		return false;
	}

	private boolean isSarachnis(Player player, int region)
	{
		return false;
	}

	private boolean isScorpia(Player player, int region)
	{
		return false;
	}

	private boolean isScurrius(Player player, int region)
	{
		return false;
	}

	private boolean isSkotizo(Player player, int region)
	{
		return false;
	}

	private boolean isSolHeredit(Player player, int region)
	{
		return false;
	}

	private boolean isTempoross(Player player, int region)
	{
		return false;
	}

	private boolean isTheHueycoatl(Player player, int region)
	{
		return false;
	}

	private boolean isTheLeviathan(Player player, int region)
	{
		return false;
	}

	private boolean isTheWhisperer(Player player, int region)
	{
		return false;
	}

	private boolean isTheatreOfBlood(Player player, int region)
	{
		return false;
	}

	private boolean isTheatreOfBloodHardMode(Player player, int region)
	{
		return false;
	}

	private boolean isThermonuclearSmokeDevil(Player player, int region)
	{
		return false;
	}

	private boolean isTombsOfAmascut(Player player, int region)
	{
		return false;
	}

	private boolean isTombsOfAmascutExpert(Player player, int region)
	{
		return false;
	}

	private boolean isVenenatis(Player player, int region)
	{
		return false;
	}

	private boolean isVetion(Player player, int region)
	{
		return false;
	}
	private boolean isCorruptedGauntlet(Player player, int region)
	{
		return region == 12127 && enabledLobby(config.bossEnabledCorruptedGauntlet());
	}

	private boolean isRoyalTitans(Player player, int region)
	{
		return
			(isInArea(player, 2948, 9571, 2958, 9583) && enabledLobby(config.bossEnabledRoyalTitans())) ||
			(region == 11669 && enabledAlways(config.bossEnabledRoyalTitans()));
	}

	private boolean isTzKalZuk(Player player, int region)
	{
		return (isInArea(player, 2482, 5124, 2509, 5090) && enabledLobby(config.bossEnabledTzKalZuk()));
	}

	private boolean isTzTokJad(Player player, int region)
	{
		return (isInArea(player, 2457, 5162, 2417, 5183) && enabledLobby(config.bossEnabledTzTokJad()));
	}

	private boolean isVardorvis(Player player, int region)
	{
		return false;
	}

	private boolean isVorkath(Player player, int region)
	{
		return
			(region == 9023 && !isInArea(player, 2261, 4054, 2283, 4076) && enabledLobby(config.bossEnabledVorkath()));
	}

	private boolean isWintertodt(Player player, int region)
	{
		return
			(region == 6461 && enabledLobby(config.bossEnabledWintertodt())) ||
			(region == 6462 && enabledAlways(config.bossEnabledWintertodt()));
	}

	private boolean isZalcano(Player player, int region)
	{
		return
			(isInArea(player, 3028, 6063, 3039, 6071) && enabledLobby(config.bossEnabledZalcano())) ||
			(region == 12126 && enabledAlways(config.bossEnabledZalcano()));
	}

	private boolean isZulrah(Player player, int region)
	{
		return region == 8751 && enabledLobby(config.bossEnabledZulrah());
	}
}
