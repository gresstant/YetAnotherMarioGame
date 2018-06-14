package com.gresstant.um.game.object;

// TODO 这只是个临时的实现

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;

public class Block extends BlockAdapter {
    Context context;
    EntityState state = EntityState.STAND;

    public Block(Context context, double x, double y) {
        this.context = context;
        this.x = x;
        this.y = y;

        horzAlign = HorzAlign.LEFT;
        vertAlign = VertAlign.TOP;
        height = 16.0;
        width = 16.0;

        imgBuffer = context.imgRes.getResource("PALEBLOCK$NORMAL$STAND")[0];
    }

    @Override public EntityState getState() {
        return state;
    }

    @Override public void setState(EntityState state) {
        this.state = state;
    }

    private BufferedImage imgBuffer;
    @Override public BufferedImage getImage() {
        return imgBuffer;
    }
}
