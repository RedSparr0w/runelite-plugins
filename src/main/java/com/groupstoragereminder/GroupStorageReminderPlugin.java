package com.groupstoragereminder;

import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.MenuEntry;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.MenuAction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

@PluginDescriptor(
  name = "Group Storage Reminder",
  description = "Reminds players to return items to group storage",
  tags = {"highlight", "group", "shared", "overlay", "storage", "reminder", "gim", "ironman", "bank"}
)
@Slf4j
public class GroupStorageReminderPlugin extends Plugin
{
  @Inject
  private OverlayManager overlayManager;

  @Inject
  private Client client;

  @Inject
  private ItemManager itemManager;

  @Inject
  private GroupStorageReminderConfig config;

  @Inject
  private PluginOverlay pluginOverlay;

  boolean bankIsOpen = false;
  boolean groupStorageIsOpen = false;
  boolean reminderTimerActive = false;
  int reminderTimer = 0;
  boolean logoutSwitcherOpen = false;
  List<String> itemsOnPlayer = new ArrayList<>();
  List<String> itemsInBank = new ArrayList<>();

  private static final String ADD_TO_REMINDER = "Add to Group Storage Reminder";
  private static final String REMOVE_FROM_REMINDER = "Remove from Group Storage Reminder";

  @Provides
  GroupStorageReminderConfig provideConfig(ConfigManager configManager)
  {
    return configManager.getConfig(GroupStorageReminderConfig.class);
  }

  @Override
  protected void startUp() throws Exception
  {
    loadItems();
    overlayManager.add(pluginOverlay);
  }

  @Override
  protected void shutDown() throws Exception
  {
    saveItems();
    overlayManager.remove(pluginOverlay);
  }

  @Subscribe
  public void onGameTick(final GameTick event)
  {
    /*
     * ============================
     * Check if the logout/world switcher tab is open.
     * ============================
     */
    Widget logoutWorldSwitcherTab = client.getWidget(InterfaceID.WORLDSWITCHER, 0);
    if (logoutWorldSwitcherTab == null) logoutWorldSwitcherTab = client.getWidget(InterfaceID.LOGOUT, 0);

    if (logoutWorldSwitcherTab != null && !logoutWorldSwitcherTab.isHidden())
    {
      logoutSwitcherOpen = true;
    }
    else if (logoutSwitcherOpen)
    {
      logoutSwitcherOpen = closeInterface();
    }

    /*
     * ============================
     * Check if the bank was open and if the bank is now closed
     * ============================
     */
    if (bankIsOpen && client.getWidget(InterfaceID.BANKMAIN, 0) == null)
    {
      bankIsOpen = closeInterface();
    }

    /*
     * ============================
     * Check if the group storage was open and if the group storage is now closed
     * ============================
     */
    if (groupStorageIsOpen && client.getWidget(InterfaceID.SHARED_BANK, 0) == null)
    {
      groupStorageIsOpen = closeInterface();
    }

    /*
     * ============================
     * If the reminder timer is active, decrement it.
     * If it reaches 0, deactivate the timer.
     * ============================
     */
    if (reminderTimerActive)
    {
      reminderTimerActive = reminderTimer-- > 0;
    }
  }

  public boolean closeInterface()
  {
    // Reset the reminder timer
    reminderTimerActive = true;
    reminderTimer = (int)(config.reminderTimerOnBankClose() / 0.6);
    return false;
  }

  @Subscribe
  public void onWidgetLoaded(WidgetLoaded event)
  {
    if (event.getGroupId() == InterfaceID.BANKMAIN)
    {
      bankIsOpen = true;
      checkBankContainsItems();
      checkItemsOnPlayer();
    }
    if (event.getGroupId() == InterfaceID.SHARED_BANK)
    {
      groupStorageIsOpen = true;
      checkItemsOnPlayer();
    }
    if (event.getGroupId() == InterfaceID.INVENTORY)
    {
      checkItemsOnPlayer();
    }
  }

