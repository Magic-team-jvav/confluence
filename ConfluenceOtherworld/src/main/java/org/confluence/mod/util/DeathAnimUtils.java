package org.confluence.mod.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.DummyMultiBufferSource;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.mixed.IGeoCube;
import org.confluence.mod.mixed.IModelPart;
import org.confluence.terraentity.entity.util.DeathAnimOptions;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author voila */
public final class DeathAnimUtils {
    public static final Map<EntityType<? extends LivingEntity>, DeathAnimOptions> options = new HashMap<>();

//    static{
//        options.put(GHAST, DeathAnimOptions.Builtin.NO_GRAVITY_LOW_SPIN.customParticle(e -> {
//        }));
//        options.put(PHANTOM, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(SPIDER, DeathAnimOptions.Builtin.NO_GRAVITY.bloodColor(0.459f, 0.706f, 1.000f));
//        options.put(CAVE_SPIDER, DeathAnimOptions.Builtin.NO_GRAVITY.bloodColor(0.459f, 0.706f, 1.000f));
//        options.put(WARDEN, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(BLAZE, DeathAnimOptions.Builtin.EXTRA_FALL.bloodColor(0.89f, 0.65f, 0.07f));
//        options.put(GIANT, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.45f, 0f, 0f));
//        options.put(WITHER, DeathAnimOptions.Builtin.EXTRA_FALL.customParticle(entity -> {
//        }));
//        options.put(VEX, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(ENDERMAN, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.627f, 0.000f, 0.659f));
//        options.put(GUARDIAN, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(ELDER_GUARDIAN, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(PIGLIN, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(PIGLIN_BRUTE, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(ENDERMITE, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(ZOMBIE, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.45f, 0f, 0f));
//        options.put(DROWNED, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.45f, 0f, 0f));
//        options.put(ZOMBIE_VILLAGER, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.45f, 0f, 0f));
//        options.put(ZOMBIFIED_PIGLIN, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.45f, 0f, 0f));
//        options.put(HUSK, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.45f, 0f, 0f));
//        options.put(ZOGLIN, DeathAnimOptions.Builtin.LOW_SPIN.bloodColor(0.45f, 0f, 0f));
//        options.put(WITHER_SKELETON, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.2f, 0.2f, 0.2f));
//        options.put(SKELETON, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.631f, 0.631f, 0.631f));
//        options.put(STRAY, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.95f, 0.95f, 0.95f));
//        options.put(CREEPER, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.44f, 0.44f, 0.44f));
//        options.put(WITCH, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(RAVAGER, DeathAnimOptions.Builtin.NO_GRAVITY_LOW_SPIN);
//        options.put(PILLAGER, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(ILLUSIONER, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(EVOKER, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(VINDICATOR, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(SILVERFISH, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(DOLPHIN, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(PUFFERFISH, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(TADPOLE, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(COD, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(SALMON, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(TROPICAL_FISH, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(IRON_GOLEM, DeathAnimOptions.Builtin.LOW_SPIN.bloodColor(1, 0.94f, 0.85f));
//        options.put(SHULKER, DeathAnimOptions.Builtin.LOW_SPIN.bloodColor(0.627f, 0.000f, 0.659f));
//        options.put(SNOW_GOLEM, DeathAnimOptions.Builtin.DEFAULT.bloodColor(1, 1, 1));
//        options.put(FOX, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(PIG, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(FROG, DeathAnimOptions.Builtin.NO_GRAVITY_LOW_SPIN);
//        options.put(STRIDER, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(OCELOT, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(PANDA, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(GOAT, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(COW, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(MOOSHROOM, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(HOGLIN, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(CHICKEN, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(RABBIT, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(HORSE, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(SKELETON_HORSE, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.631f, 0.631f, 0.631f));
//        options.put(CAMEL, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(MULE, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(LLAMA, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(TRADER_LLAMA, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(DONKEY, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(ZOMBIE_HORSE, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.45f, 0f, 0f));
//        options.put(BEE, DeathAnimOptions.Builtin.NO_GRAVITY.bloodColor(0.96f, 1, 0.73f)); // 重力
//        options.put(AXOLOTL, DeathAnimOptions.Builtin.NO_GRAVITY_LOW_SPIN);
//        options.put(TURTLE, DeathAnimOptions.Builtin.NO_XZ_SPIN_NO_GRAVITY);
//        options.put(SNIFFER, DeathAnimOptions.Builtin.LOW_SPIN);
//        options.put(SHEEP, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(POLAR_BEAR, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(WOLF, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(PARROT, DeathAnimOptions.Builtin.NO_GRAVITY);
//        options.put(CAT, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(WANDERING_TRADER, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(VILLAGER, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(ALLAY, DeathAnimOptions.Builtin.LOW_SPIN.customParticle(entity -> {
//
//        })); // TODO: 烟
//        options.put(BAT, DeathAnimOptions.Builtin.DEFAULT);
//        options.put(SLIME, DeathAnimOptions.Builtin.DEFAULT.bloodColor(0.396f, 0.659f, 0.333f));
//    }
//
//    public static void moveParts(PoseStack poseStack, Entity animatable, Object bone, float partialTick, DeathAnimOptions options){
//        if(!(animatable instanceof LivingEntity entity) || entity.isAlive() || options == null) return;
//        float[] motions;
//        if(bone instanceof GeoBone b){ // TODO: 既然GeoBone也实现了IModelPart(还没提交)就不用分开处理了
//            GeoBone parent = b.getParent();
//            motions = ((IClientLivingEntity) entity).confluence$getMotionsForBone(b);
//            if(parent != null){
//                float[] pm = ((IClientLivingEntity) entity).confluence$getMotionsForBone(parent);
//                for(int i = 0, motionsLength = motions.length; i < motionsLength; i++){
//                    motions[i] -= pm[i];
//                }
//            }
//            poseStack.translate(getOffset(entity.deathTime, motions[0], partialTick),
//                getOffset(entity.deathTime, motions[1], partialTick),
//                getOffset(entity.deathTime, motions[2], partialTick));
//            poseStack.mulPose(Axis.XP.rotationDegrees(getOffset(entity.deathTime, motions[3], partialTick)));
//            poseStack.mulPose(Axis.YP.rotationDegrees(getOffset(entity.deathTime, motions[4], partialTick)));
//            poseStack.mulPose(Axis.ZP.rotationDegrees(getOffset(entity.deathTime, motions[5], partialTick)));
//        }else if(bone instanceof ModelPart p){
//            motions = ((IClientLivingEntity) entity).confluence$getMotionsForPart(p);
//            float[] parentMotions = ((IModelPart) (Object) p).confluence$parentOffset();
//            List<ModelPart.Cube> cubes = ((ModelPartAccessor) (Object) p).getCubes();
//            PartPose deathPose = ((IClientLivingEntity) entity).confluence$getPoseForPart(p);
//            if(deathPose != null){
//                p.loadPose(deathPose);
//            }
////            p.resetPose();
//            if(cubes.size() != 0){
//                Map<String, ModelPart> children = ((ModelPartAccessor) (Object) p).getChildren();
//                float[] bak = Arrays.copyOf(motions, 6);
//                motions = Arrays.copyOf(motions, 6);
//                for(int i = 0, motionsLength = motions.length; i < motionsLength; i++){
//                    motions[i] -= parentMotions[i];
//                }
//
//                float tx = getOffset(entity.deathTime, motions[0], partialTick);
//                float footY = motions[1];
//                float tz = getOffset(entity.deathTime, motions[2], partialTick);
//                float rx = getOffset(entity.deathTime, motions[3], partialTick);
//                float ry = getOffset(entity.deathTime, motions[4], partialTick);
//                float rz = getOffset(entity.deathTime, motions[5], partialTick);
//                float ty = 0;
//                Vec3 landMotion = ((IClientLivingEntity) entity).confluence$landMotion();
//                Vec2 xz = ((IModelPart) (Object) p).confluence$landMotion();
//                if(!Double.isNaN(landMotion.y) && !options.isNoModelGravity()){  // 实体已落地
//                    landMotion = landMotion.with(Direction.Axis.Y, -0.2); // 客户端的精度太低了 算半天还不如一个固定值效果好
//                    int landTicks = ((IClientLivingEntity) entity).confluence$landTicks();
//                    int lastTick = Math.max(0, landTicks - 1);
//                    float fallDistance = (float) -(Mth.lerp(partialTick, (landMotion.y - 0.08) * lastTick, (landMotion.y - 0.08) * landTicks));
//                    ty = Math.min(fallDistance, footY);
//                    if(ty != footY){  // 模型未落地
//                        double speed = landMotion.length();
//                        float[] rot = ModUtils.dirToRot(landMotion);
//                        float motionYaw;
//                        float renderYHeadRot = entity.getViewYRot(partialTick);
//                        float renderYRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
//                        if(((IModelPart) (Object) p).confluence$isSkull()){ // 头戴头颅要特殊处理
//                            motionYaw = Mth.wrapDegrees(-rot[0] + renderYRot + (renderYHeadRot - renderYRot));
//                        }else{
//                            motionYaw = Mth.wrapDegrees(-rot[0] + 180 + renderYRot);  // 我不知道为什么是这样，是试出来的
//                        }
//                        landMotion = Vec3.directionFromRotation(rot[1], motionYaw).normalize().scale(speed);
//                        tx += Mth.lerp(partialTick, (landMotion.x * Math.pow(0.96, lastTick)) * lastTick, (landMotion.x * Math.pow(0.9, landTicks)) * landTicks);
//                        tz += Mth.lerp(partialTick, (landMotion.z * Math.pow(0.96, lastTick)) * lastTick, (landMotion.z * Math.pow(0.9, landTicks)) * landTicks);
//                    }else if(xz != null){
//                        tx = xz.x;
//                        tz = xz.y;
//                    }
//                }
//                ((IModelPart) (Object) p).confluence$landMotion(new Vec2(tx, tz));
//                poseStack.translate(tx, ty, tz);
//                poseStack.mulPose(Axis.XP.rotationDegrees(rx));
//                poseStack.mulPose(Axis.YP.rotationDegrees(ry));
//                poseStack.mulPose(Axis.ZP.rotationDegrees(rz));
//                for(ModelPart child : children.values()){
//                    ((IModelPart) (Object) child).confluence$parentOffset(bak);
//                }
//
//            }
//        }
//    }
//
//    public static float getOffset(int tick, float max, float partialTick){
//        float now = getPosition(tick, max);
//        float last = getPosition(tick - 1, max);
//        return Mth.wrapDegrees(Mth.lerp(partialTick, last, now));
//    }
//
//    /** 最大旋转和最大偏移 */
//    public static float[] createOffsets(RandomSource random, Vec3 motion, ModelPart part, DeathAnimOptions options){
//        Vector3f dir = createSpread(random, motion);
//        List<ModelPart.Cube> cubes = ((ModelPartAccessor) (Object) part).getCubes();
//        float max = Float.NEGATIVE_INFINITY;
//        for(ModelPart.Cube cube : cubes){
//            max = Math.max(cube.maxY, max);
//        }
//        float[] rot = options.getRotations();
//        return new float[]{
//            dir.x,
//            max == Float.NEGATIVE_INFINITY ? 0 : Math.max(0, (24 - (max + part.y)) / 16),
//            dir.z,
//            random.nextFloat() * rot[0] * 2 - rot[0],
//            random.nextFloat() * rot[1] * 2 - rot[1],
//            random.nextFloat() * rot[2] * 2 - rot[2]
//        };
//    }

