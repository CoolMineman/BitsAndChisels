package io.github.coolmineman.bitsandchisels.dashloader;

import io.github.coolmineman.bitsandchisels.BitItemModel;
import net.minecraft.client.render.model.BakedModel;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.annotation.DashConstructor;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;
import net.oskarstrom.dashloader.model.DashModel;

@DashObject(BitItemModel.class)
public class DashBitItemModel implements DashModel {

    @DashConstructor(ConstructorMode.EMPTY)
    public DashBitItemModel() {
    }

    @Override
    public BakedModel toUndash(DashRegistry registry) {
        return new BitItemModel();
    }


    @Override
    public int getStage() {
        return 0;
    }
}
