package org.confluence.mod.common.item.Whip;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.WhipEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terraentity.api.hit_effect.EffectStrategy;
import org.confluence.terraentity.init.TEAttributes;

public class BaseWhipItem extends Item {

    public DeferredHolder<EffectStrategy,EffectStrategy> hitEffectStrategy;
    public int hitCooldown;

    /**
     * <h1>鞭子
     * @param damage - 召唤伤害
     * @param hitEffectStrategy - 击中特效
     * @param hitCooldown - 击中同一目标的间隔
     */
    public BaseWhipItem(Properties properties,
                        float damage,
                        ModRarity rarity,
                        DeferredHolder<EffectStrategy,EffectStrategy> hitEffectStrategy,
                        int hitCooldown) {
        super(properties.attributes(
                ItemAttributeModifiers.builder()
                        .add(
                                TEAttributes.SUMMON_DAMAGE.getDelegate(),
                                new AttributeModifier(Confluence.asResource("whip_damage_modifier"), damage, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND
                        ).build()
                )
                .component(TCDataComponentTypes.MOD_RARITY, rarity)
        );
        this.hitEffectStrategy = hitEffectStrategy;
        this.hitCooldown = hitCooldown;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(!level.isClientSide){
            WhipEntity whipEntity = ModEntities.WHIP_PROJECTILE.get().create(level);
            whipEntity.setOwner(player);
            whipEntity.setPos(player.position().add(0,1,0).add(getPlayerHandPos(player)));
            whipEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0, 1.0F);
            whipEntity.hiteffect = hitEffectStrategy.get();
            whipEntity.hitCooldown = hitCooldown;
            level.addFreshEntity(whipEntity);
            player.getCooldowns().addCooldown(this, 20);
        }
        player.swing(usedHand);
        return super.use(level, player, usedHand);
    }

    private Vec3 getPlayerHandPos(Player player) {
        int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        float f =  player.yBodyRot* 0.017453292F + 1f;
        double d0 = Mth.sin(f);
        double d1 = Mth.cos(f);
        float f1 = player.getScale();
        double d2 = (double)i * 0.35 * (double)f1;
        double d3 = 0.8 * (double)f1;
        return new Vec3(-d1 * d2 - d0 * d3, 0, -d0 * d2 + d1 * d3);
    }
}