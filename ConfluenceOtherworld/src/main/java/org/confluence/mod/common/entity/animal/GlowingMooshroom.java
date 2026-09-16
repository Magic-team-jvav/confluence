package org.confluence.mod.common.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.IForgeShearable;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.item.MaterialItems;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public final class GlowingMooshroom extends Cow implements GeoEntity, IForgeShearable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GlowingMooshroom(EntityType<? extends Cow> type, Level level) {
        super(type, level);
    }

    @Override
    public Cow getBreedOffspring(ServerLevel level, AgeableMob other) {
        return CritterEntities.GLOWING_MOOSHROOM.get().create(level);
    }

    @Override
    public boolean isShearable(ItemStack stack, Level level, BlockPos pos) {
        return isAlive() && !isBaby();
    }

    @Override
    public List<ItemStack> onSheared(Player player, ItemStack stack, Level level, BlockPos pos, int fortune) {
        if (level.isClientSide || !isShearable(stack, level, pos)) return List.of();
        Cow cow = convertTo(EntityType.COW, false);
        if (cow == null) return List.of();
        cow.setHealth(Math.min(getHealth(), cow.getMaxHealth()));
        level.playSound(null, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
        return List.of(new ItemStack(MaterialItems.GLOWING_MUSHROOM.get(), 5));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
}
