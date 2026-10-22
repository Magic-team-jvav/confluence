package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
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
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SoulWeaponItem extends CustomRarityItem implements IWeaponTooltip {
    /// 主武器自带效果：物品只声明「带哪几条」，具体概率与数值全部写在 `SoulSkillParameters`。
    ///
    /// 判定入口统一是 `PlayerUtils.holdsSoulWeaponTrait`（看主手），因此换手/切换武器会立即失效。
    public enum Trait {
        /// 残破微光：手持时获得灵魂，有概率回复生命。
        HEAL_ON_SOUL_GAIN,
        /// 星魄：技能命中敌人时，概率额外落下一颗坠星砸向该敌人。
        STAR_ON_SKILL_HIT,
        /// 颂魂指引：引魂弹幕额外穿透敌怪。
        LURE_PIERCE,
        /// 带路的老前辈：引魂弹幕获得追踪。
        LURE_HOMING,
        /// 丛林的灯（生机灿烂）：技能命中敌人时附带中毒。
        POISON_ON_SKILL_HIT,
        /// 无分昼夜：技能命中敌人时附带暗影焰。
        SHADOWFLAME_ON_SKILL_HIT
    }

    protected final float damage;
    private final List<ResourceLocation> grantedSoulSkills;
    private final Set<Trait> traits;

    public SoulWeaponItem(Properties properties, ModRarity rarity, float damage) {
        this(properties, rarity, damage, List.of());
    }

    public SoulWeaponItem(Properties properties, ModRarity rarity, float damage, List<ResourceLocation> grantedSoulSkills, Trait... traits) {
        super(properties, rarity);
        this.damage = damage;
        this.grantedSoulSkills = List.copyOf(grantedSoulSkills);
        this.traits = traits.length == 0 ? Set.of() : EnumSet.copyOf(List.of(traits));
    }

    /// 返回武器自身的基础灵魂伤害，不预乘属性倍率。
    public float getSoulDamage(ItemStack stack) {
        return damage;
    }

    /// 返回手持时临时提供的技能标识。
    public List<ResourceLocation> getGrantedSoulSkills(ItemStack stack) {
        return grantedSoulSkills;
    }

    /// 该武器是否自带指定效果。
    public boolean hasTrait(Trait trait) {
        return traits.contains(trait);
    }

    public Set<Trait> getTraits() {
        return traits;
    }

    /// 主手右键通过服务端公共入口使用当前技能。
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (usedHand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.fail(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            /// 总开关解锁后，使用魂师主武器会把魔力条切成灵魂条。
            PlayerUtils.ensureResourceModeForSoulWeapon(serverPlayer);
            if (!PlayerUtils.tryUseSoulSkill(serverPlayer)) {
                return InteractionResultHolder.fail(stack);
            }
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public Attribute getTooltipDamageAttribute() {
        return LibAttributes.getSoulDamage().value();
    }

    @Override
    public double getTooltipDamage(ItemStack stack, double attributeValue) {
        return getSoulDamage(stack) * attributeValue;
    }

    @Override
    public double getTooltipDamage(ItemStack stack, double attributeValue, @Nullable Player player, boolean baseStats) {
        var skill = PlayerUtils.getCurrentSkillStack(player);
        float weaponDamage = getSoulDamage(stack);
        double damage = baseStats || player == null ? weaponDamage + skill.getDamage() : PlayerUtils.getSoulSkillDamage(player, skill, weaponDamage);
        return damage * attributeValue;
    }

}
