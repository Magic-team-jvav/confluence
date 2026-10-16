package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.api.item.IWeaponTooltip;
import org.confluence.mod.util.PlayerUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulWeaponItem extends CustomRarityItem implements IWeaponTooltip {
    protected final float damage;
    private final List<ResourceLocation> grantedSoulSkills;

    public SoulWeaponItem(Properties properties, ModRarity rarity, float damage) {
        this(properties, rarity, damage, List.of());
    }

    public SoulWeaponItem(Properties properties, ModRarity rarity, float damage, List<ResourceLocation> grantedSoulSkills) {
        super(properties, rarity);
        this.damage = damage;
        this.grantedSoulSkills = List.copyOf(grantedSoulSkills);
    }

    /// 返回武器自身的基础灵魂伤害，不预乘属性倍率。
    public float getSoulDamage(ItemStack stack) {
        return damage;
    }

    /// 返回手持时临时提供的技能标识。
    public List<ResourceLocation> getGrantedSoulSkills(ItemStack stack) {
        return grantedSoulSkills;
    }

    /// 主手右键通过服务端公共入口使用当前技能。
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (usedHand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.fail(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (!PlayerUtils.tryUseSoulSkill(serverPlayer)) {
                return InteractionResultHolder.fail(stack);
            }
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public Holder<Attribute> getTooltipDamageAttribute() {
        return LibAttributes.getSoulDamage();
    }

    @Override
    public double getTooltipDamage(ItemStack stack, double attributeValue) {
        return getSoulDamage(stack) * attributeValue;
    }
}
