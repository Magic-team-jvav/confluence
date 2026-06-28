package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * 蜂蜜史莱姆 —— 完全被动，随时间成长，玩家可用玻璃瓶采集蜂蜜。
 */
public class HoneySlime extends BaseSlime {
    private int growthTicks;

    public HoneySlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xF8E234, false, -20);
        this.growthTicks = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(0f, 0, 16.0f, -20);
    }

    @Override
    public void tick() {
        super.tick();
        setTarget(null);
        if (!level().isClientSide) {
            growthTicks++;
        }
    }

    public int getHoneySize() {
        if (growthTicks < 6000) return 1;   // < 5 minutes
        if (growthTicks < 20000) return 2;  // 5-16 minutes
        return 3;                            // > 16 minutes
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.GLASS_BOTTLE) && getHoneySize() >= 3) {
            if (!level().isClientSide) {
                held.shrink(1);
                ItemStack honey = new ItemStack(Items.HONEY_BOTTLE);
                if (!player.getInventory().add(honey)) {
                    player.drop(honey, false);
                }
                growthTicks = 0;
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }
}
