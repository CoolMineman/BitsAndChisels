package io.github.coolmineman.bitsandchisels.dashloader;

import io.github.coolmineman.bitsandchisels.BitsBlockModel;
import net.oskarstrom.dashloader.DashRegistry;
import net.oskarstrom.dashloader.api.annotation.DashConstructor;
import net.oskarstrom.dashloader.api.annotation.DashObject;
import net.oskarstrom.dashloader.api.enums.ConstructorMode;
import net.oskarstrom.dashloader.model.DashModel;

@DashObject(BitsBlockModel.class)
public class DashBitsBlockModel implements DashModel {

    @DashConstructor(ConstructorMode.EMPTY)
    public DashBitsBlockModel() {
    }

    @Override
    public BitsBlockModel toUndash(DashRegistry registry) {
        return new BitsBlockModel();
    }

    @Override
    public int getStage() {
        return 0;
    }
}
