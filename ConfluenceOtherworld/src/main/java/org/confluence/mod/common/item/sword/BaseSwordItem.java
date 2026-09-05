package org.confluence.mod.common.item.sword;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;
import org.mesdag.portlib.wrapper.common.extensions.IPortAttributesExtension;

import java.util.List;
import java.util.Optional;

public class BaseSwordItem extends SwordItem {
    private final SwordDefinition definition;
    private final @Nullable SwordProjectileComponent projectileDefinition;
    private @Nullable TooltipComponent tooltipImage;

    public BaseSwordItem(Tier tier, int rawDamage, float rawSpeed) {
        this(tier, ModRarity.WHITE, rawDamage, rawSpeed);
    }

    public BaseSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
        this(tier, rarity, rawDamage, rawSpeed, SwordDefinition.builder());
    }

    public BaseSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, SwordDefinition.Builder builder) {
        this(tier, rawDamage, rawSpeed, builder.build(tier, rarity, rawDamage, rawSpeed));
    }

    private BaseSwordItem(Tier tier, int rawDamage, float rawSpeed, SwordDefinition.BuildResult result) {
        super(tier, (int) ModItems.getAttackDamage(tier, rawDamage), ModItems.getAttackSpeed(rawSpeed), result.properties());
        definition = result.definition();
        projectileDefinition = result.projectile();
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (tooltipImage == null && definition.tooltipImage())
            tooltipImage = AltImageComponent.of(stack.getItem());
        return Optional.ofNullable(tooltipImage);
    }

    public void applyHitEffects(ItemStack weapon, @Nullable Entity attacker, LivingEntity victim, DamageSource source) {
        if (!source.is(PortTags.DamageTypes.PANIC_CAUSES)) return;
        if (attacker instanceof Player player) {
            if (!source.is(PortTags.DamageTypes.CAN_BREAK_ARMOR_STAND) || !source.is(PortTags.DamageTypes.IS_PLAYER_ATTACK) || player.getAttackStrengthScale(0.5F) <= 0.95F)
                return;
            definition.behaviors().forEach(behavior -> behavior.onDamage(weapon, player, victim, source));
        } else if (attacker instanceof LivingEntity living) {
            definition.behaviors().forEach(behavior -> behavior.onDamage(weapon, living, victim, source));
        }
    }

    public boolean tryFireProjectile(ServerPlayer player, InteractionHand hand) {
        ItemStack weapon = player.getItemInHand(hand);
        if (weapon.getItem() != this || player.getCooldowns().isOnCooldown(this)) return false;
        SwordProjectileComponent component = projectile(weapon);
        if (component == null) return false;
        int spawned = component.generation().genProjectile(player, weapon, component.getVelocity(player), () -> createProjectile(player, weapon, component));
        if (spawned == 0) return false;
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), component.getSoundEvent(), SoundSource.AMBIENT, 1.0F, 1.0F);
        player.getCooldowns().addCooldown(this, component.getCooldownTicks(player));
        player.swing(hand, true);
        return true;
    }

    public @Nullable SwordProjectileComponent projectile(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.SWORD_PROJECTILE, projectileDefinition);
    }

    private @Nullable SwordProjectile createProjectile(LivingEntity owner, ItemStack weapon, SwordProjectileComponent component) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(component.projType()).orElse(null);
        if (type == null || !(type.create(owner.level()) instanceof SwordProjectile projectile)) {
            Confluence.LOGGER.error("Sword projectile type {} must create SwordProjectile", component.projType());
            return null;
        }
        float damage = (float) (component.damageFactor() * owner.getAttributeValue(LibAttributes.getAttackDamage()));
        projectile.configure(owner, weapon, component, damage);
        return projectile;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!super.hurtEnemy(stack, target, attacker)) return false;
        definition.behaviors().forEach(behavior -> behavior.postHurtEnemy(stack, target, attacker));
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        SwordProjectileComponent data = projectile(stack);
        if (data != null) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj").withColor(0x57CDFB));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.damage").append(": x" + data.damageFactor()).withColor(0x57CDFB));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.speed").append(": " + data.baseSpeed()).withColor(0x57CDFB));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.cooldown").append(": " + data.cooldown()).withColor(0x57CDFB));
            data.trackType().ifPresent(type -> tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.track_type").append(": ").append(Component.translatable(type.getName())).withColor(0x57CDFB)));
        }
        for (int index = 0; index < definition.tooltips().size(); index++) {
            if (index == 0) tooltipComponents.add(CommonComponents.EMPTY);
            MutableComponent component = Component.translatable("tooltip.item.confluence." + BuiltInRegistries.ITEM.getKey(this).getPath() + "." + index).withStyle(style -> style.withColor(0x666666).withItalic(true));
            definition.tooltips().get(index).accept(component);
            tooltipComponents.add(component);
        }
    }

    @Override
    public AABB getSweepHitBox(ItemStack stack, Player player, Entity target) {
        return definition.specialSweep() ? getSpecialSweepArea(player) : super.getSweepHitBox(stack, player, target);
    }

    public static AABB getSpecialSweepArea(Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 up = player.getUpVector(1.0F);
        Vec3 forward = player.getViewVector(1.0F).scale(player.getAttributeValue(IPortAttributesExtension.entityInteractionRange()));
        Vec3 end = start.add(forward);
        Vec3 left = forward.cross(up);
        return new AABB(start.add(left), end.add(left.reverse()));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction itemAbility) {
        return super.canPerformAction(stack, itemAbility) && (itemAbility != ToolActions.SWORD_SWEEP || definition.canSweep());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        definition.behaviors().forEach(behavior -> behavior.inventoryTick(stack, level, entity, slotId, isSelected));
    }

    public float modifyDamage(ItemStack stack, DamageSource source, @Nullable Entity attacker, LivingEntity victim, float amount) {
        for (SwordBehavior behavior : definition.behaviors())
            amount = behavior.modifyDamage(stack, source, attacker, victim, amount);
        return amount;
    }

    public boolean hasSpecialSweep() {
        return definition.specialSweep();
    }
}
