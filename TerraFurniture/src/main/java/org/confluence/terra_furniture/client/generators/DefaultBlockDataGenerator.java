package org.confluence.terra_furniture.client.generators;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.lib.common.LibTags;
import org.confluence.terra_furniture.common.block.func.BlockSetGetter;
import org.confluence.terra_furniture.common.datagen.empowered.BlockDataGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

/**
 * 目标行为:
 * <ol>
 *     <li>准备方块模型</li>
 *     <li>
 *         <ol>
 *             <li>尝试从 <b>block/[blockTypeSet-name]/[template-name](/part_abc).json</b> 取模型</li>
 *             <li>失败, 尝试从 <b>block/templates/[template-name](/part_abc).json</b> 取模型</li>
 *             <li>失败, 发出严重提醒, 取消对当前方块的生成</li>
 *         </ol>
 *     </li>
 *     <li>准备贴图</li>
 *     <li>
 *         <ol>
 *             <li>通过hasParticle确认是否存在粒子贴图</li>
 *             <li>从 <b>block/[blockTypeSet-name]/[block-id](/pic_abc).png</b> 取图片</li>
 *             <ol>
 *                 <li>若hasParticle为true, 材质填入#0</li>
 *                 <li>若hasParticle为false, 材质填入#particle</li>
 *             </ol>
 *             <li>失败, 发出警告并尝试取 <b>block/templates/[template-name](/pic_abc).png</b></li>
 *             <ol>
 *                 <li>若hasParticle为true, 材质填入#0</li>
 *                 <li>若hasParticle为false, 材质填入#particle</li>
 *             </ol>
 *             <li>失败, 发出严重提醒, 取消对当前方块的生成</li>
 *         </ol>
 *     </li>
 *     <li>生成 <b>block/[blockTypeSet-name]/[block-id](/part_abc).json</b></li>
 *     <li>生成 <b>blockstates/[block-id].json</b></li>
 *     </br>
 *     <li>准备物品模型</li>
 *     <li>
 *         <ol>
 *             <li>尝试从 <b>item/[blockTypeSet-name]/[template-name](/part_abc).json</b> 取模型</li>
 *             <li>失败, 尝试从 <b>item/templates/[template-name](/part_abc).json</b> 取模型</li>
 *             <li>失败, 检查是否存在item/[block-id].json</li>
 *             <li>不存在, 使用方块作为父模型; 存在, 取消对当前物品的生成</li>
 *         </ol>
 *     </li>
 *     <li>当目标为定义可填入时, 准备贴图; 否则, 不进行贴图 </li>
 *     <li>
 *         <ol>
 *             <li>从 <b>item/[blockTypeSet-name]/[block-id](/pic_abc).png</b> 取图片</li>
 *             <li>失败, 发出警告并尝试取 <b>item/templates/[template-name](/pic_abc).png</b></li>
 *             <li>失败, 发出严重提醒, 停止对当前物品的生成</li>
 *         </ol>
 *     </li>
 *     <li>生成 <b>item/[block-id].json</b></li>
 * </ol>
 */
public abstract class DefaultBlockDataGenerator<T extends Block & BlockSetGetter<T>> implements BlockDataGenerator<T> {
    public boolean isBlockValid = false;
    public boolean isItemValid = false;

    /**
     * 获取默认输出位置
     */
    public String getOutputLoc(T block) {
        return block.getType().name() + "/" + getBlockPath(block);
    }

    /**
     * 方块所属材质组的名称
     */
    public String typeName(T block) {
        return block.getType().name();
    }

    /**
     * 材质组名作为前缀
     */
    public String withTypeName(T block, String loc) {
        return typeName(block) + "/" + loc;
    }

    /**
     * 构造某个模型
     */
    public  <B extends ModelBuilder<B>, P extends ModelProvider<B>> B buildModel(P provider, @NotNull String builderOutput, ResourceLocation location) {
        B mb = provider.getBuilder(builderOutput);
        return mb.parent(provider.getExistingFile(location));
    }

