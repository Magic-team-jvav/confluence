package org.confluence.mod.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ArrowItems;
import org.confluence.mod.common.item.sword.stagedy.EffectStrategy;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaseArrowEntity extends AbstractArrow {

    static final float minSpeedAttackFactor = 0.5f;//速度影响伤害的最小系数

    public static class Tuple {
        public String path;
        public Supplier<Builder> attr;
        static Tuple create(String path, Supplier<Builder> type){
            Tuple t = new Tuple();
            t.path = path;
            t.attr = type;
            return t;
        }

        //构建箭的默认属性   mc原版木箭：   setDamage：2f
        static Tuple STAR_ARROW_ENTITY = create("textures/entity/arrow/star_arrow.png",()->new Builder()
                .setDamage(4f).setPenetration(99).setKnockBack(2).setSpeedFactor(0.8f).setAutoDiscard(50).setGravity(0));
        static Tuple UNHOLY_ARROW_ENTITY = create("textures/entity/arrow/unholy_arrow.png",()->new Builder()
                .setDamage(4.5f).setPenetration(5).setKnockBack(1.5f));
        static Tuple FLAMING_ARROW_ENTITY = create("textures/entity/arrow/flaming_arrow.png",()->new Builder()
                .setDamage(4.5f).setCauseFire(10*20));
        static Tuple FROSTBURN_ARROW_ENTITY = create("textures/entity/arrow/frostburn_arrow.png",()->new Builder()
                .setDamage(4.5f).addOnHitEffect(EffectStrategy.TIME_EFFECT.apply(ModEffects.FROST_BURN,10*20)));
    }

    public static Map<Item, Tuple> selectArrowFromItemMap = Map.of(
            ArrowItems.STAR_ARROW.get(), Tuple.STAR_ARROW_ENTITY,
            ArrowItems.UNHOLY_ARROW.get(), Tuple.UNHOLY_ARROW_ENTITY,
            ArrowItems.FLAMING_ARROW.get(), Tuple.FLAMING_ARROW_ENTITY,
            ArrowItems.FROSTBURN_ARROW.get(), Tuple.FROSTBURN_ARROW_ENTITY
    );

        /*
        HELLFIRE_ARROW
        BONE_ARROW
        SHIMMER_ARROW
        */

    private static final EntityDataAccessor<String> TEXTURE_PATH = SynchedEntityData.defineId(BaseArrowEntity.class, EntityDataSerializers.STRING);
    public String texturePath = "";
    private int penetrate = 0;
    private List<LivingEntity> havenBeen = new ArrayList<>();//标记不能重复穿透
    public Builder modify = new Builder();
    private Tuple baseArrowTuple;
    public boolean fullPull = false;

    public BaseArrowEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BaseArrowEntity(LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(ModEntities.ARROW_PROJECTILE.get(), owner, owner.level(), pickupItemStack, firedFromWeapon);
        this.baseArrowTuple = selectArrowFromItemMap.get(pickupItemStack.getItem());
        this.modify = baseArrowTuple.attr.get();

    }
    public BaseArrowEntity(LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon, Consumer<Builder> modifyConsumer) {
        this(owner,pickupItemStack,firedFromWeapon);
        if(modifyConsumer!=null) modifyConsumer.accept(modify);
    }


    public void modify(Consumer<Builder> consumer){
        consumer.accept(modify);
    }

    public void onAddedToLevel(){

        if((modify.type & Tag.low_gravity)!=0) this.setNoGravity(true);
        if(baseArrowTuple!=null){
            entityData.set(TEXTURE_PATH,baseArrowTuple.path);
            this.texturePath = baseArrowTuple.path;
        }

        super.onAddedToLevel();
    }

    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        super.shoot(pX, pY, pZ, pVelocity, pInaccuracy);
        this.setDeltaMovement(getDeltaMovement().scale(modify.speedFactor));
    }

    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        if((modify.type & Tag.cause_fire)!=0) //todo 火焰附加要不要减去移动时间
            entity.setRemainingFireTicks(this.getRemainingFireTicks() + modify.causeFireTick - tickCount);

        if(!canPenetrate()) super.onHitEntity(pResult);

        if(!(entity instanceof LivingEntity)) {
            super.onHitEntity(pResult);
            return;
        }
        if(havenBeen.contains((LivingEntity) entity)) return;
        float f = (float)this.getDeltaMovement().length();
        f = Math.max(f, minSpeedAttackFactor);//速度修正系数影响的最小速度值
        float i = (float) Mth.clamp((double)f * this.getBaseDamage(), 0.0D, Integer.MAX_VALUE);

        /*暴击增伤
        if (this.isCritArrow()) {
            long j = this.random.nextInt(i / 2 + 2);
            i = (int)Math.min(j + (long)i, 2147483647L);
        }
        */
        Entity entity1 = this.getOwner();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = this.damageSources().arrow(this, this);
        } else {
            damagesource = this.damageSources().arrow(this, entity1);
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity)entity1).setLastHurtMob(entity);
            }
        }
        havenBeen.add((LivingEntity) pResult.getEntity());
        penetrate++;
        int k = entity.getRemainingFireTicks();

        if(entity.hurt(damagesource, i)){
            this.playSound(getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

            LivingEntity livingentity = (LivingEntity)entity;

            this.doPostHurtEffects(livingentity);


            if (!this.level().isClientSide) {
                livingentity.setArrowCount(livingentity.getArrowCount() + 1);
            }
            //todo 击退
            this.doKnockback(livingentity,damagesource);

            if (modify.knockBack > 0) {
                double d0 = modify.knockBack;
                Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(0.6 * d0);
                if (vec3.lengthSqr() > 0.0D) {
                    livingentity.push(vec3.x, 0.1D, vec3.z);
                }
            }

            //箭药水效果
            if (!this.level().isClientSide && entity1 instanceof LivingEntity) {
                EnchantmentHelper.doPostAttackEffects((ServerLevel) level(),entity1,damagesource);
                //
            }

            //命中自己
            if (entity1 != null && livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !this.isSilent()) {
                ((ServerPlayer)entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }

        }else {
            entity.setRemainingFireTicks(k);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }
            }
        }
    }

    @Override
    public double getBaseDamage() {
        return modify.base_damage;
    }

    @Override
    protected void doPostHurtEffects(LivingEntity pLiving) {
        if(getOwner() instanceof LivingEntity owner){
            modify.onHitEffect.forEach(m->m.accept(owner,pLiving));
            if(fullPull) modify.fullPullHitEffect.forEach(m->m.accept(owner,pLiving));
        }
        super.doPostHurtEffects(pLiving);
    }

    private static SoundEvent getSound(){
        return SoundEvents.TRIDENT_HIT_GROUND;
    }


    @Override
    protected ItemStack getDefaultPickupItem() {
        // 构造延迟
        if(this.modify == null)
            return Items.ARROW.getDefaultInstance();
        return Items.ARROW.getDefaultInstance();
    }


    @Override
    public void tick(){

        if(!level().isClientSide && tickCount> modify.auto_discard_tick)discard();
        //todo 重力调整
        if((modify.type & Tag.low_gravity)!=0) this.addDeltaMovement(new Vec3(0, modify.gravity_count, 0));

        super.tick();
    }


    public boolean canPenetrate(){
        return penetrate+1 < modify.penetration_count;
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder pBuilder){
        super.defineSynchedData(pBuilder);
        pBuilder.define(TEXTURE_PATH,"");
    }
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        this.texturePath =entityData.get(TEXTURE_PATH);

    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(selectArrowFromItemMap.containsKey(getPickupItem().getItem())) {
            texturePath = selectArrowFromItemMap.get(getPickupItem().getItem()).path;
            entityData.set(TEXTURE_PATH,texturePath);
        }
    }





    /** 能力表 **/
    static class Tag{
        static final int penetration = 1;//可穿透
        static final int low_gravity = 2;//低重力
        static final int auto_discard = 4;//超过时间自动消失
        static final int cause_fire = 8;//火焰附加

    }
    public static class Builder{
        private int type = 0;
        private int penetration_count = 0;
        private int gravity_count = 0;
        private int auto_discard_tick = 1200;
        private float base_damage = 2;
        private float speedFactor = 1;
        private float knockBack = 0;
        private int causeFireTick = 0;
        private final List<BiConsumer<LivingEntity, LivingEntity>> onHitEffect = new ArrayList<>();
        private final List<BiConsumer<LivingEntity, LivingEntity>> fullPullHitEffect = new ArrayList<>();


        public Builder addFullPullHitEffect(BiConsumer<LivingEntity, LivingEntity> consumer){
            this.fullPullHitEffect.add(consumer);
            return this;
        }
        public Builder addOnHitEffect(BiConsumer<LivingEntity, LivingEntity> consumer){
            this.onHitEffect.add(consumer);
            return this;
        }
        public Builder setDamage(float damage){//基本伤害
            base_damage = damage;
            return this;
        }
        public Builder setKnockBack(float factor){//击退修正系数
            this.knockBack = factor;
            return this;
        }


        public Builder setPenetration(int count){//穿透次数
            type|= Tag.penetration;
            penetration_count = count;
            return this;
        }
        public Builder setGravity(int gravity){//重力
            type|= Tag.low_gravity;
            gravity_count = gravity;
            return this;
        }
        public Builder setAutoDiscard(int tick){//消失tick
            type|= Tag.auto_discard;
            auto_discard_tick = tick;
            return this;
        }
        public Builder setSpeedFactor(float factor){//初始速度修正系数
            speedFactor = factor;
            return this;
        }
        public Builder setCauseFire(int tick){//初始火焰增加
            type|= Tag.cause_fire;
            this.causeFireTick = tick;
            return this;
        }


    }


}
