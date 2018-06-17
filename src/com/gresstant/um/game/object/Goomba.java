package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;

public class Goomba extends EnemyAdapter {
    public Goomba(Context context, double x, double y) {
        super(context, 16.0);
        this.x = x;
        this.y = y;
        height = 16.0;
        width = 16.0;
        state = EntityState.RUN;
        imgBuffer = context.imgRes.getResource("GOOMBA$NORMAL$WALK");
        horzAlign = HorzAlign.CENTER;
        vertAlign = VertAlign.BOTTOM;
    }

    @Override public void activate() {
        setState(EntityState.RUN);
    }

    @Override public void dispose() {
        setState(EntityState.DISPOSED);
    }

    @Override public void die(long timestamp, Runnable callback) {
        dispose();
    }

    @Override public boolean collideUpwards(boolean[] keyArray, Mario player) {
        player.die();
        return false;
    }

    @Override public boolean collideDownwards(boolean[] keyArray, Mario player) {
        die(0, null);
        player.bottomSupported = true;
        player.tryJump(System.currentTimeMillis());
        return false;
    }

    @Override public boolean collideLeftwards(boolean[] keyArray, Mario player) {
        player.die();
        return false;
    }

    @Override public boolean collideRightwards(boolean[] keyArray, Mario player) {
        player.die();
        return false;
    }

    private BufferedImage[] imgBuffer;
    @Override public BufferedImage getImage() {
        switch (getState()) {
            case DISPOSED:
                return null;
            case STILL:
            case DEAD:
            case STAND:
                return context.imgRes.getResource("GOOMBA$NORMAL$OVER")[0];
            case FROZEN:
            case RUN:
            case JUMP:
                return imgBuffer[(int) x / 4 % 2];
        }
        return null;
    }
}
