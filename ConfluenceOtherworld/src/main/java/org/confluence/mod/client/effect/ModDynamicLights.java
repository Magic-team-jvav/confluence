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
    }
}
