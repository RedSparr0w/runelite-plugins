package com.example;

import com.redprisonsentence.RedPrisonSentencePlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RedPrisonSentencePlugin.class);
		RuneLite.main(args);
	}
}