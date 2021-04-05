package net.runelite.client.plugins.staking;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Set;
import javax.inject.Inject;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.api.widgets.WidgetID.*;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.staking.StakingPlugin;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

class StakingOverlay extends WidgetItemOverlay
{
	private final ItemManager itemManager;
	private final StakingPlugin plugin;
	private final StakingConfig config;
	private final Cache<Long, Image> fillCache;

    @Inject
    private Client client;

    @Inject
	private StakingOverlay(StakingPlugin plugin, StakingConfig config, ItemManager itemManager)
	{
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.config = config;
        showOnInventory();
        fillCache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .maximumSize(32)
                .build();
	}
    private static ArrayList<Integer> scamItems = new ArrayList<>();



    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {

        scamItems.add(ItemID.DRAGON_WARHAMMER);
        scamItems.add(ItemID.GHRAZI_RAPIER);
        scamItems.add(ItemID.ZAMORAKIAN_HASTA);
        scamItems.add(ItemID.INQUISITORS_MACE);
        scamItems.add(ItemID.BLADE_OF_SAELDOR_C);
        scamItems.add(ItemID.LUCKY_CUTLASS);
        scamItems.add(ItemID.HAM_JOINT);
        scamItems.add(ItemID.SWIFT_BLADE);
        scamItems.add(ItemID.RUNE_POUCH);

        if (!scamItems.contains(itemId) || !config.highlightBlacklist())
        {
            return;
        }

        Rectangle bounds = widgetItem.getCanvasBounds();

        if (config.showTagOutline()) {
            final BufferedImage outline = itemManager.getItemOutline(itemId, widgetItem.getQuantity(), config.scamColor());
            graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
        }

        if (config.showTagFill()) {
            final Image image = getFillImage(config.scamColor(), widgetItem.getId(), widgetItem.getQuantity());
            graphics.drawImage(image, (int) bounds.getX(), (int) bounds.getY(), null);
        }

    }
    @Override
    protected void showOnInventory()
    {
        showOnInterfaces(
                DUEL_INVENTORY_GROUP_ID,
                DUEL_INVENTORY_OTHER_GROUP_ID);
    }

    private Image getFillImage (Color color,int itemId, int qty)
    {
        long key = (((long) itemId) << 32) | qty;
        Image image = fillCache.getIfPresent(key);
        if (image == null) {
            final Color fillColor = config.scamColor();
            image = ImageUtil.fillImage(itemManager.getImage(itemId, qty, false), fillColor);
            fillCache.put(key, image);
        }
        return image;
    }
}
