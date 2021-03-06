package com.gresstant.um.game.map;

import com.gresstant.um.game.Context;
import com.gresstant.um.game.object.IEntity;
import com.gresstant.um.game.object.Mario;

import java.util.function.*;

public class MapReaderContext {
    public Context context;
    public Supplier<Mario> marioSupplier;
    public Runnable lifeIncrement;
    public Consumer<IEntity> addEntityLater;
}