    public static float[] createOffsets(RandomSource random, Vec3 motion, float height, DeathAnimOptions options){
        Vector3f dir = createSpread(random, motion);
        float[] rot = options.getRotations();
        return new float[]{
            dir.x,
            -height / 16,
            dir.z,
            random.nextFloat() * rot[0] * 2 - rot[0],
            random.nextFloat() * rot[1] * 2 - rot[1],
            random.nextFloat() * rot[2] * 2 - rot[2]
        };
    }


    private static Vector3f createSpread(RandomSource random, Vec3 motion){
        float yRot = random.nextFloat() * 180 - 90;
        if(motion.equals(Vec3.ZERO)){
            motion = Vec3.ZERO.offsetRandom(random, 1);
        }
        float[] rots = VectorUtils.dirToRot(motion, true);
        rots[0] += yRot;
        return Vec3.directionFromRotation(0, rots[0]).normalize().scale(1.2).toVector3f();
    }

    /** @author ChatGPT */
    public static float cubicBezier(float t, float p0, float p1, float p2, float p3){
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;

        return uuu * p0 + 3 * uu * t * p1 + 3 * u * tt * p2 + ttt * p3;
    }

    /** @author voila, ChatGPT */
    public static float getPosition(int tick, float max){
        if(tick < 0) tick = 0;
        if(tick > 20) tick = 20;
        float t = tick / 20.0f;

        // 贝塞尔控制点  AI写的，可能写得不对，同样的数值和浏览器的结果不一样
        float p0 = 0f;
        float p1 = 1f;
        float p2 = 1f;
        float p3 = 1f;
        float bezierValue = cubicBezier(t, p0, p1, p2, p3);
        return bezierValue * max;
    }

