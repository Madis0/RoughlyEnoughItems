/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.rei.plugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.RoughlyEnoughItemsCore;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.Entry;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.gui.RecipeViewingScreen;
import me.shedaniel.rei.gui.VillagerRecipeViewingScreen;
import me.shedaniel.rei.impl.ScreenHelper;
import me.shedaniel.rei.plugin.blasting.DefaultBlastingCategory;
import me.shedaniel.rei.plugin.blasting.DefaultBlastingDisplay;
import me.shedaniel.rei.plugin.brewing.DefaultBrewingCategory;
import me.shedaniel.rei.plugin.brewing.DefaultBrewingDisplay;
import me.shedaniel.rei.plugin.campfire.DefaultCampfireCategory;
import me.shedaniel.rei.plugin.campfire.DefaultCampfireDisplay;
import me.shedaniel.rei.plugin.composting.DefaultCompostingCategory;
import me.shedaniel.rei.plugin.composting.DefaultCompostingDisplay;
import me.shedaniel.rei.plugin.crafting.DefaultCraftingCategory;
import me.shedaniel.rei.plugin.crafting.DefaultCustomDisplay;
import me.shedaniel.rei.plugin.crafting.DefaultShapedDisplay;
import me.shedaniel.rei.plugin.crafting.DefaultShapelessDisplay;
import me.shedaniel.rei.plugin.smelting.DefaultSmeltingCategory;
import me.shedaniel.rei.plugin.smelting.DefaultSmeltingDisplay;
import me.shedaniel.rei.plugin.smoking.DefaultSmokingCategory;
import me.shedaniel.rei.plugin.smoking.DefaultSmokingDisplay;
import me.shedaniel.rei.plugin.stonecutting.DefaultStoneCuttingCategory;
import me.shedaniel.rei.plugin.stonecutting.DefaultStoneCuttingDisplay;
import me.shedaniel.rei.plugin.stripping.DefaultStrippingCategory;
import me.shedaniel.rei.plugin.stripping.DefaultStrippingDisplay;
import me.shedaniel.rei.plugin.stripping.DummyAxeItem;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.minecraft.block.ComposterBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class DefaultPlugin implements REIPluginV0 {
    
    public static final Identifier CRAFTING = new Identifier("minecraft", "plugins/crafting");
    public static final Identifier SMELTING = new Identifier("minecraft", "plugins/smelting");
    public static final Identifier SMOKING = new Identifier("minecraft", "plugins/smoking");
    public static final Identifier BLASTING = new Identifier("minecraft", "plugins/blasting");
    public static final Identifier CAMPFIRE = new Identifier("minecraft", "plugins/campfire");
    public static final Identifier STONE_CUTTING = new Identifier("minecraft", "plugins/stone_cutting");
    public static final Identifier STRIPPING = new Identifier("minecraft", "plugins/stripping");
    public static final Identifier BREWING = new Identifier("minecraft", "plugins/brewing");
    public static final Identifier PLUGIN = new Identifier("roughlyenoughitems", "default_plugin");
    public static final Identifier COMPOSTING = new Identifier("minecraft", "plugins/composting");
    private static final Identifier DISPLAY_TEXTURE = new Identifier("roughlyenoughitems", "textures/gui/display.png");
    private static final Identifier DISPLAY_TEXTURE_DARK = new Identifier("roughlyenoughitems", "textures/gui/display_dark.png");
    private static final List<DefaultBrewingDisplay> BREWING_DISPLAYS = Lists.newArrayList();
    
    public static Identifier getDisplayTexture() {
        return ScreenHelper.isDarkModeEnabled() ? DISPLAY_TEXTURE_DARK : DISPLAY_TEXTURE;
    }
    
    public static void registerBrewingDisplay(DefaultBrewingDisplay display) {
        BREWING_DISPLAYS.add(display);
    }
    
    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }
    
    @Override
    public SemanticVersion getMinimumVersion() throws VersionParsingException {
        return SemanticVersion.parse("3.0-pre");
    }
    
    @Override
    public void registerEntries(EntryRegistry entryRegistry) {
        if (!RoughlyEnoughItemsCore.getConfigManager().getConfig().isLoadingDefaultPlugin()) {
            return;
        }
        Registry.ITEM.stream().forEach(item -> {
            entryRegistry.registerItemStack(item.getStackForRender());
            try {
                entryRegistry.registerItemStack(entryRegistry.getAllStacksFromItem(item));
            } catch (Exception e) {
            }
        });
        Registry.ENCHANTMENT.forEach(enchantment -> {
            for (int i = enchantment.getMinimumLevel(); i <= enchantment.getMaximumLevel(); i++) {
                Map<Enchantment, Integer> map = new HashMap<>();
                map.put(enchantment, i);
                ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
                EnchantmentHelper.set(map, itemStack);
                entryRegistry.registerItemStack(Items.ENCHANTED_BOOK, itemStack);
            }
        });
        Registry.FLUID.forEach(fluid -> {
            if (!(fluid instanceof EmptyFluid))
                entryRegistry.registerFluid(fluid);
        });
    }
    
    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        if (!RoughlyEnoughItemsCore.getConfigManager().getConfig().isLoadingDefaultPlugin()) {
            return;
        }
        recipeHelper.registerCategory(new DefaultCraftingCategory());
        recipeHelper.registerCategory(new DefaultSmeltingCategory());
        recipeHelper.registerCategory(new DefaultSmokingCategory());
        recipeHelper.registerCategory(new DefaultBlastingCategory());
        recipeHelper.registerCategory(new DefaultCampfireCategory());
        recipeHelper.registerCategory(new DefaultStoneCuttingCategory());
        recipeHelper.registerCategory(new DefaultBrewingCategory());
        recipeHelper.registerCategory(new DefaultCompostingCategory());
        recipeHelper.registerCategory(new DefaultStrippingCategory());
    }
    
    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        if (!RoughlyEnoughItemsCore.getConfigManager().getConfig().isLoadingDefaultPlugin()) {
            return;
        }
        recipeHelper.registerRecipes(CRAFTING, ShapelessRecipe.class, DefaultShapelessDisplay::new);
        recipeHelper.registerRecipes(CRAFTING, ShapedRecipe.class, DefaultShapedDisplay::new);
        recipeHelper.registerRecipes(SMELTING, SmeltingRecipe.class, DefaultSmeltingDisplay::new);
        recipeHelper.registerRecipes(SMOKING, SmokingRecipe.class, DefaultSmokingDisplay::new);
        recipeHelper.registerRecipes(BLASTING, BlastingRecipe.class, DefaultBlastingDisplay::new);
        recipeHelper.registerRecipes(CAMPFIRE, CampfireCookingRecipe.class, DefaultCampfireDisplay::new);
        recipeHelper.registerRecipes(STONE_CUTTING, StonecuttingRecipe.class, DefaultStoneCuttingDisplay::new);
        BREWING_DISPLAYS.stream().forEachOrdered(display -> recipeHelper.registerDisplay(BREWING, display));
        List<ItemStack> arrowStack = Collections.singletonList(Items.ARROW.getStackForRender());
        RoughlyEnoughItemsCore.getEntryRegistry().getEntryList().stream().filter(stack -> stack.getEntryType() == Entry.Type.ITEM && stack.getItemStack().getItem().equals(Items.LINGERING_POTION)).forEach(entry -> {
            List<List<ItemStack>> input = new ArrayList<>();
            for (int i = 0; i < 4; i++)
                input.add(arrowStack);
            input.add(Collections.singletonList(entry.getItemStack()));
            for (int i = 0; i < 4; i++)
                input.add(arrowStack);
            ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, 8);
            PotionUtil.setPotion(outputStack, PotionUtil.getPotion(entry.getItemStack()));
            PotionUtil.setCustomPotionEffects(outputStack, PotionUtil.getCustomPotionEffects(entry.getItemStack()));
            List<ItemStack> output = Collections.singletonList(outputStack);
            recipeHelper.registerDisplay(CRAFTING, new DefaultCustomDisplay(input, output));
        });
        Map<ItemConvertible, Float> map = Maps.newLinkedHashMap();
        if (ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.isEmpty())
            ComposterBlock.registerDefaultCompostableItems();
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.keySet().forEach(itemConvertible -> {
            float chance = ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getOrDefault(itemConvertible, 0);
            if (chance > 0)
                map.put(itemConvertible, chance);
        });
        List<ItemConvertible> stacks = new LinkedList<>(map.keySet());
        stacks.sort((first, second) -> {
            return (int) ((map.get(first) - map.get(second)) * 100);
        });
        for (int i = 0; i < stacks.size(); i += MathHelper.clamp(48, 1, stacks.size() - i)) {
            List<ItemConvertible> thisStacks = Lists.newArrayList();
            for (int j = i; j < i + 48; j++)
                if (j < stacks.size())
                    thisStacks.add(stacks.get(j));
            recipeHelper.registerDisplay(COMPOSTING, new DefaultCompostingDisplay(MathHelper.floor(i / 48f), thisStacks, map, Lists.newArrayList(map.keySet()), new ItemStack[]{new ItemStack(Items.BONE_MEAL)}));
        }
        DummyAxeItem.getStrippedBlocksMap().entrySet().stream().sorted(Comparator.comparing(b -> Registry.BLOCK.getId(b.getKey()))).forEach(set -> {
            recipeHelper.registerDisplay(STRIPPING, new DefaultStrippingDisplay(new ItemStack(set.getKey()), new ItemStack(set.getValue())));
        });
    }
    
    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        if (!RoughlyEnoughItemsCore.getConfigManager().getConfig().isLoadingDefaultPlugin()) {
            return;
        }
        displayHelper.getBaseBoundsHandler().registerExclusionZones(AbstractInventoryScreen.class, new DefaultPotionEffectExclusionZones());
        displayHelper.getBaseBoundsHandler().registerExclusionZones(RecipeBookProvider.class, new DefaultRecipeBookExclusionZones());
        displayHelper.registerBoundsHandler(new DisplayHelper.DisplayBoundsHandler<AbstractContainerScreen<?>>() {
            @Override
            public Class<?> getBaseSupportedClass() {
                return AbstractContainerScreen.class;
            }
            
            @Override
            public Rectangle getLeftBounds(AbstractContainerScreen<?> screen) {
                return new Rectangle(2, 0, ScreenHelper.getLastContainerScreenHooks().rei_getContainerLeft() - 4, MinecraftClient.getInstance().window.getScaledHeight());
            }
            
            @Override
            public Rectangle getRightBounds(AbstractContainerScreen<?> screen) {
                int startX = ScreenHelper.getLastContainerScreenHooks().rei_getContainerLeft() + ScreenHelper.getLastContainerScreenHooks().rei_getContainerWidth() + 2;
                return new Rectangle(startX, 0, MinecraftClient.getInstance().window.getScaledWidth() - startX - 2, MinecraftClient.getInstance().window.getScaledHeight());
            }
            
            @Override
            public float getPriority() {
                return -1.0f;
            }
        });
        displayHelper.registerBoundsHandler(new DisplayHelper.DisplayBoundsHandler<RecipeViewingScreen>() {
            @Override
            public Class<?> getBaseSupportedClass() {
                return RecipeViewingScreen.class;
            }
            
            @Override
            public Rectangle getLeftBounds(RecipeViewingScreen screen) {
                return new Rectangle(2, 0, ((RecipeViewingScreen) screen).getBounds().x - 4, MinecraftClient.getInstance().window.getScaledHeight());
            }
            
            @Override
            public Rectangle getRightBounds(RecipeViewingScreen screen) {
                int startX = ((RecipeViewingScreen) screen).getBounds().x + ((RecipeViewingScreen) screen).getBounds().width + 2;
                return new Rectangle(startX, 0, MinecraftClient.getInstance().window.getScaledWidth() - startX - 2, MinecraftClient.getInstance().window.getScaledHeight());
            }
            
            @Override
            public float getPriority() {
                return -1.0f;
            }
        });
        displayHelper.registerBoundsHandler(new DisplayHelper.DisplayBoundsHandler<VillagerRecipeViewingScreen>() {
            @Override
            public Class<?> getBaseSupportedClass() {
                return VillagerRecipeViewingScreen.class;
            }
            
            @Override
            public Rectangle getLeftBounds(VillagerRecipeViewingScreen screen) {
                return new Rectangle(2, 0, ((VillagerRecipeViewingScreen) screen).bounds.x - 4, MinecraftClient.getInstance().window.getScaledHeight());
            }
            
            @Override
            public Rectangle getRightBounds(VillagerRecipeViewingScreen screen) {
                int startX = ((VillagerRecipeViewingScreen) screen).bounds.x + ((VillagerRecipeViewingScreen) screen).bounds.width + 2;
                return new Rectangle(startX, 0, MinecraftClient.getInstance().window.getScaledWidth() - startX - 2, MinecraftClient.getInstance().window.getScaledHeight());
            }
            
            @Override
            public float getPriority() {
                return -1.0f;
            }
        });
    }
    
    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        if (!RoughlyEnoughItemsCore.getConfigManager().getConfig().isLoadingDefaultPlugin()) {
            return;
        }
        recipeHelper.registerWorkingStations(CRAFTING, new ItemStack(Items.CRAFTING_TABLE));
        recipeHelper.registerWorkingStations(SMELTING, new ItemStack(Items.FURNACE));
        recipeHelper.registerWorkingStations(SMOKING, new ItemStack(Items.SMOKER));
        recipeHelper.registerWorkingStations(BLASTING, new ItemStack(Items.BLAST_FURNACE));
        recipeHelper.registerWorkingStations(CAMPFIRE, new ItemStack(Items.CAMPFIRE));
        recipeHelper.registerWorkingStations(BREWING, new ItemStack(Items.BREWING_STAND));
        recipeHelper.registerWorkingStations(STONE_CUTTING, new ItemStack(Items.STONECUTTER));
        recipeHelper.registerWorkingStations(COMPOSTING, new ItemStack(Items.COMPOSTER));
        recipeHelper.registerAutoCraftButtonArea(CAMPFIRE, bounds -> null);
        recipeHelper.registerAutoCraftButtonArea(COMPOSTING, bounds -> null);
        recipeHelper.registerScreenClickArea(new Rectangle(88, 32, 28, 23), CraftingTableScreen.class, CRAFTING);
        recipeHelper.registerScreenClickArea(new Rectangle(137, 29, 10, 13), InventoryScreen.class, CRAFTING);
        recipeHelper.registerScreenClickArea(new Rectangle(97, 16, 14, 30), BrewingStandScreen.class, BREWING);
        recipeHelper.registerScreenClickArea(new Rectangle(78, 32, 28, 23), FurnaceScreen.class, SMELTING);
        recipeHelper.registerScreenClickArea(new Rectangle(78, 32, 28, 23), SmokerScreen.class, SMOKING);
        recipeHelper.registerScreenClickArea(new Rectangle(78, 32, 28, 23), BlastFurnaceScreen.class, BLASTING);
    }
    
    @Override
    public int getPriority() {
        return -1;
    }
    
}
