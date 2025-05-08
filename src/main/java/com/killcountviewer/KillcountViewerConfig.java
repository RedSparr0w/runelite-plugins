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

import java.awt.*;

@ConfigGroup(KillcountViewerConfig.GROUP)
public interface KillcountViewerConfig extends Config
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

	@ConfigSection(
		name = "Boss options",
		description = "Toggle which bosses to show kill count for.",
		position = 99
	)
	String highlightSection = "section";

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
	@ConfigItem(
		position = 0,
		keyName = "bossSoulWarsZeal",
		name = "Soul Wars Zeal",
		description = "Configures whether kill count is displayed within Soul Wars Zeal.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledSoulWarsZeal()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossLastManStanding",
		name = "Last Man Standing",
		description = "Configures whether kill count is displayed within Last Man Standing.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledLastManStanding()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossRiftsClosed",
		name = "Rifts Closed",
		description = "Configures whether kill count is displayed within Rifts Closed.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledRiftsClosed()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossPhosanisNightmare",
		name = "Phosani's Nightmare",
		description = "Configures whether kill count is displayed within Phosani's Nightmare.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledPhosanisNightmare()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossScurrius",
		name = "Scurrius",
		description = "Configures whether kill count is displayed within Scurrius.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledScurrius()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCorruptedGauntlet",
		name = "Corrupted Gauntlet",
		description = "Configures whether kill count is displayed within the Corrupted Gauntlet.",
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledAbyssalSire()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossAlchemicalHydra",
		name = "Alchemical Hydra",
		description = "Configures whether kill count is displayed within Alchemical Hydra.",
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledArtio()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossBarrowsChests",
		name = "Barrows Chests",
		description = "Configures whether kill count is displayed within Barrows Chests.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledBarrowsChests()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossBryophyta",
		name = "Bryophyta",
		description = "Configures whether kill count is displayed within Bryophyta.",
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledCallisto()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCalvarion",
		name = "Calvarion",
		description = "Configures whether kill count is displayed within Calvarion.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledCalvarion()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCerberus",
		name = "Cerberus",
		description = "Configures whether kill count is displayed within Cerberus.",
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledChambersOfXeric()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossChambersOfXericChallengeMode",
		name = "Chambers of Xeric (Challenge Mode)",
		description = "Configures whether kill count is displayed within Chambers of Xeric (Challenge Mode).",
		section = highlightSection
	)
	default HighlightSetting bossEnabledChambersOfXericChallengeMode()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossChaosElemental",
		name = "Chaos Elemental",
		description = "Configures whether kill count is displayed within Chaos Elemental.",
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledCommanderZilyana()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCorporealBeast",
		name = "Corporeal Beast",
		description = "Configures whether kill count is displayed within Corporeal Beast.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledCorporealBeast()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossCrazyArchaeologist",
		name = "Crazy Archaeologist",
		description = "Configures whether kill count is displayed within Crazy Archaeologist.",
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledDagannothPrime()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossDagannothRex",
		name = "Dagannoth Rex",
		description = "Configures whether kill count is displayed within Dagannoth Rex.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledDagannothRex()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossDagannothSupreme",
		name = "Dagannoth Supreme",
		description = "Configures whether kill count is displayed within Dagannoth Supreme.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledDagannothSupreme()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossDerangedArchaeologist",
		name = "Deranged Archaeologist",
		description = "Configures whether kill count is displayed within Deranged Archaeologist.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledDerangedArchaeologist()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossDukeSucellus",
		name = "Duke Sucellus",
		description = "Configures whether kill count is displayed within Duke Sucellus.",
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledGeneralGraardor()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossGiantMole",
		name = "Giant Mole",
		description = "Configures whether kill count is displayed within Giant Mole.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledGiantMole()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossGrotesqueGuardians",
		name = "Grotesque Guardians",
		description = "Configures whether kill count is displayed within Grotesque Guardians.",
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledKalphiteQueen()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossKingBlackDragon",
		name = "King Black Dragon",
		description = "Configures whether kill count is displayed within King Black Dragon.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledKingBlackDragon()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossKraken",
		name = "Kraken",
		description = "Configures whether kill count is displayed within Kraken.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledKraken()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossKreeArra",
		name = "Kree'arra",
		description = "Configures whether kill count is displayed within Kree'arra.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledKreeArra()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossKrilTsutsaroth",
		name = "K'ril Tsutsaroth",
		description = "Configures whether kill count is displayed within K'ril Tsutsaroth.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledKrilTsutsaroth()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossLunarChests",
		name = "Lunar Chests",
		description = "Configures whether kill count is displayed within Lunar Chests.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledLunarChests()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossMimic",
		name = "Mimic",
		description = "Configures whether kill count is displayed within Mimic.",
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledNex()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossNightmare",
		name = "Nightmare",
		description = "Configures whether kill count is displayed within Nightmare.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledNightmare()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossObor",
		name = "Obor",
		description = "Configures whether kill count is displayed within Obor.",
		section = highlightSection
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
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledSarachnis()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossScorpia",
		name = "Scorpia",
		description = "Configures whether kill count is displayed within Scorpia.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledScorpia()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossSkotizo",
		name = "Skotizo",
		description = "Configures whether kill count is displayed within Skotizo.",
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledSolHeredit()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTempoross",
		name = "Tempoross",
		description = "Configures whether kill count is displayed within Tempoross.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledTempoross()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTheHueycoatl",
		name = "The Hueycoatl",
		description = "Configures whether kill count is displayed within The Hueycoatl.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledTheHueycoatl()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTheLeviathan",
		name = "The Leviathan",
		description = "Configures whether kill count is displayed within The Leviathan.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledTheLeviathan()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTheWhisperer",
		name = "The Whisperer",
		description = "Configures whether kill count is displayed within The Whisperer.",
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledTheatreOfBlood()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTheatreOfBloodHardMode",
		name = "Theatre of Blood (Hard Mode)",
		description = "Configures whether kill count is displayed within Theatre of Blood (Hard Mode).",
		section = highlightSection
	)
	default HighlightSetting bossEnabledTheatreOfBloodHardMode()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossThermonuclearSmokeDevil",
		name = "Thermonuclear Smoke Devil",
		description = "Configures whether kill count is displayed within Thermonuclear Smoke Devil.",
		section = highlightSection
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
		section = highlightSection
	)
	default HighlightSetting bossEnabledTombsOfAmascut()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossTombsOfAmascutExpert",
		name = "Tombs of Amascut (Expert)",
		description = "Configures whether kill count is displayed within Tombs of Amascut (Expert).",
		section = highlightSection
	)
	default HighlightSetting bossEnabledTombsOfAmascutExpert()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossVenenatis",
		name = "Venenatis",
		description = "Configures whether kill count is displayed within Venenatis.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledVenenatis()
	{
		return HighlightSetting.LOBBY;
	}

	@ConfigItem(
		position = 0,
		keyName = "bossVetion",
		name = "Vet'ion",
		description = "Configures whether kill count is displayed within Vet'ion.",
		section = highlightSection
	)
	default HighlightSetting bossEnabledVetion()
	{
		return HighlightSetting.LOBBY;
	}
}
