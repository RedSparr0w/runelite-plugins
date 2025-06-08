package com.example;

import com.basiliskknights.BasiliskKnights;
import com.cerberus.CerberusPlugin;
import com.hunllefswitcher.HunllefSwitcherPlugin;
import com.killcountviewer.KillCountViewerPlugin;
import com.playerattacktimer.PlayerAttackTimerPlugin;
import com.tormenteddemons.TormentedDemonsPlugin;
import com.zulrah.ZulrahPlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(
			ZulrahPlugin.class,
			CerberusPlugin.class,
			PlayerAttackTimerPlugin.class,
			TormentedDemonsPlugin.class,
			KillCountViewerPlugin.class
		);
		RuneLite.main(args);
	}
}