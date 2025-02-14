package org.confluence.mod.common.item.mana;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCAttributes;

import java.util.function.Consumer;

public class ManaStaffItem<E extends Projectile> extends CustomRarityItem {
    public static final ResourceLocation ID = Confluence.asResource("mana_staff");
    private final ProjectileFactory<E> factory;
    private final int manaCost;
    private final float velocity;
    private final int cooldown;

    public ManaStaffItem(Properties properties, ModRarity rarity, ProjectileFactory<E> factory, int manaCost, float rawVelocity, int cooldown) {
        super(properties, rarity);
        this.factory = factory;
        this.manaCost = manaCost;
        this.velocity = rawVelocity / 8.0F;
        this.cooldown = cooldown;
    }

    public ManaStaffItem(ModRarity rarity, ProjectileFactory<E> factory, int manaCost, float rawVelocity, int cooldown, Consumer<ItemAttributeModifiers.Builder> consumer) {
        this(new Properties().stacksTo(1), rarity, factory, manaCost, rawVelocity, cooldown);
        addAttributeModifiers(consumer);
    }

    /**
     * @param rawVelocity 换算前的射弹速度
     */
    public ManaStaffItem(ModRarity rarity, ProjectileFactory<E> factory, int manaCost, float rawVelocity, int cooldown, double critChance) {
        this(new Properties().stacksTo(1), rarity, factory, manaCost, rawVelocity, cooldown);
        if (critChance == 0.0) return;
        addAttributeModifiers(builder -> builder.add(TCAttributes.getCriticalChance(), new AttributeModifier(ID, critChance, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BLOCK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer && couldShoot(serverPlayer, itemStack)) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            E projectile = factory.create(serverPlayer);
            beforeShoot(serverPlayer, itemStack, projectile);
            level.addFreshEntity(projectile);
            afterShoot(serverPlayer, itemStack, projectile);
        }
        return InteractionResultHolder.success(itemStack);
    }

    protected boolean couldShoot(ServerPlayer player, ItemStack itemStack) {
        return PlayerUtils.extractMana(player, () -> PrefixUtils.calculateManaCost(itemStack, manaCost));
    }

    protected void beforeShoot(ServerPlayer player, ItemStack itemStack, E projectile) {
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 0.0F);
    }

    protected void afterShoot(ServerPlayer player, ItemStack itemStack, E projectile) {
        if (cooldown > 0) {
            player.getCooldowns().addCooldown(this, cooldown);
        }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.REGULAR_STAFF_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @FunctionalInterface
    public interface ProjectileFactory<E extends Projectile> {
        E create(ServerPlayer serverPlayer);
    }
}
