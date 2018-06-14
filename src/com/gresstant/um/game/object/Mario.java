package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;

public class Mario extends EntityAdapter {
    private Context context;
    private EntityState state = EntityState.FROZEN;

    public double speedX = 0.0;
    public double speedY = 0.0;
    public double accX = 0.0;
    public double accY = 0.0;

    /**
     * 是否面向右边
     */
    public boolean faceRight = true;
    /**
     * 是否正在转身
     */
    public boolean turning = false;
    /**
     * 是否在跑。
     * 针对此变量的一切赋值操作应当交由 GamePanel 完成。
     */
    public boolean run = false;
    /**
     * 马里奥的脚下是否有东西做支撑
     * 针对此变量的一切赋值操作应当交由 GamePanel 完成。
     */
    public boolean bottomSupported = true;

    public Mario(Context context, double x, double y) {
        this.context = context;
        this.x = x;
        this.y = y;
        width = 16.0;
        height = 16.0;
        horzAlign = HorzAlign.CENTER;
        vertAlign = VertAlign.BOTTOM;
    }

    private String resBufferKey = "";
    private BufferedImage[] resBufferImg;
    private BufferedImage[] resBufferFlip;
    private BufferedImage[] getResource(String key, boolean flip) {
        if (key.equals(resBufferKey)) {
            return flip ? resBufferFlip : resBufferImg;
        } else {
            BufferedImage[] got = context.imgRes.getResource(key);
            if (got == null) return null;
            resBufferKey = key;
            resBufferImg = got;
            resBufferFlip = new BufferedImage[resBufferImg.length];
            for (int i = 0; i < resBufferImg.length; i++)
                resBufferFlip[i] = Utilities.createHorzFlipped(resBufferImg[i]);
            return flip ? resBufferFlip : resBufferImg;
        }
    }

    @Override public BufferedImage getImage() {
        if (state == EntityState.FROZEN || state == EntityState.STAND) {
            return getResource("MARIO$SMALL$STAND", !faceRight)[0];
        } else if (state == EntityState.RUN) {
            if (turning) return getResource("MARIO$SMALL$TURN", !faceRight)[0];
            return getResource("MARIO$SMALL$WALK", !faceRight)[Math.abs((int) x / 2 % 4)];
        } else if (state == EntityState.JUMP) {
            return getResource("MARIO$SMALL$JUMP", !faceRight)[0];
        } else {
            throw new RuntimeException();
        }
    }

    public void tick(int ms) {
        // V=V0+at
        // s=V0t+(at^2)/2
        double second = ms / 1000.0;

        double oldSpeedX = speedX;

        // 根据横向加速度调整速度和位置
        if (Math.abs(speedX) < (run ? context.marioMaxRunSpeed : context.marioMaxWalkSpeed)) { // 速度小于最大速度
            speedX += accX * second;
        } else if (state != EntityState.JUMP && turning) { // 没在跳，且在转身
            // 转身时八倍加速度
            speedX += 8 * accX * second;
        } else if (Math.abs(speedX) >= (run ? context.marioMaxRunSpeed : context.marioMaxWalkSpeed) &&
                Math.signum(speedX) == Math.signum(accX)) { // 已是最大速度且同向
            accX = 0.0;
        }

        // 摩擦力
        if (state == EntityState.RUN & accX == 0.0) { // 飞行或站立时没有摩擦
            accX = -Math.signum(speedX) * context.marioFraction; // TODO 这里以后应当乘以一个 ratio
            speedX += accX * second;
            if (Math.abs(speedX) - Math.abs(context.marioFraction * second) < 0.0) { // 如果摩擦加速度大到能够使得马里奥转向
                accX = 0.0;
                speedX = 0.0;
            }
        }

        // 重力下落，当然要保证不超过最大下落速度
        double oldSpeedY = speedY;
        if (!bottomSupported) {
            speedY += context.gravity * second;
            if (speedY > context.maxFallSpeed)
                speedY = context.maxFallSpeed;
        } else { // 在地上就不能掉下去了，只能向上跳
            speedY = Math.min(0.0, speedY);
        }

        // 更新位置
        x += (oldSpeedX + speedX) * second / 2.0;
        y += (oldSpeedY + speedY) * second / 2.0;

        // 重置加速度
        accX = 0.0;

        // 更新状态
        if (!bottomSupported /*|| Math.abs(speedY) < 0.001*/) { // 脚下悬空即判定为跳跃
            state = EntityState.JUMP;
        } else if (Math.abs(speedX) < 0.001) { // 针对浮点数，判断 speedX == 0
            state = EntityState.STAND;
        } else {
            state = EntityState.RUN;
        }
    }

    public void accelerate(double ratio) {
        accX = ratio * context.marioAcclerate;
        if (run) accX *= 1.5;
        if (state != EntityState.JUMP) { // 跳跃时不会转向
            if (accX > +0.0) {
                faceRight = true;
            } else if (accX < -0.0) {
                faceRight = false;
            }
        }
        turning = Math.signum(speedX) != Math.signum(accX);
    }

    private long lastJumpTimestamp;
    /**
     * 让马里奥跳起来
     * 马里奥会根据调用时的状态判断现在能不能跳，所以请在碰撞检测完毕后再调用
     */
    public void tryJump(long timestamp) {
        if (bottomSupported) {
            speedY = -192.0;
            lastJumpTimestamp = timestamp;
        } else if (timestamp - lastJumpTimestamp <= 100 && state == EntityState.JUMP) {
            speedY = -192.0;
        }
    }

    @Override public void setState(EntityState state) {
        this.state = state;
    }

    @Override public EntityState getState() {
        return state;
    }
}
