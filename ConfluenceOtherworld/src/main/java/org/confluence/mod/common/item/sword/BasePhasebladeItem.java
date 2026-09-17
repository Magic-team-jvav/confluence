package org.confluence.mod.common.item.sword;

import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.renderer.item.PhasebladeRenderer;
import org.confluence.mod.common.entity.projectile.sword.PhasebladeProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.ModItems;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public abstract class BasePhasebladeItem extends BaseSwordItem implements GeoItem {
    public enum PhaseColor {
        RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE, WHITE, PINK;

        public String resourceName() {
            return name().toLowerCase(java.util.Locale.ROOT);
        }
    }

    private static final String TURN_ON_KEY = "isTurnOn";
    private static final String GECKOLIB_ID_KEY = "GeckoLibID";
    private static final RawAnimation IDLE_OFF = RawAnimation.begin().thenLoop("idle_off");
    private static final RawAnimation IDLE_ON = RawAnimation.begin().thenLoop("idle_on");
    private final PhaseColor color;
    private final PortItemAttributeModifiers turnOnModifiers;
    private final PortItemAttributeModifiers turnOffModifiers;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected BasePhasebladeItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, PhaseColor color) {
        super(tier, rarity, rawDamage, rawSpeed, SwordDefinition.builder().unbreakable().specialSweep(0.8F).withoutBaseAttributes());
        this.color = color;
        turnOnModifiers = createAttributes(rawDamage - 1, rawSpeed - 4);
        turnOffModifiers = createAttributes(1, -2);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public abstract ResourceLocation modelResource();

    public abstract ResourceLocation animationResource();

    public abstract ProjectileGeometry projectileGeometry();

    protected abstract String texturePrefix();

    public final ResourceLocation textureResource() {
        return Confluence.asResource("textures/item/" + texturePrefix() + "/" + color.resourceName() + "_" + texturePrefix() + ".png");
    }

    public final ResourceLocation emissiveResource() {
        return Confluence.asResource("textures/item/" + texturePrefix() + "/" + color.resourceName() + "_" + texturePrefix() + "_mark.png");
    }

    public final PhaseColor color() {
        return color;
    }

    private static PortItemAttributeModifiers createAttributes(float attackDamage, float attackSpeed) {
        return PortItemAttributeModifiers.builder()
                .add(LibAttributes.getAttackDamage(), ModItems.BASE_ATTACK_DAMAGE_ID, attackDamage, PortAttributeModifier.Operation.ADD_VALUE, PortEquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, ModItems.BASE_ATTACK_SPEED_ID, attackSpeed, PortAttributeModifier.Operation.ADD_VALUE, PortEquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static boolean isTurnOn(ItemStack stack) {
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        return tag != null && tag.contains(TURN_ON_KEY, Tag.TAG_BYTE) && tag.getBoolean(TURN_ON_KEY);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND || !isTurnOn(stack))
            return InteractionResultHolder.pass(stack);
        if (player instanceof ServerPlayer serverPlayer) {
            var active = serverPlayer.serverLevel().getEntitiesOfClass(PhasebladeProjectile.class,
                    AABB.ofSize(player.position(), 256.0, 256.0, 256.0), projectile -> projectile.belongsTo(player));
            if (active.stream().anyMatch(projectile -> projectile.represents(stack))) {
                active.stream().filter(projectile -> projectile.represents(stack)).forEach(PhasebladeProjectile::recall);
            } else {
                PhasebladeProjectile projectile = this instanceof Phasesaber
                        ? ModEntities.PHASESABER_PROJECTILE.get().create(serverPlayer.serverLevel())
                        : ModEntities.PHASEBLADE_PROJECTILE.get().create(serverPlayer.serverLevel());
                if (projectile != null) {
                    projectile.configure(player, stack, (float) player.getAttributeValue(LibAttributes.getAttackDamage()),
                            (float) player.getAttributeValue(Attributes.ATTACK_KNOCKBACK));
                    if (level.addFreshEntity(projectile))
                        active.forEach(PhasebladeProjectile::recall);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /**
     * 创建射弹独立持有的武器快照。射弹始终以展开状态存在，不能跟随玩家物品栏里的
     * 同类物品在选中、切换或重新同步时一起收起。
     */
    public static ItemStack createProjectileStack(ItemStack source) {
        ItemStack projectileStack = source.copyWithCount(1);
        LibUtils.updateItemStackNbt(projectileStack, tag -> {
            tag.remove(TURN_ON_KEY);
            tag.remove(GECKOLIB_ID_KEY);
        });
        return projectileStack;
    }

    /**
     * 客户端实体专用渲染快照。只分配独立 GeckoLib 实例，不携带物品栏的开关状态。
     */
    public static ItemStack createProjectileRenderStack(ItemStack source, int entityId) {
        ItemStack renderStack = source.copyWithCount(1);
        LibUtils.updateItemStackNbt(renderStack, tag -> {
            tag.remove(TURN_ON_KEY);
            tag.putLong(GECKOLIB_ID_KEY, Long.MIN_VALUE + Integer.toUnsignedLong(entityId));
        });
        return renderStack;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel) || !(entity instanceof Player player))
            return;

        boolean shouldBeOn = selected && player.getMainHandItem() == stack;
        if (isTurnOn(stack) == shouldBeOn) return;

        LibUtils.updateItemStackNbt(stack, tag -> tag.putBoolean(TURN_ON_KEY, shouldBeOn));
        triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel), "light", shouldBeOn ? "on" : "off");
        level.playSound(null, player.blockPosition(), ModSoundEvents.LIGHTSABER_OPEN.get(), SoundSource.PLAYERS, shouldBeOn ? 2.0F : 1.0F, 1.0F);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return (isTurnOn(stack) ? turnOnModifiers : turnOffModifiers).getAttributeModifiers(slot);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "light", state -> {
            ItemStack stack = state.getData(DataTickets.ITEMSTACK);
            return state.setAndContinue(stack != null && isTurnOn(stack) ? IDLE_ON : IDLE_OFF);
        })
                .triggerableAnim("off", RawAnimation.begin().thenPlayAndHold("turn_off"))
                .triggerableAnim("on", RawAnimation.begin().thenPlayAndHold("turn_on")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private PhasebladeRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new PhasebladeRenderer();
                return renderer;
            }
        });
    }

    public record ProjectileGeometry(float minX, float maxX, float minY, float maxY, float minZ,
                                     float maxZ, float bladeLength) {
        public float width() {return Math.max(maxX - minX, maxZ - minZ);}

        public float length() {return maxY - minY;}

        public float centerX() {return (minX + maxX) * 0.5F;}

        public float centerY() {return (minY + maxY) * 0.5F;}

        public float centerZ() {return (minZ + maxZ) * 0.5F;}

        public float insertionDepth() {return bladeLength / 3.0F;}
    }
}
