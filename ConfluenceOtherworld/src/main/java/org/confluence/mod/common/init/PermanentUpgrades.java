package org.confluence.mod.common.init;

import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.api.permanent.PermanentUpgrade;
import org.confluence.lib.api.permanent.PermanentUpgradeRegistry;
import org.confluence.lib.api.permanent.PermanentUpgradeResult;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.item.MinecartItems;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.UUID;

/// 汇流来世对 MagicLib 永久升级 API 的声明式使用。
///
/// <p>这里仅描述本体内容：稳定 ID、等级、前置条件和效果。物品消耗、声音、存档、死亡复制、失败反馈与
/// 登录恢复均由 MagicLib 处理。附属模组应在自己的初始化类中使用相同 Builder 注册自己的定义，
/// 不需要修改这个清单。</p>
public final class PermanentUpgrades {
    /// 每级增加 4 点最大生命，共 15 级；回溯下限与 1.21 现有内容保持一致。
    public static final PermanentUpgrade LIFE_CRYSTAL = register(PermanentUpgrade.builder(id("life_crystal"), 15)
            // 回溯生命水晶使用负增量修改同一份状态；最低四级仍保证原版玩家拥有正数最大生命。
            .minimumLevel(-4)
            .projectAtZero()
            .effect(context -> {
                installAttribute(context.player(), Attributes.MAX_HEALTH, id("life_crystal"),
                        context.level() * 4.0,
                        context.player().getHealth() + (isIncreasing(context) ? 4.0F : 0.0F));
                checkToppedOff(context.player());
            })
            .build());

    /// 生命水晶满级后才可使用，每级再增加 1 点最大生命，共 20 级。
    public static final PermanentUpgrade LIFE_FRUIT = register(PermanentUpgrade.builder(id("life_fruit"), 20)
            .requirement(context -> LIFE_CRYSTAL.getLevel(context.player()) >= LIFE_CRYSTAL.maxLevel()
                    ? PermanentUpgradeResult.APPLIED : PermanentUpgradeResult.PREREQUISITE_MISSING)
            .effect(context -> {
                installAttribute(context.player(), Attributes.MAX_HEALTH, id("life_fruit"), context.level(),
                        context.player().getHealth() + (isIncreasing(context) ? 1.0F : 0.0F));
                checkToppedOff(context.player());
            })
            .build());

    /// 只保存一次性开关；实际生命再生算法在玩家事件中按该稳定 ID 查询。
    public static final PermanentUpgrade VITAL_CRYSTAL = simple("vital_crystal");
    /// 一次性增加 4 点护甲，稳定 UUID 保证恢复时替换而非重复叠加。
    public static final PermanentUpgrade AEGIS_APPLE = register(PermanentUpgrade.builder(id("aegis_apple"), 1)
            .effect(context -> installAttribute(context.player(), Attributes.ARMOR, id("aegis_apple"), 4.0, null))
            .build());
    /// 永久增加 5% 方块破坏速度以及方块、墙的放置速度。
    public static final PermanentUpgrade AMBROSIA = register(PermanentUpgrade.builder(id("ambrosia"), 1)
            .effect(context -> {
                installAttribute(context.player(), PortAttributesExtension.blockBreakSpeed().value(), id("ambrosia"),
                        0.05, null, AttributeModifier.Operation.MULTIPLY_TOTAL);
                installAttribute(context.player(), ConfluenceMagicLib.PLACEMENT_SPEED.get(), id("ambrosia"),
                        0.05, null, AttributeModifier.Operation.MULTIPLY_TOTAL);
            })
            .build());
    /// 只保存一次性开关；钓鱼能力读取公共等级判断是否启用。
    public static final PermanentUpgrade GUMMY_WORM = simple("gummy_worm");
    /// 一次性增加 0.03 幸运值。
    public static final PermanentUpgrade GALAXY_PEARL = register(PermanentUpgrade.builder(id("galaxy_pearl"), 1)
            .effect(context -> installAttribute(context.player(), Attributes.LUCK, id("galaxy_pearl"), 0.03, null))
            .build());
    /// 首次使用时解锁机械矿车并投递对应物品。
    public static final PermanentUpgrade MINECART_UPGRADE_KIT = register(PermanentUpgrade.builder(id("minecart_upgrade_kit"), 1)
            .effect(context -> {
                if (!context.restoring() && context.previousLevel() == 0) {
                    deliverMinecartReward(context.player());
                }
            })
            .build());
    /// 一次性增加 4 格方块交互距离。
    public static final PermanentUpgrade ARTISAN_LOAF = register(PermanentUpgrade.builder(id("artisan_loaf"), 1)
            .effect(context -> installAttribute(context.player(), PortAttributesExtension.blockInteractionRange().value(),
                    id("artisan_loaf"), 4.0, null))
            .build());

