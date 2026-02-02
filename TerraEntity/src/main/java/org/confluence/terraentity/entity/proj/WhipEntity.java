package org.confluence.terraentity.entity.proj;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.terraentity.api.entity.IAttackableProjectile;
import org.confluence.terraentity.api.entity.IPartEntityTargetable;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.data.enchantment.TEEnchantmentHelper;
import org.confluence.terraentity.data.enchantment.TEEnchantments;
import org.confluence.terraentity.data.mappeddata.WeaponMappedDatas;
import org.confluence.terraentity.data.mappeddata.data.WhipPathManager;
import org.confluence.terraentity.entity.ai.keyframe.animation.Vec3KeyframeAnimation;
import org.confluence.terraentity.entity.ai.keyframe.dynamic_curve.SplineKeyframeDynamicCurve;
import org.confluence.terraentity.init.TEAttributes;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.confluence.terraentity.item.BaseWhipItem;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;

public class WhipEntity extends Projectile {
    /**
     * 伤害衰减的最小伤害值
     */
    protected final float _damageMin = 1f; // 最小伤害

    /**
     * 每次命中一次敌人，伤害乘以_damageDecline
     */
    protected final float _damageDecline = 0.8f; // 伤害衰减

    /**
     * 从bb的坐标转到mc坐标的系数
     */
    protected final float _rangeFactor = 0.1f; // 基础鞭范围
    /**
     * 记录命中的怪物，同一怪物短时间不能多次判定
     * <p>Value: hitCooldown</p>
     */
    final Map<Entity, Integer> hitEntities = new HashMap<>();
    /**
     * 命中同一怪物伤害判定的间隔
     */
    public int hitCooldown = 5; // 击中冷却时间
    /**
     * 存在时间参考值
     */
    final int _existTick = 22; // 基础存在时间

    int existTick = _existTick;
    int drawBackTick = 10; // 返回到player的过渡时间
    protected float damageDecline = 1f; // 伤害衰减

    public EffectStrategyComponent hiteffect; // 击中特效
    public EffectStrategyComponent hiteffect_beneficial; // 农场主增益


    // 初始位置
    public Vec3 initialPosition;
    // 初始方向
    public Vec3 initDirection;
    // 关键点位置，用于插值
    public List<Vector3f> keyPositions;
    // 用于客户端插值
    public List<Vector3f> keyPositionsO;
    // 关键点的本地坐标关键帧
    List<Vec3KeyframeAnimation> parts;
    // 关键点插值器
    public SplineKeyframeDynamicCurve<Vec3KeyframeAnimation> interpolator;

    // 记录已击中的方块
    List<Vec3> hittedPos = new ArrayList<>(Collections.emptyList());

    // 攻速
    public double speed = 1;
    float weepDamage = 1;
    ItemStack weapon;
    BaseWhipItem item;
    float serverRandom = -1;

