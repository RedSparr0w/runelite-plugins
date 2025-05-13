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
import com.killcountviewer.KillCountViewerConfig.HighlightAlwaysSetting;
import com.killcountviewer.KillCountViewerConfig.HighlightRaidSetting;
import com.killcountviewer.KillCountViewerConfig.HighlightSetting;
import java.awt.*;
import java.util.function.BiConsumer;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class KillCountViewerService
{
	private final Client client;
	private final KillCountViewerConfig config;

	// The current boss zone the player is in
	// This is used to check if the player is in a boss zone and only check against that boss
	// This is set to null when the player leaves the boss zone and will check against all zones again
	// This is used to prevent checking against all zones every tick
	HiscoreSkill currentBoss = null;

	@Inject
	private KillCountViewerService(Client client, KillCountViewerConfig config, PartyService partyService)
	{
		this.config = config;
		this.client = client;
	}

	void forEachPlayer(final BiConsumer<Player, Color> consumer)
	{

		if (currentBoss == null)	return;

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

	boolean enabledRaidNormal(HighlightRaidSetting setting)
	{
		return setting == HighlightRaidSetting.NORMAL;
	}

	boolean enabledRaidExpert(HighlightRaidSetting setting)
	{
		return setting == HighlightRaidSetting.HARD;
	}

	boolean checkCurrentBoss(HiscoreSkill boss)
	{
		return currentBoss == null || currentBoss == boss;
	}

	// Check if the player is in a boss zone and return the corresponding HiscoreSkill
	HiscoreSkill getBossZone()
	{
		Player player = client.getLocalPlayer();
		if (player == null || player.getWorldLocation() == null) return null;

		int region = WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID();
		// System.out.println("Region ID: " + region + " | Current Boss: " + currentBoss);
		
		if (checkCurrentBoss(HiscoreSkill.SOUL_WARS_ZEAL) && isSoulWarsZeal(player, region)) return currentBoss = HiscoreSkill.SOUL_WARS_ZEAL;
		if (checkCurrentBoss(HiscoreSkill.LAST_MAN_STANDING) && isLastManStanding(player, region)) return currentBoss = HiscoreSkill.LAST_MAN_STANDING;
		if (checkCurrentBoss(HiscoreSkill.RIFTS_CLOSED) && isRiftsClosed(player, region)) return currentBoss = HiscoreSkill.RIFTS_CLOSED;
		if (checkCurrentBoss(HiscoreSkill.ABYSSAL_SIRE) && isAbyssalSire(player, region)) return currentBoss = HiscoreSkill.ABYSSAL_SIRE;
		if (checkCurrentBoss(HiscoreSkill.ALCHEMICAL_HYDRA) && isAlchemicalHydra(player, region)) return currentBoss = HiscoreSkill.ALCHEMICAL_HYDRA;
		if (checkCurrentBoss(HiscoreSkill.AMOXLIATL) && isAmoxliatl(player, region)) return currentBoss = HiscoreSkill.AMOXLIATL;
		if (checkCurrentBoss(HiscoreSkill.ARAXXOR) && isAraxxor(player, region)) return currentBoss = HiscoreSkill.ARAXXOR;
		if (checkCurrentBoss(HiscoreSkill.ARTIO) && isArtio(player, region)) return currentBoss = HiscoreSkill.ARTIO;
		if (checkCurrentBoss(HiscoreSkill.BARROWS_CHESTS) && isBarrowsChests(player, region)) return currentBoss = HiscoreSkill.BARROWS_CHESTS;
		if (checkCurrentBoss(HiscoreSkill.BRYOPHYTA) && isBryophyta(player, region)) return currentBoss = HiscoreSkill.BRYOPHYTA;
		if (checkCurrentBoss(HiscoreSkill.CALLISTO) && isCallisto(player, region)) return currentBoss = HiscoreSkill.CALLISTO;
		if (checkCurrentBoss(HiscoreSkill.CALVARION) && isCalvarion(player, region)) return currentBoss = HiscoreSkill.CALVARION;
		if (checkCurrentBoss(HiscoreSkill.CERBERUS) && isCerberus(player, region)) return currentBoss = HiscoreSkill.CERBERUS;
		if (checkCurrentBoss(HiscoreSkill.CHAMBERS_OF_XERIC) && isChambersOfXeric(player, region)) return currentBoss = HiscoreSkill.CHAMBERS_OF_XERIC;
		if (checkCurrentBoss(HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE) && isChambersOfXericChallengeMode(player, region)) return currentBoss = HiscoreSkill.CHAMBERS_OF_XERIC_CHALLENGE_MODE;
		if (checkCurrentBoss(HiscoreSkill.CHAOS_ELEMENTAL) && isChaosElemental(player, region)) return currentBoss = HiscoreSkill.CHAOS_ELEMENTAL;
		if (checkCurrentBoss(HiscoreSkill.CHAOS_FANATIC) && isChaosFanatic(player, region)) return currentBoss = HiscoreSkill.CHAOS_FANATIC;
		if (checkCurrentBoss(HiscoreSkill.COMMANDER_ZILYANA) && isCommanderZilyana(player, region)) return currentBoss = HiscoreSkill.COMMANDER_ZILYANA;
		if (checkCurrentBoss(HiscoreSkill.CORPOREAL_BEAST) && isCorporealBeast(player, region)) return currentBoss = HiscoreSkill.CORPOREAL_BEAST;
		if (checkCurrentBoss(HiscoreSkill.CRAZY_ARCHAEOLOGIST) && isCrazyArchaeologist(player, region)) return currentBoss = HiscoreSkill.CRAZY_ARCHAEOLOGIST;
		if (checkCurrentBoss(HiscoreSkill.DAGANNOTH_PRIME) && isDagannothPrime(player, region)) return currentBoss = HiscoreSkill.DAGANNOTH_PRIME;
		if (checkCurrentBoss(HiscoreSkill.DAGANNOTH_REX) && isDagannothRex(player, region)) return currentBoss = HiscoreSkill.DAGANNOTH_REX;
		if (checkCurrentBoss(HiscoreSkill.DAGANNOTH_SUPREME) && isDagannothSupreme(player, region)) return currentBoss = HiscoreSkill.DAGANNOTH_SUPREME;
		if (checkCurrentBoss(HiscoreSkill.DERANGED_ARCHAEOLOGIST) && isDerangedArchaeologist(player, region)) return currentBoss = HiscoreSkill.DERANGED_ARCHAEOLOGIST;
		if (checkCurrentBoss(HiscoreSkill.DUKE_SUCELLUS) && isDukeSucellus(player, region)) return currentBoss = HiscoreSkill.DUKE_SUCELLUS;
		if (checkCurrentBoss(HiscoreSkill.GENERAL_GRAARDOR) && isGeneralGraardor(player, region)) return currentBoss = HiscoreSkill.GENERAL_GRAARDOR;
		if (checkCurrentBoss(HiscoreSkill.GIANT_MOLE) && isGiantMole(player, region)) return currentBoss = HiscoreSkill.GIANT_MOLE;
		if (checkCurrentBoss(HiscoreSkill.GROTESQUE_GUARDIANS) && isGrotesqueGuardians(player, region)) return currentBoss = HiscoreSkill.GROTESQUE_GUARDIANS;
		if (checkCurrentBoss(HiscoreSkill.HESPORI) && isHespori(player, region)) return currentBoss = HiscoreSkill.HESPORI;
		if (checkCurrentBoss(HiscoreSkill.KALPHITE_QUEEN) && isKalphiteQueen(player, region)) return currentBoss = HiscoreSkill.KALPHITE_QUEEN;
		if (checkCurrentBoss(HiscoreSkill.KING_BLACK_DRAGON) && isKingBlackDragon(player, region)) return currentBoss = HiscoreSkill.KING_BLACK_DRAGON;
		if (checkCurrentBoss(HiscoreSkill.KRAKEN) && isKraken(player, region)) return currentBoss = HiscoreSkill.KRAKEN;
		if (checkCurrentBoss(HiscoreSkill.KREEARRA) && isKreeArra(player, region)) return currentBoss = HiscoreSkill.KREEARRA;
		if (checkCurrentBoss(HiscoreSkill.KRIL_TSUTSAROTH) && isKrilTsutsaroth(player, region)) return currentBoss = HiscoreSkill.KRIL_TSUTSAROTH;
		if (checkCurrentBoss(HiscoreSkill.LUNAR_CHESTS) && isLunarChests(player, region)) return currentBoss = HiscoreSkill.LUNAR_CHESTS;
		if (checkCurrentBoss(HiscoreSkill.MIMIC) && isMimic(player, region)) return currentBoss = HiscoreSkill.MIMIC;
		if (checkCurrentBoss(HiscoreSkill.NEX) && isNex(player, region)) return currentBoss = HiscoreSkill.NEX;
		if (checkCurrentBoss(HiscoreSkill.NIGHTMARE) && isNightmare(player, region)) return currentBoss = HiscoreSkill.NIGHTMARE;
		if (checkCurrentBoss(HiscoreSkill.PHOSANIS_NIGHTMARE) && isPhosanisNightmare(player, region)) return currentBoss = HiscoreSkill.PHOSANIS_NIGHTMARE;
		if (checkCurrentBoss(HiscoreSkill.OBOR) && isObor(player, region)) return currentBoss = HiscoreSkill.OBOR;
		if (checkCurrentBoss(HiscoreSkill.PHANTOM_MUSPAH) && isPhantomMuspah(player, region)) return currentBoss = HiscoreSkill.PHANTOM_MUSPAH;
		if (checkCurrentBoss(HiscoreSkill.SARACHNIS) && isSarachnis(player, region)) return currentBoss = HiscoreSkill.SARACHNIS;
		if (checkCurrentBoss(HiscoreSkill.SCORPIA) && isScorpia(player, region)) return currentBoss = HiscoreSkill.SCORPIA;
		if (checkCurrentBoss(HiscoreSkill.SCURRIUS) && isScurrius(player, region)) return currentBoss = HiscoreSkill.SCURRIUS;
		if (checkCurrentBoss(HiscoreSkill.SKOTIZO) && isSkotizo(player, region)) return currentBoss = HiscoreSkill.SKOTIZO;
		if (checkCurrentBoss(HiscoreSkill.SOL_HEREDIT) && isSolHeredit(player, region)) return currentBoss = HiscoreSkill.SOL_HEREDIT;
		if (checkCurrentBoss(HiscoreSkill.SPINDEL) && isSpindel(player, region)) return currentBoss = HiscoreSkill.SPINDEL;
		if (checkCurrentBoss(HiscoreSkill.TEMPOROSS) && isTempoross(player, region)) return currentBoss = HiscoreSkill.TEMPOROSS;
		if (checkCurrentBoss(HiscoreSkill.THE_HUEYCOATL) && isTheHueycoatl(player, region)) return currentBoss = HiscoreSkill.THE_HUEYCOATL;
		if (checkCurrentBoss(HiscoreSkill.THE_LEVIATHAN) && isTheLeviathan(player, region)) return currentBoss = HiscoreSkill.THE_LEVIATHAN;
		if (checkCurrentBoss(HiscoreSkill.THE_WHISPERER) && isTheWhisperer(player, region)) return currentBoss = HiscoreSkill.THE_WHISPERER;
		if (checkCurrentBoss(HiscoreSkill.THEATRE_OF_BLOOD) && isTheatreOfBlood(player, region)) return currentBoss = HiscoreSkill.THEATRE_OF_BLOOD;
		if (checkCurrentBoss(HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE) && isTheatreOfBloodHardMode(player, region)) return currentBoss = HiscoreSkill.THEATRE_OF_BLOOD_HARD_MODE;
		if (checkCurrentBoss(HiscoreSkill.THERMONUCLEAR_SMOKE_DEVIL) && isThermonuclearSmokeDevil(player, region)) return currentBoss = HiscoreSkill.THERMONUCLEAR_SMOKE_DEVIL;
		if (checkCurrentBoss(HiscoreSkill.TOMBS_OF_AMASCUT) && isTombsOfAmascut(player, region)) return currentBoss = HiscoreSkill.TOMBS_OF_AMASCUT;
		if (checkCurrentBoss(HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT) && isTombsOfAmascutExpert(player, region)) return currentBoss = HiscoreSkill.TOMBS_OF_AMASCUT_EXPERT;
		if (checkCurrentBoss(HiscoreSkill.VENENATIS) && isVenenatis(player, region)) return currentBoss = HiscoreSkill.VENENATIS;
		if (checkCurrentBoss(HiscoreSkill.VETION) && isVetion(player, region)) return currentBoss = HiscoreSkill.VETION;
		if (checkCurrentBoss(HiscoreSkill.THE_CORRUPTED_GAUNTLET) && isCorruptedGauntlet(player, region)) return currentBoss = HiscoreSkill.THE_CORRUPTED_GAUNTLET;
		if (checkCurrentBoss(HiscoreSkill.THE_ROYAL_TITANS) && isRoyalTitans(player, region)) return currentBoss = HiscoreSkill.THE_ROYAL_TITANS;
		if (checkCurrentBoss(HiscoreSkill.TZKAL_ZUK) && isTzKalZuk(player, region)) return currentBoss = HiscoreSkill.TZKAL_ZUK;
		if (checkCurrentBoss(HiscoreSkill.TZTOK_JAD) && isTzTokJad(player, region)) return currentBoss = HiscoreSkill.TZTOK_JAD;
		if (checkCurrentBoss(HiscoreSkill.VARDORVIS) && isVardorvis(player, region)) return currentBoss = HiscoreSkill.VARDORVIS;
		if (checkCurrentBoss(HiscoreSkill.VORKATH) && isVorkath(player, region)) return currentBoss = HiscoreSkill.VORKATH;
		if (checkCurrentBoss(HiscoreSkill.WINTERTODT) && isWintertodt(player, region)) return currentBoss = HiscoreSkill.WINTERTODT;
		if (checkCurrentBoss(HiscoreSkill.ZALCANO) && isZalcano(player, region)) return currentBoss = HiscoreSkill.ZALCANO;
		if (checkCurrentBoss(HiscoreSkill.ZULRAH) && isZulrah(player, region)) return currentBoss = HiscoreSkill.ZULRAH;

		return currentBoss = null;
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
		return
			(isInArea(player, 3601, 9483, 3629, 9458) && enabledLobby(config.bossEnabledRiftsClosed())) ||
			(region == 14484 && enabledAlways(config.bossEnabledRiftsClosed()));
	}

	private boolean isAbyssalSire(Player player, int region)
	{
		return
			(region == 12106 && enabledLobby(config.bossEnabledAbyssalSire())) ||
			((region == 11851 || region == 11850 || region == 12363 || region == 12362) && enabledAlways(config.bossEnabledAbyssalSire()));
	}

	private boolean isAlchemicalHydra(Player player, int region)
	{
		return region == 5536 && enabledLobby(config.bossEnabledAlchemicalHydra());
	}

	private boolean isAmoxliatl(Player player, int region)
	{
		return isInArea(player, 1620, 9621, 1601, 9644) && enabledLobby(config.bossEnabledAmoxliatl());
	}

	private boolean isAraxxor(Player player, int region)
	{
		return region == 14745 && enabledLobby(config.bossEnabledAraxxor());
	}

	private boolean isArtio(Player player, int region)
	{
		return
			(isInArea(player, 3108, 3679, 3123, 3665) && enabledLobby(config.bossEnabledArtio())) ||
			(region == 7092 && enabledAlways(config.bossEnabledArtio()));
	}

	private boolean isBarrowsChests(Player player, int region)
	{
		return
			(region == 14131 && enabledLobby(config.bossEnabledBarrowsChests())) ||
			(region == 14231 && enabledAlways(config.bossEnabledBarrowsChests())) ;
	}

	private boolean isBryophyta(Player player, int region)
	{
		return isInArea(player, 3166, 9902, 9180, 9894) && enabledLobby(config.bossEnabledBryophyta());
	}

	private boolean isCallisto(Player player, int region)
	{
		return 
			(isInArea(player, 3282, 3857, 3303, 3840) && enabledLobby(config.bossEnabledCallisto())) ||
			(region == 13473 && enabledAlways(config.bossEnabledCallisto()));
	}

	private boolean isCalvarion(Player player, int region)
	{
		return
			(isInArea(player, 3176, 3687, 3183, 3678) && enabledLobby(config.bossEnabledCalvarion())) ||
			(region == 7604 && enabledAlways(config.bossEnabledCalvarion()));
	}

	private boolean isCerberus(Player player, int region)
	{
		return region == 5139 && enabledLobby(config.bossEnabledCerberus());
	}

	private boolean isChambersOfXeric(Player player, int region)
	{
		return (isInArea(player, 1221, 3578, 1265, 3551) && enabledRaidNormal(config.bossEnabledChambersOfXeric()));
	}

	private boolean isChambersOfXericChallengeMode(Player player, int region)
	{
		return (isInArea(player, 1221, 3578, 1265, 3551) && enabledRaidExpert(config.bossEnabledChambersOfXeric()));
	}

	private boolean isChaosElemental(Player player, int region)
	{
		return isInArea(player, 3274, 3933, 3197, 3904) && enabledLobby(config.bossEnabledChaosElemental());
	}

	private boolean isChaosFanatic(Player player, int region)
	{
		return isInArea(player, 2960, 3858, 2986, 3831) && enabledLobby(config.bossEnabledChaosFanatic());
	}

	private boolean isCommanderZilyana(Player player, int region)
	{
		return (isInArea(player, 2909, 5257, 2932, 5276) && enabledLobby(config.bossEnabledCommanderZilyana())) ||
			(region == 11601 && enabledAlways(config.bossEnabledCommanderZilyana()));
	}

	private boolean isCorporealBeast(Player player, int region)
	{
		return 
			(isInArea(player, 2971, 4260, 2962, 4250, 2) && enabledLobby(config.bossEnabledCorporealBeast())) ||
			(region == 11842 && enabledAlways(config.bossEnabledCorporealBeast()));
	}

	private boolean isCrazyArchaeologist(Player player, int region)
	{
		return isInArea(player, 2967, 3719, 2995, 3693) && enabledLobby(config.bossEnabledCrazyArchaeologist());
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
		return isInArea(player, 3657, 3735, 3700, 3712) && enabledLobby(config.bossEnabledDerangedArchaeologist());
	}

	private boolean isDukeSucellus(Player player, int region)
	{
		return false;
	}

	private boolean isGeneralGraardor(Player player, int region)
	{
		return (isInArea(player, 2862, 5369, 2840, 5338, 2) && enabledLobby(config.bossEnabledGeneralGraardor())) ||
			(region == 11347 && enabledAlways(config.bossEnabledGeneralGraardor()));
	}

	private boolean isGiantMole(Player player, int region)
	{
		return
			(isInArea(player, 2983, 3389, 3006, 3380) && enabledLobby(config.bossEnabledGiantMole())) ||
			((region == 6993 || region == 6992) && enabledAlways(config.bossEnabledGiantMole()));
	}

	private boolean isGrotesqueGuardians(Player player, int region)
	{
		return (isInArea(player, 3429, 3545, 3405, 3531, 2) && enabledLobby(config.bossEnabledGrotesqueGuardians()));
	}

	private boolean isHespori(Player player, int region)
	{
		return (isInArea(player, 1242, 3739, 1223, 3720) || region == 5021) && enabledLobby(config.bossEnabledHespori());
	}

	private boolean isKalphiteQueen(Player player, int region)
	{
		return ((region == 12692 || region == 12948) && enabledLobby(config.bossEnabledKalphiteQueen())) ||
			((region == 12691 || region == 12947) && enabledAlways(config.bossEnabledKalphiteQueen()));
	}

	private boolean isKingBlackDragon(Player player, int region)
	{
		return (region == 12192 && enabledLobby(config.bossEnabledKingBlackDragon())) ||
			(region == 9033 && enabledAlways(config.bossEnabledKingBlackDragon()));
	}

	private boolean isKraken(Player player, int region)
	{
		return
			(isInArea(player, 2270, 10019, 2288, 10008) && enabledLobby(config.bossEnabledKraken())) ||
			(isInArea(player, 2290, 10044, 2269, 10022) && enabledAlways(config.bossEnabledKraken()));
	}

	private boolean isKreeArra(Player player, int region)
	{
		return (isInArea(player, 2822, 5294, 2854, 5279, 2) && enabledLobby(config.bossEnabledKreeArra())) ||
			(region == 11346 && enabledAlways(config.bossEnabledKreeArra()));
	}

	private boolean isKrilTsutsaroth(Player player, int region)
	{
		return (isInArea(player, 2938, 5333, 2909, 5366, 2) && enabledLobby(config.bossEnabledKrilTsutsaroth())) ||
			(region == 11603 && enabledAlways(config.bossEnabledKrilTsutsaroth()));
	}

	private boolean isLunarChests(Player player, int region)
	{
		return
			((region == 5527 || region == 6039 || region == 6037 || region == 5525 || region == 5782) && enabledLobby(config.bossEnabledLunarChests())) ||
			((region == 6038 || region == 5783 || region == 5526) && enabledAlways(config.bossEnabledLunarChests()));
	}

	private boolean isMimic(Player player, int region)
	{
		return ((isInArea(player, 1633, 3582, 1657, 3561) || isInArea(player, 1633, 3582, 1657, 3561, 1)) && enabledLobby(config.bossEnabledMimic()));
	}

	private boolean isNex(Player player, int region)
	{
		return false;
	}

	private boolean isNightmare(Player player, int region)
	{
		return region == 15256 && enabledLobby(config.bossEnabledNightmare());
	}

	private boolean isPhosanisNightmare(Player player, int region)
	{
		return false;
	}

	private boolean isObor(Player player, int region)
	{
		return isInArea(player, 3094, 9822, 3105, 9841) && enabledLobby(config.bossEnabledObor());
	}

	private boolean isPhantomMuspah(Player player, int region)
	{
		return isInArea(player, 2907, 10313, 2918, 10324) && enabledLobby(config.bossEnabledPhantomMuspah());
	}

	private boolean isSarachnis(Player player, int region)
	{
		return
			(isInArea(player, 1839, 9912, 1849, 9919) && enabledLobby(config.bossEnabledSarachnis())) ||
			(region == 7322 && enabledAlways(config.bossEnabledSarachnis()));
	}

	private boolean isScorpia(Player player, int region)
	{
		return
			(isInArea(player, 3260, 3960, 3216, 3934) && enabledLobby(config.bossEnabledScorpia())) ||
			(region == 12961 && enabledAlways(config.bossEnabledScorpia()));
	}

	private boolean isScurrius(Player player, int region)
	{
		return
			(isInArea(player, 3264, 9878, 3282, 9864) && enabledLobby(config.bossEnabledScurrius())) ||
			(isInArea(player, 3308, 9858, 3289, 9877) && enabledAlways(config.bossEnabledScurrius()));
	}

	private boolean isSkotizo(Player player, int region)
	{
		return isInArea(player, 1667, 10051, 1660, 10044) && enabledLobby(config.bossEnabledSkotizo());
	}

	private boolean isSolHeredit(Player player, int region)
	{
		return region == 7316 && enabledLobby(config.bossEnabledSolHeredit());
	}

	private boolean isSpindel(Player player, int region)
	{
		return (isInArea(player, 3177, 3750, 3189, 3730) && enabledLobby(config.bossEnabledSpindel())) ||
			(region == 6580 && enabledAlways(config.bossEnabledSpindel()));
	}

	private boolean isTempoross(Player player, int region)
	{
		return ((region == 12588 || region == 12332) && enabledLobby(config.bossEnabledTempoross())) ||
			(region == 12078 && enabledAlways(config.bossEnabledTempoross()));
	}

	private boolean isTheHueycoatl(Player player, int region)
	{
		return
			(isInArea(player, 1521, 3296, 1538, 3285) && enabledLobby(config.bossEnabledTheHueycoatl())) ||
			(isInArea(player, 1501, 3296, 1534, 3269) && enabledAlways(config.bossEnabledTheHueycoatl()));
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
		return (isInArea(player, 3640, 3235, 3684, 3203) && enabledRaidNormal(config.bossEnabledTheatreOfBlood()));
	}

	private boolean isTheatreOfBloodHardMode(Player player, int region)
	{
		return (isInArea(player, 3640, 3235, 3684, 3203) && enabledRaidExpert(config.bossEnabledTheatreOfBlood()));
	}

	private boolean isThermonuclearSmokeDevil(Player player, int region)
	{
		return false;
	}

	private boolean isTombsOfAmascut(Player player, int region)
	{
		return (region == 13454 && enabledRaidNormal(config.bossEnabledTombsOfAmascut()));
	}

	private boolean isTombsOfAmascutExpert(Player player, int region)
	{
		return (region == 13454 && enabledRaidExpert(config.bossEnabledTombsOfAmascut()));
	}

	private boolean isVenenatis(Player player, int region)
	{
		return (isInArea(player, 3309, 3808, 3327, 3782) && enabledLobby(config.bossEnabledVenenatis())) ||
			(region == 13727 && enabledAlways(config.bossEnabledVenenatis()));
	}

	private boolean isVetion(Player player, int region)
	{
		return
			(isInArea(player, 3212, 3798, 3231, 3778) && enabledLobby(config.bossEnabledVetion())) ||
			(region == 13215 && enabledAlways(config.bossEnabledVetion()));
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
		return isInArea(player, 2482, 5124, 2509, 5090) && enabledLobby(config.bossEnabledTzKalZuk());
	}

	private boolean isTzTokJad(Player player, int region)
	{
		return isInArea(player, 2457, 5162, 2417, 5183) && enabledLobby(config.bossEnabledTzTokJad());
	}

	private boolean isVardorvis(Player player, int region)
	{
		return false;
	}

	private boolean isVorkath(Player player, int region)
	{
		return(region == 9023 && !isInArea(player, 2261, 4054, 2283, 4076) && enabledLobby(config.bossEnabledVorkath()));
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
