package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/// 甜蜜史莱姆 —— 完全被动，随时间成长，玩家可用玻璃瓶采集蜂蜜。
public class SweetSlime extends BaseSlime {
    public static final int GROWTH_INTERVAL = 20000;
    private int growthTicksRemaining = GROWTH_INTERVAL;

    public SweetSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, false);
    }

    /// 甜蜜史莱姆没有仇恨与攻击行为，仅保留通用史莱姆的巡游移动。
    @Override
    protected void registerGoals() {}

    @Override
    public void tick() {
        resetFallDistance();
        super.tick();
        if (!level().isClientSide && getSlimeSize() < 3 && growthTicksRemaining-- <= 0) {
            setSlimeSize(getSlimeSize() + 1);
            growthTicksRemaining = GROWTH_INTERVAL;
        }
    }

    @Override
    protected void onLanded() {
        if (!level().isClientSide) return;
        int size = getSlimeSize();
        for (int i = 0; i < size * 8; i++) {
            float angle = random.nextFloat() * Mth.TWO_PI;
            float radius = (random.nextFloat() * 0.5F + 0.5F) * size * 0.5F;
            double offsetX = Mth.sin(angle) * radius;
            double offsetZ = Mth.cos(angle) * radius;
            level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HONEY_BLOCK.defaultBlockState()),
                    getX() + offsetX, getY(), getZ() + offsetZ, offsetX * 0.08D, 0.12D, offsetZ * 0.08D);
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
            player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, new ItemStack(Items.HONEY_BOTTLE)));
            level().playSound(null, getX(), getY(), getZ(), SoundEvents.HONEY_DRINK, SoundSource.AMBIENT, 3.0F, 1.5F);
            dropFromLootTable(damageSources().playerAttack(player), true);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
