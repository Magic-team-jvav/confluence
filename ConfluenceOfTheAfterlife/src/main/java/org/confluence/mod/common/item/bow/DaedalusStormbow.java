package org.confluence.mod.common.item.bow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.item.sword.stagedy.projectile.IProjContainer;
import org.confluence.mod.common.item.sword.stagedy.projectile.StarFuryProjContainer;
import org.confluence.terra_curio.common.component.ModRarity;

import javax.annotation.Nullable;
import java.util.List;

public class DaedalusStormbow extends TerraBowItem {

    private IProjContainer container = new StarFuryProjContainer() {
        @Override
        protected float getOffsetV(){//随机高度
            return super.getOffsetV() + (float) ((Math.random()*2-1) * 5);
        }
        @Override
        protected void init(){
            this.offsetV = 25;//初始y高度
            this.inAccuracy = 10;//不精准度
            this.predict = 25;//提前量
            this.range = 60; //攻击范围
        }
        @Override
        public float getBaseVelocity() {
            return 1.5f;
        }
        @Override
        public Projectile getProjectile(Player player,ItemStack weapon) {
            return createProjectile(player.level(), player, weapon, player.getProjectile(weapon), true);
        }
        @Override
        public SoundEvent getSound() {
            return SoundEvents.ARROW_SHOOT;
        }
    };

    public DaedalusStormbow(float baseDamage, int durability, ModRarity rarity) {
        super(baseDamage, durability, rarity);
    }

    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
        if (!level.isClientSide && livingEntity instanceof Player player && remainingUseDuration % 4 == 0) {
            container.genProjectile(player,stack);
        }
    }

    protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {}
}
