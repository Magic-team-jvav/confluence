package org.confluence.mod.client.light;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.lib.client.DynamicLightRegister;
import org.confluence.lib.client.light.DynamicItemLights;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.entity.projectile.BoomerangProjectile;
import org.confluence.mod.common.entity.projectile.HillLavaPillarProjectile;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.bomb.BaseBombEntity;
import org.confluence.mod.common.entity.projectile.mana.BaseManaStaffProjectileEntity;
import org.confluence.mod.common.entity.projectile.sword.PhasebladeProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.item.sword.BasePhasebladeItem;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.confluence.terra_curio.common.init.TCEntities;

import java.util.Map;

public final class ModDynamicLights {
    private static final Map<AttachmentEntityType<?>, Integer> ATTACHMENT_LIGHTS = Map.ofEntries(
            Map.entry(SummonerAttachmentEntityTypes.IMP_FIREBALL.get(), 10),
            Map.entry(SummonerAttachmentEntityTypes.EYE_FIREBALL.get(), 10),
            Map.entry(SummonerAttachmentEntityTypes.EYE_LASER_TURRET.get(), 6),
            Map.entry(SummonerAttachmentEntityTypes.FORBIDDEN_ORB.get(), 6),
            Map.entry(SummonerAttachmentEntityTypes.SCULK_WISP.get(), 6)
    );

    private ModDynamicLights() {}

    public static void renderAttachment(AttachmentEntityType<?> type, Vec3 position) {
        int level = ATTACHMENT_LIGHTS.getOrDefault(type, 0);
        if (level > 0) DynamicLightDispatcher.INSTANCE.addLightSource(position, level * 17);
    }

