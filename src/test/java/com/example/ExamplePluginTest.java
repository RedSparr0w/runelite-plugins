package com.example;

import com.gauntletextended.GauntletExtendedPlugin;
import com.hunllefswitcher.HunllefSwitcherPlugin;
import com.killcountviewer.KillCountViewerPlugin;
import com.tormenteddemons.TormentedDemonsPlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(
			// GauntletExtendedPlugin.class,
			HunllefSwitcherPlugin.class,
			KillCountViewerPlugin.class
		);
		RuneLite.main(args);
	}
}