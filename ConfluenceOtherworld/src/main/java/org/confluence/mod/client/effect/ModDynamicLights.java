package org.confluence.mod.client.effect;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.mod.common.init.item.ArrowItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import org.confluence.mod.common.init.block.TorchBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.lib.client.DynamicLightProvider;
import org.confluence.lib.client.DynamicLightRegister;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.item.sword.BasePhasebladeItem;
import org.confluence.mod.integration.opallight.OpalLightIntegration;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;

public final class ModDynamicLights {

    private ModDynamicLights() {}

    public static void register() {
        DynamicLightRegister.register(MonsterEntities.DUNGEON_SPIRIT.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(MonsterEntities.PIXIE.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(MonsterEntities.ILLUMINANT_BAT.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(CritterEntities.GLOWING_MOOSHROOM.get(), DynamicLightProvider.entity(6));
        DynamicLightRegister.register(CritterEntities.GLOWING_CLUCKSHROOM.get(), DynamicLightProvider.entity(6));
        DynamicLightRegister.register(MonsterEntities.BLOOD_JELLY.get(), DynamicLightProvider.entity(6));
        DynamicLightRegister.register(MonsterEntities.FUNGO_FISH.get(), DynamicLightProvider.entity(6));
        DynamicLightRegister.register(MonsterEntities.ICE_ELEMENTAL.get(), DynamicLightProvider.entity(6));
        DynamicLightRegister.register(MonsterEntities.BLUE_JELLYFISH.get(), DynamicLightProvider.entity(6));
        DynamicLightRegister.register(MonsterEntities.GREEN_JELLYFISH.get(), DynamicLightProvider.entity(6));
        DynamicLightRegister.register(MonsterEntities.PINK_JELLYFISH.get(), DynamicLightProvider.entity(6));
        DynamicLightRegister.register(CritterEntities.GLOWING_SNAIL.get(), DynamicLightProvider.entity(5));
        DynamicLightRegister.register(CritterEntities.MAGMA_SNAIL.get(), DynamicLightProvider.entity(5));
        DynamicLightRegister.register(CritterEntities.HELL_BUTTERFLY.get(), DynamicLightProvider.entity(5));
        DynamicLightRegister.register(CritterEntities.FEALING.get(), DynamicLightProvider.entity(5));
        DynamicLightRegister.register(CritterEntities.FAIRY.get(), DynamicLightProvider.entity(5));
        DynamicLightRegister.register(CritterEntities.LIGHTNING_BUG.get(), DynamicLightProvider.entity(5));
        DynamicLightRegister.register(CritterEntities.FIREFLY.get(), DynamicLightProvider.entity(4));
        DynamicLightRegister.register(MonsterEntities.LUMINOUS_SLIME.get(), DynamicLightProvider.entity(7));
        DynamicLightRegister.register(CritterEntities.PRISMATIC_LACEWING.get(), DynamicLightProvider.entity(7));
        DynamicLightRegister.registerItem(ToolItems.BOTTOMLESS_LAVA_BUCKET.get(), 10);
        DynamicLightRegister.registerItem(ArmorItems.MINING_HELMET.get(), 10);
        DynamicLightRegister.registerItem(MaterialItems.FALLING_STAR.get(), 10);
        DynamicLightRegister.registerItem(MaterialItems.GLOWING_MUSHROOM.get(), 3);
        DynamicLightRegister.registerItem(MaterialItems.SOUL_OF_LIGHT.get(), 3);
        DynamicLightRegister.registerItem(MaterialItems.SOUL_OF_NIGHT.get(), 3);
        DynamicLightRegister.registerItem(MaterialItems.SOUL_OF_FLIGHT.get(), 3);
        DynamicLightRegister.registerItem(MaterialItems.SOUL_OF_MIGHT.get(), 3);
        DynamicLightRegister.registerItem(MaterialItems.SOUL_OF_SIGHT.get(), 3);
        DynamicLightRegister.registerItem(MaterialItems.SOUL_OF_FRIGHT.get(), 3);
        DynamicLightRegister.registerItem(MaterialItems.SOUL_OF_BRIGHT.get(), 3);
        DynamicLightRegister.registerItem(MaterialItems.SOUL_OF_VOIGHT.get(), 3);
        DynamicLightRegister.registerItem(Items.LAVA_BUCKET, 15);
        // OpalLight 数据里登记过的物品，亮度以数据为准（在相位剑规则之前，返回 -1 表示没登记、继续往下）
        DynamicLightRegister.registerItemOverride(Item.class, OpalLightIntegration::itemLightLevel);
        DynamicLightRegister.registerItemOverride(BasePhasebladeItem.class, (stack, holder, projectile) ->
                !projectile && BasePhasebladeItem.isTurnOn(stack)
                        && !(holder instanceof Player player && BasePhasebladeItem.isThrown(player, stack)) ? 12 : 0);
        DynamicLightRegister.registerItemFallback(stack -> !(stack.getItem() instanceof BlockItem)
                && stack.is(ModTags.Items.PROVIDE_LIGHT) ? 10 : 0);
        DynamicLightRegister.registerItemColor(OpalLightIntegration::itemLightColor);
        CombatLights.register();
        DynamicLightRegister.addCollector(org.confluence.mod.client.gameevent.ClientGameEventSystem::collectLights);
        // OpalLight 数据里登记过的实体（发光生物、相位剑射弹）：亮度和颜色取自数据，由 Magic Lib 平滑照亮
        DynamicLightRegister.registerEntity(Entity.class, OpalLightIntegration::entityLight);
        DynamicLightRegister.registerEquipment(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD);
        DynamicLightRegister.registerEntity(Entity.class, DynamicLightProvider.entity(entity -> !(entity instanceof Projectile) && entity.isOnFire() ? 12 : 0));
        DynamicLightRegister.registerEntity(LivingEntity.class, DynamicLightProvider.entity(living -> (living.hasEffect(ModEffects.SHINE) || living.hasEffect(MobEffects.GLOWING))
                && !OpalLightIntegration.handlesLivingEffect(living) ? 10 : 0));
        DynamicLightRegister.registerEntity(Player.class, DynamicLightProvider.entity(player -> {
            int equipment = ClientPacketHandler.getLuminance(player);
            return equipment < 0 ? (player.isEyeInFluid(FluidTags.WATER) ? -equipment : 0) : equipment;
        }));
        DynamicLightRegister.registerEntity(Player.class, DynamicLightProvider.entity(TCClientPacketHandler::getLuminance));

        // 火把在水里会熄灭（拿在手里、戴在头上、掉在水里的），和泰拉瑞亚一样诅咒火把、灵液火把、珊瑚火把除外
        DynamicLightRegister.suppressItemLightWhen((stack, holder) -> holder != null && holder.isEyeInFluid(FluidTags.WATER) && goesOutInWater(stack));
        DynamicLightRegister.suppressEntityLightWhen(entity -> entity instanceof ItemEntity item && item.isInWater() && goesOutInWater(item.getItem()));

        // 会发光的箭（泰拉瑞亚里这些箭飞行时自带光）：带颜色的平滑动态光；烈焰箭进水就灭
        // 有色光按纯色照亮，饱和的红、紫看起来比数字暗，所以等级给高一些
        arrow(ModEntities.FLAMING_ARROW.get(), 13, 0xFF9A3C, true);
        arrow(ModEntities.PIRATE_FLAMING_ARROW.get(), 13, 0xFF9A3C, true);
        arrow(ModEntities.HELLFIRE_ARROW.get(), 15, 0xFF6A2A, false);
        arrow(ModEntities.FROSTBURN_ARROW.get(), 13, 0x7FD6FF, false);
        arrow(ModEntities.UNHOLY_ARROW.get(), 12, 0xA060FF, false);
        arrow(ModEntities.STAR_ARROW.get(), 15, 0xFFF0A0, false);
        arrow(ModEntities.SHIMMER_ARROW.get(), 12, 0xFF9AE6, false);

        // 同样的箭拿在手里、搭在弓上（拉弓时）、装在弩里时也发光，比飞出去的稍弱
        arrowItem(ArrowItems.FLAMING_ARROW.get(), 11, 0xFF9A3C, true);
        arrowItem(ArrowItems.HELLFIRE_ARROW.get(), 13, 0xFF6A2A, false);
        arrowItem(ArrowItems.FROSTBURN_ARROW.get(), 11, 0x7FD6FF, false);
        arrowItem(ArrowItems.UNHOLY_ARROW.get(), 10, 0xA060FF, false);
        arrowItem(ArrowItems.STAR_ARROW.get(), 13, 0xFFF0A0, false);
        arrowItem(ArrowItems.SHIMMER_ARROW.get(), 10, 0xFF9AE6, false);
        DynamicLightRegister.registerItemColor((stack, holder) -> {
            ArrowLight light = ARROW_ITEMS.get(stack.getItem());
            return light == null ? -1 : light.color();
        });
        DynamicLightRegister.registerEntity(LivingEntity.class, DynamicLightProvider.entity(
                entity -> {
                    ArrowLight light = nockedArrow(entity);
                    return light == null || light.outInWater() && entity.isEyeInFluid(FluidTags.WATER) ? 0 : light.level();
                },
                entity -> {
                    ArrowLight light = nockedArrow(entity);
                    return light == null ? DynamicLightDispatcher.LightSource.WHITE : light.color() | DynamicLightDispatcher.LightSource.TINTED;
                }));
    }

    private record ArrowLight(int level, int color, boolean outInWater) {}

    private static final java.util.Map<Item, ArrowLight> ARROW_ITEMS = new java.util.HashMap<>();

    private static void arrowItem(Item item, int level, int color, boolean outInWater) {
        ARROW_ITEMS.put(item, new ArrowLight(level, color, outInWater));
        DynamicLightRegister.registerItem(item, level);
    }

    /// 拉弓时搭着的箭（弹药栏优先，见 ExtraInventory.getProjectile），或者手里已装填的弩里的箭
    private static ArrowLight nockedArrow(LivingEntity entity) {
        if (ARROW_ITEMS.isEmpty()) return null;
        if (entity.isUsingItem() && entity.getUseItem().getItem() instanceof ProjectileWeaponItem) {
            ArrowLight light = ARROW_ITEMS.get(entity.getProjectile(entity.getUseItem()).getItem());
            if (light != null) return light;
        }
        for (InteractionHand hand : InteractionHand.values()) {
            ChargedProjectiles charged = entity.getItemInHand(hand).get(DataComponents.CHARGED_PROJECTILES);
            if (charged == null || charged.isEmpty()) continue;
            for (ItemStack projectile : charged.getItems()) {
                ArrowLight light = ARROW_ITEMS.get(projectile.getItem());
                if (light != null) return light;
            }
        }
        return null;
    }

    private static <T extends Entity> void arrow(EntityType<T> type, int level, int color, boolean outInWater) {
        int tinted = color | org.confluence.lib.client.DynamicLightDispatcher.LightSource.TINTED;
        DynamicLightRegister.register(type, DynamicLightProvider.entity(entity -> outInWater && entity.isInWater() ? 0 : level, entity -> tinted));
    }

    private static boolean goesOutInWater(ItemStack stack) {
        ArrowLight arrow = ARROW_ITEMS.get(stack.getItem());
        if (arrow != null) return arrow.outInWater();
        if (!stack.is(ModTags.Items.TORCH) && !stack.is(Items.TORCH) && !stack.is(Items.SOUL_TORCH)) return false;
        return !stack.is(TorchBlocks.CURSED_TORCH.asItem()) && !stack.is(TorchBlocks.ICHOR_TORCH.asItem()) && !stack.is(TorchBlocks.CORAL_TORCH.asItem());
    }
}
