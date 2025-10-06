package org.confluence.mod.common.item.common;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class BossSummoningItem extends TooltipItem {
    private final Predicate<Player> condition;
    private final Function<Level, Mob> factory;

    public BossSummoningItem(Predicate<Player> condition, Function<Level, Mob> factory, List<Component> tooltips) {
        super(new Properties(), ModRarity.BLUE, tooltips);
        this.condition = condition;
        this.factory = factory;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (level instanceof ServerLevel serverLevel && condition.test(player)) {
            itemStack.shrink(1);
            Mob mob = factory.apply(level);
            if (!level.getEntitiesOfClass(mob.getClass(), player.getBoundingBox().inflate(Short.MAX_VALUE)).isEmpty()) {
                return InteractionResultHolder.fail(itemStack);
            }
            if (mob instanceof AbstractTerraBossBase boss) {
                boss.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
            }
            mob.setPos(player.getX() + level.random.nextInt(-50, 51), player.getY(), player.getZ() + level.random.nextInt(-50, 51));
            level.addFreshEntity(mob);
        }
        return InteractionResultHolder.success(itemStack);
    }
}
