package org.confluence.mod.util;

import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.vertex.PoseStack;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.geckolib.IGeoCube;
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
    public static final List<Pair<ClientLevel, Entity>> toBeAdded = new ArrayList<>();
    public static final List<Entity> toBeDiscarded = new ArrayList<>();

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
        float bezierValue = LibUtils.cubicBezier(t, p0, p1, p2, p3);
        return bezierValue * max;
    }

    public static ModelPart findRootModelPart(LivingEntityRenderer<?, ?> renderer){
        EntityModel<?> model = renderer.getModel();
        ModelPart any = findAnyModelPart(model, model.getClass());
        if(any == null) return null;
        return IModelPart.of(any).confluence$root();
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
        toBeAdded.add(Pair.of(level, entity));
    }

    public static void tellDiscardEntity(Entity entity){
        toBeDiscarded.add(entity);
    }

    /** 让原版Renderer帮我变换，在model.renderToBuffer之前就会返回，就能保留变换的结果 */
    public static <T extends LivingEntity> void dummyRender(LivingEntityRenderer<T, ?> livingRenderer, LivingEntity entity, PoseStack poseStack){
        livingRenderer.render((T) entity, entity.getYRot(), 1, poseStack, DummyMultiBufferSource.INSTANCE, 0);
    }


}
