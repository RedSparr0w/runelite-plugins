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

	HiscoreSkill CurrentBoss = null;

	@Inject
	private KillcountViewerService(Client client, KillcountViewerConfig config, PartyService partyService)
	{
		this.config = config;
		this.client = client;
	}

	void forEachPlayer(final BiConsumer<Player, Color> consumer)
	{

		if (CurrentBoss == null)	return;

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

	boolean checkCurrentBoss(HiscoreSkill boss)
	{
		return CurrentBoss == null || CurrentBoss == boss;
	}

	// Check if the player is in a boss zone and return the corresponding HiscoreSkill
	HiscoreSkill getBossZone()
	{
		Player player = client.getLocalPlayer();
		if (player == null || player.getWorldLocation() == null) return null;

		int region = WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID();
		// System.out.println("Region ID: " + region + " | Current Boss: " + CurrentBoss);
		
		if (checkCurrentBoss(HiscoreSkill.SOUL_WARS_ZEAL) && isSoulWarsZeal(player, region)) return CurrentBoss = HiscoreSkill.SOUL_WARS_ZEAL;
		if (checkCurrentBoss(HiscoreSkill.LAST_MAN_STANDING) && isLastManStanding(player, region)) return CurrentBoss = HiscoreSkill.LAST_MAN_STANDING;
		if (checkCurrentBoss(HiscoreSkill.RIFTS_CLOSED) && isRiftsClosed(player, region)) return CurrentBoss = HiscoreSkill.RIFTS_CLOSED;
		if (checkCurrentBoss(HiscoreSkill.ABYSSAL_SIRE) && isAbyssalSire(player, region)) return CurrentBoss = HiscoreSkill.ABYSSAL_SIRE;
		if (checkCurrentBoss(HiscoreSkill.ALCHEMICAL_HYDRA) && isAlchemicalHydra(player, region)) return CurrentBoss = HiscoreSkill.ALCHEMICAL_HYDRA;
		if (checkCurrentBoss(HiscoreSkill.AMOXLIATL) && isAmoxliatl(player, region)) return CurrentBoss = HiscoreSkill.AMOXLIATL;
		if (checkCurrentBoss(HiscoreSkill.ARAXXOR) && isAraxxor(player, region)) return CurrentBoss = HiscoreSkill.ARAXXOR;
		if (checkCurrentBoss(HiscoreSkill.ARTIO) && isArtio(player, region)) return CurrentBoss = HiscoreSkill.ARTIO;
		if (checkCurrentBoss(HiscoreSkill.BARROWS_CHESTS) && isBarrowsChests(player, region)) return CurrentBoss = HiscoreSkill.BARROWS_CHESTS;
		if (checkCurrentBoss(HiscoreSkill.BRYOPHYTA) && isBryophyta(player, region)) return CurrentBoss = HiscoreSkill.BRYOPHYTA;
		if (checkCurrentBoss(HiscoreSkill.CALLISTO) && isCallisto(player, region)) return CurrentBoss = HiscoreSkill.CALLISTO;
		if (checkCurrentBoss(HiscoreSkill.CALVARION) && isCalvarion(player, region)) return CurrentBoss = HiscoreSkill.CALVARION;
		if (checkCurrentBoss(HiscoreSkill.CERBERUS) && isCerberus(player, region)) return CurrentBoss = HiscoreSkill.CERBERUS;
		if (checkCurrentBoss(HiscoreSkill.CHAMBERS_OF_XERIC) && isChambersOfXeric(player, region)) return CurrentBoss = HiscoreSkill.CHAMBERS_OF_XERIC;
		if (checkCurrentBoss(HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE) && isChambersOfXericChallengeMode(player, region)) return CurrentBoss = HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE;
		if (checkCurrentBoss(HiscoreSkill.CHAOS_ELEMENTAL) && isChaosElemental(player, region)) return CurrentBoss = HiscoreSkill.CHAOS_ELEMENTAL;
		if (checkCurrentBoss(HiscoreSkill.CHAOS_FANATIC) && isChaosFanatic(player, region)) return CurrentBoss = HiscoreSkill.CHAOS_FANATIC;
		if (checkCurrentBoss(HiscoreSkill.COMMANDER_ZILYANA) && isCommanderZilyana(player, region)) return CurrentBoss = HiscoreSkill.COMMANDER_ZILYANA;
		if (checkCurrentBoss(HiscoreSkill.CORPOREAL_BEAST) && isCorporealBeast(player, region)) return CurrentBoss = HiscoreSkill.CORPOREAL_BEAST;
		if (checkCurrentBoss(HiscoreSkill.CRAZY_ARCHAEOLOGIST) && isCrazyArchaeologist(player, region)) return CurrentBoss = HiscoreSkill.CRAZY_ARCHAEOLOGIST;
		if (checkCurrentBoss(HiscoreSkill.DAGANNOTH_PRIME) && isDagannothPrime(player, region)) return CurrentBoss = HiscoreSkill.DAGANNOTH_PRIME;
		if (checkCurrentBoss(HiscoreSkill.DAGANNOTH_REX) && isDagannothRex(player, region)) return CurrentBoss = HiscoreSkill.DAGANNOTH_REX;
		if (checkCurrentBoss(HiscoreSkill.DAGANNOTH_SUPREME) && isDagannothSupreme(player, region)) return CurrentBoss = HiscoreSkill.DAGANNOTH_SUPREME;
		if (checkCurrentBoss(HiscoreSkill.DERANGED_ARCHAEOLOGIST) && isDerangedArchaeologist(player, region)) return CurrentBoss = HiscoreSkill.DERANGED_ARCHAEOLOGIST;
		if (checkCurrentBoss(HiscoreSkill.DUKE_SUCELLUS) && isDukeSucellus(player, region)) return CurrentBoss = HiscoreSkill.DUKE_SUCELLUS;
		if (checkCurrentBoss(HiscoreSkill.GENERAL_GRAARDOR) && isGeneralGraardor(player, region)) return CurrentBoss = HiscoreSkill.GENERAL_GRAARDOR;
		if (checkCurrentBoss(HiscoreSkill.GIANT_MOLE) && isGiantMole(player, region)) return CurrentBoss = HiscoreSkill.GIANT_MOLE;
		if (checkCurrentBoss(HiscoreSkill.GROTESQUE_GUARDIANS) && isGrotesqueGuardians(player, region)) return CurrentBoss = HiscoreSkill.GROTESQUE_GUARDIANS;
		if (checkCurrentBoss(HiscoreSkill.HESPORI) && isHespori(player, region)) return CurrentBoss = HiscoreSkill.HESPORI;
		if (checkCurrentBoss(HiscoreSkill.KALPHITE_QUEEN) && isKalphiteQueen(player, region)) return CurrentBoss = HiscoreSkill.KALPHITE_QUEEN;
		if (checkCurrentBoss(HiscoreSkill.KING_BLACK_DRAGON) && isKingBlackDragon(player, region)) return CurrentBoss = HiscoreSkill.KING_BLACK_DRAGON;
		if (checkCurrentBoss(HiscoreSkill.KRAKEN) && isKraken(player, region)) return CurrentBoss = HiscoreSkill.KRAKEN;
		if (checkCurrentBoss(HiscoreSkill.KREEARRA) && isKreeArra(player, region)) return CurrentBoss = HiscoreSkill.KREEARRA;
		if (checkCurrentBoss(HiscoreSkill.KRIL_TSUTSAROTH) && isKrilTsutsaroth(player, region)) return CurrentBoss = HiscoreSkill.KRIL_TSUTSAROTH;
		if (checkCurrentBoss(HiscoreSkill.LUNAR_CHESTS) && isLunarChests(player, region)) return CurrentBoss = HiscoreSkill.LUNAR_CHESTS;
		if (checkCurrentBoss(HiscoreSkill.MIMIC) && isMimic(player, region)) return CurrentBoss = HiscoreSkill.MIMIC;
		if (checkCurrentBoss(HiscoreSkill.NEX) && isNex(player, region)) return CurrentBoss = HiscoreSkill.NEX;
		if (checkCurrentBoss(HiscoreSkill.NIGHTMARE) && isNightmare(player, region)) return CurrentBoss = HiscoreSkill.NIGHTMARE;
		if (checkCurrentBoss(HiscoreSkill.PHOSANIS_NIGHTMARE) && isPhosanisNightmare(player, region)) return CurrentBoss = HiscoreSkill.PHOSANIS_NIGHTMARE;
		if (checkCurrentBoss(HiscoreSkill.OBOR) && isObor(player, region)) return CurrentBoss = HiscoreSkill.OBOR;
		if (checkCurrentBoss(HiscoreSkill.PHANTOM_MUSPAH) && isPhantomMuspah(player, region)) return CurrentBoss = HiscoreSkill.PHANTOM_MUSPAH;
		if (checkCurrentBoss(HiscoreSkill.SARACHNIS) && isSarachnis(player, region)) return CurrentBoss = HiscoreSkill.SARACHNIS;
		if (checkCurrentBoss(HiscoreSkill.SCORPIA) && isScorpia(player, region)) return CurrentBoss = HiscoreSkill.SCORPIA;
		if (checkCurrentBoss(HiscoreSkill.SCURRIUS) && isScurrius(player, region)) return CurrentBoss = HiscoreSkill.SCURRIUS;
		if (checkCurrentBoss(HiscoreSkill.SKOTIZO) && isSkotizo(player, region)) return CurrentBoss = HiscoreSkill.SKOTIZO;
		if (checkCurrentBoss(HiscoreSkill.SOL_HEREDIT) && isSolHeredit(player, region)) return CurrentBoss = HiscoreSkill.SOL_HEREDIT;
		if (checkCurrentBoss(HiscoreSkill.TEMPOROSS) && isTempoross(player, region)) return CurrentBoss = HiscoreSkill.TEMPOROSS;
		if (checkCurrentBoss(HiscoreSkill.THE_HUEYCOATL) && isTheHueycoatl(player, region)) return CurrentBoss = HiscoreSkill.THE_HUEYCOATL;
		if (checkCurrentBoss(HiscoreSkill.THE_LEVIATHAN) && isTheLeviathan(player, region)) return CurrentBoss = HiscoreSkill.THE_LEVIATHAN;
		if (checkCurrentBoss(HiscoreSkill.THE_WHISPERER) && isTheWhisperer(player, region)) return CurrentBoss = HiscoreSkill.THE_WHISPERER;
		if (checkCurrentBoss(HiscoreSkill.THEATRE_OF_BLOOD) && isTheatreOfBlood(player, region)) return CurrentBoss = HiscoreSkill.THEATRE_OF_BLOOD;
		if (checkCurrentBoss(HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE) && isTheatreOfBloodHardMode(player, region)) return CurrentBoss = HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE;
		if (checkCurrentBoss(HiscoreSkill.THERMONUCLEAR_SMOKE_DEVIL) && isThermonuclearSmokeDevil(player, region)) return CurrentBoss = HiscoreSkill.THERMONUCLEAR_SMOKE_DEVIL;
		if (checkCurrentBoss(HiscoreSkill.TOMBS_OF_AMASCUT) && isTombsOfAmascut(player, region)) return CurrentBoss = HiscoreSkill.TOMBS_OF_AMASCUT;
		if (checkCurrentBoss(HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT) && isTombsOfAmascutExpert(player, region)) return CurrentBoss = HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT;
		if (checkCurrentBoss(HiscoreSkill.VENENATIS) && isVenenatis(player, region)) return CurrentBoss = HiscoreSkill.VENENATIS;
		if (checkCurrentBoss(HiscoreSkill.VETION) && isVetion(player, region)) return CurrentBoss = HiscoreSkill.VETION;
		if (checkCurrentBoss(HiscoreSkill.THE_CORRUPTED_GAUNTLET) && isCorruptedGauntlet(player, region)) return CurrentBoss = HiscoreSkill.THE_CORRUPTED_GAUNTLET;
		if (checkCurrentBoss(HiscoreSkill.THE_ROYAL_TITANS) && isRoyalTitans(player, region)) return CurrentBoss = HiscoreSkill.THE_ROYAL_TITANS;
		if (checkCurrentBoss(HiscoreSkill.TZKAL_ZUK) && isTzKalZuk(player, region)) return CurrentBoss = HiscoreSkill.TZKAL_ZUK;
		if (checkCurrentBoss(HiscoreSkill.TZTOK_JAD) && isTzTokJad(player, region)) return CurrentBoss = HiscoreSkill.TZTOK_JAD;
		if (checkCurrentBoss(HiscoreSkill.VARDORVIS) && isVardorvis(player, region)) return CurrentBoss = HiscoreSkill.VARDORVIS;
		if (checkCurrentBoss(HiscoreSkill.VORKATH) && isVorkath(player, region)) return CurrentBoss = HiscoreSkill.VORKATH;
		if (checkCurrentBoss(HiscoreSkill.WINTERTODT) && isWintertodt(player, region)) return CurrentBoss = HiscoreSkill.WINTERTODT;
		if (checkCurrentBoss(HiscoreSkill.ZALCANO) && isZalcano(player, region)) return CurrentBoss = HiscoreSkill.ZALCANO;
		if (checkCurrentBoss(HiscoreSkill.ZULRAH) && isZulrah(player, region)) return CurrentBoss = HiscoreSkill.ZULRAH;

		return CurrentBoss = null;
	}
	private boolean isSoulWarsZeal(Player player, int region)
	{
		return
			(region == 8748 && enabledLobby(config.bossEnabledSoulWarsZeal())) ||
			((region == 8493  || region == 8749 || region == 9005) && enabledAlways(config.bossEnabledSoulWarsZeal()));
	}

	private boolean isLastManStanding(Player player, int region)
	{
		return isInArea(player, 3138, 3645, 3145, 3632) && enabledLobby(config.bossEnabledLastManStanding());
	}

	private boolean isRiftsClosed(Player player, int region)
	{
		return isInArea(player, 3601, 9483, 3629, 9458) && enabledLobby(config.bossEnabledRiftsClosed()) ||
			(region == 14484 && enabledAlways(config.bossEnabledRiftsClosed()));
	}

	private boolean isAbyssalSire(Player player, int region)
	{
		return (region == 12106 && enabledLobby(config.bossEnabledAbyssalSire())) ||
			((region == 11851 || region == 11850 || region == 12363 || region == 12362) && enabledAlways(config.bossEnabledAbyssalSire()));
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
