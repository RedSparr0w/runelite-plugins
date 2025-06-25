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

  enum IconDisplaySetting
  {
    DISABLED,
    WHILE_BANK_OPEN,
    ALWAYS,
  }

  @ConfigItem(
    keyName = "showIconOnItems",
    name = "Show icon on matched items",
    description = "Show an icon on items that match your filters.",
    position = 6
  )
  default IconDisplaySetting showIconOnItems()
  {
    return IconDisplaySetting.ALWAYS;
  }

  enum IconOptions
  {
    GROUP_IRON_MAN,
    HARDCORE_GROUP_IRON_MAN,
    UNRANKED_GROUP_IRON_MAN,
    TIMER,
    WHITE_BAG,
    GOLD_LOCK,
    SILVER_LOCK,
    STEEL_LOCK,
    BLACK_LOCK,
    BRONZE_LOCK,
  }

  @ConfigItem(
    keyName = "iconDisplayType",
    name = "Remind on bank close",
    description = "Icon type to display on matched items.",
    position = 7
  )
  default IconOptions iconDisplayType()
  {
    return IconOptions.GROUP_IRON_MAN;
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
