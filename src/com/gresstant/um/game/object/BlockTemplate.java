package com.gresstant.um.game.object;

public abstract class BlockTemplate extends EntityTemplate implements IBlock {
    @Override public void activate() {
        setState(EntityState.STILL);
    }

    @Override public void dispose() {
        setState(EntityState.DISPOSED);
    }

    @Override public void tick(int ms) {}

    @Override public boolean collideUpwards(boolean[] keyArray, IEntity entity) {
        return true;
    }

    @Override public boolean collideDownwards(boolean[] keyArray, IEntity entity) {
        return true;
    }

    @Override public boolean collideLeftwards(boolean[] keyArray, IEntity entity) {
        return true;
    }

    @Override public boolean collideRightwards(boolean[] keyArray, IEntity entity) {
        return true;
    }
}
