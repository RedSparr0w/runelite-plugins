package com.example;

import com.killcountviewer.KillcountViewerPlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(KillcountViewerPlugin.class);
		RuneLite.main(args);
	}
}