    /// 魔力星继续由 ManaStorage 持久化，演示外部能力如何通过 levelAccess 接入公共 API。
    public static final PermanentUpgrade MANA_CRYSTAL = register(PermanentUpgrade.builder(id("mana_crystal"), 9)
            .levelAccess(player -> ManaStorage.of(player).getStarUpgrades(),
                    (player, level) -> ManaStorage.of(player).setStarUpgrades(level))
            .projectAtZero()
            // 魔力附件会在死亡时复制，但客户端 HUD 缓存必须在使用、登录和重生恢复时重新同步。
            .effect(context -> {
                PlayerUtils.syncMana2Client(context.player());
                checkToppedOff(context.player());
            })
            .build());
    /// 一次性魔力恢复强化开关；实际恢复逻辑从公共等级读取。
    public static final PermanentUpgrade ARCANE_CRYSTAL = simple("arcane_crystal");

    private PermanentUpgrades() {}

    public static void init() {
        // 触发类初始化；具体定义已在静态字段中完成公共注册。
    }

    public static void checkToppedOff(ServerPlayer player) {
        if (LIFE_CRYSTAL.getLevel(player) >= LIFE_CRYSTAL.maxLevel() &&
                LIFE_FRUIT.getLevel(player) >= LIFE_FRUIT.maxLevel() &&
                MANA_CRYSTAL.getLevel(player) >= MANA_CRYSTAL.maxLevel()) {
            AchievementUtils.awardAchievement(player, "topped_off");
        }
    }

    private static PermanentUpgrade simple(String path) {
        return register(PermanentUpgrade.builder(id(path), 1).build());
    }

    private static PermanentUpgrade register(PermanentUpgrade upgrade) {
        return PermanentUpgradeRegistry.register(upgrade);
    }

    private static net.minecraft.resources.ResourceLocation id(String path) {
        return Confluence.asResource(path);
    }

    private static void deliverMinecartReward(ServerPlayer player) {
        ItemStack reward = MinecartItems.MECHANICAL_CART.get().getDefaultInstance();
        boolean storedCompletely = player.getInventory().add(reward);
        if (!storedCompletely && !reward.isEmpty() && player.drop(reward, false) == null) {
            throw new IllegalStateException("Mechanical minecart reward could not be inserted or dropped");
        }
    }

    private static boolean isIncreasing(org.confluence.lib.api.permanent.PermanentUpgradeContext context) {
        return !context.restoring() && context.level() > context.previousLevel();
    }

    private static void installAttribute(ServerPlayer player, Attribute attribute,
                                         net.minecraft.resources.ResourceLocation upgradeId,
                                         double amount, Float healedHealth) {
        installAttribute(player, attribute, upgradeId, amount, healedHealth, AttributeModifier.Operation.ADDITION);
    }

    private static void installAttribute(ServerPlayer player, Attribute attribute,
                                         net.minecraft.resources.ResourceLocation upgradeId, double amount,
                                         Float healedHealth, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttributes().getInstance(attribute);
        if (instance == null) {
            throw new IllegalStateException("Player is missing an attribute required by a permanent upgrade: " + attribute);
        }
        UUID modifierId = PortAttributeModifier.rl2uuid(upgradeId);
        if (amount == 0.0) {
            // 零级投影负责清除回溯前遗留的稳定修饰符；remove 同样是幂等操作。
            instance.removeModifier(modifierId);
            if (healedHealth != null)
                player.setHealth(Math.min((float) instance.getValue(), healedHealth));
            return;
        }
        instance.addOrReplacePermanentModifier(new AttributeModifier(modifierId,
                "permanent_upgrade", amount, operation));
        if (healedHealth != null)
            player.setHealth(Math.min((float) instance.getValue(), healedHealth));
    }
}
