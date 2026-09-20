package org.confluence.mod.common.entity.npc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.entity.monster.slime.BaseSlime;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.SwordItems;

public class TownSlimeNPC extends BaseNPC {
    public TownSlimeNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile profile) {
        super(type, level, profile);
        moveControl = new HopMoveControl(this);
    }

    @Override
    public boolean canDefendSelf() {
        return false;
    }

    @Override
    public boolean isTownPet() {
        return true;
    }

    public static TownSlimeNPC unlock(ServerLevel level, EntityType<TownSlimeNPC> type, Vec3 position) {
        if (!LibUtils.isDev()) return null;
        // 解锁记录是全局进度，不表示其他区域已经拥有这一种 NPC。
        NPCSpawner.Region region = new NPCSpawner.Region(net.minecraft.core.BlockPos.containing(position));
        if (NPCSpawner.INSTANCE.hasNPCAlive(region, type)) return null;
        TownSlimeNPC slime = type.create(level);
        if (slime == null) return null;
        slime.setPos(position);
        if (!level.addFreshEntity(slime)) return null;
        NPCSpawner.INSTANCE.onNPCAdded(slime);
        return slime;
    }

    public static boolean tryEquipSquire(BaseSlime slime) {
        if (!(slime.level() instanceof ServerLevel level) || slime.getBossOwnerUUID() != null)
            return false;
        EntityType<?> type = slime.getType();
        if (type != MonsterEntities.BLUE_SLIME.get() && type != MonsterEntities.GREEN_SLIME.get()
                && type != MonsterEntities.RED_SLIME.get() && type != MonsterEntities.PURPLE_SLIME.get()
                && type != MonsterEntities.YELLOW_SLIME.get() && type != MonsterEntities.ICE_SLIME.get()
                && type != MonsterEntities.JUNGLE_SLIME.get() && type != MonsterEntities.DESERT_SLIME.get()
                && type != MonsterEntities.PINK_SLIME.get()) return false;
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, slime.getBoundingBox().inflate(0.2))) {
            if ((item.getItem().is(ArmorItems.COPPER_HELMET.get()) || item.getItem().is(SwordItems.COPPER_SHORT_SWORD.get()))
                    && unlock(level, NpcEntities.SQUIRE_SLIME.get(), slime.position()) != null) {
                item.getItem().shrink(1);
                if (item.getItem().isEmpty()) item.discard();
                slime.discard();
                return true;
            }
        }
        return false;
    }

    private static final class HopMoveControl extends MoveControl {
        private final TownSlimeNPC slime;
        private int jumpDelay;

        private HopMoveControl(TownSlimeNPC slime) {
            super(slime);
            this.slime = slime;
        }

        @Override
        public void tick() {
            slime.setXxa(0.0F);
            slime.setZza(0.0F);
            slime.setSpeed(0.0F);
            if (operation != Operation.MOVE_TO || slime.getInteractingPlayer() != null) return;
            operation = Operation.WAIT;

            double dx = wantedX - slime.getX();
            double dz = wantedZ - slime.getZ();
            if (dx * dx + dz * dz < 0.0001D) return;
            float yaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
            slime.setYRot(rotlerp(slime.getYRot(), yaw, 90.0F));
            slime.setYBodyRot(slime.getYRot());

            // 导航只指定方向；落地等待时不施加行走输入，起跳后才向前移动。
            if (slime.onGround()) {
                if (jumpDelay-- > 0) return;
                jumpDelay = 10 + slime.getRandom().nextInt(10);
                slime.getJumpControl().jump();
            } else if (slime.isInWaterOrBubble() || slime.isInLava()) {
                slime.getJumpControl().jump();
            }
            slime.setSpeed((float) (speedModifier * slime.getAttributeValue(Attributes.MOVEMENT_SPEED)));
        }
    }
}