    public static ModelPart findRootModelPart(LivingEntityRenderer<?, ?> renderer){
        EntityModel<?> model = renderer.getModel();
        ModelPart any = findAnyModelPart(model, model.getClass());
        if(any == null) return null;
        return ((IModelPart) (Object) (any)).confluence$root();
    }
    public static ModelPart findAnyModelPart(Object model, Class<?> finding){
        if(model instanceof HierarchicalModel<?> hierarchicalModel){
            return hierarchicalModel.root();
        }
        for(Field field : finding.getDeclaredFields()){
            try{
                field.setAccessible(true);
                if(field.get(model) instanceof ModelPart part){
                    return part;
                }
            }catch(IllegalAccessException | InaccessibleObjectException e){
                Confluence.LOGGER.error("field.get: ", e);
            }
        }
        if(Model.class.isAssignableFrom(finding.getSuperclass())){
            return findAnyModelPart(model, finding.getSuperclass());
        }
        return null;
    }

    public static List<ModelPart> findAllModelPart(LivingEntityRenderer<?, ?> renderer){  // 用List是为了保证顺序
//        List<ModelPart> cache = ((ILivingEntityRenderer) renderer).confluence$getPartsCache();
//        if(!cache.isEmpty()){  // 要是有哪个Layer是中途加进去的而不是构造的时候就加的就会出问题
//            return cache;
//        }
        EntityModel<?> model = renderer.getModel();
        List<ModelPart> ret = findAllModelPart(model, model.getClass());
//        for(RenderLayer<LivingEntity, EntityModel<LivingEntity>> layer : ((LivingEntityRendererAccessor) renderer).getLayers()){
//            for(Field field : layer.getClass().getDeclaredFields()){
//                try{
//                    field.setAccessible(true);
//                    if(field.get(layer) instanceof EntityModel<?> layerModel){
//                        ret.addAll(findAllModelPart(layerModel, layerModel.getClass()));
//                    }
//                }catch(IllegalAccessException | InaccessibleObjectException e){
//                    Confluence.LOGGER.error("field.get: ", e);
//                }
//            }
//            if(layer instanceof CustomHeadLayer<?, ?>){
//                for(SkullModelBase skullModels : ((CustomHeadLayerAccessor) layer).getSkullModels().values()){
//                    for(ModelPart skull : findAllModelPart(skullModels, skullModels.getClass())){
//                        ((IModelPart) (Object) skull).confluence$isSkull(true);
//                        ret.add(skull);
//                    }
//                }
//            }
//        }
//        cache.addAll(ret);
        return ret;
    }

