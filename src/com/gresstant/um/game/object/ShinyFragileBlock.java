package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

public class ShinyFragileBlock extends FragileBlock {
    public ShinyFragileBlock(Context context, double x, double y) {
        super(context, x, y);
        imgBuffer = context.imgRes.getResource("SHINYBLOCK$NORMAL$STAND")[0];
    }
}
