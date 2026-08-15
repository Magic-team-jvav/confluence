package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * 蜂蜜史莱姆会随时间成长，玩家可用玻璃瓶采集蜂蜜。
 */
public class HoneySlime extends BaseSlime {
    public static final int GROWTH_INTERVAL = 20000;
    private int growthTicksRemaining = GROWTH_INTERVAL;

    public HoneySlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xF8E234, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(0f, 0, 16.0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getSlimeSize() < 3 && growthTicksRemaining-- <= 0) {
            setSlimeSize(getSlimeSize() + 1);
            growthTicksRemaining = GROWTH_INTERVAL;
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide) {
            return super.mobInteract(player, hand);
        }
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.GLASS_BOTTLE) && getSlimeSize() == 3) {
            setSlimeSize(random.nextInt(1, 3));
            player.addItem(new ItemStack(Items.HONEY_BOTTLE));
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            level().playSound(null, getX(), getY(), getZ(),
                    SoundEvents.HONEY_DRINK, SoundSource.AMBIENT, 3.0F, 1.5F);
            dropFromLootTable(damageSources().playerAttack(player), true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