    public static List<ModelPart> findAllModelPart(Object model, Class<?> finding){
        List<ModelPart> ret = new ArrayList<>();
        if(model instanceof HierarchicalModel<?> hierarchicalModel){
            ret.addAll(hierarchicalModel.root().getAllParts().toList());
            return ret;
        }
        for(Field field : finding.getDeclaredFields()){
            try{
                field.setAccessible(true);
                if(field.get(model) instanceof IModelPart part){
                    ret.addAll(part.confluence$root().getAllParts().toList());
                    break;
                }
            }catch(IllegalAccessException | InaccessibleObjectException e){
                Confluence.LOGGER.error("field.get: ", e);
            }
        }
        if(ret.isEmpty() && Model.class.isAssignableFrom(finding.getSuperclass())){
            ret.addAll(findAllModelPart(model, finding.getSuperclass()));
        }
        return ret;
    }

//    public static List<CoreGeoBone> findAllBones(BakedGeoModel model){
//        List<CoreGeoBone> ret = new ArrayList<>(/*model.topLevelBones()*/);
//        for(GeoBone bone : model.topLevelBones()){
//            ret.addAll(addChildBones(bone));
//        }
//        return ret;
//    }
//
//    private static List<GeoBone> addChildBones(GeoBone bone){
//        List<GeoBone> ret = new ArrayList<>();
//        List<GeoBone> children = bone.getChildBones();
//        if(children.size() == 0){
//            ret.add(bone);
////            Confluence.LOGGER.info("{} {} {} {}",bone.getName(),bone.getPivotX(),bone.getPosX(),bone.getLocalPosition());
//        }else{
//            for(GeoBone child : children){
//                ret.addAll(addChildBones(child));
//            }
//        }
//        return ret;
//    }
//
//
//    public static void addBloodParticles(Entity entity){
//        if(!(entity.level() instanceof ServerLevel serverLevel)) return;
//        AABB bb = entity.getBoundingBox();
//        Vec3 motion = entity.getDeltaMovement();
//        Vec3 pos = bb.getCenter();
//        float[] color;
//        DeathAnimOptions options = getDeathAnimOptions(entity);
//        if(options != null){
//            color = options.getBloodColor();
//            if(options.makeParticle(entity)){
//                return;
//            }
//        }else{
//            color = DeathAnimOptions.Builtin.DEFAULT.getBloodColor();
//        }
//        ParticleOptions blood = new BloodParticleOptions(color[0], color[1], color[2], motion.x, motion.y, motion.z);
//        ParticleOptions bodyPart = new BodyPartsParticleOptions(motion.x, motion.y, motion.z, entity.getId());
//        serverLevel.sendParticles(blood, pos.x, pos.y, pos.z, calcParticleCount(bb), bb.getXsize() / 3, bb.getYsize() / 3, bb.getZsize() / 3, 0);
//        serverLevel.sendParticles(bodyPart, pos.x, pos.y, pos.z, calcParticleCount(bb), bb.getXsize() / 3, bb.getYsize() / 3, bb.getZsize() / 3, 0);
//    }

