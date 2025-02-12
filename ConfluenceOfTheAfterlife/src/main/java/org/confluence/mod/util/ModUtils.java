package org.confluence.mod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.mixed.Immunity;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.component.NbtComponent;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terra_curio.util.TCUtils;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terraentity.TerraEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

import static org.confluence.mod.common.item.common.CoinItem.MAX_STACK_SIZE;
import static org.confluence.mod.common.item.common.CoinItem.UPGRADES_COUNT;

public final class ModUtils {
    public static final Direction[] HORIZONTAL = new Direction[]{Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH};
    public static final Direction[] DIRECTIONS = Direction.values();
    public static final String NO_DROPS_TAG = "confluence:no_drops";
    public static final Set<String> CONFLUENCE_NAMESPACES = Set.of(Confluence.MODID, TerraCurio.MODID, TerraEntity.MODID, TerraGuns.MODID);

    public static void createItemEntity(ItemStack itemStack, double x, double y, double z, Level level, int pickUpDelay) {
        if (itemStack.isEmpty()) return;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
        itemEntity.setPickUpDelay(pickUpDelay);
        level.addFreshEntity(itemEntity);
    }

    public static void createItemEntity(ItemStack itemStack, Vec3 pos, Level level, int pickUpDelay) {
        createItemEntity(itemStack, pos.x, pos.y, pos.z, level, pickUpDelay);
    }

