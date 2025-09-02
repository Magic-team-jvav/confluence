package org.confluence.mod.common.init;

import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.BaitItems;
import org.confluence.mod.common.init.item.FoodItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.recipe.*;
import org.confluence.mod.common.recipe.special.BoomBunnyRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Confluence.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Confluence.MODID);

    public static final Supplier<RecipeType<AltarRecipe>> ALTAR_TYPE = registerType("altar");
    public static final Supplier<RecipeSerializer<?>> ALTAR_SERIALIZER = SERIALIZERS.register("altar", AltarRecipe.Serializer::new);
    public static final Supplier<RecipeType<SkyMillRecipe>> SKY_MILL_TYPE = registerType("sky_mill");
    public static final Supplier<RecipeSerializer<?>> SKY_MILL_SERIALIZER = SERIALIZERS.register("sky_mill", SkyMillRecipe.Serializer::new);
    public static final Supplier<RecipeType<HeavyWorkBenchRecipe>> HEAVY_WORK_BENCH_TYPE = registerType("heavy_work_bench");
    public static final Supplier<RecipeSerializer<?>> HEAVY_WORK_BENCH_SERIALIZER = SERIALIZERS.register("heavy_work_bench", HeavyWorkBenchRecipe.Serializer::new);
    public static final Supplier<RecipeType<HellforgeRecipe>> HELLFORGE_TYPE = registerType("hellforge");
    public static final Supplier<RecipeSerializer<?>> HELLFORGE_SERIALIZER = SERIALIZERS.register("hellforge", HellforgeRecipe.Serializer::new);
    public static final Supplier<RecipeType<FletchingTableRecipe>> FLETCHING_TABLE_TYPE = registerType("fletching_table");
    public static final Supplier<RecipeSerializer<?>> FLETCHING_TABLE_SERIALIZER = SERIALIZERS.register("fletching_table", FletchingTableRecipe.Serializer::new);
    public static final Supplier<RecipeType<AlchemyTableRecipe>> ALCHEMY_TABLE_TYPE = registerType("alchemy_table");
    public static final Supplier<RecipeSerializer<?>> ALCHEMY_TABLE_SERIALIZER = SERIALIZERS.register("alchemy_table", AlchemyTableRecipe.Serializer::new);
    public static final Supplier<RecipeType<CookingPotRecipe>> COOKING_POT_TYPE = registerType("cooking_pot");
    public static final Supplier<RecipeSerializer<?>> COOKING_POT_SERIALIZER = SERIALIZERS.register("cooking_pot", CookingPotRecipe.Serializer::new);
    public static final Supplier<RecipeType<SawmillRecipe>> SAWMILL_TYPE = registerType("sawmill");
    public static final Supplier<RecipeSerializer<?>> SAWMILL_SERIALIZER = SERIALIZERS.register("sawmill", SawmillRecipe.Serializer::new);
    public static final Supplier<RecipeType<SolidifierRecipe>> SOLIDIFIER_TYPE = registerType("solidifier");
    public static final Supplier<RecipeSerializer<?>> SOLIDIFIER_SERIALIZER = SERIALIZERS.register("solidifier", SolidifierRecipe.Serializer::new);
    public static final Supplier<RecipeType<CrystalBallRecipe>> CRYSTAL_BALL_TYPE = registerType("crystal_ball");
    public static final Supplier<RecipeSerializer<?>> CRYSTAL_BALL_SERIALIZER = SERIALIZERS.register("crystal_ball", CrystalBallRecipe.Serializer::new);
    public static final Supplier<RecipeType<HardmodeAnvilRecipe>> HARDMODE_ANVIL_TYPE = registerType("hardmode_anvil");
    public static final Supplier<RecipeSerializer<?>> HARDMODE_ANVIL_SERIALIZER = SERIALIZERS.register("hardmode_anvil", HardmodeAnvilRecipe.Serializer::new);
    public static final Supplier<RecipeType<ItemTransmutationRecipe>> ITEM_TRANSMUTATION_TYPE = registerType("item_transmutation");
    public static final Supplier<RecipeSerializer<?>> ITEM_TRANSMUTATION_SERIALIZER = SERIALIZERS.register("item_transmutation", ItemTransmutationRecipe.Serializer::new);
    public static final Supplier<RecipeType<HardmodeForgeRecipe>> HARDMODE_FORGE_TYPE = registerType("hardmode_forge");
    public static final Supplier<RecipeSerializer<?>> HARDMODE_FORGE_SERIALIZER = SERIALIZERS.register("hardmode_forge", HardmodeForgeRecipe.Serializer::new);
    public static final Supplier<RecipeType<LoomRecipe>> LOOM_TYPE = registerType("loom");
    public static final Supplier<RecipeSerializer<?>> LOOM_SERIALIZER = SERIALIZERS.register("loom", LoomRecipe.Serializer::new);
    public static final Supplier<RecipeType<DyeVatRecipe>> DYE_VAT_TYPE = registerType("dye_vat");
    public static final Supplier<RecipeSerializer<?>> DYE_VAT_SERIALIZER = SERIALIZERS.register("dye_vat", DyeVatRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, BoomBunnyRecipe.Serializer> BOOM_BUNNY_SERIALIZER = SERIALIZERS.register("boom_bunny", BoomBunnyRecipe.Serializer::new);

    private static <R extends Recipe<?>> Supplier<RecipeType<R>> registerType(String id) {
        return TYPES.register(id + "_type", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return "confluence:" + id;
            }
        });
    }

    public static void register(IEventBus bus) {
        ShapedRecipePattern.setCraftingSize(4, 4);
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }

    public static final class Brewing {
        private static final Object2IntMap<Item> MATERIAL_ID_MAP = new Object2IntOpenHashMap<>();
        private static final Object2ObjectMap<int[], ItemStack> MATERIAL_TO_RESULT = new Object2ObjectOpenHashMap<>();

        public static void initialize() {
            if (!CommonConfigs.BREWING_STAND_RECIPE.get()) return;
            registerMaterial(MaterialItems.WATERLEAF.get());
            registerMaterial(MaterialItems.FIREBLOSSOM.get());
            registerMaterial(MaterialItems.MOONGLOW.get());
            registerMaterial(MaterialItems.BLINKROOT.get());
            registerMaterial(MaterialItems.SHIVERTHORN.get());
            registerMaterial(MaterialItems.DAYBLOOM.get());
            registerMaterial(MaterialItems.DEATHWEED.get());
            registerMaterial(MaterialItems.LENS.get());
            registerMaterial(Items.COBWEB);
            registerMaterial(FoodItems.ARMORED_CAVE_FISH.get());
            registerMaterial(Items.FEATHER);
            registerMaterial(DecorativeBlocks.CRISPY_HONEY_BLOCK.get().asItem());
            registerMaterial(Items.FIRE_CORAL);
            registerMaterial(BaitItems.LADYBUG.get());
            registerMaterial(FoodItems.FLASHFIN_KOI.get());
            registerMaterial(FoodItems.OBSIDIAN_FISH.get());
            registerMaterial(FoodItems.COLORFUL_MINERAL_FISH.get());
            registerMaterial(FoodItems.SCARLET_TIGER_FISH.get());
            registerMaterial(MaterialItems.ANTLION_MANDIBLE.get());
            registerMaterial(MaterialItems.PINK_PEARL.get());
            registerMaterial(MaterialItems.BLACK_PEARL.get());
            registerMaterial(MaterialItems.PEARL.get());
            registerMaterial(MaterialItems.SHARK_FIN.get());
            registerMaterial(MaterialItems.RAW_LEAD.get());
            registerMaterial(Items.OBSIDIAN);
            registerMaterial(Items.RAW_IRON);
            registerMaterial(Items.RAW_GOLD);
            registerMaterial(Items.BONE);
            registerMaterial(Items.CACTUS);
            registerMaterial(FoodItems.PRINCESS_FISH.get());
            registerMaterial(FoodItems.BLOODY_PIRANHAS.get());
            registerMaterial(MaterialItems.RAW_PLATINUM.get());
            registerMaterial(MaterialItems.LIFE_MUSHROOM.get());
            registerMaterial(FoodItems.CHAOS_FISH.get());
            registerMaterial(FoodItems.MIRROR_FISH.get());
            registerMaterial(FoodItems.EBONY_KOI.get());
            registerMaterial(NatureBlocks.GLOWING_MUSHROOM.get().asItem());
            registerMaterial(FoodItems.STINKY_FISH.get());
            registerMaterial(MaterialItems.AMBER.get());
            registerMaterial(FoodItems.MOTTLED_OILFISH.get());


            // 箭术
            registerMix(new Item[]{
                    MaterialItems.DAYBLOOM.get(),
                    MaterialItems.LENS.get()
            }, PotionItems.ARCHERY_POTION.toStack());
            // 建造者
            registerMix(new Item[]{
                    MaterialItems.BLINKROOT.get(),
                    MaterialItems.SHIVERTHORN.get(),
                    MaterialItems.MOONGLOW.get()
            }, PotionItems.BUILDER_POTION.toStack());
            // 危险感
            registerMix(new Item[]{
                    MaterialItems.SHIVERTHORN.get(),
                    Items.COBWEB
            }, PotionItems.DANGERSENSE_POTION.toStack());
            // 耐力
            registerMix(new Item[]{
                    FoodItems.ARMORED_CAVE_FISH.get(),
                    MaterialItems.BLINKROOT.get()
            }, PotionItems.ENDURANCE_POTION.toStack());
            // 羽落
            registerMix(new Item[]{
                    MaterialItems.DAYBLOOM.get(),
                    MaterialItems.BLINKROOT.get(),
                    Items.FEATHER
            }, PotionItems.FEATHERFALL_POTION.toStack());
            // 钓鱼
            registerMix(new Item[]{
                    DecorativeBlocks.CRISPY_HONEY_BLOCK.get().asItem(),
                    MaterialItems.WATERLEAF.get(),
            }, PotionItems.FISHING_POTION.toStack());
            // 脚蹼
            registerMix(new Item[]{
                    MaterialItems.SHIVERTHORN.get(),
                    MaterialItems.WATERLEAF.get(),
            }, PotionItems.FLIPPER_POTION.toStack());
            // 鱼腮
            registerMix(new Item[]{
                    MaterialItems.WATERLEAF.get(),
                    Items.FIRE_CORAL
            }, PotionItems.GILLS_POTION.toStack());
            // 重力
            registerMix(new Item[]{
                    MaterialItems.FIREBLOSSOM.get(),
                    MaterialItems.DEATHWEED.get(),
                    MaterialItems.BLINKROOT.get(),
                    Items.FEATHER
            }, PotionItems.GRAVITATION_POTION.toStack());
            // 强效幸运
            registerMix(new Item[]{
                    MaterialItems.WATERLEAF.get(),
                    BaitItems.LADYBUG.get(),
                    MaterialItems.PINK_PEARL.get()
            }, PotionItems.GREATER_LUCK_POTION.toStack());
            // 幸运
            registerMix(new Item[]{
                    MaterialItems.WATERLEAF.get(),
                    BaitItems.LADYBUG.get(),
                    MaterialItems.BLACK_PEARL.get()
            }, PotionItems.LUCK_POTION.toStack());
            // 弱幸运
            registerMix(new Item[]{
                    MaterialItems.WATERLEAF.get(),
                    BaitItems.LADYBUG.get(),
                    MaterialItems.PEARL.get()
            }, PotionItems.LESSER_LUCK_POTION.toStack());
            // 拾心
            registerMix(new Item[]{
                    FoodItems.SCARLET_TIGER_FISH.get(),
                    MaterialItems.DAYBLOOM.get()
            }, PotionItems.HEART_REACH_POTION.toStack());
            // 狩猎
            registerMix(new Item[]{
                    MaterialItems.DAYBLOOM.get(),
                    MaterialItems.BLINKROOT.get(),
                    MaterialItems.SHARK_FIN.get()
            }, PotionItems.HUNTER_POTION.toStack());
            // 狱火
            registerMix(new Item[]{
                    FoodItems.FLASHFIN_KOI.get(),
                    FoodItems.OBSIDIAN_FISH.get(),
                    FoodItems.OBSIDIAN_FISH.get(),
                    MaterialItems.FIREBLOSSOM.get(),
            }, PotionItems.INFERNO_POTION.toStack());
            // 铁皮
            registerMix(new Item[]{
                    MaterialItems.DAYBLOOM.get(),
                    MaterialItems.RAW_LEAD.get()
            }, PotionItems.IRON_SKIN_POTION.toStack());
            registerMix(new Item[]{
                    MaterialItems.DAYBLOOM.get(),
                    Items.RAW_IRON
            }, PotionItems.IRON_SKIN_POTION.toStack());
            // 生命力
            registerMix(new Item[]{
                    FoodItems.COLORFUL_MINERAL_FISH.get(),
                    MaterialItems.MOONGLOW.get(),
                    MaterialItems.SHIVERTHORN.get(),
                    MaterialItems.WATERLEAF.get()
            }, PotionItems.LIFEFORCE_POTION.toStack());
            // 爱情
            registerMix(new Item[]{
                    FoodItems.PRINCESS_FISH.get(),
                    MaterialItems.SHIVERTHORN.get(),
            }, PotionItems.LOVE_POTION.toStack());
            // 魔能
            registerMix(new Item[]{
                    MaterialItems.MOONGLOW.get(),
                    MaterialItems.DEATHWEED.get(),
                    MaterialItems.FALLING_STAR.get()
            }, PotionItems.MAGIC_POWER_POTION.toStack());
            // 魔力回复
            registerMix(new Item[]{
                    MaterialItems.MOONGLOW.get(),
                    MaterialItems.DAYBLOOM.get(),
                    MaterialItems.FALLING_STAR.get()
            }, PotionItems.MANA_REGENERATION_POTION.toStack());
            // 挖矿
            registerMix(new Item[]{
                    MaterialItems.ANTLION_MANDIBLE.get(),
                    MaterialItems.BLINKROOT.get()
            }, PotionItems.MINING_POTION.toStack());
            // 夜猫子
            registerMix(new Item[]{
                    MaterialItems.DAYBLOOM.get(),
                    MaterialItems.BLINKROOT.get()
            }, PotionItems.NIGHT_OWL_POTION.toStack());
            // 黑曜石皮
            registerMix(new Item[]{
                    MaterialItems.FIREBLOSSOM.get(),
                    MaterialItems.WATERLEAF.get(),
                    Items.OBSIDIAN
            }, PotionItems.OBSIDIAN_SKIN_POTION.toStack());
            // 暴怒
            registerMix(new Item[]{
                    FoodItems.BLOODY_PIRANHAS.get(),
                    MaterialItems.DEATHWEED.get()
            }, PotionItems.RAGE_POTION.toStack());
            // 再生
            registerMix(new Item[]{
                    MaterialItems.DAYBLOOM.get(),
                    MaterialItems.LIFE_MUSHROOM.get()
            }, PotionItems.REGENERATION_POTION.toStack());
            // 光环
            registerMix(new Item[]{
                    MaterialItems.DAYBLOOM.get(),
                    NatureBlocks.GLOWING_MUSHROOM.get().asItem()
            }, PotionItems.SHINE_POTION.toStack());
            // 洞探
            registerMix(new Item[]{
                    MaterialItems.BLINKROOT.get(),
                    MaterialItems.MOONGLOW.get(),
                    MaterialItems.RAW_PLATINUM.get()
            }, PotionItems.SPELUNKER_POTION.toStack());
            registerMix(new Item[]{
                    MaterialItems.BLINKROOT.get(),
                    MaterialItems.MOONGLOW.get(),
                    Items.RAW_GOLD
            }, PotionItems.SPELUNKER_POTION.toStack());
            // 敏捷
            registerMix(new Item[]{
                    MaterialItems.BLINKROOT.get(),
                    Items.CACTUS
            }, PotionItems.SWIFTNESS_POTION.toStack());
            // 荆棘
            registerMix(new Item[]{
                    MaterialItems.DEATHWEED.get(),
                    Items.CACTUS
            }, PotionItems.THORNS_POTION.toStack());
            // 泰坦
            registerMix(new Item[]{
                    MaterialItems.DUNGEON_DEMON_BONE.get(),
                    MaterialItems.DEATHWEED.get(),
                    MaterialItems.SHIVERTHORN.get()
            }, PotionItems.TITAN_POTION.toStack());
            // 水上漂
            registerMix(new Item[]{
                    MaterialItems.WATERLEAF.get(),
                    MaterialItems.SHARK_FIN.get()
            }, PotionItems.WATER_WALKING_POTION.toStack());
            // 怒气
            registerMix(new Item[]{
                    FoodItems.EBONY_KOI.get(),
                    MaterialItems.DEATHWEED.get()
            }, PotionItems.WRATH_POTION.toStack());
            // 回忆
            registerMix(new Item[]{
                    FoodItems.MIRROR_FISH.get(),
                    MaterialItems.DAYBLOOM.get()
            }, PotionItems.RECALL_POTION.toStack());
            // 虫洞
            registerMix(new Item[]{
                    FoodItems.MIRROR_FISH.get(),
                    MaterialItems.BLINKROOT.get()
            }, PotionItems.WORMHOLE_POTION.toStack());
            // 隐身
            registerMix(new Item[]{
                    MaterialItems.BLINKROOT.get(),
                    MaterialItems.MOONGLOW.get()
            }, PotionItems.INVISIBILITY_POTION.toStack());
            // 传送
            registerMix(new Item[]{
                    FoodItems.CHAOS_FISH.get(),
                    MaterialItems.FIREBLOSSOM.get()
            }, PotionItems.RANDOM_TELEPORT_POTION.toStack());
            // 臭味
            registerMix(new Item[]{
                    FoodItems.STINKY_FISH.get(),
                    MaterialItems.DEATHWEED.get()
            }, PotionItems.STINK_POTION.toStack());
            // 宝匣
            registerMix(new Item[]{
                    MaterialItems.AMBER.get(),
                    MaterialItems.MOONGLOW.get(),
                    MaterialItems.SHIVERTHORN.get(),
                    MaterialItems.WATERLEAF.get()
            }, PotionItems.CRATE_POTION.toStack());
            // 弹药储备
            registerMix(new Item[]{
                    FoodItems.PISCES_FIN_COD.get(),
                    MaterialItems.MOONGLOW.get()
            }, PotionItems.AMMO_RESERVATION_POTION.toStack());
            // 召唤
            registerMix(new Item[]{
                    FoodItems.MOTTLED_OILFISH.get(),
                    MaterialItems.MOONGLOW.get()
            }, PotionItems.SUMMONING_POTION.toStack());
        }

        private static void registerMaterial(Item material) {
            if (MATERIAL_ID_MAP.containsKey(material)) return;
            MATERIAL_ID_MAP.put(material, MATERIAL_ID_MAP.size());
        }

        private static void registerMix(Item[] materials, ItemStack result) {
            int[] ids = Arrays.stream(materials).mapToInt(material -> MATERIAL_ID_MAP.getOrDefault(material, -1)).filter(id -> id != -1).toArray();
            Arrays.sort(ids);
            MATERIAL_TO_RESULT.put(ids, result);
        }

        public static void registerRecipes(Consumer<IBrewingRecipe> consumer) {
            consumer.accept(new IBrewingRecipe() {
                @Override
                public boolean isInput(@NotNull ItemStack input) {
                    return input.is(PotionItems.CHAOS_POTION) || input.is(PotionItems.BOTTLED_WATER);
                }

                @Override
                public boolean isIngredient(@NotNull ItemStack ingredient) {
                    return MATERIAL_ID_MAP.containsKey(ingredient.getItem());
                }

                @Override
                public @NotNull ItemStack getOutput(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
                    if (!isIngredient(ingredient) || !isInput(input)) return ItemStack.EMPTY;
                    int addition = MATERIAL_ID_MAP.getOrDefault(ingredient.getItem(), -1);
                    if (addition == -1) return ItemStack.EMPTY;
                    int[] materials = append(getMaterials(input), addition);
                    if (Arrays.stream(materials).anyMatch(i -> i < 0)) return ItemStack.EMPTY;
                    int length = materials.length;
                    boolean isorted = true;
                    outer:
                    for (Map.Entry<int[], ItemStack> entry : MATERIAL_TO_RESULT.entrySet()) {
                        int[] ids = entry.getKey();
                        if (ids.length != length) continue;
                        if (isorted) {
                            Arrays.sort(materials);
                            isorted = false;
                        }
                        for (int i = 0; i < ids.length; i++) {
                            if (ids[i] != materials[i]) continue outer;
                        }
                        return entry.getValue().copy();
                    }
                    ItemStack stack = PotionItems.CHAOS_POTION.toStack();
                    setMaterials(stack, materials);
                    return stack;
                }
            });
            int gel = -1;
            int life_mushroom = -2;
            int glowing_mushroom = -3;
            int lesser_healing_potion = -4;
            int lesser_mana_potion = -5;
            consumer.accept(new IBrewingRecipe() {
                @Override
                public boolean isInput(@NotNull ItemStack input) {
                    return input.is(PotionItems.BOTTLE);
                }

                @Override
                public boolean isIngredient(@NotNull ItemStack ingredient) {
                    return ingredient.is(MaterialItems.GEL) || ingredient.is(MaterialItems.LIFE_MUSHROOM);
                }

                @Override
                public @NotNull ItemStack getOutput(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
                    if (!isIngredient(ingredient) || !isInput(input)) return ItemStack.EMPTY;
                    int[] materials = getMaterials(input);
                    if (materials.length >= 2) return ItemStack.EMPTY;
                    int material;
                    if (ingredient.is(MaterialItems.GEL)) {
                        material = gel;
                    } else if (ingredient.is(MaterialItems.LIFE_MUSHROOM)) {
                        material = life_mushroom;
                    } else {
                        return ItemStack.EMPTY;
                    }
                    if (materials.length == 0) {
                        ItemStack stack = PotionItems.CHAOS_POTION.toStack();
                        setMaterials(stack, new int[]{material});
                        return stack;
                    } else {
                        int first = materials[0];
                        if ((first == gel && material == life_mushroom) || (first == life_mushroom && material == gel)) {
                            return PotionItems.LESSER_HEALING_POTION.toStack();
                        }
                        ItemStack stack = PotionItems.CHAOS_POTION.toStack();
                        setMaterials(stack, append(materials, material));
                        return stack;
                    }
                }
            });
            consumer.accept(new IBrewingRecipe() {
                @Override
                public boolean isInput(@NotNull ItemStack input) {
                    return input.is(PotionItems.LESSER_HEALING_POTION);
                }

                @Override
                public boolean isIngredient(@NotNull ItemStack ingredient) {
                    return ingredient.is(PotionItems.LESSER_HEALING_POTION) || ingredient.is(MaterialItems.GLOWING_MUSHROOM);
                }

                @Override
                public @NotNull ItemStack getOutput(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
                    if (!isIngredient(ingredient) || !isInput(input)) return ItemStack.EMPTY;
                    int[] materials = getMaterials(input);
                    if (materials.length >= 3) return ItemStack.EMPTY;
                    int material;
                    if (ingredient.is(PotionItems.LESSER_HEALING_POTION)) {
                        material = lesser_healing_potion;
                    } else if (ingredient.is(MaterialItems.GLOWING_MUSHROOM)) {
                        material = glowing_mushroom;
                    } else {
                        return ItemStack.EMPTY;
                    }
                    if (materials.length == 0) {
                        ItemStack stack = PotionItems.CHAOS_POTION.toStack();
                        setMaterials(stack, new int[]{material});
                        return stack;
                    } else {
                        materials = append(materials, material);
                        boolean l = false;
                        boolean g = false;
                        for (int m : materials) {
                            if (m == lesser_healing_potion) l = true;
                            else if (m == glowing_mushroom) g = true;
                        }
                        if (l && g) return PotionItems.HEALING_POTION.toStack();
                        ItemStack stack = PotionItems.CHAOS_POTION.toStack();
                        setMaterials(stack, materials);
                        return stack;
                    }
                }
            });
            consumer.accept(new IBrewingRecipe() {
                @Override
                public boolean isInput(@NotNull ItemStack input) {
                    return input.is(PotionItems.LESSER_MANA_POTION);
                }

                @Override
                public boolean isIngredient(@NotNull ItemStack ingredient) {
                    return ingredient.is(PotionItems.LESSER_MANA_POTION) || ingredient.is(MaterialItems.GLOWING_MUSHROOM);
                }

                @Override
                public @NotNull ItemStack getOutput(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
                    if (!isIngredient(ingredient) || !isInput(input)) return ItemStack.EMPTY;
                    int[] materials = getMaterials(input);
                    if (materials.length >= 3) return ItemStack.EMPTY;
                    int material;
                    if (ingredient.is(PotionItems.LESSER_MANA_POTION)) {
                        material = lesser_mana_potion;
                    } else if (ingredient.is(MaterialItems.GLOWING_MUSHROOM)) {
                        material = glowing_mushroom;
                    } else {
                        return ItemStack.EMPTY;
                    }
                    if (materials.length == 0) {
                        ItemStack stack = PotionItems.CHAOS_POTION.toStack();
                        setMaterials(stack, new int[]{material});
                        return stack;
                    } else {
                        materials = append(materials, material);
                        boolean l = false;
                        boolean g = false;
                        for (int m : materials) {
                            if (m == lesser_healing_potion) l = true;
                            else if (m == glowing_mushroom) g = true;
                        }
                        if (l && g) return PotionItems.MANA_POTION.toStack();
                        ItemStack stack = PotionItems.CHAOS_POTION.toStack();
                        setMaterials(stack, materials);
                        return stack;
                    }
                }
            });
        }

        private static int[] append(int[] a, int addition) {
            int length = a.length;
            int[] materials = IntArrays.forceCapacity(a, length + 1, length);
            materials[length] = addition;
            return materials;
        }

        public static void addMaterials(ItemStack potion, Item... materials) {
            LibUtils.updateItemStackNbt(potion, tag -> {
                int[] ids = tag.getIntArray("confluence:potion_materials");
                for (Item material : materials) {
                    int id = MATERIAL_ID_MAP.getOrDefault(material, -1);
                    if (id != -1) ids = append(ids, id);
                }
                Arrays.sort(ids);
                tag.putIntArray("confluence:potion_materials", ids);
            });
        }

        public static void setMaterials(ItemStack potion, Item[] materials) {
            LibUtils.updateItemStackNbt(potion, tag -> {
                int[] ids = Arrays.stream(materials).mapToInt(material -> MATERIAL_ID_MAP.getOrDefault(material, -1)).filter(id -> id != -1).toArray();
                Arrays.sort(ids);
                tag.putIntArray("confluence:potion_materials", ids);
            });
        }

        public static void setMaterials(ItemStack potion, int[] materials) {
            Arrays.sort(materials);
            LibUtils.updateItemStackNbt(potion, tag -> tag.putIntArray("confluence:potion_materials", materials));
        }

        public static int[] getMaterials(ItemStack potion) {
            CompoundTag tag = LibUtils.getItemStackNbtIfPresent(potion);
            if (tag == null) return new int[0];
            return tag.getIntArray("confluence:potion_materials");
        }
    }
}