    public static void register() {
        DynamicLightRegister.registerEntityLuminance(ModEntities.FALLING_STAR.get(), 12);
        DynamicLightRegister.registerEntityLuminance(ModEntities.BALL_OF_FIRE.get(), 12);
        DynamicLightRegister.registerEntityLuminance(ModEntities.FLAMELASH.get(), 12);
        DynamicLightRegister.registerEntityLuminance(ModEntities.FLAME_CLOUD.get(), 12);
        DynamicLightRegister.registerEntityLuminance(ModEntities.FIRE_IMP_PROJECTILE.get(), 12);
        DynamicLightRegister.registerEntityLuminance(ModEntities.CULTIST_FIREBALL.get(), 12);
        DynamicLightRegister.registerEntityLuminance(ModEntities.CULTIST_ICE_MIST.get(), 6);
        DynamicLightRegister.registerEntityLuminance(ModEntities.CULTIST_LIGHTNING_ORB.get(), 10);
        DynamicLightRegister.registerEntityLuminance(ModEntities.CASCADE_FIRE.get(), 12);
        DynamicLightRegister.registerEntityLuminance(ModEntities.CURSED_FLAMES.get(), 15);
        DynamicLightRegister.registerEntityLuminance(ModEntities.CLINGER_FLAME.get(), 15);
        DynamicLightRegister.registerEntityLuminance(ModEntities.STAR_FURY.get(), 10);
        DynamicLightRegister.registerEntityLuminance(ModEntities.STAR_CANNON_BULLET.get(), 10);
        DynamicLightRegister.registerEntityLuminance(TCEntities.STAR_CLOAK.get(), 8);
        DynamicLightRegister.registerEntityLuminance(ModEntities.RAINBOW.get(), 10);
        DynamicLightRegister.registerEntityLuminance(ModEntities.BALL_OF_FROST.get(), 8);
        DynamicLightRegister.registerEntityLuminance(ModEntities.NPC_SHADOWFLAME_SKULL.get(), 8);
        DynamicLightRegister.registerEntityLuminance(ModEntities.MONSTER_LASER.get(), 10);
        DynamicLightRegister.registerEntityLuminance(ModEntities.MARTIAN_ELECTRIC_BOLT.get(), 10);
        DynamicLightRegister.registerEntityLuminance(ModEntities.MAGIC_MISSILE.get(), 10);
        DynamicLightRegister.registerEntityLuminance(ModEntities.WATER_BOLT.get(), 8);
        DynamicLightRegister.registerEntityLuminance(ModEntities.MAGIC_DAGGER.get(), 8);
        DynamicLightRegister.registerEntityLuminance(ModEntities.CRYSTAL_VILE_SHARD.get(), 8);
        DynamicLightRegister.registerEntityLuminance(ModEntities.CRYSTAL_STORM.get(), 6);
        DynamicLightRegister.registerEntityLuminance(ModEntities.CHIK_CRYSTAL.get(), 5);
        DynamicLightRegister.registerEntityLuminance(ModEntities.SKY_FRACTURE.get(), 8);
        DynamicLightRegister.registerEntityLuminance(ModEntities.FROST_BEAM.get(), 8);
        DynamicLightRegister.registerEntityLuminance(ModEntities.FROST_BLAST.get(), 8);
        DynamicLightRegister.registerEntityLuminance(ModEntities.STORM_SPEAR_SHOT.get(), 8);
        DynamicLightRegister.registerEntityLuminance(MonsterEntities.DUNGEON_SPIRIT.get(), 8);
        DynamicLightRegister.registerEntityLuminance(MonsterEntities.PIXIE.get(), 8);
        DynamicLightRegister.registerEntityLuminance(MonsterEntities.ILLUMINANT_BAT.get(), 8);
        DynamicLightRegister.registerEntityLuminance(CritterEntities.GLOWING_MOOSHROOM.get(), 6);
        DynamicLightRegister.registerEntityLuminance(CritterEntities.GLOWING_CLUCKSHROOM.get(), 6);
        DynamicLightRegister.registerEntityLuminance(MonsterEntities.BLOOD_JELLY.get(), 6);
        DynamicLightRegister.registerEntityLuminance(MonsterEntities.FUNGO_FISH.get(), 6);
        DynamicLightRegister.registerEntityLuminance(MonsterEntities.ICE_ELEMENTAL.get(), 6);
        DynamicLightRegister.registerEntityLuminance(MonsterEntities.BLUE_JELLYFISH.get(), 6);
        DynamicLightRegister.registerEntityLuminance(MonsterEntities.GREEN_JELLYFISH.get(), 6);
        DynamicLightRegister.registerEntityLuminance(MonsterEntities.PINK_JELLYFISH.get(), 6);
        DynamicLightRegister.registerEntityLuminance(ModEntities.HOTLINE_FISHING_HOOK.get(), 6);
        DynamicLightRegister.registerEntityLuminance(CritterEntities.GLOWING_SNAIL.get(), 5);
        DynamicLightRegister.registerEntityLuminance(CritterEntities.MAGMA_SNAIL.get(), 5);
        DynamicLightRegister.registerEntityLuminance(CritterEntities.HELL_BUTTERFLY.get(), 5);
        DynamicLightRegister.registerEntityLuminance(CritterEntities.FEALING.get(), 5);
        DynamicLightRegister.registerEntityLuminance(CritterEntities.FAIRY.get(), 5);
        DynamicLightRegister.registerEntityLuminance(CritterEntities.LIGHTNING_BUG.get(), 5);
        DynamicLightRegister.registerEntityLuminance(CritterEntities.FIREFLY.get(), 4);
        DynamicLightRegister.registerEntityLuminance(MonsterEntities.LUMINOUS_SLIME.get(), 7);
        DynamicLightRegister.registerEntityLuminance(CritterEntities.PRISMATIC_LACEWING.get(), 7);
        DynamicItemLights.register(ToolItems.BOTTOMLESS_LAVA_BUCKET.get(), 10);
        DynamicItemLights.register(ArmorItems.MINING_HELMET.get(), 10);
        DynamicItemLights.register(MaterialItems.FALLING_STAR.get(), 10);
        DynamicItemLights.register(MaterialItems.GLOWING_MUSHROOM.get(), 3);
        DynamicItemLights.register(MaterialItems.SOUL_OF_LIGHT.get(), 3);
        DynamicItemLights.register(MaterialItems.SOUL_OF_NIGHT.get(), 3);
        DynamicItemLights.register(MaterialItems.SOUL_OF_FLIGHT.get(), 3);
        DynamicItemLights.register(MaterialItems.SOUL_OF_MIGHT.get(), 3);
        DynamicItemLights.register(MaterialItems.SOUL_OF_SIGHT.get(), 3);
        DynamicItemLights.register(MaterialItems.SOUL_OF_FRIGHT.get(), 3);
        DynamicItemLights.register(MaterialItems.SOUL_OF_BRIGHT.get(), 3);
        DynamicItemLights.register(MaterialItems.SOUL_OF_VOIGHT.get(), 3);
        DynamicItemLights.registerProjectile(GunItems.LUMINITE_BULLET.get(), 10);
        DynamicItemLights.registerProjectile(BoomerangItems.FLAMARANG.get(), 8);
        DynamicItemLights.registerProjectile(YoyoItems.HEL_FIRE.get(), 8);
        DynamicItemLights.registerProjectile(GunItems.CURSED_BULLET.get(), 8);
        DynamicItemLights.registerProjectile(GunItems.ICHOR_BULLET.get(), 8);
        DynamicItemLights.registerProjectile(BoomerangItems.ICE_BOOMERANG.get(), 5);
        DynamicItemLights.registerProjectile(BoomerangItems.ENCHANTED_BOOMERANG.get(), 5);
        DynamicItemLights.registerProjectile(BoomerangItems.SHROOMERANG.get(), 5);
        DynamicItemLights.registerProjectile(GunItems.CRYSTAL_BULLET.get(), 5);
        DynamicItemLights.registerProjectile(BoomerangItems.TRIMARANG.get(), 6);
        DynamicItemLights.registerProjectile(YoyoItems.CASCADE.get(), 6);
        DynamicItemLights.registerProjectile(YoyoItems.AMAROK.get(), 6);
        DynamicItemLights.registerProjectile(YoyoItems.CHIK.get(), 6);
        DynamicItemLights.registerProjectile(YoyoItems.KRAKEN.get(), 6);
        DynamicItemLights.registerProjectile(GunItems.CHLOROPHYTE_BULLET.get(), 6);
        DynamicItemLights.registerProjectile(YoyoItems.TERRARIAN.get(), 9);
        DynamicItemLights.register(Items.LAVA_BUCKET, 15);
        DynamicItemLights.registerProjectile(Items.LAVA_BUCKET, 15);
        DynamicItemLights.registerOverride(BasePhasebladeItem.class, (stack, holder, projectile) ->
                projectile || BasePhasebladeItem.isTurnOn(stack)
                        && !(holder instanceof Player player && BasePhasebladeItem.isThrown(player, stack)) ? 12 : 0);
        DynamicItemLights.registerFallback(stack -> !(stack.getItem() instanceof BlockItem)
                && stack.is(ModTags.Items.PROVIDE_LIGHT) ? 10 : 0);
        DynamicLightRegister.registerEquipment(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD);
        DynamicLightRegister.registerEntityLuminance(Entity.class, entity -> entity.isOnFire() ? 12 : 0);
        DynamicLightRegister.registerEntityLuminance(HillLavaPillarProjectile.class, pillar -> pillar.isActive() ? 12 : 0);
        DynamicLightRegister.registerEntityLuminance(BaseManaStaffProjectileEntity.class, staff -> {
            var variant = staff.getVariant();
            return variant == BaseManaStaffProjectileEntity.Variant.SPARK ? 12
                    : variant == BaseManaStaffProjectileEntity.Variant.FROST ? 5
                    : variant == BaseManaStaffProjectileEntity.Variant.THUNDER_ZAPPER ? 10 : 8;
        });
        DynamicLightRegister.registerEntityLuminance(HostileParticleProjectile.class, projectile -> switch (projectile.getVariant()) {
            case HILL_FIRE_BOUND, FIRE_IMP, INFERNO_BOLT -> 12;
            case WATER_SPHERE, CHAOS_BALL, RUNE_BLAST, SHADOW_BEAM, GASTROPOD,
                 WALL_OF_FLESH_LASER -> 8;
            case LOST_SOUL, VILE_SPIT -> 6;
        });
        DynamicLightRegister.registerEntityLuminance(CurioFishingHook.class,
                hook -> hook.getVariant() != CurioFishingHook.Variant.COMMON ? 5 : 0);
        DynamicLightRegister.registerEntityLuminance(BaseArrowEntity.class, BaseArrowEntity::getLuminance);
        DynamicLightRegister.registerEntityLuminance(BaseBombEntity.class,
                bomb -> bomb.emitter != null && !bomb.emitter.isRemoved() ? 4 : 0);
        DynamicLightRegister.registerEntityLuminance(LivingEntity.class,
                living -> living.hasEffect(ModEffects.SHINE) || living.hasEffect(MobEffects.GLOWING) ? 10 : 0);
        DynamicLightRegister.registerEntityLuminance(Player.class, player -> {
            int equipment = ClientPacketHandler.getLuminance(player);
            return equipment < 0 ? (player.isEyeInFluid(FluidTags.WATER) ? -equipment : 0) : equipment;
        });
        DynamicLightRegister.registerEntityLuminance(Player.class, TCClientPacketHandler::getLuminance);
        DynamicLightRegister.registerProjectileItem(PhasebladeProjectile.class, PhasebladeProjectile::getItem);
        DynamicLightRegister.registerProjectileItem(BoomerangProjectile.class, BoomerangProjectile::getWeapon);
        DynamicLightRegister.registerProjectileItem(YoyoEntity.class, YoyoEntity::getWeapon);
        DynamicLightRegister.registerProjectileItem(BaseBulletEntity.class, BaseBulletEntity::getBulletStack);
        // 太空枪共用子弹实体，沿用已有同步的枪械类型标记。
        DynamicLightRegister.registerEntityLuminance(BaseBulletEntity.class, bullet -> {
            if (bullet.getColorID().equals(BuiltInRegistries.ITEM.getKey(ManaWeaponItems.SPACE_GUN.get()).getPath()))
                return 10;
            if (bullet.getBulletStack().is(GunItems.METEOR_SHOT.get())) return 5;
            return bullet.getBulletStack().is(GunItems.NANO_BULLET.get()) ? 6 : 0;
        });
    }
}
