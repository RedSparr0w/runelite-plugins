package com.example;

import com.basiliskknights.BasiliskKnights;
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
			BasiliskKnights.class
		);
		RuneLite.main(args);
	}
}