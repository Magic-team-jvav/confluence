package org.confluence.mod.client.light;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.lib.client.DynamicLightProvider;
import org.confluence.lib.client.DynamicLightRegister;
import org.confluence.lib.client.light.DynamicLightEffects;
import org.confluence.mod.client.handler.ClientPacketHandler;
import org.confluence.mod.common.entity.boss.PrimeEnderDragon;
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
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.confluence.terra_curio.common.init.TCEntities;

public final class ModDynamicLights {

    private ModDynamicLights() {}

    public static void register() {
        var attachments = DynamicLightRegister.<AttachmentEntityType<?>, AttachmentEntity>registerGroup(
                (partialTick, consumer) -> {
                    var level = Minecraft.getInstance().level;
                    if (level == null) return;
                    for (var player : level.players()) {
                        for (AttachmentEntity entity : player.getData(SummonerAttachmentTypes.ENTITY_DATA).getRenderCache()) {
                            consumer.accept(entity);
                        }
                    }
                }, AttachmentEntity::getType);
        attachments.register(SummonerAttachmentEntityTypes.IMP_FIREBALL.get(), (entity, partialTick) ->
                new DynamicLightDispatcher.LightSource(entity.getRenderNode(partialTick).pos(), 170));
        attachments.register(SummonerAttachmentEntityTypes.EYE_FIREBALL.get(), (entity, partialTick) ->
                new DynamicLightDispatcher.LightSource(entity.getRenderNode(partialTick).pos(), 170));
        attachments.register(SummonerAttachmentEntityTypes.EYE_LASER_TURRET.get(), (entity, partialTick) ->
                new DynamicLightDispatcher.LightSource(entity.getRenderNode(partialTick).pos(), 102));
        attachments.register(SummonerAttachmentEntityTypes.FORBIDDEN_ORB.get(), (entity, partialTick) ->
                new DynamicLightDispatcher.LightSource(entity.getRenderNode(partialTick).pos(), 102));
        attachments.register(SummonerAttachmentEntityTypes.SCULK_WISP.get(), (entity, partialTick) ->
                new DynamicLightDispatcher.LightSource(entity.getRenderNode(partialTick).pos(), 102));
        DynamicLightRegister.registerEntity(PrimeEnderDragon.class, (dragon, partialTick) -> {
            float range = dragon.getLaserRange();
            if (range > 0) {
                Vec3 origin = dragon.getLaserOrigin(partialTick);
                float pitch = Mth.rotLerp(partialTick, dragon.xRotO, dragon.getXRot());
                float yaw = Mth.rotLerp(partialTick, dragon.yRotO, dragon.getYRot());
                Vec3 end = origin.add(Vec3.directionFromRotation(pitch, yaw).scale(range));
                // 光束有多个采样点，直接提交本帧集合，不再额外返回中心光源。
                DynamicLightEffects.renderBeam(origin, end, 10);
            }
            return null;
        });
        DynamicLightRegister.register(ModEntities.FALLING_STAR.get(), DynamicLightProvider.entity(12));
        DynamicLightRegister.register(ModEntities.BALL_OF_FIRE.get(), DynamicLightProvider.entity(12));
        DynamicLightRegister.register(ModEntities.FLAMELASH.get(), DynamicLightProvider.entity(12));
        DynamicLightRegister.register(ModEntities.FLAME_CLOUD.get(), DynamicLightProvider.entity(12));
        DynamicLightRegister.register(ModEntities.FIRE_IMP_PROJECTILE.get(), DynamicLightProvider.entity(12));
        DynamicLightRegister.register(ModEntities.CULTIST_FIREBALL.get(), DynamicLightProvider.entity(12));
        DynamicLightRegister.register(ModEntities.CULTIST_ICE_MIST.get(), DynamicLightProvider.entity(6));
        DynamicLightRegister.register(ModEntities.CULTIST_LIGHTNING_ORB.get(), DynamicLightProvider.entity(10));
        DynamicLightRegister.register(ModEntities.CASCADE_FIRE.get(), DynamicLightProvider.entity(12));
        DynamicLightRegister.register(ModEntities.CURSED_FLAMES.get(), DynamicLightProvider.entity(15));
        DynamicLightRegister.register(ModEntities.CLINGER_FLAME.get(), DynamicLightProvider.entity(15));
        DynamicLightRegister.register(ModEntities.STAR_FURY.get(), DynamicLightProvider.entity(10));
        DynamicLightRegister.register(ModEntities.STAR_CANNON_BULLET.get(), DynamicLightProvider.entity(10));
        DynamicLightRegister.register(TCEntities.STAR_CLOAK.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(ModEntities.RAINBOW.get(), DynamicLightProvider.entity(10));
        DynamicLightRegister.register(ModEntities.BALL_OF_FROST.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(ModEntities.NPC_SHADOWFLAME_SKULL.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(ModEntities.MONSTER_LASER.get(), DynamicLightProvider.entity(10));
        DynamicLightRegister.register(ModEntities.MARTIAN_ELECTRIC_BOLT.get(), DynamicLightProvider.entity(10));
        DynamicLightRegister.register(ModEntities.MAGIC_MISSILE.get(), DynamicLightProvider.entity(10));
        DynamicLightRegister.register(ModEntities.WATER_BOLT.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(ModEntities.MAGIC_DAGGER.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(ModEntities.CRYSTAL_VILE_SHARD.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(ModEntities.CRYSTAL_STORM.get(), DynamicLightProvider.entity(6));
        DynamicLightRegister.register(ModEntities.CHIK_CRYSTAL.get(), DynamicLightProvider.entity(5));
        DynamicLightRegister.register(ModEntities.SKY_FRACTURE.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(ModEntities.FROST_BEAM.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(ModEntities.FROST_BLAST.get(), DynamicLightProvider.entity(8));
        DynamicLightRegister.register(ModEntities.STORM_SPEAR_SHOT.get(), DynamicLightProvider.entity(8));
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
        DynamicLightRegister.register(ModEntities.HOTLINE_FISHING_HOOK.get(), DynamicLightProvider.entity(6));
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
        DynamicLightRegister.registerProjectile(GunItems.LUMINITE_BULLET.get(), 10);
        DynamicLightRegister.registerProjectile(BoomerangItems.FLAMARANG.get(), 8);
        DynamicLightRegister.registerProjectile(YoyoItems.HEL_FIRE.get(), 8);
        DynamicLightRegister.registerProjectile(GunItems.CURSED_BULLET.get(), 8);
        DynamicLightRegister.registerProjectile(GunItems.ICHOR_BULLET.get(), 8);
        DynamicLightRegister.registerProjectile(BoomerangItems.ICE_BOOMERANG.get(), 5);
        DynamicLightRegister.registerProjectile(BoomerangItems.ENCHANTED_BOOMERANG.get(), 5);
        DynamicLightRegister.registerProjectile(BoomerangItems.SHROOMERANG.get(), 5);
        DynamicLightRegister.registerProjectile(GunItems.CRYSTAL_BULLET.get(), 5);
        DynamicLightRegister.registerProjectile(BoomerangItems.TRIMARANG.get(), 6);
        DynamicLightRegister.registerProjectile(YoyoItems.CASCADE.get(), 6);
        DynamicLightRegister.registerProjectile(YoyoItems.AMAROK.get(), 6);
        DynamicLightRegister.registerProjectile(YoyoItems.CHIK.get(), 6);
        DynamicLightRegister.registerProjectile(YoyoItems.KRAKEN.get(), 6);
        DynamicLightRegister.registerProjectile(GunItems.CHLOROPHYTE_BULLET.get(), 6);
        DynamicLightRegister.registerProjectile(YoyoItems.TERRARIAN.get(), 9);
        DynamicLightRegister.registerItem(Items.LAVA_BUCKET, 15);
        DynamicLightRegister.registerProjectile(Items.LAVA_BUCKET, 15);
        DynamicLightRegister.registerItemOverride(BasePhasebladeItem.class, (stack, holder, projectile) ->
                projectile || BasePhasebladeItem.isTurnOn(stack)
                        && !(holder instanceof Player player && BasePhasebladeItem.isThrown(player, stack)) ? 12 : 0);
        DynamicLightRegister.registerItemFallback(stack -> !(stack.getItem() instanceof BlockItem)
                && stack.is(ModTags.Items.PROVIDE_LIGHT) ? 10 : 0);
        DynamicLightRegister.registerEquipment(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.HEAD);
        DynamicLightRegister.registerEntity(Entity.class, DynamicLightProvider.entity(entity -> entity.isOnFire() ? 12 : 0));
        DynamicLightRegister.registerEntity(HillLavaPillarProjectile.class, DynamicLightProvider.entity(pillar -> pillar.isActive() ? 12 : 0));
        DynamicLightRegister.registerEntity(BaseManaStaffProjectileEntity.class, DynamicLightProvider.entity(staff -> {
            var variant = staff.getVariant();
            return variant == BaseManaStaffProjectileEntity.Variant.SPARK ? 12
                    : variant == BaseManaStaffProjectileEntity.Variant.FROST ? 5
                    : variant == BaseManaStaffProjectileEntity.Variant.THUNDER_ZAPPER ? 10 : 8;
        }));
        DynamicLightRegister.registerEntity(HostileParticleProjectile.class, DynamicLightProvider.entity(projectile -> switch (projectile.getVariant()) {
            case HILL_FIRE_BOUND, FIRE_IMP, INFERNO_BOLT -> 12;
            case WATER_SPHERE, CHAOS_BALL, RUNE_BLAST, SHADOW_BEAM, GASTROPOD,
                 WALL_OF_FLESH_LASER -> 8;
            case LOST_SOUL, VILE_SPIT -> 6;
        }));
        DynamicLightRegister.registerEntity(CurioFishingHook.class, DynamicLightProvider.entity(hook -> hook.getVariant() != CurioFishingHook.Variant.COMMON ? 5 : 0));
        DynamicLightRegister.registerEntity(BaseArrowEntity.class, DynamicLightProvider.entity(BaseArrowEntity::confluence$getLuminance));
        DynamicLightRegister.registerEntity(BaseBombEntity.class, DynamicLightProvider.entity(bomb -> bomb.emitter != null && !bomb.emitter.isRemoved() ? 4 : 0));
        DynamicLightRegister.registerEntity(LivingEntity.class, DynamicLightProvider.entity(living -> living.hasEffect(ModEffects.SHINE.get()) || living.hasEffect(MobEffects.GLOWING) ? 10 : 0));
        DynamicLightRegister.registerEntity(Player.class, DynamicLightProvider.entity(player -> {
            int equipment = ClientPacketHandler.getLuminance(player);
            return equipment < 0 ? (player.isEyeInFluid(FluidTags.WATER) ? -equipment : 0) : equipment;
        }));
        DynamicLightRegister.registerEntity(Player.class, DynamicLightProvider.entity(TCClientPacketHandler::getLuminance));
        DynamicLightRegister.registerProjectileItem(PhasebladeProjectile.class, PhasebladeProjectile::getItem);
        DynamicLightRegister.registerProjectileItem(BoomerangProjectile.class, BoomerangProjectile::getWeapon);
        DynamicLightRegister.registerProjectileItem(YoyoEntity.class, YoyoEntity::getWeapon);
        DynamicLightRegister.registerProjectileItem(BaseBulletEntity.class, BaseBulletEntity::getBulletStack);
        // 太空枪共用子弹实体，沿用已有同步的枪械类型标记。
        DynamicLightRegister.registerEntity(BaseBulletEntity.class, DynamicLightProvider.entity(bullet -> {
            if (bullet.getColorID().equals(BuiltInRegistries.ITEM.getKey(ManaWeaponItems.SPACE_GUN.get()).getPath()))
                return 10;
            if (bullet.getBulletStack().is(GunItems.METEOR_SHOT.get())) return 5;
            return bullet.getBulletStack().is(GunItems.NANO_BULLET.get()) ? 6 : 0;
        }));
    }
}
