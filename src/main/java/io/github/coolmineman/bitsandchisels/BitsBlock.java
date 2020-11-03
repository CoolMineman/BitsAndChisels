package io.github.coolmineman.bitsandchisels;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class BitsBlock extends Block implements BlockEntityProvider {

    public BitsBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BitsBlockEntity();
    }
    
}
