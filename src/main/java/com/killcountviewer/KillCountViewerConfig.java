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
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup(KillCountViewerConfig.GROUP)
public interface KillCountViewerConfig extends Config
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

	@ConfigItem(
		position = 3,
		keyName = "lookupCooldown",
		name = "Killcount lookup cooldown",
		description = "Ticks between each kill count lookup request, to avoid spamming API."
	)
	@Range(
		min = 1,
		max = 20
	)
	default int lookupCooldown()
	{
		return 10;
	}

	@ConfigSection(
		name = "Boss options",
		description = "Toggle which bosses to show kill count for (Always will not show everywhere in game, just while in the boss fight as well as the lobby).",
		position = 10
	)
	String BOSS_SECTION = "BossSection";

	@ConfigSection(
		name = "Raid options",
		description = "Toggle which raids to show kill count for and type of kill count (only visible in lobby).",
		position = 20
	)
	String RAID_SECTION = "RaidSection";

	@ConfigSection(
		name = "Activity options",
		description = "Toggle which activities to show kill count for (Always will not show everywhere in game, just while in the activity as well as the lobby).",
		position = 30
	)
	String ACTIVITY_SECTION = "ActivitySection";

	enum HighlightSetting
	{
		DISABLED,
		LOBBY,
	}

	enum HighlightAlwaysSetting
	{
		DISABLED,
		LOBBY,
		ALWAYS,
	}

	enum HighlightRaidSetting
	{
		DISABLED,
		NORMAL,
		HARD,
	}

	@ConfigItem(
		position = 0,
		keyName = "bossSoulWarsZeal",
		name = "Soul Wars Zeal",
		description = "Configures whether kill count is displayed within Soul Wars Zeal.",
		section = ACTIVITY_SECTION
	)
	default HighlightAlwaysSetting bossEnabledSoulWarsZeal()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossLastManStanding",
		name = "Last Man Standing",
		description = "Configures whether kill count is displayed within Last Man Standing.",
		section = ACTIVITY_SECTION
	)
	default HighlightAlwaysSetting bossEnabledLastManStanding()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossGuardiansoftheRift",
		name = "Guardians of the Rift",
		description = "Configures whether kill count is displayed within Guardian of the Guardians of the Rift.",
		section = ACTIVITY_SECTION
	)
	default HighlightAlwaysSetting bossEnabledRiftsClosed()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossPhosanisNightmare",
		name = "Phosani's Nightmare",
		description = "Configures whether kill count is displayed within Phosani's Nightmare.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledPhosanisNightmare()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossScurrius",
		name = "Scurrius",
		description = "Configures whether kill count is displayed within Scurrius.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledScurrius()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCorruptedGauntlet",
		name = "Corrupted Gauntlet",
		description = "Configures whether kill count is displayed within the Corrupted Gauntlet.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledCorruptedGauntlet()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossRoyalTitans",
		name = "Royal Titans",
		description = "Configures whether kill count is displayed within Royal Titans.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledRoyalTitans()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTzKalZuk",
		name = "TzKal-Zuk",
		description = "Configures whether kill count is displayed within TzKal-Zuk.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledTzKalZuk()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTzTokJad",
		name = "TzTok-Jad",
		description = "Configures whether kill count is displayed within TzTok-Jad.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledTzTokJad()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossVardorvis",
		name = "Vardorvis",
		description = "Configures whether kill count is displayed within Vardorvis.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledVardorvis()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossVorkath",
		name = "Vorkath",
		description = "Configures whether kill count is displayed within Vorkath.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledVorkath()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossWintertodt",
		name = "Wintertodt",
		description = "Configures whether kill count is displayed within Wintertodt.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledWintertodt()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossZalcano",
		name = "Zalcano",
		description = "Configures whether kill count is displayed within Zalcano.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledZalcano()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossZulrah",
		name = "Zulrah",
		description = "Configures whether kill count is displayed within Zulrah.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledZulrah()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossAbyssalSire",
		name = "Abyssal Sire",
		description = "Configures whether kill count is displayed within Abyssal Sire.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledAbyssalSire()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossAlchemicalHydra",
		name = "Alchemical Hydra",
		description = "Configures whether kill count is displayed within Alchemical Hydra.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledAlchemicalHydra()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossAmoxliatl",
		name = "Amoxliatl",
		description = "Configures whether kill count is displayed within Amoxliatl.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledAmoxliatl()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossAraxxor",
		name = "Araxxor",
		description = "Configures whether kill count is displayed within Araxxor.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledAraxxor()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossArtio",
		name = "Artio",
		description = "Configures whether kill count is displayed within Artio.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledArtio()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossBarrowsChests",
		name = "Barrows Chests",
		description = "Configures whether kill count is displayed within Barrows Chests.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledBarrowsChests()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossBryophyta",
		name = "Bryophyta",
		description = "Configures whether kill count is displayed within Bryophyta.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledBryophyta()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCallisto",
		name = "Callisto",
		description = "Configures whether kill count is displayed within Callisto.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledCallisto()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCalvarion",
		name = "Calvarion",
		description = "Configures whether kill count is displayed within Calvarion.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledCalvarion()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCerberus",
		name = "Cerberus",
		description = "Configures whether kill count is displayed within Cerberus.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledCerberus()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossChambersOfXeric",
		name = "Chambers of Xeric",
		description = "Configures whether kill count is displayed within Chambers of Xeric.",
		section = RAID_SECTION
	)
	default HighlightRaidSetting bossEnabledChambersOfXeric()
	{
		return HighlightRaidSetting.NORMAL;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossChaosElemental",
		name = "Chaos Elemental",
		description = "Configures whether kill count is displayed within Chaos Elemental.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledChaosElemental()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossChaosFanatic",
		name = "Chaos Fanatic",
		description = "Configures whether kill count is displayed within Chaos Fanatic.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledChaosFanatic()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCommanderZilyana",
		name = "Commander Zilyana",
		description = "Configures whether kill count is displayed within Commander Zilyana.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledCommanderZilyana()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCorporealBeast",
		name = "Corporeal Beast",
		description = "Configures whether kill count is displayed within Corporeal Beast.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledCorporealBeast()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCrazyArchaeologist",
		name = "Crazy Archaeologist",
		description = "Configures whether kill count is displayed within Crazy Archaeologist.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledCrazyArchaeologist()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossDagannothPrime",
		name = "Dagannoth Prime",
		description = "Configures whether kill count is displayed within Dagannoth Prime.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledDagannothPrime()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossDagannothRex",
		name = "Dagannoth Rex",
		description = "Configures whether kill count is displayed within Dagannoth Rex.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledDagannothRex()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossDagannothSupreme",
		name = "Dagannoth Supreme",
		description = "Configures whether kill count is displayed within Dagannoth Supreme.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledDagannothSupreme()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossDerangedArchaeologist",
		name = "Deranged Archaeologist",
		description = "Configures whether kill count is displayed within Deranged Archaeologist.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledDerangedArchaeologist()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossDukeSucellus",
		name = "Duke Sucellus",
		description = "Configures whether kill count is displayed within Duke Sucellus.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledDukeSucellus()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossGeneralGraardor",
		name = "General Graardor",
		description = "Configures whether kill count is displayed within General Graardor.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledGeneralGraardor()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossGiantMole",
		name = "Giant Mole",
		description = "Configures whether kill count is displayed within Giant Mole.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledGiantMole()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossGrotesqueGuardians",
		name = "Grotesque Guardians",
		description = "Configures whether kill count is displayed within Grotesque Guardians.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledGrotesqueGuardians()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossHespori",
		name = "Hespori",
		description = "Configures whether kill count is displayed within Hespori.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledHespori()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossKalphiteQueen",
		name = "Kalphite Queen",
		description = "Configures whether kill count is displayed within Kalphite Queen.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledKalphiteQueen()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossKingBlackDragon",
		name = "King Black Dragon",
		description = "Configures whether kill count is displayed within King Black Dragon.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledKingBlackDragon()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossKraken",
		name = "Kraken",
		description = "Configures whether kill count is displayed within Kraken.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledKraken()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossKreeArra",
		name = "Kree'arra",
		description = "Configures whether kill count is displayed within Kree'arra.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledKreeArra()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossKrilTsutsaroth",
		name = "K'ril Tsutsaroth",
		description = "Configures whether kill count is displayed within K'ril Tsutsaroth.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledKrilTsutsaroth()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossLunarChests",
		name = "Lunar Chests",
		description = "Configures whether kill count is displayed within Lunar Chests.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledLunarChests()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossMimic",
		name = "Mimic",
		description = "Configures whether kill count is displayed within Mimic.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledMimic()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossNex",
		name = "Nex",
		description = "Configures whether kill count is displayed within Nex.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledNex()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossNightmare",
		name = "Nightmare",
		description = "Configures whether kill count is displayed within Nightmare.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledNightmare()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossObor",
		name = "Obor",
		description = "Configures whether kill count is displayed within Obor.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledObor()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossPhantomMuspah",
		name = "Phantom Muspah",
		description = "Configures whether kill count is displayed within Phantom Muspah.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledPhantomMuspah()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossSarachnis",
		name = "Sarachnis",
		description = "Configures whether kill count is displayed within Sarachnis.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledSarachnis()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossScorpia",
		name = "Scorpia",
		description = "Configures whether kill count is displayed within Scorpia.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledScorpia()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossSkotizo",
		name = "Skotizo",
		description = "Configures whether kill count is displayed within Skotizo.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledSkotizo()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossSolHeredit",
		name = "Sol Heredit",
		description = "Configures whether kill count is displayed within Sol Heredit.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledSolHeredit()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossSpindel",
		name = "Spindel",
		description = "Configures whether kill count is displayed within Spindel.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledSpindel()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTempoross",
		name = "Tempoross",
		description = "Configures whether kill count is displayed within Tempoross.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledTempoross()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTheHueycoatl",
		name = "Hueycoatl",
		description = "Configures whether kill count is displayed within The Hueycoatl.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledTheHueycoatl()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTheLeviathan",
		name = "Leviathan",
		description = "Configures whether kill count is displayed within The Leviathan.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledTheLeviathan()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTheWhisperer",
		name = "Whisperer",
		description = "Configures whether kill count is displayed within The Whisperer.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledTheWhisperer()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTheatreOfBlood",
		name = "Theatre of Blood",
		description = "Configures whether kill count is displayed within Theatre of Blood.",
		section = RAID_SECTION
	)
	default HighlightRaidSetting bossEnabledTheatreOfBlood()
	{
		return HighlightRaidSetting.NORMAL;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossThermonuclearSmokeDevil",
		name = "Thermonuclear Smoke Devil",
		description = "Configures whether kill count is displayed within Thermonuclear Smoke Devil.",
		section = BOSS_SECTION
	)
	default HighlightSetting bossEnabledThermonuclearSmokeDevil()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTombsOfAmascut",
		name = "Tombs of Amascut",
		description = "Configures whether kill count is displayed within Tombs of Amascut.",
		section = RAID_SECTION
	)
	default HighlightRaidSetting bossEnabledTombsOfAmascut()
	{
		return HighlightRaidSetting.NORMAL;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossVenenatis",
		name = "Venenatis",
		description = "Configures whether kill count is displayed within Venenatis.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledVenenatis()
	{
		return HighlightAlwaysSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossVetion",
		name = "Vet'ion",
		description = "Configures whether kill count is displayed within Vet'ion.",
		section = BOSS_SECTION
	)
	default HighlightAlwaysSetting bossEnabledVetion()
	{
		return HighlightAlwaysSetting.LOBBY;
	}
}
