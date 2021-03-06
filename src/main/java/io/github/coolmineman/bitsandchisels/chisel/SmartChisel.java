package io.github.coolmineman.bitsandchisels.chisel;

import io.github.coolmineman.bitsandchisels.BitsAndChisels;
import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import io.github.coolmineman.bitsandchisels.api.BitUtils;
import io.github.coolmineman.bitsandchisels.api.client.RedBoxCallback;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SmartChisel extends ToolItem implements ServerPlayNetworking.PlayChannelHandler {
    public static final Identifier PACKET_ID = new Identifier("bitsandchisels", "smart_chisel_packet");

    BlockPos block = null;

    int bitx = 0;
    int bity = 0;
    int bitz = 0;

    boolean haspos1 = false;

    private long lastBreakTick = 0;

    public SmartChisel(Settings settings) {
        super(ToolMaterials.IRON, settings);
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID, this);
    }

    public void initClient() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            Item i = player.getStackInHand(hand).getItem();
            if (i instanceof SmartChisel) {
                return ((SmartChisel)i).interactBreakBlockClient(player, world, pos);
            }
            return ActionResult.PASS;
        });
        RedBoxCallback.EVENT.register((redBoxDrawer, matrixStack, vertexConsumer, worldoffsetx, worldoffsety, worldoffsetz) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player.getMainHandStack().getItem() == BitsAndChisels.SMART_CHISEL && haspos1) {
                HitResult hit = client.crosshairTarget;
                if (hit.getType() == HitResult.Type.BLOCK) {
                    Direction direction = ((BlockHitResult)hit).getSide();
                    BlockPos pos = ((BlockHitResult)hit).getBlockPos();
                    if (pos.getSquaredDistance(block) > 9) return;
                    int x = (int) Math.floor(((hit.getPos().getX() - pos.getX()) * 16) + (direction.getOffsetX() * -0.5d));
                    int y = (int) Math.floor(((hit.getPos().getY() - pos.getY()) * 16) + (direction.getOffsetY() * -0.5d));
                    int z = (int) Math.floor(((hit.getPos().getZ() - pos.getZ()) * 16) + (direction.getOffsetZ() * -0.5d));
                    
                    int blockXOffset = pos.getX() - block.getX();
                    int blockYOffset = pos.getY() - block.getY();
                    int blockZOffset = pos.getZ() - block.getZ();

                    int minx = Math.min(bitx, blockXOffset * 16 + x);
                    int miny = Math.min(bity, blockYOffset * 16 + y);
                    int minz = Math.min(bitz, blockZOffset * 16 + z);

                    int maxx = Math.max(bitx, blockXOffset * 16 + x);
                    int maxy = Math.max(bity, blockYOffset * 16 + y);
                    int maxz = Math.max(bitz, blockZOffset * 16 + z);
                    
                    redBoxDrawer.drawRedBox(matrixStack, vertexConsumer, block, minx, miny, minz, maxx + 1, maxy + 1, maxz + 1, worldoffsetx, worldoffsety, worldoffsetz);
                }
            }
        });
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int x1 = buf.readInt();
        int y1 = buf.readInt();
        int z1 = buf.readInt();
        int x2 = buf.readInt();
        int y2 = buf.readInt();
        int z2 = buf.readInt();
        server.execute(() -> {
            if (
                player.getMainHandStack().getItem() == BitsAndChisels.SMART_CHISEL &&
                player.getBlockPos().getSquaredDistance(pos.getX(), pos.getY(), pos.getZ(), true) < 81 &&
                x1 <= x2 && y1 <= y2 && z1 <= z2 &&
                ((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)) + ((z2 - z1) * (z2 - z1)) <= 1000000
            ) {
                World world = player.world;
                BlockPos.Mutable mut = new BlockPos.Mutable();
                for (int i = x1; i <= x2; i++) {
                    for (int j = y1; j <= y2; j++) {
                        for (int k = z1; k <= z2; k++) {
                            mut.set(pos.getX() + Math.floorDiv(i, 16), pos.getY() + Math.floorDiv(j, 16), pos.getZ() + Math.floorDiv(k, 16));
                            int x = Math.floorMod(i, 16);
                            int y = Math.floorMod(j, 16);
                            int z = Math.floorMod(k, 16);
                            BlockState oldstate = BitUtils.getBit(world, mut, x, y, z);
                            if (BitUtils.exists(oldstate) && BitUtils.setBit(world, mut, x, y, z, Blocks.AIR.getDefaultState())) {
                                player.getInventory().offerOrDrop(BitUtils.getBitItemStack(oldstate));
                            }
                        }
                    }
                }
                int blockx1 = pos.getX() + Math.floorDiv(x1, 16);
                int blocky1 = pos.getY() + Math.floorDiv(y1, 16);
                int blockz1 = pos.getZ() + Math.floorDiv(z1, 16);
                int blockx2 = pos.getX() + Math.floorDiv(x2, 16);
                int blocky2 = pos.getY() + Math.floorDiv(y2, 16);
                int blockz2 = pos.getZ() + Math.floorDiv(z2, 16);
                for (int i = blockx1; i <= blockx2; i++) {
                    for (int j = blocky1; j <= blocky2; j++) {
                        for (int k = blockz1; k <= blockz2; k++) {
                            mut.set(i, j, k);
                            BitUtils.update(world, mut);
                        }
                    }
                }
            }
        });
    }


    public ActionResult interactBreakBlockClient(PlayerEntity player, World world, BlockPos pos) {
        if (getTime() - lastBreakTick < 5) return ActionResult.CONSUME;
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;

        if (hit.getType() == HitResult.Type.BLOCK) {
            Direction direction = ((BlockHitResult)hit).getSide();
            int x = (int) Math.floor(((hit.getPos().getX() - pos.getX()) * 16) + (direction.getOffsetX() * -0.5d));
            int y = (int) Math.floor(((hit.getPos().getY() - pos.getY()) * 16) + (direction.getOffsetY() * -0.5d));
            int z = (int) Math.floor(((hit.getPos().getZ() - pos.getZ()) * 16) + (direction.getOffsetZ() * -0.5d));
            lastBreakTick = getTime();
            if (world.getBlockEntity(pos) instanceof BitsBlockEntity || BitUtils.exists(BitUtils.getBit(world, pos, x, y, z))) {
                if (!haspos1 || pos.getSquaredDistance(block) > 9) {
                    block = pos;
                    bitx = x;
                    bity = y;
                    bitz = z;
                    haspos1 = true;
                } else {
                    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());

                    int blockXOffset = pos.getX() - block.getX();
                    int blockYOffset = pos.getY() - block.getY();
                    int blockZOffset = pos.getZ() - block.getZ();

                    int minx = Math.min(bitx, blockXOffset * 16 + x);
                    int miny = Math.min(bity, blockYOffset * 16 + y);
                    int minz = Math.min(bitz, blockZOffset * 16 + z);

                    int maxx = Math.max(bitx, blockXOffset * 16 + x);
                    int maxy = Math.max(bity, blockYOffset * 16 + y);
                    int maxz = Math.max(bitz, blockZOffset * 16 + z);

                    passedData.writeBlockPos(block);
                    passedData.writeInt(minx);
                    passedData.writeInt(miny);
                    passedData.writeInt(minz);
                    passedData.writeInt(maxx);
                    passedData.writeInt(maxy);
                    passedData.writeInt(maxz);
                    ClientPlayNetworking.send(PACKET_ID, passedData);
                    haspos1 = false;
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.CONSUME;
    }

    private static long getTime() {
        return System.currentTimeMillis() / 50;
    }
}