  @Subscribe
  public void onItemContainerChanged(ItemContainerChanged event)
  {
    if (event.getContainerId() == InventoryID.BANK)
    {
      checkBankContainsItems();
    }

    // Check if the bank or group storage is open before checking items on player as we don't want to check every time the inventory changes
    if (!bankIsOpen && !groupStorageIsOpen) return;
    
    if (event.getContainerId() == InventoryID.INV)
    {
      checkItemsOnPlayer();
    }
  }

  @Subscribe
  public void onMenuEntryAdded(MenuEntryAdded event)
  {
    // Only add menu entries if group storage is open
    if (!groupStorageIsOpen)
      return;

    // Get the item id
    if (event.getItemId() <= 0 || (event.getOption() != null && !event.getOption().equalsIgnoreCase("Examine")))
      return;

    String itemName = Text.removeTags(itemManager.getItemComposition(event.getItemId()).getName());
    Set<String> itemSet = getItemListSet();

    boolean inList = itemSet.stream().anyMatch(s -> s.equalsIgnoreCase(itemName));
    String option = "<col=" + (inList ? "e74c3c" : "2ecc71") + ">" + (inList ? REMOVE_FROM_REMINDER : ADD_TO_REMINDER) + "</col>";

    client.createMenuEntry(-1)
      .setOption(option)
      .setTarget("")
      .setType(MenuAction.RUNELITE)
      .onClick(e -> handleReminderMenuClick(itemName, inList));
  }

  private void handleReminderMenuClick(String itemName, boolean inList)
  {
    Set<String> itemSet = getItemListSet();
    if (inList)
    {
      // Remove (case-insensitive)
      itemSet.removeIf(s -> s.equalsIgnoreCase(itemName));
    }
    else
    {
      itemSet.add(itemName);
    }
    saveItemListSet(itemSet);
  }

  private Set<String> getItemListSet()
  {
    String[] items = config.itemList().split("\n");
    Set<String> set = new LinkedHashSet<>();
    for (String s : items)
    {
      String trimmed = s.trim();
      if (!trimmed.isEmpty())
        set.add(trimmed);
    }
    return set;
  }

  private void saveItemListSet(Set<String> set)
  {
    String joined = String.join("\n", set);
    config.setItemList(joined);
  }

  private void checkItemsOnPlayer()
  {
    itemsOnPlayer.clear();
    ItemContainer inventory = client.getItemContainer(InventoryID.INV);
    ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
    List<Item> allItems = new ArrayList<>();
    if (equipment != null) allItems.addAll(List.of(equipment.getItems()));
    if (inventory != null) allItems.addAll(List.of(inventory.getItems()));
    for (Item item : allItems)
    {
      int itemId = item.getId();
      if (itemId <= 0) continue;

      ItemComposition comp = itemManager.getItemComposition(itemId);
      String itemName = Text.removeTags(comp.getName());
      for (String targetName : getItemListSet())
      {
        if (WildcardMatcher.matches(targetName, itemName)) itemsOnPlayer.add(itemName);
      }
    }
    saveItems();
  }

  private void checkBankContainsItems()
  {
    ItemContainer bankContainer = client.getItemContainer(InventoryID.BANK);
    if (bankContainer != null)
    {
      itemsInBank.clear();
      Item[] bankItems = bankContainer.getItems();

      for (Item item : bankItems)
      {
        // Skip items with invalid item ID
        int itemId = item.getId();
        if (itemId <= 0) continue;
        // Skip placeholder items
        ItemComposition comp = itemManager.getItemComposition(itemId);
        if (comp.getPlaceholderTemplateId() != -1) continue;

        String itemName = Text.removeTags(comp.getName());
        for (String targetName : getItemListSet())
        {
          if (WildcardMatcher.matches(targetName, itemName)) itemsInBank.add(itemName);
        }
      }
      saveItems();
    }
  }

