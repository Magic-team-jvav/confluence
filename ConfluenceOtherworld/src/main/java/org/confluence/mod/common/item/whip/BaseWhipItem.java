package org.confluence.mod.common.item.whip;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.whip.*;
import org.confluence.mod.api.whip.curve.WhipCurve;
import org.confluence.mod.api.whip.curve.WhipCurves;
import org.confluence.mod.common.entity.projectile.whip.WhipAttackEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.summoner.summonMark.SummonMarkType;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import java.util.Objects;
import java.util.function.Supplier;

// 鞭子的共享挥动流程；具体数值、命中行为和外观由各物品类提供。
public class BaseWhipItem extends Item {
    private final float baseDamage;
    private final int durationTicks;
    private final int hitCooldownTicks;
    private final Supplier<? extends WhipTagEffect> tagEffect;
    private final WhipAppearance appearance;
    private Supplier<SummonMarkType> summonMarkType = null;

    public BaseWhipItem(String name, float baseDamage, float attackSpeedModifier, float range, int hitCooldownTicks, Supplier<? extends WhipTagEffect> tagEffect) {
        // 默认外观沿用 1.21 的十六像素分段模型。
        this(baseDamage, attackSpeedModifier, range, hitCooldownTicks, tagEffect,
                WhipAppearance.segments(WhipSegment.fixedSpacing(Confluence.asResource("item/whip/" + name), 16)));
    }

    public BaseWhipItem(float baseDamage, float attackSpeedModifier, float range, int hitCooldownTicks, Supplier<? extends WhipTagEffect> tagEffect, WhipAppearance appearance) {
        super(createProperties(attackSpeedModifier, range));
        this.baseDamage = baseDamage;
        this.durationTicks = Math.max(1, (int) (80.0 / (4.0 * (1.0 + attackSpeedModifier))));
        this.hitCooldownTicks = hitCooldownTicks;
        this.tagEffect = Objects.requireNonNull(tagEffect);
        this.appearance = Objects.requireNonNull(appearance);
    }

    private static Properties createProperties(float attackSpeedModifier, float range) {
        PortItemAttributeModifiers attributes = PortItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_SPEED, new PortAttributeModifier(Confluence.asResource("whip_attack_speed_modifier"), attackSpeedModifier, PortAttributeModifier.Operation.ADD_MULTIPLIED_BASE), PortEquipmentSlotGroup.MAINHAND)
                .add(ConfluenceMagicLib.WHIP_RANGE, new PortAttributeModifier(Confluence.asResource("whip_range_modifier"), range, PortAttributeModifier.Operation.ADD_MULTIPLIED_BASE), PortEquipmentSlotGroup.MAINHAND)
                .build();
        return new Properties().stacksTo(1).unbreakable().attributes(attributes);
    }

    public SummonMarkType getSummonMarkType() {
        return summonMarkType.get();
    }

    public float baseDamage() {return baseDamage;}

    public int durationTicks() {return durationTicks;}

    public int hitCooldownTicks() {return hitCooldownTicks;}

    public WhipTagEffect tagEffect() {return tagEffect.get();}

    public float damageFalloff() {return 0.8F;}

    public float minimumDamageMultiplier() {return 0.2F;}

    public boolean shouldApplyTag(int hitIndex) {return true;}

    public boolean penetratesBlocks() {return false;}

    public WhipCurve curve() {return WhipCurves.DEFAULT;}

    public void onDirectHit(WhipDirectHitContext context) {}

    public boolean canHitFriendlySummons() {return false;}

    public void onFriendlyHit(WhipFriendlyHitContext context) {}

    public WhipAppearance appearance() {
        return appearance;
    }

    /// 所有输入入口都经过服务端会话，不能通过连点或切换鞭子叠加攻击。
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer)
            WhipSession.requestSwing(serverPlayer, hand);
        // 仅在服务端实际创建攻击时挥手，拒绝续发时不能触发原版空挥动画。
        return InteractionResultHolder.consume(stack);
    }

    WhipAttackEntity createAttack(ServerPlayer player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        WhipAttackEntity attack = new WhipAttackEntity(ModEntities.WHIP_ATTACK.get(), player.level());
        HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        attack.setOwner(player);
        attack.setDamage(baseDamage() * (float) player.getAttributeValue(LibAttributes.getSummonDamage()));
        attack.initialize(stack, player.getLookAngle(), arm, resolveDurationTicks(player), (float) player.getAttributeValue(ConfluenceMagicLib.WHIP_RANGE));
        attack.setPos(handPosition(player, arm, 1.0F));
        if (!player.level().addFreshEntity(attack)) return null;
        player.swing(hand, true);
        player.awardStat(Stats.ITEM_USED.get(this));
        return attack;
    }

    public static float swingStep(Player player) {
        double speed = player.getAttributeValue(Attributes.ATTACK_SPEED);
        // 仅影响挥鞭速度，不给近战武器添加攻速属性。
        if (player.hasEffect(ModEffects.JUNGLES_FURY.get())) speed *= 1.12;
        return Double.isFinite(speed) ? (float) Mth.clamp(speed / 80.0, 0.0, 1.0) : 0.0F;
    }

    /// 按 1.21 的公式把玩家当前攻击速度换算为一次完整挥鞭所需的 tick 数。
    /// 物品本身、词缀、盔甲和状态效果对攻击速度的修改都会在读取属性时自然合并。
    public static int resolveDurationTicks(Player player) {
        Objects.requireNonNull(player, "Whip player must not be null");
        float step = swingStep(player);
        return step > 0.0F ? Math.max(1, (int) Math.ceil(1.0 / step)) : Integer.MAX_VALUE;
    }

    /// 逻辑与渲染共享持手锚点，避免第一人称另行变形鞭根。
    public static Vec3 handPosition(Player player, HumanoidArm arm, float partialTick) {
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float yaw = Mth.rotLerp(partialTick, player.yRotO, player.getYRot());
        float pitch = Mth.lerp(partialTick, player.xRotO, player.getXRot());
        Vec3 forward = Vec3.directionFromRotation(pitch, yaw);
        Vec3 right = Vec3.directionFromRotation(0.0F, yaw + 90.0F);
        Vec3 up = right.cross(forward).normalize();
        float scale = player.getScale();
        // 前后和高低偏移都跟随俯仰，直上直下时仍由 yaw 确定左右手方向。
        return player.getEyePosition(partialTick).add(right.scale(side * 0.25 * scale)).add(forward.scale(0.4 * scale)).add(up.scale(-0.45 * scale));
    }

}
