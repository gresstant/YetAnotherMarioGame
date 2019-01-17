package com.gresstant.um.game.object;

public abstract class BackgroundTemplate extends BlockTemplate {
    @Override public boolean collideUpwards(boolean[] keyArray, IEntity entity) {
        return false;
    }

    @Override public boolean collideDownwards(boolean[] keyArray, IEntity entity) {
        return false;
    }

    @Override public boolean collideLeftwards(boolean[] keyArray, IEntity entity) {
        return false;
    }

    @Override public boolean collideRightwards(boolean[] keyArray, IEntity entity) {
        return false;
    }
}