    /**
     * 判断并处理模型生成的主方法，具体见行间注释
     */
    public <B extends ModelBuilder<B>, P extends ModelProvider<B>> Pair<ResourceState, B> processModel(T block, P provider, ExistingFileHelper helper, @Nullable String part, AccessType type) {
        part = part != null && !part.isBlank() ? "/" + part : "";
        String blockPath = getBlockPath(block) + part;

        String output = type.equals(AccessType.ITEM) ? prefix(blockPath, type) : prefix(getOutputLoc(block), type) + part;

        // 1. 存在特制变体模型
        ResourceLocation location = toResourceLocation(prefix(withTypeName(block, getTemplateType(block) + part), type));
        if (helper.exists(location, MODEL)) {
            return Pair.of(ResourceState.EXIST, buildModel(provider, output, location));
        }

        // 2. 尝试返回默认的模板模型
        location = toResourceLocation(prefix(getTemplateLoc(block) + part, type));
        if (helper.exists(location, MODEL)) {
            return Pair.of(ResourceState.TEMPLATE, buildModel(provider, output, location));
        }

        // 3. 对物品额外进行默认检测或方块父获取
        if (type.equals(AccessType.ITEM)) {
            // 4. 已经存在于源的独立完整物品模型
            location = toResourceLocation(output + part);
            if (helper.exists(location, MODEL)) {
                return Pair.of(ResourceState.COMPLETED, null);
            }
            // 5. 是仅贴图物品
            if (block.isLayeredItemTexture()) {
                return Pair.of(ResourceState.LAYERED, buildModel(provider, output, ResourceLocation.withDefaultNamespace("item/generated")));
            }
            // 6. 存在对应的整个方块模型
            location = toResourceLocation(prefix(getOutputLoc(block) + part, AccessType.BLOCK));
            if (helper.exists(location, MODEL)) {
                return Pair.of(ResourceState.COMPLETED, buildModel(provider, output, location));
            }
        }

        // 错误: 不存在对应的模板模型或变体, 可能存在错误的纳入范围, 是否应该考虑从自动生成器中将其剔除?
        reportModel(prefix(getTemplateLoc(block) + part, type), blockPath, typeName(block));
        return Pair.of(ResourceState.NOT_EXIST, null);
    }

    /**
     * 处理包含粒子的材质贴图，贴图得使用0和particle或全部是particle
     */
    public <B extends ModelBuilder<B>> Pair<ResourceState, B> processTexture(T block, B mb, @Nullable String name, AccessType type) {
        boolean hasParticle = block.hasParticle(block) && !type.equals(AccessType.ITEM);
        if (hasParticle && ! block.isSpecialParticleTexture()) {
            try {
                mb.texture("particle", block.getType().getParticle());
            } catch (IllegalArgumentException e) {
                reportTexture(getBlockPath(block), e);
            }
        }
        return processTexture(block, mb, name, hasParticle ? "0" : "particle", type);
    }

    /**
     * 通用贴图方法，会寻找贴图
     */
    public <B extends ModelBuilder<B>> Pair<ResourceState, B> processTexture(T block, B mb, @Nullable String name, String key, AccessType type) {
        name = name != null && !name.isBlank() ? "/" + name : "";
        String blockPath = getBlockPath(block) + name;
        try {
            // 如果存在变体图片，添加该图片
            return Pair.of(ResourceState.EXIST, mb.texture(key, toResourceLocation(prefix(withTypeName(block, blockPath), type))));
        } catch (IllegalArgumentException ignore) {
            warnTexture(prefix(withTypeName(block, blockPath), type), blockPath); // 警告: 变体图片在实际环境中是必须的
            try {
                return Pair.of(ResourceState.TEMPLATE, mb.texture(key, toResourceLocation(prefix(getTemplateLoc(block) + name, type))));
            } catch (IllegalArgumentException exception) {
                reportTexture(blockPath, exception); // 错误: 不存在这个模板的WIP贴图
                return Pair.of(ResourceState.NOT_EXIST, null);
            }
        }
    }

