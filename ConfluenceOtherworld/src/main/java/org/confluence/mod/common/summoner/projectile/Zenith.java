package org.confluence.mod.common.summoner.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.attachmentEntity.SyncFieldDispatcher;
import org.confluence.mod.common.summoner.particle.ParticleHelper;
import org.confluence.mod.common.summoner.particle.ZenithParticleOptions;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.jetbrains.annotations.NotNull;
import org.mesdag.portlib.client.PortDeltaTicker;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Zenith extends Projectile implements IEntityCollision<Zenith> {

    public Vec3 initialPosition = Vec3.ZERO;
    public RenderType renderType;
    public float alpha;

    /**
     * 剑型的同步编解码器。源实现放在 {@code SummonerStreamCodecs.ZENITH_RENDER_TYPE}，
     * 由 {@code StreamCodec.composite(LyraStreamCodecs.STRING_UTF8, Enum::name, RenderType::valueOf)} 构成，
     * 这里用 PortLib 的字符串编解码器等价实现（同样是按 name 编码）。
     */
    private static final PortStreamCodec<ByteBuf, RenderType> ZENITH_RENDER_TYPE =
            PortByteBufCodecs.STRING_UTF8.map(RenderType::valueOf, Enum::name);

    public Zenith() {
        super(SummonerAttachmentEntityTypes.ZENITH);
        setMaxTickCount(30);
        setPhysics(false);
        Random random = new Random();
        this.renderType = RenderType.values()[random.nextInt(RenderType.values().length)];
        this.alpha = random.nextFloat(0.105f, 1);
        if (alpha > 0.8) {
            alpha = 1;
        }
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LibStreamCodecUtils.VEC_3, () -> initialPosition, value -> initialPosition = value);
        fields.field(ZENITH_RENDER_TYPE, () -> renderType, value -> renderType = value);
    }

    @Override
    public void tick() {
        super.tick();
        ArrayList<PathNode> pathNodes = getHistoryNodes();
        historyNodes.clear();
        historyNodes.addAll(pathNodes);
        RandomSource random = getRandom();
        if (tickCount > 2 && tickCount < 9 && random.nextFloat() < 0.5f) {
            PathNode last = historyNodes.get(0);
            int count = random.nextIntBetweenInclusive(1, 5);
            for (int i = 0; i < count; i++) {
                Vec3 pos = getCurrentPathNode().pos();
                Vec3 direction = pos.subtract(last.pos());
                pos = pos.add(direction.scale(random.nextFloat() * 2)).offsetRandom(random, 0.5f);
                // 天顶剑粒子：颜色跟随剑型，大小/速度/阻力按范围随机，寿命固定 10 tick 自然消退
                float scale = Mth.nextFloat(random, 0.02F, 0.04F);
                float speed = Mth.nextFloat(random, 0.6F, 1.2F);
                float friction = Mth.nextFloat(random, 0.35F, 0.7F);
                int life = random.nextIntBetweenInclusive(5, 40);
                ParticleHelper.create(owner.level())
                        .type(new ZenithParticleOptions(renderType.getColor(), life, friction, scale))
                        .pos(pos.add(owner.getPosition(1.0F).subtract(initialPosition)))
                        .velocity(direction.normalize().scale(speed))
                        .count(0)
                        .emit();
            }
        }
    }

    @Override
    public boolean isAlive() {
        return isExecutingPath();
    }

    @Override
    public PathNode getRenderNode(float partialTick) {
        PathNode renderNode = super.getRenderNode(partialTick);
        Vec3 currentPos = owner.getPosition(partialTick);
        return renderNode.modifyPos(renderNode.pos().add(currentPos.subtract(initialPosition)));
    }

    @Override
    public ArrayList<PathNode> getHistoryNodes() {
        ArrayList<PathNode> pathNodes = super.getHistoryNodes();
        if (tickCount > 0) {
            ArrayList<PathNode> list = new ArrayList<>();
            float partialTick = getPartialTick();
            Vec3 currentPos = owner.getPosition(partialTick);
            for (PathNode pathNode : pathNodes) {
                list.add(pathNode.modifyPos(pathNode.pos().add(currentPos.subtract(initialPosition))));
            }
            return list;
        }
        return pathNodes;
    }

    /**
     * 取当前部分刻（partialTick），等价于源实现的 {@code RenderUtil.getPartialTick()}：
     * 客户端取渲染插值，其余情况返回 1（即实体当前位置）。
     * <p>
     * 1.20.1 的 common 侧没有 partialTick 来源（{@code client/summoner/RenderUtil} 不能引入 common），
     * 因此保留源实现的 dist 判断：只在实际客户端环境下调用客户端单例
     * {@link PortDeltaTicker}（其内部即 {@code Minecraft.getInstance().timer}，与源实现
     * {@code Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true)} 一致）。
     * 客户端类引用放在嵌套类里，专用服务端不会加载它。
     * </p>
     */
    private static float getPartialTick() {
        return FMLEnvironment.dist.isClient() ? ClientPartialTick.get() : 1.0F;
    }

    /** 客户端专用持有者：仅在 dist 为客户端时被加载。 */
    private static final class ClientPartialTick {
        private static float get() {
            return PortDeltaTicker.INSTANCE.getGameTimeDeltaPartialTick(true);
        }
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-1, -0.5, -1.5, 1, 0.5, 2);
    }

    @Override
    public boolean isValidCollisionTarget(Zenith entity, LivingEntity target) {
        return owner != target;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            attack(hit.entity(), getDamage(), 1);
        }
    }

    /**
     * 天顶剑轨迹所使用的剑类型，颜色为各自轨迹的渲染颜色。
     */
    public enum RenderType {
        // 铜短剑 - 浅橙色 (#EBA687)
        COPPER_SHORT_SWORD("copper_short_sword", 0xEBA687),
        // 魔光剑 - 紫色 (#7A42BF)
        LIGHTS_BANE("lights_bane", 0x7A42BF),
        // 村正 - 海军蓝 (#384ED2)
        MURAMASA("muramasa", 0x384ED2),
        // 泰拉魔刃 - 亮薄荷绿 (#B2FFB4)
        TERRA_BLADE("terra_blade", 0xB2FFB4),
        // 血腥屠刀 - 红色 (#ED1C24)
        BLOOD_BUTCHERER("blood_butcherer", 0xED1C24),
        // 星怒 - 粉色 (#EC3EC0)
        STARFURY("starfury", 0xEC3EC0),
        // 附魔剑 - 浅蓝色 (#5B9EE8)
        ENCHANTED_SWORD("enchanted_sword", 0x5B9EE8),
        // 养蜂人 - 黄色 (#FFE745)
        BEE_KEEPER("bee_keeper", 0xFFE745),
        // 草剑 - 绿色 (#6BCB00)
        BLADE_OF_GRASS("blade_of_grass", 0x6BCB00),
        // 火山 - 橙色 (#FE9E23)
        FIERY_GREATSWORD("fiery_greatsword", 0xFE9E23),
        // 永夜刃 - 紫色 (#B336C9)
        NIGHTS_EDGE("nights_edge", 0xB336C9),
        // 真永夜刃 - 紫色 (#B336C9)
        TRUE_NIGHTS_EDGE("true_nights_edge", 0xB336C9),
        // 断钢剑 - 黄色 (#ECC813)
        EXCALIBUR("excalibur", 0xECC813),
        // 真断钢剑 - 黄色 (#ECC813)
        TRUE_EXCALIBUR("true_excalibur", 0xECC813),
        // 无头骑士剑 - 橙色 (#FC5F04)
        THE_HORSEMANS_BLADE("the_horsemans_blade", 0xFC5F04),
        // 种子弯刀 - 绿色 (#8FD71D)
        SEEDLER("seedler", 0x8FD71D),
        // 泰拉刃 - 浅绿色 (#50DE7A)
        TRUE_TERRA_BLADE("true_terra_blade", 0x50DE7A),
        // 波涌之刃 - 青色 (#54EAF5)
        INFLUX_WAVER("influx_waver", 0x54EAF5),
        // 狂星之怒 - 粉色 (#ED3F85)
        STAR_WRATH("star_wrath", 0xED3F85),
        // 彩虹猫之刃 - 浅粉色 (#FEC2FA)
        MEOWMERE("meowmere", 0xFEC2FA),
        // 天顶剑 - 亮薄荷绿 (#B2FFB4)
        ZENITH("zenith", 0xB2FFB4);

        private final ResourceLocation texture;
        private final int color;

        /** 贴图/模型目录名，同时作为 JSON 模型注册用的模型 id 路径段。 */
        private final String textureName;

        RenderType(String textureName, int color) {
            // 与 assets/confluence/lyra_model/json/projectile/zenith/<name>/<name>.png 一一对应
            this.textureName = textureName;
            this.texture = Confluence.asResource("lyra_model/json/projectile/zenith/" + textureName + "/" + textureName);
            this.color = color;
        }

        public ResourceLocation getTexture() {
            return texture;
        }

        public String textureName() {
            return textureName;
        }

        public int getColor() {
            return color;
        }

        public int getColorARBG(float alpha) {
            // 1.20.1 的 FastColor.ARGB32 没有 (alpha, rgb) 重载，按位拆解后调用四参版本，结果与源实现一致
            return FastColor.ARGB32.color((int) (alpha * 255), FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
        }
    }
}