    protected static final EntityDataAccessor<Vector3f> DATA_INITIAL_POSITION = SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Vector3f> DATA_INITIAL_DIRECTION = SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.VECTOR3);
    protected static final EntityDataAccessor<Integer> DATA_INITIAL_EXISTING_TIME = SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> DATA_WEAPON = SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Float> DATA_SERVER_RANDOM = SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.FLOAT);

    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level level) {
        super(entityType, level);

        this.noPhysics = true;
        this.noCulling = true;
    }

    /**
     * 设置鞭的武器
     */
    public void setWeapon(ItemStack weapon) {
        this.entityData.set(DATA_WEAPON, weapon);
        if(weapon.getItem() instanceof BaseWhipItem item1){
            item = item1;
        }
        var data = weapon.get(TEDataComponentTypes.EFFECT_STRATEGY);
        if (data != null)
            hiteffect = data;
        var data1 = weapon.get(TEDataComponentTypes.EFFECT_STRATEGY_BENEFICIAL);
        if (data1 != null)
            hiteffect_beneficial = data1;
        boolean triggerSweep = random.nextFloat() < 0.2f;
        serverRandom =  triggerSweep? 1: 0;
        this.entityData.set(DATA_SERVER_RANDOM, serverRandom);
        updateWeapon(weapon, triggerSweep);
    }

    private void updateWeapon(ItemStack weapon, boolean sweep){
        int sweepLevel = TEEnchantmentHelper.getEnchantmentLevel(TEEnchantments.WHIP_SWEEP, weapon);
        WhipPathManager.WhipPath path1 = MappedDataTypes.getData(MappedDataTypes.WEAPON_MAP_DATAS, WeaponMappedDatas.WHIP_PATHS).getWhipPath(weapon);
        if(sweepLevel > 0 && sweep) {
            // 横扫之鞭
            parts = path1.sweep;
            this.weepDamage += sweepLevel * 0.2f;
        }else {
            parts = path1.keys;
        }
        keyPositions = new ArrayList<>();
        keyPositionsO = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            keyPositions.add(new Vector3f());
            keyPositionsO.add(new Vector3f());
        }
        interpolator = new SplineKeyframeDynamicCurve<>(parts);
    }
    /**
     * 获取鞭的武器
     */
    public ItemStack getWeapon() {
        return this.entityData.get(DATA_WEAPON);
    }

    /**
     * 设置鞭存在时间
     */
    public void setExistTick(int existTick) {
        this.entityData.set(DATA_INITIAL_EXISTING_TIME, existTick);
        this.speed = (double) this.existTick / existTick;
        this.existTick = existTick;
        this.drawBackTick = (int) (existTick * 0.5F);
    }

    /**
     * 获取鞭范围
     */
    public double getRange(Player player) {
        return _rangeFactor * player.getAttribute(TEAttributes.WHIP_RANGE).getValue();
    }


    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            super.tick();
            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            this.setDeltaMovement(vec3.add(vec3.normalize().scale(0.1)));
            this.setPos(d0, d1, d2);
        } else {
            this.discard();
        }

        if (!level().isClientSide) {
            if (existTick < tickCount) {
                discard();
                return;
            }
        }

        if((int)(existTick * 0.3f) == tickCount){
            if(this.getOwner() != null) {
                this.getOwner().playSound(TESounds.WHIP_ATTACK.get(), 0.6F + getRandom().nextFloat() * 0.2f, 1.0F);
            }
        }


        if(parts == null || parts.isEmpty()) return;
        this.speed = (double) _existTick / this.existTick;
        if (getOwner() instanceof Player owner) {
            if (initialPosition != null && initDirection != null) {
                this.updatePosition(owner);
                this.attackDetect(owner);
            }
        }
    }

    private void attackDetect(Player owner) {
        boolean trigger = false;
        // 可以插值让攻击更准确
        List<Vec3> attackPoints = keyPositions.stream().map(Vec3::new).toList();

        float additionalRange = serverRandom == 1? 0.5f: 0;
        // 攻击
        float range = 1.5f + additionalRange;
        for (Vec3 attackPoint : attackPoints) {
            Vec3 pos = attackPoint.add(initialPosition);
            AABB aabb = new AABB(pos.x - range, pos.y - range, pos.z - range,
                    pos.x + range, pos.y + range, pos.z + range);

            if (!level().isClientSide){
                for (int x0 = (int) (pos.x - range); x0 <= pos.x + range; x0++){
                    for (int y0 = (int) (pos.y - range); y0 <= pos.y + range; y0++){
                        for (int z0 = (int) (pos.z - range); z0 <= pos.z + range; z0++){
                            Vec3 forwardP = new Vec3(x0,y0,z0);
                            if (!hittedPos.contains(forwardP)){
                                BlockPos blockPos = BlockPos.containing(forwardP);
                                BlockState blockstate = level().getBlockState(blockPos);
                                BlockHitResult blockHitResult = new BlockHitResult(forwardP, this.getDirection(), blockPos, true);
                                blockstate.onProjectileHit(level(), blockstate, blockHitResult, this);
                                hittedPos.add(forwardP);
                            }
                        }
                    }
                }
            }

            for (var entity : level().getEntities(this, aabb, e -> e != getOwner())) {
                IAttackableProjectile.tryHit(entity, getDamageSource());

                // 某些鞭子禁止穿墙攻击
                if (item != null && !item.canPenetrate && level().clip(new ClipContext(owner.getEyePosition(), entity.position().add(0, entity.getBbHeight(), 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner)).getType() != HitResult.Type.MISS
                        && level().clip(new ClipContext(owner.getEyePosition(), entity.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner)).getType() != HitResult.Type.MISS
                        &&level().clip(new ClipContext(owner.getEyePosition(), entity.position().add(0, entity.getBbHeight() * 0.5f, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner)).getType() != HitResult.Type.MISS) {
                    hitEntities.put(entity, hitCooldown);
                    continue;
                }

                if(!hitEntities.containsKey(entity)){
                    if (entity instanceof LivingEntity hurter) {
                        // 命中无多体节敌人
                        if(owner.canAttack(hurter) && TEUtils.projectileCanHurtEntityTest.test(this, hurter)) {
                            hitEntities.put(entity, hitCooldown);
                            trigger = doHurt(owner, hurter, hurter);
                        }
                        if(hurter instanceof ISummonMob){
                            if(hiteffect_beneficial != null){
                                hiteffect_beneficial.applyAll(owner, hurter);
                            }
                        }

                    }else if(entity instanceof PartEntity<?> partEntity){
                        // 名字多体节敌人
                        if(partEntity.getParent() instanceof LivingEntity hurter){
                            if(owner.canAttack(hurter) && TEUtils.projectileCanHurtEntityTest.test(this, hurter)) {
                                hitEntities.put(entity, hitCooldown);
                                trigger = doHurt(owner, hurter, partEntity);
                            }
                        }
                    }
                }else{
                    hitEntities.put(entity, hitEntities.get(entity) - 1);
                    if(hitEntities.get(entity) <= 0){
                        hitEntities.remove(entity);
                    }
                    return;
                }
            }
        }
        if(trigger){
            // 命中敌人造成伤害才消耗耐久
            getWeapon().hurtAndBreak(1, owner, EquipmentSlot.MAINHAND);
        }
    }

    private void updatePosition(Player owner) {
        // 计算关键点位置
        float yaw = (float) (Math.PI - Math.atan2(initDirection.z, initDirection.x));
        float pitch = (float) (-Math.atan2(initDirection.y,
                Math.sqrt(initDirection.x * initDirection.x + initDirection.z * initDirection.z)));
        Quaternionf q = new Quaternionf()
                .rotateY(yaw)
                .rotateZ(pitch);
        for (int i = 0; i < parts.size(); i++) {
            // 世界坐标变换
            Vec3KeyframeAnimation p = parts.get(i);
            Vec3 pos = p.cal(tickCount * speed).multiply(getRange(owner), -1, 1);
            Vector3f lp = pos.toVector3f();
            q.transform(lp);
            keyPositionsO.set(i, keyPositions.get(i));
            keyPositions.set(i, lp);
        }

        if(tickCount > drawBackTick){
            // 过渡到player位置
            double delta = (double) (tickCount - drawBackTick) / (existTick - drawBackTick);
            double lerpx = Mth.lerp(delta, getX(), getOwner().getEyePosition().x);
            double lerpy = Mth.lerp(delta, getY(), getOwner().position().y + getOwner().getEyeHeight() * 0.5f);
            double lerpz = Mth.lerp(delta, getZ(), getOwner().getEyePosition().z);

            Vec3 dir = new Vec3(lerpx - getX(), lerpy - getY(), lerpz - getZ());
            setDeltaMovement(0,0,0);
            move(MoverType.SELF, dir.scale(0.5f));

        }
    }

    protected boolean doHurt(LivingEntity owner, LivingEntity hurter, Entity actualHurter){
        double damage = owner.getAttributeValue(TEAttributes.SUMMON_DAMAGE);
        boolean trigger = false;
        if(TEUtils.attackTamableTest.test(owner, hurter)){
            owner.setLastHurtMob(hurter); // 让召唤物可以攻击敌人
            if(actualHurter instanceof PartEntity<?> && owner instanceof IPartEntityTargetable targetable){
                targetable.setActualTargetEntity(actualHurter);
            }
            trigger = true;
            damage *= damageDecline;
            damageDecline = Math.max(_damageMin, damageDecline * _damageDecline);
            if (hiteffect != null) {
                hiteffect.applyAll( owner, hurter);
            }
        }else{
            // 当命中宠物时
            if(getWeapon().getItem() == TEWhipItems.LEATHER_WHIP.get()){
                if(hiteffect_beneficial != null){
                    hiteffect_beneficial.applyAll( owner, hurter);
                }
                // 如果是皮鞭
                damage *= 0.2F;
            }else{
                return false;
            }
        }
        actualHurter.hurt(getDamageSource(), (float) damage * weepDamage);
        return trigger;
    }

    protected DamageSource getDamageSource(){
        if(this.getOwner() != null){
            return TETags.DamageTypes.of(level(), TETags.DamageTypes.SUMMON,  getOwner());
        }
        return damageSources().magic();
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Nullable
    protected ParticleOptions getTrailParticle() {
        return null;
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_INITIAL_POSITION, new Vector3f(0, 0, 0));
        builder.define(DATA_INITIAL_DIRECTION, new Vector3f(0, 0, 0));
        builder.define(DATA_WEAPON, ItemStack.EMPTY);
        builder.define(DATA_INITIAL_EXISTING_TIME, 22);
        builder.define(DATA_SERVER_RANDOM, -1f);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> var1){
        if(level().isClientSide){

            if (var1 == DATA_INITIAL_POSITION) {
                initialPosition = new Vec3(this.entityData.get(DATA_INITIAL_POSITION));
            } else if (var1 == DATA_INITIAL_DIRECTION) {
                initDirection = new Vec3(this.entityData.get(DATA_INITIAL_DIRECTION));
            }else if (var1 == DATA_INITIAL_EXISTING_TIME) {
                existTick = this.entityData.get(DATA_INITIAL_EXISTING_TIME);
                this.drawBackTick = (int) (existTick * 0.5F);
            }else if(var1 == DATA_WEAPON){
                weapon = this.entityData.get(DATA_WEAPON);
                if(weapon.getItem() instanceof BaseWhipItem whip){
                    this.item = whip;
                }
            }else if(var1 == DATA_SERVER_RANDOM){
                this.serverRandom = this.entityData.get(DATA_SERVER_RANDOM);
            }
            boolean keyframeRelated = var1 == DATA_WEAPON || var1 == DATA_SERVER_RANDOM;
            if(keyframeRelated){
                if(weapon != null && serverRandom != -1){
                    updateWeapon(this.entityData.get(DATA_WEAPON), serverRandom == 1);
                }
            }
        }
    }

    @Override
    public void shootFromRotation(Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        float f = -Mth.sin(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        float f1 = -Mth.sin((x + z) * 0.017453292F);
        float f2 = Mth.cos(y * 0.017453292F) * Mth.cos(x * 0.017453292F);
        this.shoot(f, f1, f2, velocity, inaccuracy);
        Vec3 vec3 = shooter.getKnownMovement();
        Vec3 dir = new Vec3(f, f1, f2);
        if(TEUtils.angleBetween(shooter.getLookAngle(), vec3) < 1.5f){
            this.setDeltaMovement(this.getDeltaMovement().add(vec3.x, vec3.y * 0.2F, vec3.z));
        }else{
            this.setDeltaMovement(this.getDeltaMovement().add(vec3.x * 0.23f, vec3.y * 0.2F, vec3.z * 0.23f));
        }
        dir = dir.normalize().scale(this.getDeltaMovement().length());
        this.setDeltaMovement(dir);

        this.initialPosition = position();
        this.initDirection = dir;
        this.entityData.set(DATA_INITIAL_POSITION, initialPosition.toVector3f());
        this.entityData.set(DATA_INITIAL_DIRECTION, initDirection.toVector3f());
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        return false;
    }


}
