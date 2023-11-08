package com.ardacraft.ardastuff.mixin;

import com.mojang.datafixers.DataFixerBuilder;
import net.minecraft.datafixer.Schemas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Schemas.class)
public class SchemaMixin {

    @Overwrite
    private static void build(DataFixerBuilder builder) {
        // oh, no!
    }
}
