package org.confluence.mod.common.item.bow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.terraentity.api.entity.IGeneration;
import org.confluence.terraentity.registries.generation.variant.AboveFallenGeneration;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class DaedalusStormbow extends BaseTerraBowItem {
    private final IGeneration generation = new AboveFallenGeneration(30, 60, 25, 2, 25, 5);

    public DaedalusStormbow(float baseDamage, ModRarity rarity) {
        super(baseDamage, new ModifyArrowBuilder().setRarity(rarity));
    }

    public void onUseTick(@NotNull Level level, @NotNull LivingEntity owner, @NotNull ItemStack weapon, int remainingUseDuration) {
        super.onUseTick(level, owner, weapon, remainingUseDuration);
        if (!level.isClientSide && owner instanceof Player player && remainingUseDuration % 4 == 0) {
            generation.genProjectile(player, weapon, 2f, () -> {
                ItemStack itemstack = owner.getProjectile(weapon);
                if (itemstack.isEmpty()) return null;
                ItemStack ammo = itemstack.copyWithCount(1);
                itemstack.shrink(1);
                return createProjectile(owner.level(), owner, weapon, ammo, true);
            });
        }
    }

    protected void shoot(@NotNull ServerLevel level, @NotNull LivingEntity shooter, @NotNull InteractionHand hand, @NotNull ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {}


//    @Override
//    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
//        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
//        tooltipComponents.add(Component.literal("miss translation: 33% no consume ammo"));
//    }
}
