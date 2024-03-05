package com.ardacraft.ardastuff.mixin;


import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

import static net.minecraft.world.Heightmap.populateHeightmaps;

@Mixin(Heightmap.class)
public class HeightMapMixin {

    @Inject(method = "setTo", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V"), cancellable = true)
    public void suppressWarning(Chunk chunk, Heightmap.Type type, long[] values, CallbackInfo ci) {
        populateHeightmaps(chunk, EnumSet.of(type));
        ci.cancel();
    }
}
