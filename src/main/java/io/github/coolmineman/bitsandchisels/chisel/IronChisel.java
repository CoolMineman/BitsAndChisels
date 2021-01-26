package io.github.coolmineman.bitsandchisels.chisel;

import java.util.Optional;

import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import io.github.coolmineman.bitsandchisels.api.BitUtils;
import io.github.coolmineman.bitsandchisels.api.client.RedBoxCallback;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class IronChisel extends ToolItem {

    public static final Identifier PACKET_ID = new Identifier("bitsandchisels", "iron_chisel_packet");
    private long lastBreakTick = 0;

    public IronChisel(Settings settings) {
        super(ToolMaterials.STONE, settings);
    }

    public void init() {
        ServerSidePacketRegistry.INSTANCE.register(PACKET_ID, (packetContext, attachedData) -> {
            BlockPos pos = attachedData.readBlockPos();
            int x = attachedData.readInt();
            int y = attachedData.readInt();
            int z = attachedData.readInt();
            packetContext.getTaskQueue().execute(() -> {
                // Execute on the main thread
                PlayerEntity player = packetContext.getPlayer();
                World world = player.world;
                ItemStack stack = player.getMainHandStack();
                if (world.canSetBlock(pos) && stack.getItem() == BitsAndChisels.IRON_CHISEL && player.getBlockPos().getSquaredDistance(pos.getX(), pos.getY(), pos.getZ(), true) < 81) {
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                            for (int k = 0; k < 4; k++) {
                                Optional<BlockState> oldstate = BitUtils.getBit(world, pos, x + i, y + j, z + k);
                                if (oldstate.isPresent() && BitUtils.setBit(world, pos, x + i, y + j, z + k, Blocks.AIR.getDefaultState())) {
                                    BitUtils.update(world, pos);
                                    if (!oldstate.get().isAir()) player.inventory.offerOrDrop(world, BitUtils.getBitItemStack(oldstate.get()));
                                }
                            }
                        }
                    }
                }
            });
        });
    }

    public void initClient() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            Item i = player.getStackInHand(hand).getItem();
            if (i instanceof IronChisel) {
                return ((IronChisel)i).interactBreakBlockClient(player, world, pos);
            }
            return ActionResult.PASS;
        });
        RedBoxCallback.EVENT.register((redBoxDrawer, matrixStack, vertexConsumer, worldoffsetx, worldoffsety, worldoffsetz) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player.getMainHandStack().getItem() == BitsAndChisels.IRON_CHISEL) {
                HitResult hit = client.crosshairTarget;
                if (hit.getType() == HitResult.Type.BLOCK) {
                    Direction direction = ((BlockHitResult)hit).getSide();
                    BlockPos pos = ((BlockHitResult)hit).getBlockPos();
                    int x = (int) Math.floor(Math.floor(((hit.getPos().getX() - pos.getX()) * 16) + (direction.getOffsetX() * -0.5d)) / 4) * 4;
                    int y = (int) Math.floor(Math.floor(((hit.getPos().getY() - pos.getY()) * 16) + (direction.getOffsetY() * -0.5d)) / 4) * 4;
                    int z = (int) Math.floor(Math.floor(((hit.getPos().getZ() - pos.getZ()) * 16) + (direction.getOffsetZ() * -0.5d)) / 4) * 4;
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                            for (int k = 0; k < 4; k++) {
                                redBoxDrawer.drawRedBox(matrixStack, vertexConsumer, pos, x + i, y + j, z + k, worldoffsetx, worldoffsety, worldoffsetz);
                            }
                        }
                    }
                }
            }
        });
    }

    public ActionResult interactBreakBlockClient(PlayerEntity player, World world, BlockPos pos) {
        if (player.world.getTime() - lastBreakTick < 5) return ActionResult.CONSUME;
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;

        if (hit.getType() == HitResult.Type.BLOCK) {
            Direction direction = ((BlockHitResult)hit).getSide();
            int x = (int) Math.floor(Math.floor(((hit.getPos().getX() - pos.getX()) * 16) + (direction.getOffsetX() * -0.5d)) / 4) * 4;
            int y = (int) Math.floor(Math.floor(((hit.getPos().getY() - pos.getY()) * 16) + (direction.getOffsetY() * -0.5d)) / 4) * 4;
            int z = (int) Math.floor(Math.floor(((hit.getPos().getZ() - pos.getZ()) * 16) + (direction.getOffsetZ() * -0.5d)) / 4) * 4;
            if (world.getBlockEntity(pos) instanceof BitsBlockEntity || BitUtils.exists(BitUtils.getBit(world, pos, x, y, z))) {
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeBlockPos(pos);
                passedData.writeInt(x);
                passedData.writeInt(y);
                passedData.writeInt(z);
                ClientSidePacketRegistry.INSTANCE.sendToServer(PACKET_ID, passedData);
                lastBreakTick = player.world.getTime();
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.CONSUME;
    }
    
}