    /**
     * 模型贴图一步到位的方法，对简单生成判断稍长但对多模型方块极为友好
     */
    public <B extends ModelBuilder<B>, P extends ModelProvider<B>> @Nullable B fullGenBlock(T block, P provider, ExistingFileHelper helper, @Nullable String part, boolean singleTexture) {
        Pair<ResourceState, B> mb = processModel(block, provider, helper, part, AccessType.BLOCK);
        if (mb.getFirst().equals(ResourceState.NOT_EXIST)) return null;
        mb = processTexture(block, mb.getSecond(), singleTexture ? null : part, AccessType.BLOCK);
        if (!mb.getFirst().equals(ResourceState.EXIST)) isBlockValid = false;
        return mb.getSecond();
    }

    @Override
    public void buildBlockWithTemplate(T block, BlockStateProvider builderProvider, ExistingFileHelper helper) {
        Pair<ResourceState, BlockModelBuilder> buildResult = processModel(block, builderProvider.models(), helper,null, AccessType.BLOCK);
        if (buildResult.getFirst().equals(ResourceState.NOT_EXIST)) return;
        isBlockValid = true;
        buildResult = processTexture(block, buildResult.getSecond(), null, AccessType.BLOCK);
        if (!buildResult.getFirst().equals(ResourceState.NOT_EXIST)) builderProvider.simpleBlock(block, buildResult.getSecond());
        if (!buildResult.getFirst().equals(ResourceState.EXIST)) isBlockValid = false;
    }

    @Override
    public void buildItemWithTemplate(T block, ItemModelProvider provider, ExistingFileHelper helper) {
        Pair<ResourceState, ItemModelBuilder> buildResult = processModel(block, provider, helper, null, AccessType.ITEM);
        if (buildResult.getFirst().equals(ResourceState.COMPLETED)) {
            isItemValid = true;
            return;
        }
        if (buildResult.getFirst().equals(ResourceState.NOT_EXIST) || buildResult.getSecond() == null) return;
        buildResult = processTexture(block, buildResult.getSecond(), null, buildResult.getFirst().equals(ResourceState.LAYERED) ? "layer0" : "particle", block.isLayeredItemTexture() ? AccessType.ITEM : AccessType.BLOCK);
        if (buildResult.getFirst().equals(ResourceState.EXIST)) isItemValid = true;
    }

    @Override
    public void addBlockTags(T block, BlockTagsProvider provider, HashSet<TagKey<Block>> keys) {
        keys.addAll(block.getType().getTagKeys().get());
    }

    @Override
    public void addItemTags(T block, ItemTagsProvider provider, HashSet<TagKey<Item>> keys) {
        keys.addAll(isBlockValid && isItemValid ? List.of() : List.of(LibTags.Items.WIP));
    }

    /**
     * 标记模型生成状态使用的枚举
     */
    public enum ResourceState {
        /**
         * 模型生成: 表示不存在该模型的任何正常生成，属于<b>错误</b><p>
         * 自动贴图: 同理
         */
        NOT_EXIST,
        /**
         * 物品模型生成可能返回的<b>特殊状态</b>，意味着输出位置已经存在该模型或有对应方块可以直接生成模型<p>
         * 生成时如果拿到这个状态就<b>不用继续处理贴图了</b>。
         */
        COMPLETED,
        /**
         * 物品模型生成可能返回的<b>特殊状态</b>，表示目标使用单一层贴图模式<p>
         * 生成时如果拿到这个状态得<b>把贴图贴给layer0</b>。
         */
        LAYERED,
        /**
         * 模型生成: 表示取到了模板文件，上贴图就行<p>
         * 自动贴图: 取到默认贴图了，是变体没贴图，属于<b>贴图不完善错误</b>
         */
        TEMPLATE,
        /**
         * 最正常的状态，一切良好
         */
        EXIST
    }
}
