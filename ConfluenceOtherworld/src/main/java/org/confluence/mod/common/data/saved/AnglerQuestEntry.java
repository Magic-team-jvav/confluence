package org.confluence.mod.common.data.saved;

import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.entity.npc.trade.TradeCondition;
import org.jetbrains.annotations.Nullable;

public record AnglerQuestEntry(ItemStack fish, @Nullable TradeCondition condition) {}
