package com.example;

import com.prisonsentence.PrisonSentencePlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(PrisonSentencePlugin.class);
		RuneLite.main(args);
	}
}