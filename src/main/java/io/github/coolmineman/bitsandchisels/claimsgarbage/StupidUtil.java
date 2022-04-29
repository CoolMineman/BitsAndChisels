package io.github.coolmineman.bitsandchisels.claimsgarbage;

import java.util.ArrayList;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class StupidUtil {
    private StupidUtil() { }

    private static ArrayList<Stupid> ugg = new ArrayList<>();

    static {
        FabricLoader fl = FabricLoader.getInstance();
        if (fl.isModLoaded("flan")) ugg.add(FlanIsStupid.INSTANCE);
        if (fl.isModLoaded("goml")) ugg.add(GomlIsVeryStupid.INSTANCE);
    }

    public static boolean canBreak(ServerPlayerEntity player, BlockPos pos) {
        for (Stupid s : ugg) {
            if (!s.canBreak(player, pos)) return false;
        }
        return true;
    }

    public static boolean canPlace(ServerPlayerEntity player, BlockPos pos) {
        for (Stupid s : ugg) {
            if (!s.canPlace(player, pos)) return false;
        }
        return true;
    }
}
