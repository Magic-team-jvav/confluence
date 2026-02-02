package org.confluence.terra_furniture.common.datagen.empowered;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terra_furniture.TerraFurniture;

import java.util.HashSet;

public interface BlockDataGenerator<T extends Block> {
    String TEMPLATE_FOLDER = "templates";
    ExistingFileHelper.ResourceType TEXTURE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");
    ExistingFileHelper.ResourceType MODEL = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", "models");

    /**
     * 生成方块模型与方块状态表
     */
    void buildBlockWithTemplate(T block, BlockStateProvider builderProvider, ExistingFileHelper helper);

    /**
     * 生成物品模型
     */
    void buildItemWithTemplate(T block, ItemModelProvider provider, ExistingFileHelper helper);

    /**
     * 对应方块的模板类型
     */
    String getTemplateType(T block);

    /**
     * 生成方块标签，在现有基础上添加标签请用{@link #addBlockTags(Block, BlockTagsProvider, HashSet)}
     */
    default HashSet<TagKey<Block>> getRegBlockTags(T block, BlockTagsProvider provider) {
        HashSet<TagKey<Block>> keys = new HashSet<>();
        addBlockTags(block, provider, keys);
        return keys;
    }

    /**
     * 添加方块标签
     */
    void addBlockTags(T block, BlockTagsProvider provider, HashSet<TagKey<Block>> keys);

    /**
     * 生成物品标签，在现有基础上添加标签请用{@link #addItemTags(Block, ItemTagsProvider, HashSet)}
     */
    default HashSet<TagKey<Item>> getRegItemTags(T block, ItemTagsProvider provider) {
        HashSet<TagKey<Item>> keys = new HashSet<>();
        addItemTags(block, provider, keys);
        return keys;
    }

    /**
     * 添加物品标签
     */
    void addItemTags(T block, ItemTagsProvider provider, HashSet<TagKey<Item>> keys);

    /**
     * 获取模板文件位置
     */
    default String getTemplateLoc(T block) {
        return TEMPLATE_FOLDER + "/" + getTemplateType(block);
    }

    /**
     * 添加前缀
     */
    default String prefix(String loc, AccessType type) {
        return type.prefix(loc);
    }

    /**
     * 转换为泰拉家具资源路径
     */
    default ResourceLocation toResourceLocation(String loc) {
        return TerraFurniture.asResource(loc);
    }

    /**
     * 获取方块id名
     */
    default String getBlockPath(T block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    /**
     * 警告贴图资源
     */
    default void warnTexture(String targetLoc, String blockId) {
        TerraFurniture.LOGGER.warn("Can't get {} for {} ! Using default texture!", targetLoc, blockId);
    }

    /**
     * 对未完成项目的报错
     */
    default void reportModel(String targetLoc, String blockId, String typeId) {
        TerraFurniture.LOGGER.error(
                "Missing model [{}] for block [{}] with type [{}]. WIP",
                targetLoc, blockId, typeId
        );
    }

    /**
     * 对未完成项目的报错
     */
    default void reportTexture(String targetId, Exception e) {
        TerraFurniture.LOGGER.error("Can't find target {} with default texture for usage!", targetId, e);
    }

    /**
     * 前缀类型
     */
    enum AccessType {
        BLOCK("block"),
        ITEM("item");

        private final String type;
        AccessType(String type) {
            this.type = type;
        }

        public String prefix(String loc) {
            return this.type + "/" + loc;
        }

    }
}
