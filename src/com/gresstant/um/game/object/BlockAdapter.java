package com.gresstant.um.game.object;

public abstract class BlockAdapter extends EntityAdapter implements IBlock {
    @Override public void activate() {
        setState(EntityState.STILL);
    }

    @Override public void dispose() {
        setState(EntityState.DISPOSED);
    }
}
