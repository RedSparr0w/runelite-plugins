/*
 * Copyright (c) 2020, dutta64 <https://github.com/dutta64>
 * Copyright (c) 2019, kThisIsCvpv <https://github.com/kThisIsCvpv>
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
 * Copyright (c) 2019, kyle <https://github.com/xKylee>
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

package com.gauntletextended;

import com.google.inject.Provides;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.HeadIcon;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.NullNpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Player;
import net.runelite.api.Projectile;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.gauntletextended.entity.Demiboss;
import com.gauntletextended.entity.Hunllef;
import com.gauntletextended.entity.Missile;
import com.gauntletextended.entity.Resource;
import com.gauntletextended.entity.Tornado;
import com.gauntletextended.overlay.Overlay;
import com.gauntletextended.overlay.OverlayGauntlet;
import com.gauntletextended.overlay.OverlayHunllef;
import com.gauntletextended.overlay.OverlayPrayerBox;
import com.gauntletextended.overlay.OverlayPrayerWidget;
import com.gauntletextended.resource.ResourceManager;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Gauntlet Extended",
	enabledByDefault = false,
	description = "All-in-one plugin for the Gauntlet.",
	tags = {"gauntlet"}
)
@Singleton
public class GauntletExtendedPlugin extends Plugin
{
	public static final int ONEHAND_SLASH_AXE_ANIMATION = 395;
	public static final int ONEHAND_CRUSH_PICKAXE_ANIMATION = 400;
	public static final int ONEHAND_CRUSH_AXE_ANIMATION = 401;
	public static final int UNARMED_PUNCH_ANIMATION = 422;
	public static final int UNARMED_KICK_ANIMATION = 423;
	public static final int BOW_ATTACK_ANIMATION = 426;
	public static final int ONEHAND_STAB_HALBERD_ANIMATION = 428;
	public static final int ONEHAND_SLASH_HALBERD_ANIMATION = 440;
	public static final int ONEHAND_SLASH_SWORD_ANIMATION = 390;
	public static final int ONEHAND_STAB_SWORD_ANIMATION = 386;
	public static final int HIGH_LEVEL_MAGIC_ATTACK = 1167;
	public static final int HUNLEFF_TORNADO = 8418;

	private static final Set<Integer> MELEE_ANIM_IDS = Set.of(
		ONEHAND_STAB_SWORD_ANIMATION, ONEHAND_SLASH_SWORD_ANIMATION,
		ONEHAND_SLASH_AXE_ANIMATION, ONEHAND_CRUSH_PICKAXE_ANIMATION,
		ONEHAND_CRUSH_AXE_ANIMATION, UNARMED_PUNCH_ANIMATION,
		UNARMED_KICK_ANIMATION, ONEHAND_STAB_HALBERD_ANIMATION,
		ONEHAND_SLASH_HALBERD_ANIMATION
	);

	private static final Set<Integer> ATTACK_ANIM_IDS = new HashSet<>();

	static
	{
		ATTACK_ANIM_IDS.addAll(MELEE_ANIM_IDS);
		ATTACK_ANIM_IDS.add(BOW_ATTACK_ANIMATION);
		ATTACK_ANIM_IDS.add(HIGH_LEVEL_MAGIC_ATTACK);
	}

	private static final Set<Integer> HUNLLEF_IDS = Set.of(
		NpcID.CRYSTALLINE_HUNLLEF, NpcID.CRYSTALLINE_HUNLLEF_9022,
		NpcID.CRYSTALLINE_HUNLLEF_9023, NpcID.CRYSTALLINE_HUNLLEF_9024,
		NpcID.CORRUPTED_HUNLLEF, NpcID.CORRUPTED_HUNLLEF_9036,
		NpcID.CORRUPTED_HUNLLEF_9037, NpcID.CORRUPTED_HUNLLEF_9038
	);

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private GauntletExtendedConfig config;

	@Inject
	private ResourceManager resourceManager;

	@Inject
	private SkillIconManager skillIconManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private OverlayGauntlet overlayGauntlet;

	@Inject
	private OverlayHunllef overlayHunllef;

	@Inject
	private OverlayPrayerWidget overlayPrayerWidget;

	@Inject
	private OverlayPrayerBox overlayPrayerBox;

	private Set<Overlay> overlays;

	@Getter
	private final Set<Resource> resources = new HashSet<>();

	@Getter
	private final Set<GameObject> utilities = new HashSet<>();

	@Getter
	private final Set<Tornado> tornadoes = new HashSet<>();

	@Getter
	private final Set<Demiboss> demibosses = new HashSet<>();

	@Getter
	private final Set<NPC> strongNpcs = new HashSet<>();

	@Getter
	private final Set<NPC> weakNpcs = new HashSet<>();

	private final List<Set<?>> entitySets = Arrays.asList(resources, utilities, tornadoes, demibosses, strongNpcs, weakNpcs);

	@Getter
	private Missile missile;

	@Getter
	private Hunllef hunllef;

	@Getter
	@Setter
	private boolean wrongAttackStyle;

	@Getter
	@Setter
	private boolean switchWeapon;

	private boolean inGauntlet;
	private boolean inHunllef;

	@Provides
	GauntletExtendedConfig getConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(GauntletExtendedConfig.class);
	}

	@Override
	protected void startUp()
	{
		if (overlays == null)
		{
			overlays = Set.of(overlayGauntlet, overlayHunllef, overlayPrayerWidget, overlayPrayerBox);
		}

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(this::pluginEnabled);
		}
	}

	@Override
	protected void shutDown()
	{
		overlays.forEach(o -> overlayManager.remove(o));

		inGauntlet = false;
		inHunllef = false;

		hunllef = null;
		missile = null;
		wrongAttackStyle = false;
		switchWeapon = false;

		resourceManager.reset();

		entitySets.forEach(Set::clear);
	}

	@Subscribe
	private void onConfigChanged(final ConfigChanged event)
	{
		if (!event.getGroup().equals("gauntlet"))
		{
			return;
		}

		switch (event.getKey())
		{
			case "resourceIconSize":
				if (!resources.isEmpty())
				{
					resources.forEach(r -> r.setIconSize(config.resourceIconSize()));
				}
				break;
			case "resourceTracker":
				if (inGauntlet && !inHunllef)
				{
					resourceManager.reset();
					resourceManager.init();
				}
				break;
			case "projectileIconSize":
				if (missile != null)
				{
					missile.setIconSize(config.projectileIconSize());
				}
				break;
			case "hunllefAttackStyleIconSize":
				if (hunllef != null)
				{
					hunllef.setIconSize(config.hunllefAttackStyleIconSize());
				}
				break;
			case "mirrorMode":
				overlays.forEach(overlay -> {
					overlay.determineLayer();

					if (overlayManager.anyMatch(o -> o == overlay))
					{
						overlayManager.remove(overlay);
						overlayManager.add(overlay);
					}
				});
				break;
			default:
				break;
		}
	}

	@Subscribe
	private void onVarbitChanged(final VarbitChanged event)
	{
		if (isHunllefVarbitSet())
		{
			if (!inHunllef)
			{
				initHunllef();
			}
		}
		else if (isGauntletVarbitSet())
		{
			if (!inGauntlet)
			{
				initGauntlet();
			}
		}
		else
		{
			if (inGauntlet || inHunllef)
			{
				shutDown();
			}
		}
	}

	@Subscribe
	private void onGameTick(final GameTick event)
	{
		if (hunllef == null)
		{
			return;
		}

		hunllef.decrementTicksUntilNextAttack();

		if (missile != null && missile.getProjectile().getRemainingCycles() <= 0)
		{
			missile = null;
		}

		if (!tornadoes.isEmpty())
		{
			tornadoes.forEach(Tornado::updateTimeLeft);
		}
	}

	@Subscribe
	private void onGameStateChanged(final GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOADING:
				resources.clear();
				utilities.clear();
				break;
			case LOGIN_SCREEN:
			case HOPPING:
				shutDown();
				break;
		}
	}

	@Subscribe
	private void onWidgetLoaded(final WidgetLoaded event)
	{
		if (event.getGroupId() == WidgetID.GAUNTLET_TIMER_GROUP_ID)
		{
			resourceManager.init();
		}
	}

	@Subscribe
	private void onNpcSpawned(final NpcSpawned event)
	{
		final NPC npc = event.getNpc();

		final int id = npc.getId();

		if (HUNLLEF_IDS.contains(id))
		{
			hunllef = new Hunllef(npc, skillIconManager, config.hunllefAttackStyleIconSize());
		}
	}

	@Subscribe
	private void onNpcDespawned(final NpcDespawned event)
	{
		final NPC npc = event.getNpc();

		final int id = npc.getId();

		if (HUNLLEF_IDS.contains(id))
		{
			hunllef = null;
		}
	}

	@Subscribe
	private void onHunllefAttack(final NpcDespawned event)
	{
		// TODO: Figure out when hunllef is attacking
		if (hunllef == null)
		{
			return;
		}


		hunllef.updateAttackCount();
	}

	@Subscribe
	private void onChatMessage(final ChatMessage event)
	{
		final ChatMessageType type = event.getType();

		if (type == ChatMessageType.SPAM || type == ChatMessageType.GAMEMESSAGE)
		{
			resourceManager.parseChatMessage(event.getMessage());
		}
	}

	@Subscribe
	private void onActorDeath(final ActorDeath event)
	{
		if (event.getActor() != client.getLocalPlayer())
		{
			return;
		}
	}

	@Subscribe
	private void onAnimationChanged(final AnimationChanged event)
	{
		if (!isHunllefVarbitSet() || hunllef == null)
		{
			return;
		}

		final Actor actor = event.getActor();

		final int animationId = actor.getAnimation();

		if (actor instanceof Player)
		{
			if (!ATTACK_ANIM_IDS.contains(animationId))
			{
				return;
			}

			final boolean validAttack = isAttackAnimationValid(animationId);

			if (validAttack)
			{
				wrongAttackStyle = false;
				hunllef.updatePlayerAttackCount();

				if (hunllef.getPlayerAttackCount() == 1)
				{
					switchWeapon = true;
				}
			}
			else
			{
				wrongAttackStyle = true;
			}
		}
		else if (actor instanceof NPC)
		{
			if (animationId == HUNLEFF_TORNADO)
			{
				hunllef.updateAttackCount();
			}
		}
	}
	// TODO: Update the headIcon after every fith attack, assume the player is using the correct attack style to start with
	final HeadIcon headIcon = null;

	private boolean isAttackAnimationValid(final int animationId)
	{
		if (headIcon == null)
		{
			return true;
		}

		switch (headIcon)
		{
			case MELEE:
				if (MELEE_ANIM_IDS.contains(animationId))
				{
					return false;
				}
				break;
			case RANGED:
				if (animationId == BOW_ATTACK_ANIMATION)
				{
					return false;
				}
				break;
			case MAGIC:
				if (animationId == HIGH_LEVEL_MAGIC_ATTACK)
				{
					return false;
				}
				break;
		}

		return true;
	}

	private void pluginEnabled()
	{
		if (isGauntletVarbitSet())
		{
			resourceManager.init();
			addSpawnedEntities();
			initGauntlet();
		}

		if (isHunllefVarbitSet())
		{
			initHunllef();
		}
	}

	private void addSpawnedEntities()
	{
		for (final NPC npc : client.getNpcs())
		{
			onNpcSpawned(new NpcSpawned(npc));
		}
	}

	private void initGauntlet()
	{
		inGauntlet = true;

		overlayManager.add(overlayGauntlet);
	}

	private void initHunllef()
	{
		inHunllef = true;

		resourceManager.reset();

		overlayManager.remove(overlayGauntlet);
		overlayManager.add(overlayHunllef);
		overlayManager.add(overlayPrayerWidget);
		overlayManager.add(overlayPrayerBox);
	}

	private boolean isGauntletVarbitSet()
	{
		return client.getVarbitValue(9178) == 1;
	}

	private boolean isHunllefVarbitSet()
	{
		return client.getVarbitValue(9177) == 1;
	}
}
