package org.confluence.terraentity.utils;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.entity.PartEntity;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.util.LibUtils;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.IAttackableProjectile;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.confluence.terraentity.config.ServerConfig;
import org.confluence.terraentity.entity.boss.AbstractTerraBossBase;
import org.confluence.terraentity.mixed.IAttributeInstance;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.world.item.Item.getPlayerPOVHitResult;


public final class TEUtils {

    private TEUtils() {
    }

    public static float nextFloat(RandomSource randomSource, float origin, float bound) {
        if (origin >= bound) {
            throw new IllegalArgumentException("bound - origin is non positive");
        } else {
            return origin + randomSource.nextFloat() * (bound - origin);
        }
    }

    public static double nextDouble(RandomSource randomSource, double origin, double bound) {
        if (origin >= bound) {
            throw new IllegalArgumentException("bound - origin is non positive");
        } else {
            return origin + randomSource.nextDouble() * (bound - origin);
        }
    }


    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> getTicker(BlockEntityType<A> a, BlockEntityType<E> b, BlockEntityTicker<? super E> ticker) {
        return a == b ? (BlockEntityTicker<A>) ticker : null;
    }

    public static boolean isHalloween() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        return (month == Calendar.OCTOBER && date >= 10) || // 从 10月10日
                (month == Calendar.NOVEMBER && date == 1);  // 到 11月01日
    }

    /**
     * 把向量转成角度
     */
    public static float[] dirToRot(Vec3 vec) {
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;

        double yaw = Math.toDegrees(Mth.atan2(-x, z));
        double pitch = Math.toDegrees(Mth.atan2(-y, Math.sqrt(x * x + z * z)));

        return new float[]{(float) yaw, (float) pitch};
    }

    /**
     * 把角度转成向量
     *
     * @param yaw   角度的yaw，单位为角度而非弧度
     * @param pitch 角度的pitch，单位为角度
     * @return 返回朝向对应角度（yaw、pitch）的单位向量
     */
    public static Vec3 rotToDir(float yaw, float pitch) {
        float yawRad = yaw * Mth.DEG_TO_RAD;
        float pitchRad = pitch * Mth.DEG_TO_RAD;
        // Mth类的三角函数优化较好
        double y = -1 * Mth.sin(pitchRad);
        double div = Mth.cos(pitchRad);
        double x = -1 * Mth.sin(yawRad);
        double z = Mth.cos(yawRad);
        x *= div;
        z *= div;
        return new Vec3(x, y, z); // Vec3.directionFromRotation(pitch, yaw);
    }

    /**
     * 更新实体朝向
     */
    public static void updateEntityRotation(Entity entity, Vec3 dir) {
        float[] angle = dirToRot(dir);
        entity.setYRot(angle[0]);
        entity.setXRot(angle[1]);
    }

    /**
     * 获得两个位置之间的方向向量；若两点重合则默认返回向上的向量
     * 若要自定义默认返回的向量，请在length后传入一个默认向量
     *
     * @param start  开始位置的位置向量
     * @param end    结束位置的位置向量
     * @param length 返回向量的长度
     */
    public static Vec3 getDirection(Vec3 start, Vec3 end, double length) {
        return getDirection(start, end, length, new Vec3(0, length, 0));
    }

    /**
     * 获得两个位置之间的方向向量；若两点重合则默认返回的向量
     *
     * @param start      开始位置的位置向量
     * @param end        结束位置的位置向量
     * @param length     返回向量的长度
     * @param defaultVec 两点重合时返回的默认向量（注：直接原样返回，不会判定该向量的长度）
     */
    public static Vec3 getDirection(Vec3 start, Vec3 end, double length, Vec3 defaultVec) {
        return getDirection(start, end, length, defaultVec, false);
    }

    /**
     * 获得两个位置之间的方向向量
     * 若preserveShorterVectors为true且两点之间的距离小于length则不会改变向量长度
     *
     * @param start                  开始位置的位置向量
     * @param end                    结束位置的位置向量
     * @param length                 返回向量的长度
     * @param defaultVec             两点重合时返回的默认向量（注：直接原样返回，不会判定该向量的长度）
     * @param preserveShorterVectors 若向量比length短，是否保留原向量
     */
    public static Vec3 getDirection(Vec3 start, Vec3 end, double length,
                                    Vec3 defaultVec, boolean preserveShorterVectors) {
        Vec3 result = end.subtract(start);
        double distSqr = result.lengthSqr();
        // 此时直接返回比length更短的向量
        if (preserveShorterVectors && distSqr <= length * length) {
            return result;
        }
        // 向量长度重设为length

        // 两点之间过近
        if (distSqr < 1e-9) {
            return defaultVec;
        }
        result.scale(length / Math.sqrt(distSqr));
        return result;
    }


    public static void testMessage(Player player, String msg) {
        player.sendSystemMessage(Component.literal(msg));
    }

    public static void testMessage(Level level, String msg) {
        for (Player ply : level.players())
            ply.sendSystemMessage(Component.literal(msg));
    }

    /**
     * 为专家?在处理if...else if时应先使用isMaster
     */
    public static boolean isAtLeastExpert(Level level, BlockPos pos) {
        return LibUtils.isAtLeastExpert(level, pos);
    }

    /**
     * 为大师?在处理if...else if时应先使用此方法
     */
    public static boolean isMaster(Level level, BlockPos pos) {
        return LibUtils.isMaster(level, pos);
    }

    /**
     * 根据游戏难度选择值
     *
     * @param classic 经典难度的值
     * @param expert  专家难度的值
     * @param master  大师难度的值
     * @return 选择到的值
     */
    public static <T> T switchByDifficulty(Level level, BlockPos pos, T classic, T expert, T master) {
        return LibUtils.switchByDifficulty(level, pos, classic, expert, master);
    }

    /**
     * 获取当前难度的不同属性加成倍率
     * @return 倍率
     */
    public static float getMultiple(Level level, BlockPos pos, Holder<Attribute> attribute) {
        if(attribute == Attributes.MAX_HEALTH)
            return switchByDifficulty(level, pos, 0.66f, 1f, 1.5f);
        else if(attribute == Attributes.ATTACK_DAMAGE)
            return switchByDifficulty(level, pos, 0.66f, 1f, 1.5f);
        else return 1f;
    }

    static ResourceLocation healthKey = TerraEntity.space("server_modifier_max_health");
    static ResourceLocation damageKey = TerraEntity.space("server_modifier_max_attack_damage");
    static ResourceLocation difficultyHealthKey = TerraEntity.space("difficulty_modifier_max_health");
    static ResourceLocation difficultyDamageKey = TerraEntity.space("difficulty_modifier_attack_damage");

    public static void multiplePlayerEnhance(LivingEntity entity) {
        if(!entity.level().isClientSide) {
            float multiplier = getMultiple(entity.level(), entity.blockPosition(), Attributes.MAX_HEALTH);
            int size = Math.min(entity.level().players().size(), 8);
            var healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
            if (healthAttribute != null) {
                if (!healthAttribute.hasModifier(difficultyHealthKey))
                    healthAttribute.addPermanentModifier(new AttributeModifier(difficultyHealthKey, multiplier * size - 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                if (!healthAttribute.hasModifier(healthKey)) {
                    healthAttribute.addPermanentModifier(new AttributeModifier(healthKey, ServerConfig.BOSS_ATTRIBUTES_MULTIPLIER_HEALTH.get() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                    entity.setHealth(entity.getMaxHealth());
                }
            }
            var damageAttribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damageAttribute != null) {
                if (!damageAttribute.hasModifier(difficultyDamageKey))
                    damageAttribute.addPermanentModifier(new AttributeModifier(difficultyDamageKey, multiplier - 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                if (!damageAttribute.hasModifier(damageKey))
                    damageAttribute.addPermanentModifier(new AttributeModifier(damageKey, ServerConfig.BOSS_ATTRIBUTES_MULTIPLIER_DAMAGE.get() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    public static void monsterEnhance(LivingEntity entity) {
        if(entity instanceof Boss || entity instanceof AbstractTerraBossBase || entity instanceof ISummonMob ) return;
        if(!ServerConfig.ENHANCE_ALL_MONSTER.get() && !BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace().equals(TerraEntity.MODID)) return;
        if(entity.level() instanceof ServerLevel level) {
            float multiplier;
            if (LibUtils.getChunkIfLoaded(level.getChunkSource(), entity.chunkPosition()) == null) {
                multiplier = 1;
            } else {
                multiplier = getMultiple(entity.level(), entity.blockPosition(), Attributes.MAX_HEALTH);
            }
            var healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
            if (healthAttribute != null) {
                if (!healthAttribute.hasModifier(healthKey)) {
                    healthAttribute.addPermanentModifier(new AttributeModifier(healthKey, ServerConfig.MONSTER_ATTRIBUTES_MULTIPLIER_HEALTH.get() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                    entity.setHealth(entity.getMaxHealth());
                }
            }
            var damageAttribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (damageAttribute != null) {
                if (!damageAttribute.hasModifier(damageKey))
                    damageAttribute.addPermanentModifier(new AttributeModifier(damageKey, ServerConfig.MONSTER_ATTRIBUTES_MULTIPLIER_DAMAGE.get() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                if (!damageAttribute.hasModifier(difficultyDamageKey))
                    damageAttribute.addPermanentModifier(new AttributeModifier(difficultyDamageKey, multiplier - 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    /**
     * 获得从实体A到实体B的单位向量，即A→B
     *
     * @param a 实体A
     * @param b 实体B
     * @return A→B的单位向量
     */
    public static Vec3 getVectorA2B(Entity a, Entity b) {
        return b.position().subtract(a.position()).normalize();
    }

    /**
     * 给予实体B一个击退动量，方向为A→B
     *
     * @param a       实体A
     * @param b       实体B
     * @param scale   击退动量的缩放
     * @param motionY 击退的Y轴动量
     */
    public static void knockBackA2B(Entity a, Entity b, double scale, double motionY) {
        if (b instanceof LivingEntity living) {
            AttributeInstance instance = living.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (instance != null) scale *= (1.0 - instance.getValue());
        }
        if (scale > 0.0) {
            if (a instanceof LivingEntity living) {
                AttributeInstance instance = living.getAttribute(Attributes.ATTACK_KNOCKBACK);
                if (instance != null) scale *= (1.0 + instance.getValue());
            }
            b.addDeltaMovement(getVectorA2B(a, b).scale(scale).add(0.0, motionY, 0.0));
        }
    }

    public static Vec3 componentMin(Vec3 vec1, Vec3 vec2) {
        return new Vec3(Math.min(vec1.x, vec2.x), Math.min(vec1.y, vec2.y), Math.min(vec1.z, vec2.z));
    }

    public static Vec3 componentMax(Vec3 vec1, Vec3 vec2) {
        return new Vec3(Math.max(vec1.x, vec2.x), Math.max(vec1.y, vec2.y), Math.max(vec1.z, vec2.z));
    }


    /**
     * 将输入的向量的某个轴乘一个缩放
     *
     * @param vec3  输入的向量
     * @param axis  某个轴
     * @param scale 缩放
     * @return 新向量
     */
    public static Vec3 relativeScale(Vec3 vec3, Direction.Axis axis, double scale) {
        double x = axis == Direction.Axis.X ? scale * vec3.x : vec3.x;
        double y = axis == Direction.Axis.Y ? scale * vec3.y : vec3.y;
        double z = axis == Direction.Axis.Z ? scale * vec3.z : vec3.z;
        return new Vec3(x, y, z);
    }

    /**
     * 计算向量夹角
     * @param v1
     * @param v2
     * @return degree
     */
    public static double angleBetween(Vec3 v1,Vec3 v2){
        return Math.acos(v1.dot(v2)/v1.length()/v2.length());
    }

    /**
     * 球坐标
     * @param r 半径
     * @param theta yaw
     * @param beta pitch - 90°
     * @return 方向向量
     */
    public static Vec3 sphere(float r, float theta, float beta){
        double x = r * Math.sin(beta) * Math.cos(theta);
        double y = r * Math.cos(beta);
        double z = r * Math.sin(beta) * Math.sin(theta);
        return new Vec3(x, y, z);
    }


    public static Vec3 circle(float r, float theta){
        double x = r * Math.cos(theta);
        double y = r * Math.sin(theta);
        return new Vec3(x, 0, y);
    }

    /**
     * 根据权重随机获取物品
     */
    public static <T> T getRandomByWeight(Map<T, Float> map) {
        // 计算总权重
        float totalWeight = 0.0f;

        for (var pair : map.values()) {
            totalWeight += pair;
        }

        if (totalWeight == 0.0f) {
            throw new IllegalArgumentException("Total weight cannot be zero.");
        }

        float randomValue = ThreadLocalRandom.current().nextFloat(0, totalWeight);

        // 遍历物品，累积权重，直到累积权重超过随机数
        float cumulativeWeight = 0.0f;
        for (var entry : map.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (cumulativeWeight >= randomValue) {
                return entry.getKey();
            }
        }
        // 理论上不会走到这里
        throw new IllegalStateException("Failed to find random item.");
    }

    /**
     * 根据权重随机获取物品
     */
    public static <T> T getRandomByWeightInt(Map<T, Integer> map) {
        // 计算总权重
        float totalWeight = 0.0f;

        for (var pair : map.values()) {
            totalWeight += pair;
        }

        if (totalWeight == 0.0f) {
            throw new IllegalArgumentException("Total weight cannot be zero.");
        }

        float randomValue = ThreadLocalRandom.current().nextFloat(0, totalWeight);

        // 遍历物品，累积权重，直到累积权重超过随机数
        float cumulativeWeight = 0.0f;
        for (var entry : map.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (cumulativeWeight >= randomValue) {
                return entry.getKey();
            }
        }
        // 理论上不会走到这里
        throw new IllegalStateException("Failed to find random item.");
    }

    public static <T> T getRandomByWeightInt(List<T> items, List<Integer> weights) {
        if (items == null || weights == null || items.size() != weights.size() || items.isEmpty()) {
            throw new IllegalArgumentException("Items and weights must be non-null, non-empty, and of the same size.");
        }

        // 计算总权重
        float totalWeight = 0.0f;
        for (var weight : weights) {
            totalWeight += weight;
        }

        if (totalWeight == 0.0f) {
            throw new IllegalArgumentException("Total weight cannot be zero.");
        }

        float randomValue = ThreadLocalRandom.current().nextFloat(0, totalWeight);

        // 遍历物品，累积权重，直到累积权重超过随机数
        float cumulativeWeight = 0.0f;
        for (int i = 0; i < items.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (cumulativeWeight >= randomValue) {
                return items.get(i);
            }
        }
        // 理论上不会走到这里
        throw new IllegalStateException("Failed to find random item.");
    }


    /**
     * 获取玩家视角下距离指定距离的实体
     * @param entity
     * @param distance
     * @return
     */
    public static @Nullable EntityHitResult getEyeTraceHitResult(Entity entity, double distance){
        AABB aabb = entity.getBoundingBox().inflate(distance);
        Vec3 from = entity.getEyePosition();
        Vec3 to = entity.getEyePosition().add(entity.getLookAngle().scale(distance));
        return ProjectileUtil.getEntityHitResult(entity.level(), entity, from, to, aabb, e-> true, 0.1F);
    }

    /**
     * 获取玩家视角下方块
     */
    public static BlockPos getEyeBlockHitResult(Player player){
        final BlockHitResult result = getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.SOURCE_ONLY);
        return result.getBlockPos();
    }

    /**
     * 获取视角前方的位置
     */
    public static Vec3 getEyeVec3(Entity entity, float distance, float partialTicks){
        return entity.getEyePosition(partialTicks).add(entity.getLookAngle().normalize().scale(distance));
    }

    /**
     * 有无视线阻挡
     */
    public static boolean canSeePos(Entity entity, Vec3 pos){
        return entity.level().clip(new ClipContext(entity.getEyePosition(), pos, ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;
    }


    public static boolean isFTWWorld(ServerLevel level) {
        return false; // confluence mixin here
    }

    /**
     * 最简单的追踪机制，计算过程如下：<br>
     * 1. 将当前的弹幕方向进行缩放 <br>
     * 2. 将目标方向（即追踪弹幕想要去的方向）以一定权重加入缩放后的弹幕方向 <br>
     * 3. 调整最终方向向量长度返回 <br>
     * 优点：计算量少，逻辑简单 <br>
     * 缺点：在allowLowerSpd为false  且  移动方向与想要弹幕追踪到的方向相反时，追踪能力极为有限；
     * interpolateBasis方法以更多的计算量为代价提供更加平滑和可调整的追踪弹道。
     *
     * @param currDir            当前的弹幕方向向量
     * @param targetDir          弹幕追踪目标方向向量
     * @param currDirScaleFactor 当前弹幕方向计算前的缩放比例
     * @param homingPower        弹幕追踪时根据目标方向调整的量
     * @param maxSpeed           弹幕追踪时最大速度; 取值范围 - [0, inf)
     * @param minSpeed           弹幕追踪时最低速度; 取值范围 - [0, maxSpeed]
     * @param defaultDir         若更新完毕的弹幕方向为0，但最低速度要求不为0时，返回defaultDir方向（长度会更新为minSpeed）。
     *                           *警告*：此向量不要为0
     * @return 最终更新完毕的方向向量
     */
    public static Vec3 interpolateSimple(Vec3 currDir, Vec3 targetDir, double currDirScaleFactor, double homingPower,
                                         double maxSpeed, double minSpeed, Vec3 defaultDir) {
        Vec3 result = currDir;
        // 若当前方向和目标方向的方向向量均不为0时才考虑进行方向调整
        // 为了方向的准确性，我们将不考虑长度 < 0.001 (即lengthSqr < 1e-9) 的向量
        if (currDir.lengthSqr() > 1e-9 && targetDir.lengthSqr() > 1e-9) {
            result = currDir.multiply(currDirScaleFactor, currDirScaleFactor, currDirScaleFactor);
            // normalize不会造成NaN，此处长度 > 0.001
            Vec3 targetComponent = targetDir.normalize().multiply(homingPower, homingPower, homingPower);
            result = result.add(targetComponent);
        }
        // 最后，根据最大最小速度的要求更改向量长度
        double vecLen = result.length();
        if (vecLen > maxSpeed) {
            double factor = maxSpeed / vecLen;
            // 不使用normalize以减少一次计算向量长度带来的sqrt运算
            result = result.multiply(factor, factor, factor);
        } else if (vecLen < minSpeed) {
            // 若结果向量过短，使用defaultDir
            if (vecLen < 1e-5) {
                result = defaultDir;
                vecLen = result.length();
            }
            double factor = minSpeed / vecLen;
            // 不使用normalize以减少一次计算向量长度带来的sqrt运算
            result = result.multiply(factor, factor, factor);
        }
        return result;
    }

    /**
     * 将currDir方向的单位向量记为v1, 我们使用向量投影的方式构造单位向量v2，使得v1与v2为currDir, targetDir平面上的基，且v1垂直于v2. <br>
     * 将currDir的长度写为c，则currDir=cv1+0v2；另，有a,b使得targetDir可被写成av1+bv2. <br>
     * 注意到，[c,0]转换到[a,b]需要一个旋转+缩放；同样的，对于基{v1,v2}中，这一系数的变换也将currDir变换到targetDir. <br>
     * 然而，一般而言，我们希望追踪弹幕每次只进行这一变换的一部分以获得更合理的弹道。 <br>
     * 因此，我们对旋转角和缩放长度进行插值。至此，我们即可获得最终的方向向量。 <br>
     * 注：若我们把v1看做x轴，v2看做y轴，则角度插值和角度均应在一二象限。 <br>
     * 另，从以上过程中可知，**targetDir的方向和长度都至关重要**。<br>
     *
     * @param currDir            当前弹幕的方向向量
     * @param targetDir          方向向量，记录了追踪的最终方向与长度（想要达到的速度）
     * @param angleInterpolator      提供角度插值；输入为当前方向和追踪方向的角度差，输出为追踪所变换的角度
     * @param lengthInterpolator 提供向量长度（即速度）插值；输入为当前方向和追踪方向的长度差，输出为追踪所变换的向量长度
     * @return 变换完毕的向量
     */
    public static Vec3 interpolateBasis(Vec3 currDir, Vec3 targetDir,
                                        ToDoubleFunction<Double> angleInterpolator, ToDoubleFunction<Double> lengthInterpolator) {
        double currDirLen = currDir.length();
        double targetDirLen = targetDir.length();
        // 以下多次用到仅单次使用的乘数的设计，干脆公用同一个变量
        double multi;
        // 起始向量与目标向量均为0，直接返回原向量
        if (currDirLen < 1e-5 && targetDirLen < 1e-5) {
            return currDir;
        }
        // 若仅有起始速度为0，则直接返回0向量到目标向量的插值
        if (currDir.lengthSqr() < 1e-9) {
            multi = lengthInterpolator.applyAsDouble(targetDirLen) / targetDirLen;
            return targetDir.multiply(multi, multi, multi);
        }
        // 若仅有终止速度为0，则直接返回起始向量到0向量的线性插值，即起始向量*(1-进度)。
        if (targetDir.lengthSqr() < 1e-9) {
            multi = 1 - (lengthInterpolator.applyAsDouble(currDirLen) / currDirLen);
            return currDir.multiply(multi, multi, multi);
        }
        // 此时，起始终止速度均不为0，后续操作不会造成NaN值。按照注释中的步骤获得结果。
        multi = 1 / currDirLen;
        Vec3 v1 = currDir.multiply(multi, multi, multi);
        Vec3 v1Component = vectorProjection(targetDir, v1);
        Vec3 v2 = targetDir.subtract(v1Component);
        double a, b; // 此处的a,b见上方的方法注释中说明
        double v1CompLen = v1Component.length();
        double v2Len = v2.length();
        // 夹角大于pi/2时，即cos(theta)<0或v1·v1Component<0时，a是负数
        a = v1CompLen * Math.signum(v1.dot(v1Component));
        // 此处的v2方向正确，但尚未转化为单位向量；若v2近似地为0, 即v1与v2共线。
        if (v2Len < 1e-5) {
            b = 0;
        } else {
            b = v2Len;
            multi = 1 / v2Len;
            v2 = v2.multiply(multi, multi, multi);
        }
        // targetDir = [a,b]·[v1,v2]; angleRad = angle([1,0], [a,b]) = atan2(b,a)
        double angleRad = Math.atan2(b, a);
        // 计算角度插值
        double angleDelta = angleInterpolator.applyAsDouble(angleRad);
        // 获得旋转后的方向；此时方向向量为单位向量。
        multi = Math.cos(angleDelta);
        Vec3 result = v1.multiply(multi, multi, multi);
        multi = Math.sin(angleDelta);
        result = result.add(v2.multiply(multi, multi, multi));
        // 计算长度插值
        double length = currDirLen + lengthInterpolator.applyAsDouble(targetDirLen - currDirLen);
        return result.multiply(length, length, length);
    }

    /**
     * 返回一个可以被interpolateBasis作为angleInterpolator或lengthInterpolator使用的线性插值。<br>
     * 即，若progress为0，则插值一定提供0，在追踪中表现为不追踪；<br>
     * 若progress为1，则插值一定提供全额变化值，在追踪中表现为瞬间完全调整方向。<br>
     * 例：progress为0.5，则插值一定提供变化值的一半，在追踪中表现为方向（弧度）/速度 *误差越大，调整速度越快*。<br>
     *
     * @param progress 插值强度；越接近0越弱，越接近1越强。取值范围 - [0, 1]
     * @return 插值ToDoubleFunction
     */
    public static ToDoubleFunction<Double> getLerp(double progress) {
        return x -> x * progress;
    }

    /**
     * 返回一个可以被interpolateBasis作为angleInterpolator或lengthInterpolator使用的阈值式插值。<br>
     * 即，若progress为0，则插值一定提供0，在追踪中表现为不追踪；<br>
     * 否则，插值提供 变化值 与 阈值 中更小的一者，在追踪中表现为方向（弧度）/速度的误差以 *恒定的效率* 被修正。<br>
     * **再次注意：方向（弧度）的插值单位为弧度而非角度！**<br>
     *
     * @param efficiency 插值强度；越接近0越弱，越高越强。取值范围 - [0, inf)
     * @return 插值ToDoubleFunction
     */
    public static ToDoubleFunction<Double> getThresholdInterpolator(double efficiency) {
        return x -> Math.min(x, efficiency);
    }

    /**
     * 向量投影；**toProjectOnto不可以为0向量**！
     *
     * @param vector        被投影的向量
     * @param toProjectOnto 投影的目标向量
     * @return 投影结果
     */
    public static Vec3 vectorProjection(Vec3 vector, Vec3 toProjectOnto) {
        // toProjectOnto.lengthSqr = toProjectOnto.dot(toProjectOnto)
        double factor = toProjectOnto.dot(vector) / toProjectOnto.lengthSqr();
        return toProjectOnto.multiply(factor, factor, factor);
    }


    /**
     * 获取包围盒内锥形射线内的目标
     * @param ori 起始点
     * @param end 终止点
     * @param range 若owner为null，则为包围盒范围，否则无效
     * @param maxAngle 最大角度
     * @return 若直接命中，返回命中的目标；否则返回最近有效的目标
     */
    public static LivingEntity getAABBAngleTarget(Vec3 ori, Vec3 end, Level level, @Nullable Entity owner, double range, double maxAngle, Predicate<Entity> filter){
        //扩大包围盒
        AABB aabb;
        if(owner!=null){
            aabb = owner.getBoundingBox().inflate(range);
        }
        else{
            aabb = new AABB(ori,end).inflate(range);
        }
        Vec3 direction = end.subtract(ori);
        List<HitResult> hits = new ArrayList<>();
        List<HitResult> subHits = new ArrayList<>();
        List<? extends Entity> entities = level.getEntities(owner,aabb, entity1 -> entity1.isPickable() && entity1.isAlive() && filter.test(entity1));
        for(var e : entities){
            //获取视线交点
            Vec3 vec3 = e.getBoundingBox().clip(ori,end).orElse(null);
            //优先指向的目标
            if(vec3!=null){
                //System.multiOut.println("point directly");
                EntityHitResult entityHitResult = new EntityHitResult(e,vec3);
                hits.add(entityHitResult);
            }//自瞄其他夹角小于一定度数的目标
            else if(hits.isEmpty() && TEUtils.angleBetween(e.position().subtract(ori),end.subtract(ori)) < maxAngle *  Math.PI/180){
                EntityHitResult entityHitResult = new EntityHitResult(e,e.position());
                subHits.add(entityHitResult);
            }
        }


        if(!hits.isEmpty()){
            //射线命中的目标 按距离排序
            hits.sort((o1,o2)-> {
                double v1 = o1.getLocation().distanceToSqr(ori);
                double v2 = o2.getLocation().distanceToSqr(ori);
                if (v1==v2)return 0;
                return v1 < v2 ? -1 : 1;
            });
            for(HitResult hitResult : hits) {
                if (hitResult instanceof EntityHitResult entityHitResult &&
                        (
                                entityHitResult.getEntity() instanceof LivingEntity livingEntity &&
                                        livingEntity instanceof Enemy &&
                                        !(livingEntity instanceof ISummonMob)
                        )) {
                    return livingEntity;
                }
            }
        }else if(!subHits.isEmpty()){
            //未命中的目标 按角度排序
            subHits.sort((o1,o2)-> {
                double v1 = TEUtils.angleBetween(o1.getLocation().subtract(ori), direction);
                double v2 = TEUtils.angleBetween(o2.getLocation().subtract(ori), direction);
                if (v1 == v2)return 0;
                return v1 < v2 ?-1:1;
            });
            HitResult hitResult = subHits.getFirst();
            if(hitResult instanceof  EntityHitResult entityHitResult &&
                    entityHitResult.getEntity() instanceof LivingEntity livingEntity){
                return livingEntity;
            }
        }
        return null;
    }

    public static Vec3 getPlayerHandPos(Player player) {
        int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        float f =  player.yBodyRot* 0.017453292F + 1f;
        double d0 = Mth.sin(f);
        double d1 = Mth.cos(f);
        float f1 = player.getScale();
        double d2 = (double)i * 0.25 * (double)f1;
        double d3 = 0.8 * (double)f1;
        return new Vec3(-d1 * d2 - d0 * d3, 0, -d0 * d2 + d1 * d3);
    }

    /**
     * 测试攻击驯养动物
     */
    public static BiPredicate<Entity, Entity> attackTamableTest = (owner, target) -> {
        Entity actualTarget = target instanceof PartEntity<?> part ? part.getParent() : target;
        if(actualTarget == null) return false;

        if(
                owner != null && (
                        actualTarget instanceof TamableAnimal animal &&
                                owner instanceof LivingEntity living &&
                                animal.isOwnedBy(living)
                )
        ){
            return false;
        }
        if(actualTarget instanceof ISummonMob) {
            return false;
        }

        return true;
    };

    /**
     * <h1>统一弹幕目标伤害过滤</h1>
     */
    public static BiPredicate<Projectile, Entity> projectileCanHurtEntityTest = (projectile, target)-> {

        if(target instanceof IAttackableProjectile projectile1 && projectile1.canBeAttacked()){
            return true;
        }

        if (!target.isAttackable() ||  target instanceof     Npc  || target instanceof ArmorStand) {
            return false;
        }
        if(target instanceof  LivingEntity living){
            if(!living.canBeSeenByAnyone() || !living.canBeSeenAsEnemy()){
                return false;
            }
        }
        Entity entity = projectile.getOwner();
        // 防止击中仆从
        if(!attackTamableTest.test(entity, target)){
            return false;
        }

        if(entity == null || !entity.isPassengerOfSameVehicle(target)) {
            return true;
        }
        return target != entity;
    };

    /**
     * <h1>统一弹幕能否命中目标和索敌过滤</h1>
     */
    public static BiPredicate<Projectile, Entity> projectileCanHitEntityTest = (projectile, target)-> {
        Entity entity = projectile.getOwner();
        // 不能攻击主人
        if(entity == target) return false;

        if(target instanceof IAttackableProjectile projectile1 && projectile1.canBeAttacked()){
            return true;
        }

        if (!target.isAttackable()) {
            // 不可攻击的实体
            return false;
        }

        if(!(target instanceof LivingEntity)){
            // 可以攻击多体节生物
            if(target instanceof PartEntity<?> part && part.getParent() instanceof LivingEntity){
                return true;
            }
            return false;
        }

        if(entity != null && entity.isPassengerOfSameVehicle(target)) {
            // 不能攻击坐骑
            return false;
        }
        return true;

    };

    public static boolean isPassInvulnerableDamageSource(DamageSource source, DamageSources sources) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source == sources.genericKill();
    }

    /**
     * 获取向量从v1指向v2的旋转四元数
     */
    public static Quaternionf rotateFromV1ToV2(Vector3fc v1, Vector3fc v2) {
        // 1. 归一化向量
        Vector3f v1Norm = new Vector3f(v1).normalize();
        Vector3f v2Norm = new Vector3f(v2).normalize();

        // 2. 计算点积和叉乘
        float dot = v1Norm.dot(v2Norm);
        Vector3f cross = new Vector3f();
        v1Norm.cross(v2Norm, cross);

        // 3. 处理特殊情况
        if (Math.abs(dot) >= 1.0f - 1e-6f) { // 平行或反平行
            return dot > 0 ? new Quaternionf() : // 同向返回单位四元数
                    new Quaternionf().fromAxisAngleRad(new Vector3f(1, 0, 0), (float) Math.PI); // 反向旋转180度
        }

        // 4. 计算旋转轴和角度
        float sinTheta = cross.length();
        float theta = (float) Math.atan2(sinTheta, dot);
        cross.normalize(); // 归一化旋转轴

        // 5. 构造四元数
        return new Quaternionf().fromAxisAngleRad(cross, theta);
    }


    /**
     * 计算射线与AABB的交点
     * @param start 射线起点
     * @param dir 射线方向
     * @param aabb 包围盒
     * @return 若交点存在，返回交点坐标；否则返回null
     */
    public static @Nullable Vec3 calRayToAABB(Vec3 start, Vec3 dir, AABB aabb) {
        double tMin = 0.0;
        double tMax = Double.MAX_VALUE;

        // 检查射线与x轴的交点
        double t1 = (aabb.minX - start.x) / dir.x;
        double t2 = (aabb.maxX - start.x) / dir.x;
        if (dir.x < 0) {
            double temp = t1;
            t1 = t2;
            t2 = temp;
        }
        if (t1 > tMin) tMin = t1;
        if (t2 < tMax) tMax = t2;
        if (tMin > tMax) return null;

        // 检查射线与y轴的交点
        t1 = (aabb.minY - start.y) / dir.y;
        t2 = (aabb.maxY - start.y) / dir.y;
        if (dir.y < 0) {
            double temp = t1;
            t1 = t2;
            t2 = temp;
        }
        if (t1 > tMin) tMin = t1;
        if (t2 < tMax) tMax = t2;
        if (tMin > tMax) return null;

        // 检查射线与z轴的交点
        t1 = (aabb.minZ - start.z) / dir.z;
        t2 = (aabb.maxZ - start.z) / dir.z;
        if (dir.z < 0) {
            double temp = t1;
            t1 = t2;
            t2 = temp;
        }
        if (t1 > tMin) tMin = t1;
        if (t2 < tMax) tMax = t2;
        if (tMin > tMax) return null;

        return start.add(dir.scale(tMin));
    }

    public static void consumeItemCount(List<ItemStack> have, Item item, int consumeCount) {
        int count = 0;
        for (ItemStack stack : have) {
            if (stack.is(item) && count < consumeCount) {
                int toConsume = Math.min(stack.getCount(), consumeCount - count);
                stack.shrink(toConsume);
                count += toConsume;
            }
        }
    }

    public static double lerpMotion(double partialTickTotal, double transitionTime, double start, double end){
        return Mth.lerp(org.joml.Math.min(partialTickTotal  / transitionTime,1), start, end);
    }

/*
    public static boolean hasBoss(double radius, Level level,
                                  AABB box){
        boolean flag = false;
        for (Entity entity : getNearbyEntities(radius, level, Entity.class, box)) {
            if (entity instanceof Boss) {
                flag = true;
                break;
            }
        }
        return flag;
    }
*//*
    public static int getRespawnWaitTime(LocalPlayer player) {
        boolean hasBoss = hasBoss(Short.MAX_VALUE, player.level(),
                player.getBoundingBox());
        if (hasBoss) {
            return player.getRandom().nextInt(ModConfigs.BOSS_RESPAWN_TIME_MIN.get()
                    , ModConfigs.BOSS_RESPAWN_TIME_MAX.get());
        } else {
            return player.getRandom().nextInt(ModConfigs.DEFAULT_RESPAWN_TIME_MIN.get(),
                    ModConfigs.DEFAULT_RESPAWN_TIME_MAX.get());
        }
    }*/

    public static ItemStack enchantedBook(HolderLookup.RegistryLookup<Enchantment> registryLookup, ResourceKey<Enchantment> key, int level) {
        ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
        book.enchant(registryLookup.getOrThrow(key), level);
        return book;
    }
    public static ItemStack enchantedBook(Holder<Enchantment> enchantment, int level) {
        ItemStack book = Items.ENCHANTED_BOOK.getDefaultInstance();
        book.enchant(enchantment, level);
        return book;
    }

    public static<T extends Entity> T spawnEntity(Supplier<? extends T> entitySupplier, ServerLevel serverLevel, Vec3 pos){
        T entity = entitySupplier.get();
        if (entity != null) {
            entity.moveTo(pos);
            if(internalSpawnEntity(entity, serverLevel)){
                serverLevel.addFreshEntityWithPassengers(entity);
            }
            return entity;
        }
        return null;
    }

    public static<T extends Entity> T spawnEntity(EntityType<? extends T> type, ServerLevel level, Vec3 pos){
        return spawnEntity(() -> type.create(level), level, pos);
    }

    /**
     * 通过finalize事件初始化生物
     * @return 是否应该生成
     */
    public static boolean internalSpawnEntity(Entity entity, ServerLevel serverLevel){
        if (entity instanceof Mob mob) {
            mob.yHeadRot = mob.getYRot();
            mob.yBodyRot = mob.getYRot();
            // 事件中对生物血量修饰，生物finalizeSpawn中可能设置自身的属性baseValue
            net.neoforged.neoforge.event.EventHooks.finalizeMobSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, null);

            if (mob.isSpawnCancelled()) {
                mob.discard();
                return false;
            }
        }
        return true;
    }

    /**
     * 获取百分比增伤
     * <p>E.G</p>
     * <p>1.2 -> +20%</p>
     */
    public static float getAttributePercent(Holder<Attribute> attribute, LivingEntity entity){
        AttributeInstance instance = entity.getAttribute(attribute);
        if(instance!= null){
            return (float) ((IAttributeInstance) instance).terraentity$getPercentage();
        }
        return 1;
    }

    public static <K, V> Map<K,V> listToMap(Stream<? extends Pair<K, V>> pairs){
        return pairs.collect(Collectors.toMap(Pair::left, Pair::right));
    }

    public static Vec3 entityLerpMovement(Entity entity, float partialTick){
        return new Vec3(entity.xo, entity.yo, entity.zo).lerp(entity.position(), partialTick);
//        return new Vec3(entity.getX())
    }

    public static BlockPos findNearbyBlockEntity(Level level, BlockPos center, int radius, BiPredicate<BlockPos, BlockEntity> predicate) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                Map<BlockPos, BlockEntity> entities = level.getChunk(
                        SectionPos.blockToSectionCoord(center.getX()) + i,
                        SectionPos.blockToSectionCoord(center.getZ()) + j).getBlockEntities();
                if (!entities.isEmpty()) {
                    for (Map.Entry<BlockPos, BlockEntity> entry : entities.entrySet()) {
                        if(predicate.test(entry.getKey(), entry.getValue())) {
                            return entry.getKey();
                        }
                    }

                }
            }
        }
        return null;
    }
}