    public static void createItemEntity(Item item, int count, double x, double y, double z, Level level, int pickUpDelay) {
        if (count <= 0 || item == Items.AIR) return;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, new ItemStack(item, count));
        itemEntity.setPickUpDelay(pickUpDelay);
        level.addFreshEntity(itemEntity);
    }

    public static void createItemEntity(Item item, int count, Vec3 pos, Level level, int pickUpDelay) {
        createItemEntity(item, count, pos.x, pos.y, pos.z, level, pickUpDelay);
    }

    public static void dropMoney(int amount, double x, double y, double z, Level level) {
        int copper_count = amount % UPGRADES_COUNT;
        int i = ((amount - copper_count) / UPGRADES_COUNT);
        int silver_count = i % UPGRADES_COUNT;
        int j = ((i - silver_count) / UPGRADES_COUNT);
        int golden_count = j % UPGRADES_COUNT;
        int k = (j - golden_count) / UPGRADES_COUNT;
        createItemEntity(ModItems.COPPER_COIN.get(), copper_count, x, y, z, level, 0);
        createItemEntity(ModItems.SILVER_COIN.get(), silver_count, x, y, z, level, 0);
        createItemEntity(ModItems.GOLDEN_COIN.get(), golden_count, x, y, z, level, 0);
        createItemEntity(ModItems.PLATINUM_COIN.get(), k, x, y, z, level, 0);
    }

    public static void dropMoney(long amount, double x, double y, double z, Level level) {
        while (amount > 0x3F3F3F3F) {
            dropMoney(0x3F3F3F3F, x, y, z, level);
            amount -= 0x3F3F3F3F;
        }
        dropMoney((int) amount, x, y, z, level);
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> getTicker(BlockEntityType<A> a, BlockEntityType<E> b, BlockEntityTicker<? super E> ticker) {
        return a == b ? (BlockEntityTicker<A>) ticker : null;
    }

    /**
     * 把向量转成角度
     *
     * @return [yaw, pitch]
     */
    public static float[] dirToRot(Vec3 vec) {
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;
        double h = vec.horizontalDistance();
        return new float[]{
                (float) Mth.atan2(-x, z) * Mth.RAD_TO_DEG,
                (float) Mth.atan2(-y, h) * Mth.RAD_TO_DEG
        };
    }

    /**
     * 为专家?在处理if...else if时应先使用:
     *
     * @see ModUtils#isMaster(Level)
     */
    public static boolean isAtLeastExpert(Level level) {
        return level.getDifficulty().getId() >= Difficulty.NORMAL.getId();
    }

    /**
     * 为大师?在处理if...else if时应先使用此方法
     */
    public static boolean isMaster(Level level) {
        return level.getDifficulty() == Difficulty.HARD;
    }

    /**
     * 根据游戏难度选择值
     *
     * @param classic 经典难度的值
     * @param expert  专家难度的值
     * @param master  大师难度的值
     * @return 选择到的值
     */
    public static <T> T switchByDifficulty(Level level, T classic, T expert, T master) {
        return switch (level.getDifficulty()) {
            case PEACEFUL, EASY -> classic;
            case NORMAL -> expert;
            case HARD -> master;
        };
    }

    /**
     * 根据游戏难度选择值，但是根据区域难度
     *
     * @param classic   经典难度的值
     * @param expert    专家难度的值
     * @param master    大师难度的值
     * @param legendary 传奇难度的值
     * @return 选择到的值
     */
    public static <T> T switchByDifficulty(Level level, BlockPos blockPos, T classic, T expert, T master, T legendary) {
        float difficulty = level.getCurrentDifficultyAt(blockPos).getEffectiveDifficulty();
        if (difficulty >= 4) return legendary;
        if (difficulty >= 3) return master;
        if (difficulty >= 2) return expert;
        return classic;
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

    public static Direction[] directionsInAxis(Direction.Axis axis) {
        return switch (axis) {
            case X -> new Direction[]{Direction.EAST, Direction.WEST};
            case Y -> new Direction[]{Direction.UP, Direction.DOWN};
            default -> new Direction[]{Direction.SOUTH, Direction.NORTH};
        };
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

    public static void lightningPathList(List<Vector3d> locationList, double dist, int move, RandomSource random) {
        double distSqr = dist * dist;
        boolean refined;
        do {
            refined = false;
            for (int i = 0; i < locationList.size() - 1; i++) {
                Vector3d point1 = locationList.get(i);
                Vector3d point2 = locationList.get(i + 1);
                double distanceSqr = point2.distanceSquared(point1);
                if (distanceSqr > distSqr) {
                    Vector3d midpoint = new Vector3d();
                    point1.add(point2, midpoint).mul(0.5);
                    double offset = Math.sqrt(distanceSqr) / move;
                    double twoOffset = offset * 2;
                    midpoint.x = midpoint.x + (random.nextDouble() - 0.5) * twoOffset;
                    midpoint.y = midpoint.y + (random.nextDouble() - 0.5) * twoOffset;
                    midpoint.z = midpoint.z + (random.nextDouble() - 0.5) * twoOffset;
                    locationList.add(i + 1, midpoint);
                    refined = true;
                }
            }
        } while (refined);
    }

    /**
     * 仅获取
     *
     * @see TCUtils#getItemStackNbt(ItemStack) 获取或创建
     */
    public static @Nullable CompoundTag getItemStackNbt(ItemStack itemStack) {
        NbtComponent component = itemStack.get(TCDataComponentTypes.NBT);
        if (component == null) return null;
        return component.nbt();
    }

    @Nullable
    public static Immunity getImmunityCause(DamageSource damageSource) {
        Entity directEntity = damageSource.getDirectEntity();
        ItemStack weaponItemStack = damageSource.getWeaponItem();
        if (weaponItemStack != null) {
            Item weaponItem = weaponItemStack.getItem();
            boolean fromConfluence = isFromConfluence(BuiltInRegistries.ITEM, weaponItem);
            if (fromConfluence && (weaponItem instanceof SwordItem) && directEntity instanceof Projectile projectile) {
                return (Immunity) projectile;
            } else if (weaponItem instanceof Immunity im) {
                return switch (im.confluence$getImmunityType()) {
                    case STATIC -> im;
                    case LOCAL -> (Immunity) (Object) weaponItemStack;
                };
            } else if (fromConfluence) {
                return (Immunity) (Object) weaponItemStack;
            }
        }
        if (directEntity instanceof Projectile && isFromConfluence(BuiltInRegistries.ENTITY_TYPE, directEntity.getType())) {
            return (Immunity) directEntity;
        }
        // TODO: 打表
        return null;
    }

    public static <T> boolean isFromConfluence(Registry<T> registry, T obj) {
        ResourceLocation key = registry.getKey(obj);
        return key != null && CONFLUENCE_NAMESPACES.contains(key.getNamespace());
    }

    public static int getSlotIndex(@Nullable EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 0;
            case CHEST -> 1;
            case LEGS -> 2;
            case FEET -> 3;
            case null, default -> -1;
        };
    }

    public static int getDayTime(int hour, int minute) {
        if (hour < 0 || hour > 23) throw new DateTimeParseException("hour bounds is [0, 23], currently is " + hour, "", 0);
        if (minute < 0 || minute > 59) throw new DateTimeParseException("minute bounds is [0, 59], currently is " + minute, "", 0);
        int i = (hour - 6) * 1000;
        int j = (int) (minute / 0.06F);
        if (i < 0) i += 24000;
        return i + j;
    }

    public static boolean isWithinDayTime(int start, int end, long time) {
        time %= 24000L;
        if (start > end) {
            return time >= start || time <= end;
        }
        return time >= start && time <= end;
    }

    public static boolean isWithinDayTime(int startHour, int startMinute, int endHour, int endMinute, long time) {
        return isWithinDayTime(getDayTime(startHour, startMinute), getDayTime(endHour, endMinute), time);
    }

    public static Vector3d toVector3d(BlockPos blockPos) {
        Vec3 center = blockPos.getCenter();
        return new Vector3d(center.x, center.y, center.z);
    }

    public static BlockPos fromVector3d(Vector3d vector3d) {
        return new BlockPos(Mth.floor(vector3d.x), Mth.floor(vector3d.y), Mth.floor(vector3d.z));
    }

    public static int getMaxStackSize(int original) {
        return Math.max(original, MAX_STACK_SIZE);
    }
}
