package me.shedaniel.rei.mixin;

import me.shedaniel.rei.RoughlyEnoughItemsCore;
import me.shedaniel.rei.listeners.RecipeSync;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.play.server.SPacketUpdateRecipes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    
    @Shadow @Final private RecipeManager recipeManager;
    
    @Inject(method = "handleUpdateRecipes", at = @At("RETURN"))
    private void onUpdateRecipes(SPacketUpdateRecipes packetIn, CallbackInfo ci) {
        RoughlyEnoughItemsCore.getListeners(RecipeSync.class).forEach(recipeSync -> recipeSync.recipesLoaded(this.recipeManager));
    }
    
}