  void saveItems()
  {
    config.setStoredInventory(String.join(",", itemsOnPlayer));
    config.setStoredBank(String.join(",", itemsInBank));
  }

  void loadItems()
  {
    // Clear existing items
    itemsOnPlayer.clear();
    itemsInBank.clear();

    // Load items from inventory/worn
    for (String item : config.storedInventory().split(","))
    {
      if (!item.isEmpty()) itemsOnPlayer.add(item);
    }

    // Load items from bank
    for (String item : config.storedBank().split(","))
    {
      if (!item.isEmpty()) itemsInBank.add(item);
    }
  }

  public static class PluginOverlay extends Overlay
  {
    private final GroupStorageReminderPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    // Injected dependencies
    @Inject
    public PluginOverlay(GroupStorageReminderPlugin plugin)
    {
      super(plugin);
      this.plugin = plugin;
      setPosition(OverlayPosition.TOP_LEFT);
      setPriority(OverlayPriority.HIGH);
      setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
      if (
        !plugin.logoutSwitcherOpen &&
        !plugin.bankIsOpen &&
        !plugin.groupStorageIsOpen &&
        !(plugin.reminderTimerActive && !plugin.itemsOnPlayer.isEmpty()) &&
        !(plugin.reminderTimerActive && !plugin.itemsInBank.isEmpty()) &&
        !(!plugin.itemsInBank.isEmpty() && plugin.config.alwaysShowBankedItems()) &&
        !(!plugin.itemsOnPlayer.isEmpty() && plugin.config.alwaysShowInventoryItems())
      ) return null;

      if (plugin.itemsOnPlayer.isEmpty() && plugin.itemsInBank.isEmpty()) return null; // Don't render if no items to show

      panelComponent.getChildren().clear();

      String baseText = "GROUP STORAGE REMINDER";

      int size = graphics.getFontMetrics().stringWidth(baseText);
      panelComponent.setPreferredSize(new Dimension(size + 20, 0));

      panelComponent.getChildren().add(LineComponent.builder()
        .left(baseText)
        .leftColor(Color.WHITE)
        .build());

      // Show any items in bank
      if (!plugin.itemsInBank.isEmpty() && (plugin.logoutSwitcherOpen || plugin.bankIsOpen || plugin.groupStorageIsOpen || plugin.reminderTimerActive || plugin.config.alwaysShowBankedItems()))
      {
        panelComponent.getChildren().add(LineComponent.builder()
          .left("BANKED ITEMS:")
          .leftColor(Color.WHITE)
          .build());
        for (String itemName : plugin.itemsInBank)
        {
          panelComponent.getChildren().add(LineComponent.builder()
            .left(itemName)
            .leftColor(plugin.config.reminderColor())
            .build());
        }
      }

      // Only show items on player if bank or group storage is open or timer still active
      if (!plugin.itemsOnPlayer.isEmpty() && (plugin.logoutSwitcherOpen || plugin.bankIsOpen || plugin.groupStorageIsOpen || plugin.reminderTimerActive || plugin.config.alwaysShowInventoryItems()))
      {
        panelComponent.getChildren().add(LineComponent.builder()
          .left("INVENTORY/WORN:")
          .leftColor(Color.WHITE)
          .build());
        for (String itemName : plugin.itemsOnPlayer)
        {
          panelComponent.getChildren().add(LineComponent.builder()
            .left(itemName)
            .leftColor(plugin.config.reminderColor())
            .build());
        }
      }

      if (plugin.logoutSwitcherOpen)
      {
        panelComponent.getChildren().add(LineComponent.builder()
          .left("!!! ITEMS NOT IN STORAGE !!!")
          .leftColor(plugin.client.getTickCount() % 2 == 0 ? Color.RED : Color.WHITE)
          .build());
      }

      return panelComponent.render(graphics);
    }
  }
}
