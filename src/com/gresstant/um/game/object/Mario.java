package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;

public class Mario extends EntityAdapter {
    private Context context;

    public Mario(Context context) {
        this.context = context;
    }

    @Override public BufferedImage getImage() {
        return context.imgRes.getResource("MARIO$SMALL$STAND")[0];
    }
}
