package com.hunllefswitcher;

import com.google.inject.Provides;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.game.SkillIconManager;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import net.runelite.client.ui.overlay.OverlayManager;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigGroup;

import net.runelite.api.Actor;

@Slf4j
@PluginDescriptor(
	name = "Hunllef Switcher"
)
public class HunllefSwitcherPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CounterOverlay counterOverlay;

	@ConfigGroup("example")
	public interface ExampleConfig extends Config
	{
		@ConfigItem(
				keyName = "fontSize",
				name = "Font Size",
				description = "Adjust the font size for the attack counter"
		)
		default int fontSize()
		{
			return 6;
		}
	}

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(counterOverlay);
	}

	private int ticksSinceNoInteraction = -1;
	private NPC hunllefNPC = null;

	@Subscribe
	public void onGameTick(net.runelite.api.events.GameTick event)
	{
		Actor target = client.getLocalPlayer().getInteracting();

		if (target instanceof NPC && isCorruptedHunllef((NPC) target)) {
			System.out.println("Interacting with: " + target.getName());
			hunllefNPC = (NPC) target;
			if (ticksSinceNoInteraction < 0) {
				overlayManager.add(counterOverlay);
			}
			ticksSinceNoInteraction = 0;
		} else if (ticksSinceNoInteraction >= 0) {
			ticksSinceNoInteraction++;
			// if the player hasn't interacted with NPC for 30 ticks, stop tracking
			if (ticksSinceNoInteraction >= 10)
			{
				resetTracker();
				overlayManager.remove(counterOverlay);
				hunllefNPC = null;
			}
		}
	}

	// check if the NPC is a Tormented Demon
	private boolean isCorruptedHunllef(NPC npc)
	{
		// check the NPC name
		return npc.getName() != null && npc.getName().contains("Hunllef");
	}

	private void resetTracker()
	{
		log.info("Resetting tracker due to loss of interaction.");
		ticksSinceNoInteraction = -1;
		hunllefAttackCount = 0;
		hunllefAttackStyle = AttackStyle.RANGED;
	}

	//enums for each type
	private enum AttackStyle {
		MAGIC, RANGED, MELEE
	}

	public int hunllefAttackCount = 0;
	public int playerAttackCount = 0;
	public AttackStyle hunllefAttackStyle = AttackStyle.RANGED;
	public AttackStyle hunllefPrayerStyle = null;
	
	public static final int ONEHAND_SLASH_AXE_ANIMATION = 395;
	public static final int ONEHAND_CRUSH_PICKAXE_ANIMATION = 400;
	public static final int ONEHAND_CRUSH_AXE_ANIMATION = 401;
	public static final int UNARMED_PUNCH_ANIMATION = 422;
	public static final int UNARMED_KICK_ANIMATION = 423;
	// Halberd animations
	public static final int ONEHAND_STAB_HALBERD_ANIMATION = 428;
	public static final int ONEHAND_SLASH_HALBERD_ANIMATION = 440;
	public static final int ONEHAND_SLASH_SWORD_ANIMATION = 390;
	public static final int ONEHAND_STAB_SWORD_ANIMATION = 386;
	// Bow and magic animations
	public static final int BOW_ATTACK_ANIMATION = 426;
	public static final int HIGH_LEVEL_MAGIC_ATTACK = 1167;
	// Hunllef animations
	private static final int HUNLLEF_ATTACK = 8419;
	private static final int HUNLEFF_TORNADO = 8418;
	private static final int HUNLLEF_SWITCH = 8754;
	private static final int HUNLLEF_STOMP = 8420;

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

	private void playerAnimationChanged(AnimationChanged event)
	{
		if (event.getActor() != client.getLocalPlayer()) {
			return;
		}

		int animationId = event.getActor().getAnimation();

		// Check if we are attacking agianst prayed style (we shouldn't be), these wont' count as an attack
		switch (hunllefPrayerStyle)
		{
			case AttackStyle.MELEE:
				if (MELEE_ANIM_IDS.contains(animationId)) return;
				break;
			case AttackStyle.RANGED:
				if (animationId == BOW_ATTACK_ANIMATION) return;
				break;
			case AttackStyle.MAGIC:
				if (animationId == HIGH_LEVEL_MAGIC_ATTACK) return;
				break;
			case null:
				break;
		}

		// We attacked with a different style
		playerAttackCount = (playerAttackCount + 1) % 6;

		if (playerAttackCount == 0) {
			if (MELEE_ANIM_IDS.contains(animationId)) {
				hunllefPrayerStyle = AttackStyle.MELEE;
			} else if (animationId == BOW_ATTACK_ANIMATION) {
				hunllefPrayerStyle = AttackStyle.RANGED;
			} else {
				hunllefPrayerStyle = AttackStyle.MAGIC;
			}
		}
	}

	//method to detect and keep tracker of attacks by hunllef
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		// Check if the event is for the local player
		if (event.getActor() == client.getLocalPlayer()) {
			playerAnimationChanged(event);
			return;
		}
		if (!(event.getActor() instanceof NPC)) {
			return;
		}

		NPC npc = (NPC) event.getActor();

		if (!isCorruptedHunllef(npc)) {
			return;
		}

		int animationId = npc.getAnimation();

		//detect animation
		if (animationId == HUNLLEF_ATTACK || animationId == HUNLEFF_TORNADO) {
			hunllefAttackCount = (hunllefAttackCount + 1) % 4;
			if (hunllefAttackCount == 0) {
				hunllefAttackStyle = hunllefAttackStyle == AttackStyle.MAGIC ? AttackStyle.RANGED : AttackStyle.MAGIC;
			}
		}
		// Reset the counter if the animation is switch, we must have been stomped
		else if (animationId == HUNLLEF_SWITCH) {
			hunllefAttackCount = 0;
		}

		ticksSinceNoInteraction = 0;

		//tool to find melee, range, magic id's
		log.info("Animation: " + animationId);
	}

	public static class CounterOverlay extends Overlay
	{
	
		@Inject
		private SkillIconManager skillIconManager;

		private final Client client;
		private final HunllefSwitcherPlugin plugin;
		private final int iconSize = 20;
		private BufferedImage originalMagicIcon;
		private BufferedImage originalRangeIcon;
		private BufferedImage originalMeleeIcon;

		// Injected dependencies
		@Inject
		public CounterOverlay(Client client, HunllefSwitcherPlugin plugin)
		{
			super(plugin);
			this.client = client;
			this.plugin = plugin;
			setPosition(OverlayPosition.TOP_CENTER);
			setLayer(OverlayLayer.ABOVE_SCENE);
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			if (this.originalMagicIcon == null)
			{
				this.originalMagicIcon = skillIconManager.getSkillImage(Skill.MAGIC);
			}
			if (this.originalRangeIcon == null)
			{
				this.originalRangeIcon = skillIconManager.getSkillImage(Skill.RANGED);
			}
			if (this.originalMeleeIcon == null)
			{
				this.originalMeleeIcon = skillIconManager.getSkillImage(Skill.ATTACK);
			}
			renderAttackCount(graphics);
			renderAttackStyleIcon(graphics);
			renderPrayerStyleIcon(graphics);
			return null;
		}

		public void renderAttackCount(Graphics2D graphics)
		{
			int x = 20;
			int y = 0;

			Color textColor = plugin.hunllefAttackStyle == AttackStyle.RANGED ? Color.GREEN : Color.BLUE;

			// create the counter text to display
			final String counterText = String.format("%d | %d", plugin.hunllefAttackCount, plugin.playerAttackCount);

			// get the current font size from the config
			int fontSize = plugin.config.fontSize();

			// set text font
			graphics.setFont(new Font("Arial", Font.BOLD, fontSize));

			//draw the background box
			int boxWidth = graphics.getFontMetrics().stringWidth(counterText) + 10;
			int boxHeight = graphics.getFontMetrics().getHeight() + 5;

			graphics.setColor(new Color(0, 0, 0, 150));
			graphics.fillRect(x - 5, y - 5, boxWidth, boxHeight);

			// Draw the text over the box
			graphics.setColor(textColor);
			graphics.drawString(counterText, x, y + boxHeight - 5);
		}

		public void renderAttackStyleIcon(Graphics2D graphics)
		{
		}

		public void renderPrayerStyleIcon(Graphics2D graphics)
		{
			BufferedImage icon = getIcon(plugin.hunllefPrayerStyle);
			final Point point = Perspective.getCanvasImageLocation(client, plugin.hunllefNPC.getLocalLocation(), icon, plugin.hunllefNPC.getLogicalHeight() - 100);

			if (point == null)
			{
				return;
			}

			graphics.drawImage(icon, point.getX(), point.getY(), null);
		}

		public BufferedImage getIcon(AttackStyle style)
		{
			switch (style)
			{
				case AttackStyle.MAGIC:
					return ImageUtil.resizeImage(originalMagicIcon, iconSize, iconSize);
				case AttackStyle.RANGED:
					return ImageUtil.resizeImage(originalRangeIcon, iconSize, iconSize);
				case AttackStyle.MELEE:
					return ImageUtil.resizeImage(originalMeleeIcon, iconSize, iconSize);
			}
			return ImageUtil.resizeImage(originalMeleeIcon, iconSize, iconSize);
		}
	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}