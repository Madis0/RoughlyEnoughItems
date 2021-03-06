/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.rei.plugin.smoking;

import me.shedaniel.rei.api.TransferRecipeDisplay;
import me.shedaniel.rei.plugin.DefaultPlugin;
import me.shedaniel.rei.server.ContainerInfo;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultSmokingDisplay implements TransferRecipeDisplay {
    
    private SmokingRecipe display;
    private List<List<ItemStack>> input;
    private List<ItemStack> output;
    
    public DefaultSmokingDisplay(SmokingRecipe recipe) {
        this.display = recipe;
        this.input = recipe.getPreviewInputs().stream().map(i -> Arrays.asList(i.getStackArray())).collect(Collectors.toList());
        this.input.add(FurnaceBlockEntity.createFuelTimeMap().keySet().stream().map(Item::getStackForRender).collect(Collectors.toList()));
        this.output = Collections.singletonList(recipe.getOutput());
    }
    
    @Override
    public Optional<Identifier> getRecipeLocation() {
        return Optional.ofNullable(display).map(AbstractCookingRecipe::getId);
    }
    
    @Override
    public List<List<ItemStack>> getInput() {
        return input;
    }
    
    public List<ItemStack> getFuel() {
        return input.get(1);
    }
    
    @Override
    public List<ItemStack> getOutput() {
        return output;
    }
    
    @Override
    public Identifier getRecipeCategory() {
        return DefaultPlugin.SMOKING;
    }
    
    @Override
    public List<List<ItemStack>> getRequiredItems() {
        return input;
    }
    
    public Optional<SmokingRecipe> getOptionalRecipe() {
        return Optional.ofNullable(display);
    }
    
    @Override
    public int getWidth() {
        return 1;
    }
    
    @Override
    public int getHeight() {
        return 1;
    }
    
    @Override
    public List<List<ItemStack>> getOrganisedInput(ContainerInfo<Container> containerInfo, Container container) {
        return display.getPreviewInputs().stream().map(i -> Arrays.asList(i.getStackArray())).collect(Collectors.toList());
    }
    
}
