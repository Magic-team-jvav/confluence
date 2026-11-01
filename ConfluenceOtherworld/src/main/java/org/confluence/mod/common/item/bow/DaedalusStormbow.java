package org.confluence.mod.common.item.bow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.util.generation.variant.AboveFallenGeneration;

import javax.annotation.Nullable;
import java.util.List;

public class DaedalusStormbow extends BaseTerraBowItem {
    private final IGeneration generation = new AboveFallenGeneration(30, 60, 25, 2, 25, 5);

    public DaedalusStormbow(float baseDamage, ModRarity rarity) {
        super(baseDamage, new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity));
    }

    @Override
    public void onUseTick(Level level, LivingEntity owner, ItemStack weapon, int remainingUseDuration) {
        super.onUseTick(level, owner, weapon, remainingUseDuration);
        if (level.isClientSide || !(owner instanceof Player player) || remainingUseDuration % 4 != 0) return;
        ItemStack ammunition = owner.getProjectile(weapon);
        if (ammunition.isEmpty()) return;
        /// 一根箭可以被转化成一发或多发（血雨弓 1–3 束血雨），箭本身只扣一根。
        ItemStack ammo = ammunition.copyWithCount(1);
        int shots = getVolleySize(level.random);
        if (shots <= 0) return;
        for (int i = 0; i < shots; i++) {
            generation.genProjectile(player, weapon, 2f, () -> createProjectile(owner.level(), owner, weapon, ammo, true));
        }
        ammunition.shrink(1);
    }

    /// 单次触发消耗一根箭后射出几发；代达罗斯风暴弓固定 1 发。
    protected int getVolleySize(RandomSource random) {
        return 1;
    }

    @Override
    public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {}

    @Override
    public double getTooltipAttackSpeed(ItemStack stack, double attributeValue) {
        return 5.0;
    }
}
