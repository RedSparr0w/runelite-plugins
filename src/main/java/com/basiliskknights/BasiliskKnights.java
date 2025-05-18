package com.basiliskknights;

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
import net.runelite.api.Player;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

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
	name = "Basilisk Knights"
)
public class BasiliskKnights extends Plugin
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
		int fontSize();
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(counterOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(counterOverlay);
	}

	boolean hasTarget = false;

	@Subscribe
	public void onGameTick(net.runelite.api.events.GameTick event)
	{
			ticksToNextFlick = Math.max(ticksToNextFlick - 1, 0);

			Actor target = client.getLocalPlayer().getInteracting();

			// if interacting with a Tormented Demon, start tracking
			if (target instanceof NPC && isBasiliskKnight((NPC) target))
			{
				hasTarget = true;
			} else {
				hasTarget = false;
			}
	}

	private boolean isBasiliskKnight(NPC npc)
	{
		// check the NPC name
		return npc.getName() != null && npc.getName().equals("Basilisk Knight");
	}

	//------------------------------------------------------------------------------------------------------------

	public int ticksToNextFlick = 0;
	public int currentAnimation = -1;

	//method to detect and keep tracker of attacks by tormented demon
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		Player player = client.getLocalPlayer();
		// check if the event is for the local player
		if (event.getActor() != player) {
			return;
		}

		currentAnimation = player.getAnimation();
		
		if (currentAnimation != 7552) {
			return;
		}
		ticksToNextFlick = 6;
	}

	public static class CounterOverlay extends Overlay
	{
		private final Client client;
		private final BasiliskKnights plugin;

		// Injected dependencies
		@Inject
		public CounterOverlay(Client client, BasiliskKnights plugin)
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
			if (!plugin.hasTarget)
			{
				return null;
			}
			int x = 0;
			int y = 50;

			Color textColor = Color.YELLOW;

			// create the counter text to display
			String counterText = "Flick Counter: " + plugin.ticksToNextFlick;

			// get the current font size from the config
			int fontSize = plugin.config.fontSize();

			// set text font
			graphics.setFont(new Font("Arial", Font.BOLD, fontSize));

			//draw the background box
			int boxWidth = graphics.getFontMetrics().stringWidth(counterText) + 10;
			int boxHeight = graphics.getFontMetrics().getHeight() + 20;

			graphics.setColor(plugin.ticksToNextFlick >= 5 ? Color.RED : Color.BLACK); // semi-transparent black
			graphics.fillRect(x - 5, y - 5, boxWidth, boxHeight);

			// Draw the text over the box
			graphics.setColor(textColor);
			graphics.drawString(counterText, x, y + boxHeight - 15);

			return null;
		}

	}




	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}