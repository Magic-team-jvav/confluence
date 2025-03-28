package org.confluence.mod.client.gui;

import net.minecraft.world.item.Item;
import org.confluence.terraentity.init.item.TEBoomerangItems;

import java.util.HashMap;
import java.util.Map;

public class TooltipManager {

    public static final String prefix = "confluence.sponsor";
    private Map<Item, String> item_donator;
    private static TooltipManager ins;

    public TooltipManager() {
        ins = this;
    }

    public static TooltipManager getInstance() {
        if (ins == null) {
            ins = new TooltipManager();
            ins.item_donator = new HashMap<>();
            ins.add(TEBoomerangItems.BeiDou_BOOMERANG.get(), "bei_dou");

        }
        return ins;
    }

    private void add(Item item, String tooltip){
        item_donator.put(item, tooltip);
    }

    public boolean contains(Item item){
        return item_donator.containsKey(item);
    }

    public String getTooltip(Item item){
        return item_donator.get(item);
    }


}
