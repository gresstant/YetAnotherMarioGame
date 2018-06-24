package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;

public class CoinHelper extends BackgroundAdapter {
    private EntityState state = EntityState.FROZEN;
    private Context context;
    private int timer = 0;

    public CoinHelper(Context context) {
        this.context = context;

        width = 16.0;
        height = 16.0;
        horzAlign = HorzAlign.CENTER;
        vertAlign = VertAlign.BOTTOM;
        imgBuffer = context.imgRes.getResource("COIN$NORMAL$STAND")[0];
    }

    @Override public EntityState getState() {
        return state;
    }

    @Override public void setState(EntityState state) {
        this.state = state;
    }

    @Override public void die(long timestamp, Runnable callback) {
        dispose();
    }

    @Override public void tick(int ms) {
        if (getState() == EntityState.STILL) timer += ms;
        if (getState() == EntityState.STILL && timer > 300) dispose();
    }

    BufferedImage imgBuffer;
    @Override public BufferedImage getImage() {
        int offset = Math.min((int) (timer / 200.0 * imgBuffer.getHeight()), imgBuffer.getHeight());
        if (offset <= 0) return null;
        BufferedImage output = new BufferedImage(imgBuffer.getWidth(), offset, BufferedImage.TYPE_INT_ARGB);
        output.createGraphics().drawImage(imgBuffer, 0, 0, null);
        return output;
    }
}
