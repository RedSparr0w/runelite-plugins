package com.basiliskknights;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.NPC;
import net.runelite.api.Player;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

import java.awt.*;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
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
		private final PanelComponent panelComponent = new PanelComponent();

		// Injected dependencies
		@Inject
		public CounterOverlay(Client client, BasiliskKnights plugin)
		{
			super(plugin);
			this.client = client;
			this.plugin = plugin;
			setPosition(OverlayPosition.TOP_CENTER);
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			if (!plugin.hasTarget)
			{
				return null;
			}
			panelComponent.getChildren().clear();
			panelComponent.setBackgroundColor(plugin.ticksToNextFlick >= 5 ? Color.ORANGE : Color.GRAY);

			// Create our string we will display (replace actual value with 0 so size doesn't change)
			String counterText = "Flick Counter: 0";

			int size = graphics.getFontMetrics().stringWidth(counterText);
			panelComponent.setPreferredSize(new Dimension(size + 20,0));
			panelComponent.setOrientation(ComponentOrientation.VERTICAL);
			
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Flick Counter: ")
				.right(""+plugin.ticksToNextFlick)
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