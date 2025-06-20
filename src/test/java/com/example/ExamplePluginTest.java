package com.example;

import com.groupstoragereminder.GroupStorageReminderPlugin;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GroupStorageReminderPlugin.class);
		RuneLite.main(args);
	}
}