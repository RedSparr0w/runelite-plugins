package com.example;

import com.killcountviewer.KillCountViewerPlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(KillCountViewerPlugin.class);
		RuneLite.main(args);
	}
}