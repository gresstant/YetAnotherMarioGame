package com.gresstant.um.game.object;

public abstract class BlockAdapter extends EntityAdapter implements IBlock {
    @Override public void activate() {
        setState(EntityState.STILL);
    }

    @Override public void dispose() {
        setState(EntityState.DISPOSED);
    }

    @Override public void tick(int ms) {}

    @Override public boolean collideUpwards(boolean[] keyArray, Mario player) {
        return true;
    }

    @Override public boolean collideDownwards(boolean[] keyArray, Mario player) {
        return true;
    }

    @Override public boolean collideLeftwards(boolean[] keyArray, Mario player) {
        return true;
    }

    @Override public boolean collideRightwards(boolean[] keyArray, Mario player) {
        return true;
    }
}
