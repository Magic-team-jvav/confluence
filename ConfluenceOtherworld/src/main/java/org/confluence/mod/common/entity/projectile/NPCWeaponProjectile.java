package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// NPC 专用的虚拟弹药实体；只承载 NPC 自卫攻击，不消耗或掉落玩家物品。
public class NPCWeaponProjectile extends ThrowableItemProjectile {
    private float damage;
    private ResourceLocation effect = NPCProjectileEffects.NONE;
    @Nullable
    private UUID homingTargetId;

    /// 注册表工厂与客户端生成实体使用的基础构造器。
    public NPCWeaponProjectile(EntityType<? extends NPCWeaponProjectile> type, Level level) {
        super(type, level);
    }

    /// 创建带物品外观、固定伤害和命中策略 ID 的 NPC 虚拟弹药。
    public NPCWeaponProjectile(LivingEntity owner, ItemStack item, float damage, ResourceLocation effect) {
        super(ModEntities.NPC_WEAPON_PROJECTILE.get(), owner, owner.level());
        setItem(item.copyWithCount(1));
        this.damage = Math.max(0, damage);
        this.effect = effect;
    }

    /// 设置服务端追踪目标；不设置时保持普通抛射运动。
    public void setHomingTarget(@Nullable LivingEntity target) {
        homingTargetId = target == null ? null : target.getUUID();
    }

    /// 缺少同步物品时使用原版箭矢作为安全回退外观。
    @Override
    protected Item getDefaultItem() {
        return Items.ARROW;
    }

    /// 在原版弹体过滤之外调用命中策略自己的目标规则。
    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && NPCProjectileEffects.get(effect).canHit(createContext(), target);
    }

    /// 将实体命中交给 ID 对应策略，弹体本身不依赖任何具体效果类型。
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide) return;
        Entity target = result.getEntity();
        NPCProjectileEffects.get(effect).apply(createContext(), target);
        discard();
    }

    /// 方块命中同样调用策略，使爆炸等效果不要求必须直接命中实体。
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            NPCProjectileEffects.get(effect).apply(createContext(), null);
            discard();
        }
    }

    /// 清理飞行时间过长的虚拟弹药，避免未命中弹体长期占用世界实体。
    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            updateHomingMovement();
            if (tickCount > 100) discard();
        }
    }

    /// 保存服务端伤害与策略 ID，保证弹体跨区块卸载后行为不变。
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", damage);
        tag.putString("Effect", effect.toString());
        if (homingTargetId != null) tag.putUUID("HomingTarget", homingTargetId);
    }

    /// 恢复伤害与策略 ID；损坏或缺失的 ID 退回普通命中策略。
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = Math.max(0, tag.getFloat("Damage"));
        effect = ResourceLocation.tryParse(tag.getString("Effect"));
        if (effect == null) effect = NPCProjectileEffects.NONE;
        homingTargetId = tag.hasUUID("HomingTarget") ? tag.getUUID("HomingTarget") : null;
    }

    /// 创建包含当前所有者和伤害值的命中上下文。
    private NPCProjectileEffects.Context createContext() {
        LivingEntity owner = getOwner() instanceof LivingEntity living ? living : null;
        return new NPCProjectileEffects.Context(this, owner, damage);
    }

    /// 逐步把现有速度转向存活目标，避免追踪弹体瞬间折线转向。
    private void updateHomingMovement() {
        if (!(level() instanceof ServerLevel level) || homingTargetId == null) return;
        Entity entity = level.getEntity(homingTargetId);
        if (!(entity instanceof LivingEntity target) || !target.isAlive()) {
            homingTargetId = null;
            return;
        }
        Vec3 offset = target.getEyePosition().subtract(position());
        if (offset.lengthSqr() < 0.0001) return;
        double speed = Math.max(0.5, getDeltaMovement().length());
        Vec3 desired = offset.normalize().scale(speed);
        setDeltaMovement(getDeltaMovement().scale(0.8).add(desired.scale(0.2)));
        hasImpulse = true;
    }
}
