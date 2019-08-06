/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.rei.plugin;

import me.shedaniel.rei.RoughlyEnoughItemsCore;
import me.shedaniel.rei.api.PluginDisabler;
import me.shedaniel.rei.api.PluginFunction;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.autocrafting.*;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.minecraft.util.Identifier;

public class DefaultAutoCraftingPlugin implements REIPluginV0 {
    
    public static final Identifier PLUGIN = new Identifier("roughlyenoughitems", "default_auto_crafting_plugin");
    
    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }
    
    @Override
    public SemanticVersion getMinimumVersion() throws VersionParsingException {
        return SemanticVersion.parse("2.10");
    }
    
    @Override
    public void onFirstLoad(PluginDisabler pluginDisabler) {
        if (!RoughlyEnoughItemsCore.getConfigManager().getConfig().loadDefaultPlugin) {
            pluginDisabler.disablePluginFunction(PLUGIN, PluginFunction.REGISTER_ITEMS);
            pluginDisabler.disablePluginFunction(PLUGIN, PluginFunction.REGISTER_CATEGORIES);
            pluginDisabler.disablePluginFunction(PLUGIN, PluginFunction.REGISTER_RECIPE_DISPLAYS);
            pluginDisabler.disablePluginFunction(PLUGIN, PluginFunction.REGISTER_OTHERS);
        }
    }
    
    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerAutoCraftingHandler(new AutoCraftingTableBookHandler());
        recipeHelper.registerAutoCraftingHandler(new AutoInventoryBookHandler());
        recipeHelper.registerAutoCraftingHandler(new AutoFurnaceBookHandler());
        recipeHelper.registerAutoCraftingHandler(new AutoSmokerBookHandler());
        recipeHelper.registerAutoCraftingHandler(new AutoBlastingBookHandler());
        recipeHelper.registerAutoCraftingHandler(new AutoCraftingTableHandler());
    }
    
}
