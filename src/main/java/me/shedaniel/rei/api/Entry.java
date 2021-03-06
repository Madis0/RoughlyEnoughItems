/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.rei.api;

import me.shedaniel.rei.impl.FluidEntry;
import me.shedaniel.rei.impl.ItemStackEntry;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface Entry {
    @SuppressWarnings("deprecation")
    static Entry create(ItemStack itemStack) {
        return new ItemStackEntry(itemStack);
    }
    
    @SuppressWarnings("deprecation")
    static Entry create(Fluid fluid) {
        return new FluidEntry(fluid);
    }
    
    Type getEntryType();
    
    @Nullable
    ItemStack getItemStack();
    
    @Nullable
    Fluid getFluid();
    
    public static enum Type {
        ITEM, FLUID
    }
}
