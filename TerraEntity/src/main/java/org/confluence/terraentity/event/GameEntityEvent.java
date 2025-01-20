package org.confluence.terraentity.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.confluence.terraentity.entity.ai.Boss;
import org.confluence.terraentity.entity.monster.AbstractMonster;
import org.confluence.terraentity.entity.monster.Decayeder;
import org.confluence.terraentity.entity.monster.demoneye.DemonEye;
import org.confluence.terraentity.entity.monster.demoneye.DemonEyeVariant;
import org.confluence.terraentity.entity.monster.slime.BaseSlime;
import org.confluence.terraentity.entity.monster.slime.BlackSlime;
import org.confluence.terraentity.entity.monster.slime.HoneySlime;
import org.confluence.terraentity.entity.util.DeathAnimOptions;
import org.confluence.terraentity.init.TEEffects;
import org.confluence.terraentity.init.TEEntities;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.utils.FloatRGB;

import static org.confluence.terraentity.TerraEntity.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
public class GameEntityEvent {
    @SubscribeEvent
    public static void entityJoinLevel(EntityJoinLevelEvent event) {
        Level level = event.getLevel();
        if (event.loadedFromDisk() || !(level instanceof ServerLevel serverLevel)) return;
        if (event.getEntity() instanceof Zombie zombie && !zombie.isBaby() && !zombie.isVehicle() && zombie.getRandom().nextFloat() < 0.05F) {
            BaseSlime slime = TEEntities.BLUE_SLIME.get().create(level);
            if (slime != null) {
                slime.moveTo(zombie.getX(), zombie.getY(), zombie.getZ(), zombie.getYRot(), 0.0F);
                slime.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(zombie.blockPosition()), MobSpawnType.JOCKEY, null);
                slime.startRiding(zombie);
                level.addFreshEntity(slime);
            }
        }
        if (event.getEntity() instanceof Boss boss && !level.isClientSide){
            if (boss.shouldShowMessage()){
                Component mes;
                FloatRGB color;
                if (event.getEntity() instanceof DeathAnimOptions dao){
                    float[] _color = dao.getBloodColor();
                    color = new FloatRGB(_color[0], _color[1], _color[2]);
                } else {
                    color = new FloatRGB(0.7F, 0, 0);
                }
                if (event.getEntity().getCustomName() != null){
                    mes = Component.translatable("message.terraentity.boss_spawn",
                            event.getEntity().getCustomName().getString()).withColor(color.get()).withStyle(ChatFormatting.BOLD);
                } else {
                    mes = Component.translatable("message.terraentity.boss_spawn",
                            event.getEntity().getName().getString()).withColor(color.get()).withStyle(ChatFormatting.BOLD);
                }
                for (Player player : level.players()){
                    player.sendSystemMessage(mes);
                }
            }
        }
    }

    @SubscribeEvent
    public static void FinalizeSpawnRegister(FinalizeSpawnEvent event) {
        if(event.getEntity() instanceof BlackSlime entity){
            entity.finalizeSpawn(entity.getRandom(),event.getDifficulty());
        }

    }
    @SubscribeEvent
    public static void FinalizeSpawnRegister(LivingEvent.LivingJumpEvent event) {

    }

    @SubscribeEvent
    public static void entityDeathLevel(LivingDeathEvent event) {
        Level level = event.getEntity().level();
        if (event.getEntity() instanceof Boss boss && !level.isClientSide){
            if (boss.shouldShowMessage()){
                Component mes;
                FloatRGB color;
                if (event.getEntity() instanceof DeathAnimOptions dao){
                    float[] _color = dao.getBloodColor();
                    color = new FloatRGB(_color[0], _color[1], _color[2]);
                } else {
                    color = new FloatRGB(0.7F, 0, 0);
                }
                if (event.getEntity().getCustomName() != null){
                    mes = Component.translatable("message.terraentity.boss_leave",
                            event.getEntity().getCustomName().getString()).withColor(color.get()).withStyle(ChatFormatting.BOLD);
                } else {
                    mes = Component.translatable("message.terraentity.boss_leave",
                            event.getEntity().getName().getString()).withColor(color.get()).withStyle(ChatFormatting.BOLD);
                }
                for (Player player : level.players()){
                    player.sendSystemMessage(mes);   //todo 报两次
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingDamageEntity(LivingDamageEvent.Post event) {
        // LivingEntity e = (LivingEntity) event.getSource().getEntity();
        // Caused by: java.lang.ClassCastException: class net.minecraft.world.entity.projectile.Arrow cannot be cast to class net.minecraft.world.entity.LivingEntity
        LivingEntity e1 = event.getEntity();
        Level level = event.getEntity().level();
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (event.getSource().getEntity() instanceof Decayeder dp){
            if (!e1.hasEffect(TEEffects.DEMONIC_THOUGHTS)){
                e1.addEffect(new MobEffectInstance(
                        TEEffects.DEMONIC_THOUGHTS, 200
                ), dp);
            } else {
                e1.removeEffect(TEEffects.DEMONIC_THOUGHTS);
                e1.hurt(event.getSource(), 6);
                AbstractMonster soulEater = TEEntities.EATER_OF_SOULS.get().create(level);
                soulEater.setPos(e1.getEyePosition());
                soulEater.setTarget(e1);
                level.addFreshEntity(soulEater);
                e1.removeEffect(TEEffects.DEMONIC_THOUGHTS);
            }
        }
    }

    @SubscribeEvent
    public static void entityInteract(PlayerInteractEvent.EntityInteract event){
        ItemStack item = event.getItemStack();
        if(!(event.getTarget() instanceof LivingEntity entity)) return;
        Player player = event.getEntity();
        Level level = event.getLevel();
        if (    entity.getType().equals(TEEntities.BLUE_SLIME.get()) ||
                entity.getType().equals(TEEntities.GREEN_SLIME.get()) ||
                entity.getType().equals(TEEntities.PURPLE_SLIME.get())){
            if (item.is(TETags.Items.HONEY_TRANSLATION_BUCKET)){
                HoneySlime slime = TEEntities.HONEY_SLIME.get().create(level);
                item.shrink(1);
                player.addItem(new ItemStack(Items.BUCKET));
                slime.setSize(2, true);
                slime.setPos(entity.position());
                slime.setXRot(entity.getXRot());
                slime.setYRot(entity.getYRot());
                level.addFreshEntity(slime);
                entity.remove(Entity.RemovalReason.DISCARDED);
            } else if (item.is(TETags.Items.HONEY_TRANSLATION)){
                HoneySlime slime = TEEntities.HONEY_SLIME.get().create(level);
                item.shrink(1);
                slime.setSize(2, true);
                slime.setPos(entity.position());
                slime.setXRot(entity.getXRot());
                slime.setYRot(entity.getYRot());
                level.addFreshEntity(slime);
                entity.remove(Entity.RemovalReason.DISCARDED);
            } else if (item.is(TETags.Items.HONEY_TRANSLATION_NOT_CONSUMED)){
                HoneySlime slime = TEEntities.HONEY_SLIME.get().create(level);
                slime.setSize(2, true);
                slime.setPos(entity.position());
                slime.setXRot(entity.getXRot());
                slime.setYRot(entity.getYRot());
                level.addFreshEntity(slime);
                entity.remove(Entity.RemovalReason.DISCARDED);
                event.setCanceled(true);
                return;
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void mobFinalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        RandomSource randomSource = mob.getRandom();
        if (mob instanceof DemonEye demonEye) {
            demonEye.setVariant(DemonEyeVariant.random(randomSource));
        } else if (mob instanceof BlackSlime blackSlime) {
            blackSlime.finalizeSpawn(randomSource, event.getDifficulty());
        }
    }

}
