/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.rei.api;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface EntryRegistry {
    
    /**
     * Gets the current unmodifiable item list
     *
     * @return an unmodifiable item list
     */
    List<Entry> getEntryList();
    
    /**
     * Gets the current modifiable item list
     *
     * @return an modifiable item list
     */
    @Deprecated
    List<Entry> getModifiableEntryList();
    
    /**
     * Gets all possible stacks from an item
     *
     * @param item the item to find
     * @return the array of possible stacks
     */
    ItemStack[] getAllStacksFromItem(Item item);
    
    /**
     * Registers an new stack to the item list
     *
     * @param afterItem the stack to put after
     * @param stack     the stack to register
     */
    void registerItemStack(Item afterItem, ItemStack stack);
    
    void registerFluid(Fluid fluid);
    
    /**
     * Registers multiple stacks to the item list
     *
     * @param afterItem the stack to put after
     * @param stacks    the stacks to register
     */
    default void registerItemStack(Item afterItem, ItemStack... stacks) {
        for (int i = stacks.length - 1; i >= 0; i--) {
            ItemStack stack = stacks[i];
            if (stack != null && !stack.isEmpty())
                registerItemStack(afterItem, stack);
        }
    }
    
    /**
     * Registers multiple stacks to the item list
     *
     * @param stacks the stacks to register
     */
    default void registerItemStack(ItemStack... stacks) {
        registerItemStack(null, stacks);
    }
    
    /**
     * Checks if a stack is already registered
     *
     * @param stack the stack to check
     * @return whether the stack has been registered
     */
    default boolean alreadyContain(ItemStack stack) {
        return getEntryList().stream().filter(entry -> entry.getEntryType() == Entry.Type.ITEM).anyMatch(entry -> ItemStack.areEqualIgnoreDamage(stack, entry.getItemStack()));
    }
    
}
