package org.confluence.mod.common.item.bow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.common.generation.variant.AboveFallenGeneration;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import javax.annotation.Nullable;
import java.util.List;

public class DaedalusStormbow extends BaseTerraBowItem {
    private final IGeneration generation = new AboveFallenGeneration(30, 60, 25, 2, 25, 5);

    public DaedalusStormbow(float baseDamage, ModRarity rarity) {
        super(baseDamage, new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    @Override
    public void onUseTick(Level level, LivingEntity owner, ItemStack weapon, int remainingUseDuration) {
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

    @Override
    public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {}
}