    @Nullable
    public static DeathAnimOptions getDeathAnimOptions(Entity entity){
        return entity instanceof DeathAnimOptions r ? r : entity == null ? null : options.get(entity.getType());
    }

    public static boolean hasDeathAnimOptions(Entity entity){
        return getDeathAnimOptions(entity) != null;
    }

    public static int calcParticleCount(AABB range){
        double x = range.getXsize() * range.getYsize() * range.getZsize();
        return (int) (85 * Math.log(x + 1));
    }

    public static GeoCube duplicateGeoCube(GeoCube geoCube){
        GeoQuad[] quads = geoCube.quads();
        GeoQuad[] newQuads = new GeoQuad[quads.length];
        float[] avCoords = new float[3];
        float[] minCoords = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
        float[] maxCoords = new float[]{-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
        int coordsCount = 0;
        for(int j = 0, quadsLength = quads.length; j < quadsLength; j++){
            GeoQuad quad = quads[j];
            if(quad == null){
                continue;
            }
            GeoVertex[] vertices = quad.vertices();
            GeoVertex[] newVertex = new GeoVertex[vertices.length];
            for(int i = 0, verticesLength = vertices.length; i < verticesLength; i++){
                GeoVertex vertex = vertices[i];
                Vector3f pos = vertex.position();
                avCoords[0] += pos.x;
                avCoords[2] += pos.z;
                if(pos.x<minCoords[0]) minCoords[0] = pos.x;
                if(pos.x>maxCoords[0]) maxCoords[0] = pos.x;
                if(pos.y<minCoords[1]) minCoords[1] = pos.y;
                if(pos.y>maxCoords[1]) maxCoords[1] = pos.y;
                if(pos.z<minCoords[2]) minCoords[2] = pos.z;
                if(pos.z>maxCoords[2]) maxCoords[2] = pos.z;
                coordsCount++;
                newVertex[i] = new GeoVertex(new Vector3f(pos), vertex.texU(), vertex.texV());
            }
            newQuads[j] = new GeoQuad(newVertex, new Vector3f(quad.normal()), quad.direction());
        }
        if(coordsCount == 0){
            return null;
        }

        avCoords[0] /= coordsCount;
        avCoords[1] = minCoords[1];
        avCoords[2] /= coordsCount;
        Vec3 offset =  new Vec3(avCoords[0], avCoords[1], avCoords[2]);
        GeoCube newCube = new GeoCube(newQuads, geoCube.pivot().subtract(offset.scale(16)), geoCube.rotation(), geoCube.size(), geoCube.inflate(), geoCube.mirror());
        moveToOrigin(newCube, offset);
        IGeoCube iGeoCube = IGeoCube.of(newCube);
        iGeoCube.confluence$setMaxCoords(maxCoords);
        iGeoCube.confluence$setMinCoords(minCoords);
        return newCube;
    }

    public static void moveToOrigin(GeoCube cube, Vec3 centroid){
        for(GeoQuad quad : cube.quads()){
            if(quad == null) continue;
            for(GeoVertex vertex : quad.vertices()){
                Vector3f pos = vertex.position();
                pos.set(pos.x - centroid.x, pos.y - centroid.y, pos.z - centroid.z);
            }
        }
    }

    public static void tellAddEntity(ClientLevel level,Entity entity){
        Minecraft.getInstance().tell(() -> level.addEntity(entity));
    }

    /** 让原版Renderer帮我变换，在model.renderToBuffer之前就会返回，就能保留变换的结果 */
    public static <T extends LivingEntity> void dummyRender(LivingEntityRenderer<T, ?> livingRenderer, LivingEntity entity, PoseStack poseStack){
        livingRenderer.render((T) entity, entity.getYRot(), 1, poseStack, DummyMultiBufferSource.INSTANCE, 0);
    }


}
