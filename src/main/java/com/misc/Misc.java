package com.misc;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.NPC;
import net.runelite.api.Player;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import java.awt.*;
import net.runelite.client.ui.overlay.OverlayManager;

import net.runelite.api.Actor;

@Slf4j
@PluginDescriptor(
	name = "zzMisc Sparr0w"
)
public class Misc extends Plugin
{
	@Inject
	private ConfigManager configManager;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PluginOverlay pluginOverlay;

	@Inject
	private ChatCommandManager chatCommandManager;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(pluginOverlay);
		chatCommandManager.registerCommandAsync("!misc", this::commandRun);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(pluginOverlay);
	}

	private void commandRun(ChatMessage chatMessage, String message)
	{
		log.info("Command run: {}", message);
		// Example of how to use the kill count
		String boss = "Zulrah";
		int kc = getKc(boss);
		log.info("Kill count for {}: {}", boss, kc);
	}

	private int getKc(String boss)
	{
		Integer killCount = configManager.getRSProfileConfiguration("killcount", boss.toLowerCase(), int.class);
		return killCount == null ? 0 : killCount;
	}

	@Subscribe
	public void onGameTick(net.runelite.api.events.GameTick event)
	{

	}

	//method to detect and keep tracker of attacks by tormented demon
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		Player player = client.getLocalPlayer();
		// check if the event is for the local player
		if (event.getActor() != player) {
			return;
		}

		int animation = player.getAnimation();
	}

	public static class PluginOverlay extends Overlay
	{
		private final Client client;
		private final Misc plugin;
		private final PanelComponent panelComponent = new PanelComponent();

		// Injected dependencies
		@Inject
		public PluginOverlay(Client client, Misc plugin)
		{
			super(plugin);
			this.client = client;
			this.plugin = plugin;
			setPosition(OverlayPosition.TOP_CENTER);
		}

		@Override
		public Dimension render(Graphics2D graphics)
		{
			panelComponent.getChildren().clear();
			panelComponent.setBackgroundColor(Color.GRAY);

			// Create our string we will display (replace actual value with 0 so size doesn't change)
			String counterText = "Test Counter: 0";

			int size = graphics.getFontMetrics().stringWidth(counterText);
			panelComponent.setPreferredSize(new Dimension(size + 20, 0));
			panelComponent.setOrientation(ComponentOrientation.VERTICAL);
			
			panelComponent.getChildren().add(LineComponent.builder()
				.left("Test Counter: ")
				.right(""+0)
				.build());

			return panelComponent.render(graphics);
		}
	}
}