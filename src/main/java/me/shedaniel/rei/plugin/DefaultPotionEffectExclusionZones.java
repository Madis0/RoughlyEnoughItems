/*
 * Roughly Enough Items by Danielshe.
 * Licensed under the MIT License.
 */

package me.shedaniel.rei.plugin;

import com.google.common.collect.Ordering;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.impl.ScreenHelper;
import me.shedaniel.rei.listeners.AbstractInventoryScreenHooks;
import me.shedaniel.rei.listeners.ContainerScreenHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class DefaultPotionEffectExclusionZones implements Function<Boolean, List<Rectangle>> {
    @Override
    public List<Rectangle> apply(Boolean isOnRightSide) {
        if (isOnRightSide || !(ScreenHelper.getLastContainerScreen() instanceof AbstractInventoryScreen) || !((AbstractInventoryScreenHooks) ScreenHelper.getLastContainerScreen()).rei_doesOffsetGuiForEffects())
            return Collections.emptyList();
        Collection<StatusEffectInstance> activePotionEffects = MinecraftClient.getInstance().player.getStatusEffects();
        if (activePotionEffects.isEmpty())
            return Collections.emptyList();
        ContainerScreenHooks hooks = ScreenHelper.getLastContainerScreenHooks();
        List<Rectangle> list = new ArrayList<>();
        int x = hooks.rei_getContainerLeft() - 124;
        int y = hooks.rei_getContainerTop();
        int height = 33;
        if (activePotionEffects.size() > 5)
            height = 132 / (activePotionEffects.size() - 1);
        for (StatusEffectInstance instance : Ordering.natural().sortedCopy(activePotionEffects)) {
            list.add(new Rectangle(x, y, 166, height));
            y += height;
        }
        return list;
    }
}
