package com.tormenteddemons;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.NPC;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TextComponent;

import java.awt.*;

import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;

import net.runelite.api.Point; // Runelite Point
import net.runelite.client.ui.overlay.OverlayManager;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigGroup;

import net.runelite.api.Actor;

@Slf4j
@PluginDescriptor(
	name = "Tormented Demons"
)
public class TormentedDemonsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TDCounterOverlay tdCounterOverlay;

	@ConfigGroup("example")
	public interface ExampleConfig extends Config
	{
		@ConfigItem(
				keyName = "fontSize",
				name = "Font Size",
				description = "Adjust the font size for the attack counter"
		)
		int fontSize();
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(tdCounterOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(tdCounterOverlay);
	}

	private int ticksSinceNoInteraction = 0;
	//stores current attack style
	private static class AttackTracker {
		NPC demon = null;
		AttackStyle style;
		int attacks;

		AttackTracker(AttackStyle style) {
			this.setStyle(style);
		}

		public void setDemon(NPC demon) {
			this.demon = demon;
		}

		public void setStyle(AttackStyle style) {
			this.style = style;
			this.attacks = 1; // Reset count when style changes
		}

		public void reset() {
			this.demon = null;
			this.style = null;
			this.attacks = 0;
		}
	}
	AttackTracker tracker = new AttackTracker(null);

	//enums for each type
	private enum AttackStyle {
		MELEE, MAGIC, RANGED
	}

	@Subscribe
	public void onGameTick(net.runelite.api.events.GameTick event)
	{

		Actor target = client.getLocalPlayer().getInteracting();

		// if interacting with a Tormented Demon, start tracking
		if (target instanceof NPC && isTormentedDemon((NPC) target))
		{

			if (tracker.demon == null)
			{
				tracker.setDemon((NPC) target);
				ticksSinceNoInteraction = 0;
			}
		}
		else
		{
			// if no demon is being interacted with, increment the tick counter
			if (tracker.demon != null)
			{
				ticksSinceNoInteraction++;
				// if the player hasn't interacted with a demon for 16 ticks, stop tracking
				if (ticksSinceNoInteraction >= 16)
				{
					tracker.reset();
					ticksSinceNoInteraction = 0;
				}
			}
		}
	}

	// check if the NPC is a Tormented Demon
	private boolean isTormentedDemon(NPC npc)
	{
		// check the NPC name
		return npc.getName() != null && npc.getName().equals("Tormented Demon");
	}

	//method to detect and keep tracker of attacks by tormented demon
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!(event.getActor() instanceof NPC)) {
			return;
		}

		NPC npc = (NPC) event.getActor();

		if (tracker.demon != npc) {
			return; // Only track if the player is interacting with this NPC
		}

		if (npc.getName() == null || !npc.getName().equalsIgnoreCase("Tormented Demon")) {
			return;
		}

		int animationId = npc.getAnimation();

		//call style to get the style which a tormented demon is using.
		AttackStyle style = null;

		//detect animation
		if (MELEE_ANIMS.contains(animationId)) {
			style = AttackStyle.MELEE;
		} else if (MAGIC_ANIMS.contains(animationId)) {
			style = AttackStyle.MAGIC;
		} else if (RANGED_ANIMS.contains(animationId)) {
			style = AttackStyle.RANGED;
		}

		if (style != null) {
			int npcIndex = npc.getIndex();
			ticksSinceNoInteraction = 0;

			if (tracker.style != style) {
				// New style or new tracker
				tracker.setStyle(style);
			} else {
				tracker.attacks++;
			}
		}

		//tool to find melee, range, magic id's
		//log.info("Tormented Demon animation: " + animationId);
	}
	private static final Set<Integer> MELEE_ANIMS = Set.of(11392);
	private static final Set<Integer> MAGIC_ANIMS = Set.of(11388);
	private static final Set<Integer> RANGED_ANIMS = Set.of(11389);

	public static class TDCounterOverlay extends Overlay
	{
		private final Client client;
		private final TormentedDemonsPlugin plugin;
		private final PanelComponent panelComponent = new PanelComponent();

		// Injected dependencies
		@Inject
		public TDCounterOverlay(Client client, TormentedDemonsPlugin plugin)
		{
			super(plugin);
			this.client = client;
			this.plugin = plugin;
			setPosition(OverlayPosition.TOP_CENTER);
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			if (plugin.tracker.demon == null || client.getGameState() != GameState.LOGGED_IN)
			{
				return null; // Don't render if no demon is being tracked or not logged in
			}

			panelComponent.getChildren().clear();

			// Create our string we will display so the size doesn't change
			String counterText = "Ranged Hits: 0";

			int size = graphics.getFontMetrics().stringWidth(counterText);
			panelComponent.setPreferredSize(new Dimension(size + 20, 0));
			panelComponent.setOrientation(ComponentOrientation.VERTICAL);

			// determine color based on attack style
			Color textColor;
			switch (plugin.tracker.style)
			{
				case MELEE:
					textColor = Color.RED;
					break;
				case MAGIC:
					textColor = Color.BLUE;
					break;
				case RANGED:
					textColor = Color.GREEN;
					break;
				default:
					textColor = Color.YELLOW;
					break;
			}
			
			panelComponent.getChildren().add(LineComponent.builder()
				.left(plugin.tracker.style.toString() + " Hits: ")
				.leftColor(textColor)
				.right("" + plugin.tracker.attacks)
				.rightColor(textColor)
				.build());

			return panelComponent.render(graphics);
		}

	}




	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}