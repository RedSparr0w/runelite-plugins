package com.groupstoragereminder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup(GroupStorageReminderConfig.GROUP)
public interface GroupStorageReminderConfig extends Config
{
  String GROUP = "groupstoragereminder";

  @ConfigItem(
    keyName = "reminderColor",
    name = "Reminder text color",
    description = "Color to highlight items that need to be returned.",
    position = 4
  )
  default Color reminderColor()
  {
    return new Color(241, 196, 15);
  }

  @ConfigItem(
    keyName = "alwaysShowBankedItems",
    name = "Always show banked items",
    description = "Continue to show a reminder for banked items.",
    position = 5
  )
  default boolean alwaysShowBankedItems()
  {
    return true;
  }

  @ConfigItem(
    keyName = "alwaysShowInventoryItems",
    name = "Always show inventory items",
    description = "Continue to show a reminder for worn items and items in inventory.",
    position = 5
  )
  default boolean alwaysShowInventoryItems()
  {
    return false;
  }

  @ConfigItem(
    keyName = "reminderTimerOnBankClose",
    name = "Remind on bank close",
    description = "Continue to show a reminder when closing the bank with tracked items still in inventory/worn/bank for x seconds.",
    position = 5
  )
  default int reminderTimerOnBankClose()
  {
    return 10;
  }

  @ConfigItem(
    keyName = "itemList",
    name = "Items List",
    description = "The list of items to remind players to return to group storage. Each item name should be on a new line.",
    position = 99
  )
  default String itemList()
  {
    return "Dragon warhammer\nTwisted bow\nBandos god sword"; // Default items, can be overridden in the config
  }

  @ConfigItem(
    keyName = "itemList",
    name = "",
    description = ""
  )
  void setItemList(String items);


  /*
   * Store our items between sessions.
   */
  @ConfigItem(
    keyName = "storedInventory",
    name = "Stored Inventory",
    description = "Serialized list of inventory items",
    hidden = true
  )
  default String storedInventory() { return ""; }

  @ConfigItem(
    keyName = "storedInventory",
    name = "Stored Inventory",
    description = ""
  )
  void setStoredInventory(String items);

  @ConfigItem(
    keyName = "storedBank",
    name = "Stored Bank",
    description = "Serialized list of bank items",
    hidden = true
  )
  default String storedBank() { return ""; }

  @ConfigItem(
    keyName = "storedBank",
    name = "Stored Bank",
    description = ""
  )
  void setStoredBank(String items);
}
