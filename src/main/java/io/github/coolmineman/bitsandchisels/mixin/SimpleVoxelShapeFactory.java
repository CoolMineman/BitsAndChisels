package io.github.coolmineman.bitsandchisels.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.VoxelSet;

@Mixin(SimpleVoxelShape.class)
public interface SimpleVoxelShapeFactory {
    @Invoker("<init>")
    public static SimpleVoxelShape getSimpleVoxelShape(VoxelSet voxelSet) {
        throw new AssertionError();
    }
}
