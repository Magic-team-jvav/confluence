package org.confluence.mod.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Function4;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.terraentity.entity.ai.Boss;
import org.confluence.terraentity.utils.TEUtils;
import org.joml.Vector3d;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public final class ModUtils {
    public static final Direction[] DIRECTIONS = Direction.values();
    public static final Direction[] HORIZONTAL = new Direction[]{Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH};

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

    public static void createItemEntity(ItemStack itemStack, double x, double y, double z, Level level) {
        createItemEntity(itemStack, x, y, z, level, 40);
    }

    public static void createItemEntity(List<ItemStack> itemStacks, double x, double y, double z, Level level) {
        for (ItemStack itemStack : itemStacks) {
            createItemEntity(itemStack, x, y, z, level, 40);
        }
    }

    public static void createItemEntity(ItemStack itemStack, double x, double y, double z, Level level, int pickUpDelay) {
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
        itemEntity.setPickUpDelay(pickUpDelay);
        level.addFreshEntity(itemEntity);
    }

    public static void createItemEntity(Item item, int count, double x, double y, double z, Level level) {
        createItemEntity(item, count, x, y, z, level, 40);
    }

    public static void createItemEntity(Item item, int count, double x, double y, double z, Level level, int pickUpDelay) {
        if (count <= 0) return;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, new ItemStack(item, count));
        itemEntity.setPickUpDelay(pickUpDelay);
        level.addFreshEntity(itemEntity);
    }

    public static void createItemEntity(ItemStack itemStack, Vec3 vec, Level level) {
        createItemEntity(itemStack, vec.x, vec.y, vec.z, level, 40);
    }

    public static void dropMoney(int amount, double x, double y, double z, Level level) {
        int copper_count = amount % 9;
        int i = ((amount - copper_count) / 9);
        int silver_count = i % 9;
        int j = ((i - silver_count) / 9);
        int golden_count = j % 9;
        int k = (j - golden_count) / 9;
        createItemEntity(ModItems.COPPER_COIN.get(), copper_count, x, y, z, level, 0);
        createItemEntity(ModItems.SILVER_COIN.get(), silver_count, x, y, z, level, 0);
        createItemEntity(ModItems.GOLDEN_COIN.get(), golden_count, x, y, z, level, 0);
        createItemEntity(ModItems.PLATINUM_COIN.get(), k, x, y, z, level, 0);
    }

    public static Component getModifierTooltip(double amount, String type) {
        boolean b = amount > 0.0;
        amount *= 100.0;
        return Component.translatable(
                "prefix.confluence.tooltip." + (b ? "plus" : "take"),
                ATTRIBUTE_MODIFIER_FORMAT.format(b ? amount : -amount),
                Component.translatable("prefix.confluence.tooltip." + type)
        ).withStyle(b ? ChatFormatting.BLUE : ChatFormatting.RED);
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> getTicker(BlockEntityType<A> a, BlockEntityType<E> b, BlockEntityTicker<? super E> ticker) {
        return a == b ? (BlockEntityTicker<A>) ticker : null;
    }

    public static boolean isHalloween() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        return (month == Calendar.OCTOBER && date >= 15) || // 从 十月中旬
                (month == Calendar.NOVEMBER && date <= 15); // 到 十一月中旬
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
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);
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

    /**
     * 测试信息；使用此接口有助于集中管理防止漏网之鱼
     */
    public static void testMessage(String msg) {
        Confluence.LOGGER.info(msg);
    }

    public static void testMessage(Player player, String msg) {
        player.sendSystemMessage(Component.literal(msg));
    }

    public static void testMessage(Level level, String msg) {
        for (Player ply : level.players())
            ply.sendSystemMessage(Component.literal(msg));
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

    private static ChatFormatting getPotionCategoryColor(MobEffect effect) {
        return effect.getCategory().equals(MobEffectCategory.NEUTRAL) ?
                ChatFormatting.GRAY : effect.getCategory().equals(MobEffectCategory.BENEFICIAL) ?
                ChatFormatting.BLUE : ChatFormatting.RED;
    }

    /**
     * 计算向量夹角
     *
     * @return degree
     */
    public static double angleBetween(Vec3 v1, Vec3 v2) {
        return Math.acos(v1.dot(v2) / v1.length() / v2.length());
    }

    /**
     * 将游戏缓存的贴图写入文件
     *
     * @param nativeImage 游戏缓存的贴图
     * @param path        文件全路径，比如<code>FMLPaths.GAMEDIR.getPrefab().resolve("redstone.png")</code>
     * @param argbMixer   argb的混合方法
     */
    public static void writeImageToFile(NativeImage nativeImage, Path path, Function4<Integer, Integer, Integer, Integer, Integer> argbMixer) {
        int[] pixels = nativeImage.getPixelsRGBA();
        Path parent = path.getParent();
        try {
            if (!Files.exists(parent)) Files.createDirectories(parent);
            BufferedImage image = new BufferedImage(nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < nativeImage.getHeight(); ++i) {
                for (int j = 0; j < nativeImage.getWidth(); ++j) {
                    int color = pixels[j + i * nativeImage.getWidth()];
                    int a = color >>> 24;
                    int b = color >> 16 & 255;
                    int g = color >> 8 & 255;
                    int r = color & 255;
                    image.setRGB(j, i, argbMixer.apply(a, r, g, b));
                }
            }
            ImageIO.write(image, "png", path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeRawImageToFile(NativeImage nativeImage, Path path) {
        writeImageToFile(nativeImage, path, (a, r, g, b) -> a << 24 | r << 16 | g << 8 | b);
    }

    public static void writeGrayImageToFile(NativeImage nativeImage, Path path) {
        writeImageToFile(nativeImage, path, (a, r, g, b) -> {
            int avg = (r + g + b) / 3;
            return a << 24 | avg << 16 | avg << 8 | avg;
        });
    }

    public static void lightningPathList(List<Vector3d> locationList, double dis, int move, RandomSource random) {
        boolean refined;
        do {
            refined = false;
            for (int i = 0; i < locationList.size() - 1; i++) {
                Vector3d point1 = locationList.get(i);
                Vector3d point2 = locationList.get(i + 1);
                double distance = Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2) + Math.pow(point2.z - point1.z, 2));
                if (distance > dis) {
                    Vector3d midpoint = new Vector3d();
                    midpoint.x = ((point1.x + point2.x) / 2);
                    midpoint.y = ((point1.y + point2.y) / 2);
                    midpoint.z = ((point1.z + point2.z) / 2);
                    double offset = distance / move;
                    midpoint.x = midpoint.x + (random.nextDouble() - 0.5) * offset * 2;
                    midpoint.y = midpoint.y + (random.nextDouble() - 0.5) * offset * 2;
                    midpoint.z = midpoint.z + (random.nextDouble() - 0.5) * offset * 2;
                    locationList.add(i + 1, midpoint);
                    refined = true;
                }
            }
        } while (refined);
    }


    /**
     * 检测半径内是否存在boss
     *
     * @param radius 检测半径
     * @param level  level
     * @param box    参照实体碰撞箱
     * @return 是否存在boss
     */
    public static boolean hasBoss(double radius, Level level,
                                  AABB box) {
        boolean flag = false;
        for (Entity entity : TEUtils.getNearbyEntities(radius, level, Entity.class, box)) {
            if (entity instanceof Boss) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static boolean hasBoss(Level level,
                                  AABB box) {
        return hasBoss(Short.MAX_VALUE, level, box);
    }

    /**
     * 获取玩家复活时间
     *
     * @param player 玩家
     * @return 复活时间
     */
    public static int getRespawnWaitTime(LocalPlayer player) {
        boolean hasBoss = hasBoss(player.level(), player.getBoundingBox());
        if (hasBoss) {
            return player.getRandom().nextInt(CommonConfigs.BOSS_RESPAWN_TIME_MIN.get()
                    , CommonConfigs.BOSS_RESPAWN_TIME_MAX.get());
        } else {
            return player.getRandom().nextInt(CommonConfigs.DEFAULT_RESPAWN_TIME_MIN.get(),
                    CommonConfigs.DEFAULT_RESPAWN_TIME_MAX.get());
        }
    }
}
