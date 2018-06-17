package com.gresstant.um.game.object;

public abstract class BackgroundAdapter extends BlockAdapter {
    @Override public boolean collideUpwards(boolean[] keyArray, Mario player) {
        return false;
    }

    @Override public boolean collideDownwards(boolean[] keyArray, Mario player) {
        return false;
    }

    @Override public boolean collideLeftwards(boolean[] keyArray, Mario player) {
        return false;
    }

    @Override public boolean collideRightwards(boolean[] keyArray, Mario player) {
        return false;
    }
}
