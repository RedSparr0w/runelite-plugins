package com.hunllefswitcher;

import com.google.inject.Provides;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.NPC;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
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
	private CounterOverlay tdCounterOverlay;

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
		overlayManager.remove(tdCounterOverlay);
	}

	private int ticksSinceNoInteraction = -1;

	@Subscribe
	public void onGameTick(net.runelite.api.events.GameTick event)
	{
		Actor target = client.getLocalPlayer().getInteracting();

		if (target instanceof NPC && isCorruptedHunllef((NPC) target)) {
			System.out.println("Interacting with: " + target.getName());

			if (ticksSinceNoInteraction < 0) {
				overlayManager.add(tdCounterOverlay);
			}
			ticksSinceNoInteraction = 0;
		} else if (ticksSinceNoInteraction >= 0) {
			ticksSinceNoInteraction++;
			// if the player hasn't interacted with NPC for 30 ticks, stop tracking
			if (ticksSinceNoInteraction >= 10)
			{
				resetTracker();
				overlayManager.remove(tdCounterOverlay);
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
		attackCount = 0;
		style = AttackStyle.RANGED;
	}

	//enums for each type
	private enum AttackStyle {
		MAGIC, RANGED
	}

	public int attackCount = 0;
	public AttackStyle style = AttackStyle.RANGED;

	//method to detect and keep tracker of attacks by tormented demon
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!(event.getActor() instanceof NPC)) {
			return;
		}

		NPC npc = (NPC) event.getActor();

		if (!isCorruptedHunllef(npc)) {
			return;
		}

		int animationId = npc.getAnimation();

		//detect animation
		if (animationId == ATTACK || animationId == TORNADO) {
			attackCount = (attackCount + 1) % 4;
			if (attackCount == 0) {
				style = style == AttackStyle.MAGIC ? AttackStyle.RANGED : AttackStyle.MAGIC;
			}
		}
		// Reset the counter if the animation is switch, we must have been stomped
		else if (animationId == SWITCH) {
			attackCount = 0;
		}

		ticksSinceNoInteraction = 0;

		//tool to find melee, range, magic id's
		log.info("Animation: " + animationId);
	}
	private static final int ATTACK = 8419;
	private static final int TORNADO = 8418;
	private static final int SWITCH = 8754;
	private static final int STOMP = 8420;

	public static class CounterOverlay extends Overlay
	{
		private final Client client;
		private final HunllefSwitcherPlugin plugin;

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
			int x = 20;
			int y = 0;

			Color textColor = plugin.style == AttackStyle.RANGED ? Color.GREEN : Color.BLUE;

			// create the counter text to display
			String counterText = plugin.style.toString() + " Hits: " + plugin.attackCount;

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

			return null;
		}

	}




	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}