package io.github.coolmineman.bitsandchisels;

import org.jetbrains.annotations.Nullable;

import io.github.coolmineman.bitsandchisels.mixin.SimpleVoxelShapeFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class BitsBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {
    private BlockState[][][] states;
    @Environment(EnvType.CLIENT)
    protected Mesh mesh;
    protected VoxelShape shape = VoxelShapes.fullCube();
    protected NbtCompound nbtCache;

    public BitsBlockEntity(BlockPos pos, BlockState state) {
        this(Blocks.AIR.getDefaultState(), pos, state);
    }

    public BitsBlockEntity(BlockState fillState, BlockPos pos, BlockState state) {
        this(new BlockState[16][16][16], pos, state);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    states[i][j][k] = fillState;
                }
            }
        }
    }

    public BitsBlockEntity(BlockState[][][] states, BlockPos pos, BlockState state) {
        super(BitsAndChisels.BITS_BLOCK_ENTITY, pos, state);
        this.states = states;
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        if (nbtCache == null) {
            BitsAndChisels.LOGGER.error("NbtCache should not be null!");
            BitNbtUtil.write3DBitArray(tag, states);
        } else {
            tag.copyFrom(nbtCache);
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        BitNbtUtil.read3DBitArray(tag, states);
        rebuildShape();
        if (getWorld() != null && getWorld().isClient) {
            postFromClientTag();
        } else {
            rebuildNbtCache();
        }
    }
    
    private void rebuildNbtCache() {
        NbtCompound c = new NbtCompound();
        BitNbtUtil.write3DBitArray(c, states);
        nbtCache = c;
    }

    public void setState(int x, int y, int z, BlockState state) {
        states[x][y][z] = state;
    }

    /**
     * Used to replace the backing array, don't call this w/o a good reason.
     */
    public void setStates(BlockState[][][] states) {
        this.states = states;
    }

    public void rebuildServer() {
        rebuildShape();
        rebuildNbtCache();
    }

    public BlockState getState(int x, int y, int z) {
        return states[x][y][z];
    }

    public BlockState[][][] getStates() {
        return states;
    }

    protected void rebuildShape() {
        boolean fullcube = true;
        BitSetVoxelSet set = new BitSetVoxelSet(16, 16, 16);
        BlockState firststate = states[0][0][0];
        int totalLight = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    BlockState state = states[i][j][k];
                    totalLight += state.getLuminance();
                    if (!state.isAir()) {
                        set.set(i, j, k);
                    }
                    if (firststate != state) {
                        fullcube = false;
                    }
                }
            }
        }
        shape = SimpleVoxelShapeFactory.getSimpleVoxelShape(set);
        if (world != null) {
            BlockState state = world.getBlockState(pos);
            if (fullcube) {
                world.setBlockState(pos, states[0][0][0]);
            }
            int targetlight = MathHelper.clamp((int)(Math.sqrt(totalLight) * 0.0625 /*16/sqrt(4096)*/), 0, 16);
            if (state.get(BitsBlock.LIGHT_LEVEL) != targetlight) {
                world.setBlockState(pos, state.with(BitsBlock.LIGHT_LEVEL, targetlight), 0);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    protected void rebuildMesh() {
        mesh = BitMeshes.createMesh(states, world, pos);
    }

    public void postFromClientTag() {
        rebuildMesh();
        MinecraftClient.getInstance().worldRenderer.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, be -> ((BitsBlockEntity)be).createNbt());
    }

    public void sync() {
        ((ServerWorld) world).getChunkManager().markForUpdate(getPos());
    }

    // Begin crimes agains modding
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        sync();
        return new NbtCompound();
    }
    // End crimes agains modding

    @Environment(EnvType.CLIENT)
    @Override
    public @Nullable Object getRenderAttachmentData() {
        return mesh;
    }

}
