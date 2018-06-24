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

    public int invincibleTimer = 0;

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
     * 针对此变量的赋 true 值操作应当交由 GamePanel 完成。
     */
    public boolean run = false;
    /**
     * 马里奥的形态。
     * 务必使用 setter 进行修改。
     */
    private GrowthState growth = GrowthState.SMALL;
    /**
     * 用于辅助 getImage 工作。
     */
    private String growthString = "SMALL";
    /**
     * 是否在蹲。
     * 针对此变量的赋 true 值操作应当交由 GamePanel 通过 trySquat 完成。
     */
    private boolean squating = false;
    /**
     * 马里奥的脚下是否有东西做支撑
     * 针对此变量的赋 true 值操作应当交由 GamePanel 完成。
     */
    public boolean bottomSupported = false;
    /**
     * 马里奥的头顶是否右东西挡住
     * 针对此变量的赋 true 值操作应当交由 GamePanel 完成。
     */
    public boolean topSupported = false;

    public boolean leftSuported = false;
    public boolean rightSuported = false;

    public enum GrowthState {
        SMALL,
        BIG,
        BULLET
    }

    public Mario(Context context, double x, double y, Runnable dieCallback) {
        this.context = context;
        horzAlign = HorzAlign.CENTER;
        vertAlign = VertAlign.BOTTOM;
        setLeft(x);
        setTop(y);
        setGrowth(GrowthState.SMALL);
        this.dieCallback = dieCallback;
    }

    public GrowthState getGrowth() {
        return growth;
    }

    public void setGrowth(GrowthState growth) {
        this.growth = growth;
        switch (growth) {
            case SMALL:
                width = 16.0;
                height = 16.0;
                growthString = "SMALL";
                break;
            case BIG:
                width = 16.0;
                height = 32.0;
                growthString = "LARGE";
                break;
            case BULLET:
                width = 16.0;
                height = 32.0;
                growthString = "FIRE";
                break;
            default:
                throw new RuntimeException("not implemented");
        }
    }

    private String resBufferKey = "";
    private BufferedImage[] resBufferImg;
    private BufferedImage[] resBufferInvincible;
    private BufferedImage[] resBufferFlip;
    private BufferedImage[] resBufferFlipInvincible;
    private BufferedImage[] getResource(String key, boolean flip) {
        if (!key.equals(resBufferKey)) {
            BufferedImage[] got = context.imgRes.getResource(key);
            if (got == null) return null;
            resBufferKey = key;
            resBufferImg = got;
            resBufferInvincible = new BufferedImage[resBufferImg.length];
            resBufferFlip = new BufferedImage[resBufferImg.length];
            resBufferFlipInvincible = new BufferedImage[resBufferImg.length];
            for (int i = 0; i < resBufferImg.length; i++) {
                resBufferInvincible[i] = Utilities.createOpacity(resBufferImg[i], 0.8f);
                resBufferFlip[i] = Utilities.createHorzFlipped(resBufferImg[i]);
                resBufferFlipInvincible[i] = Utilities.createOpacity(resBufferFlip[i], 0.8f);
            }
        }
        return flip ? (invincibleTimer > 0 ? resBufferFlipInvincible : resBufferFlip)
                : (invincibleTimer > 0 ? resBufferInvincible : resBufferImg);
    }

    @Override public BufferedImage getImage() {
        switch (state) {
            case FROZEN:
                return getResource("MARIO$" + growthString + "$STAND", false)[0];
            case STAND:
                if (squating) return getResource("MARIO$" + growthString + "$SQUAT", !faceRight)[0];
                return getResource("MARIO$" + growthString + "$STAND", !faceRight)[0];
            case RUN:
                if (squating) return getResource("MARIO$" + growthString + "$SQUAT", !faceRight)[0];
                if (turning) return getResource("MARIO$" + growthString + "$TURN", !faceRight)[0];
                return getResource("MARIO$" + growthString + "$WALK", !faceRight)[Math.abs((int) x / 2 % 4)];
            case JUMP:
                if (squating) return getResource("MARIO$" + growthString + "$SQUAT", !faceRight)[0];
                return getResource("MARIO$" + growthString + "$JUMP", !faceRight)[0];
            case DEAD:
                return getResource("MARIO$SMALL$OVER", !faceRight)[0];
            case DISPOSED:
                return null;
            default:
                throw new RuntimeException();
        }
    }

    /**
     * 通知实体进行换帧必需的运算
     * @param ms 上一帧到这一帧所经过的时间，单位毫秒
     */
    public void tick(int ms) {
        switch (state) {
            case JUMP: case RUN: case STAND:
                tickAlive(ms);
                break;
            case FROZEN: case DISPOSED:
                break; // do nothing
            case DEAD:
                tickDieAnimation(ms);
                break;
            default:
                throw new RuntimeException();
        }
    }

    private void tickAlive(int ms) {
        // V=V0+at
        // s=V0t+(at^2)/2
        double second = ms / 1000.0;

        double oldSpeedX = speedX;

        if (invincibleTimer > 0) invincibleTimer -= ms;

        // 根据横向加速度调整速度和位置
        if (leftSuported && speedX < 0.0 || rightSuported & speedX > 0.0) {
            speedX = 0.0;
            oldSpeedX = 0.0; // 强制去除位移
            accX = 0.0;
        } else if (Math.abs(speedX) < (run ? context.marioMaxRunSpeed : context.marioMaxWalkSpeed)) { // 速度小于最大速度
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
            accX = squating ? -Math.signum(speedX) * context.marioFraction * 2.0 :
                    -Math.signum(speedX) * context.marioFraction; // TODO 这里以后应当乘以一个 ratio
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

        // 更新状态
        if (!bottomSupported && (Math.abs(speedY) > context.gravity * second || state == EntityState.JUMP)) { // 脚下悬空，且速度超过一定值(或之前为跳跃)即判定为跳跃
            state = EntityState.JUMP;
        } else if (Math.abs(speedX) < 0.001) { // 针对浮点数，判断 speedX == 0
            state = EntityState.STAND;
        } else {
            state = EntityState.RUN;
        }

        // 重置
        accX = 0.0;
        bottomSupported = false;
        topSupported = false;
        leftSuported = false;
        rightSuported = false;
        //squating = false; // 在这里重置会导致后面绘图时获取不到状态
    }

    private long dieTimer = 0;
    private void tickDieAnimation(int ms) {
        // assert dieTimestamp != 0;
        dieTimer += ms;
        long animeTimer = dieTimer;//System.currentTimeMillis() - dieTimestamp;
        if (animeTimer <= 600) {
            // do nothing
        } else if (animeTimer <= 3000) {
            double time = animeTimer / 1000.0 - 0.6;
            imgOffsetAdjustY = context.marioJumpSpeed * 1.5 * time + context.gravity * time * time / 2.0;
        } else {
            // TODO dispose
            if (dieCallback != null) dieCallback.run();
            dieTimer = 0;
            setState(EntityState.DISPOSED);
        }
    }

    public void accelerate(double ratio) {
        if (squating) return;
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
        if (topSupported) return;
        if (bottomSupported) {
            context.oggPlayer.apply("jump.ogg");
            speedY = context.marioJumpSpeed;
            lastJumpTimestamp = timestamp;
        } else if (timestamp - lastJumpTimestamp <= 200 && state == EntityState.JUMP) {
            speedY = context.marioJumpSpeed - (timestamp - lastJumpTimestamp) / 2.5;
        }
    }

    public void tinyJump(long timestamp) {
        speedY = context.marioJumpSpeed / 2.0;
        lastJumpTimestamp = timestamp;
    }

    public void trySquat(boolean val) {
        squating = val && (growth == GrowthState.BIG || growth == GrowthState.BULLET);
    }

    public boolean isSquat() {
        return squating;
    }

    private long dieTimestamp = 0;
    private Runnable dieCallback = null;

    public void die() {
        context.bgmPlayer.tryStop();
        context.sePlayer.playOnce(context.midiRes.getResource("death")[0]);
        state = EntityState.DEAD;
        setGrowth(GrowthState.SMALL);
    }

    /**
     * 向实体发送消息，使其设定自身状态为 DEAD 并播放死亡动画（如果有）
     * 死亡动画播放完毕后会回调指定函数，之后将自身设定为 DISPOSED
     * @param timestamp 死亡时的时间戳
     * @param callback 回调函数，可为空
     */
    public void die(long timestamp, Runnable callback) {
        if (dieTimestamp != 0) throw new RuntimeException(String.valueOf(dieTimestamp));
        dieTimestamp = timestamp;
        dieCallback = callback;
        die();
    }

    public void hurt() {
        if (invincibleTimer > 0) return;
        switch (getGrowth()) {
            case SMALL:
                die();
                break;
            case BIG:
                setGrowth(GrowthState.SMALL);
                invincibleTimer = 1000;
                context.oggPlayer.apply("dokan.ogg");
                break;
            case BULLET:
                setGrowth(GrowthState.BIG);
                invincibleTimer = 1000;
                context.oggPlayer.apply("dokan.ogg");
                break;
        }
    }

    @Override public double getHeight() {
        if (squating)
            return super.getHeight() / 2.0;
        return super.getHeight();
    }

    @Override public void setState(EntityState state) {
        this.state = state;
    }

    @Override public EntityState getState() {
        return state;
    }

    @Override public void activate() {
        setState(EntityState.STAND);
    }

    @Override public void dispose() {
        setState(EntityState.DISPOSED);
    }

    @Override public boolean collideUpwards(boolean[] keyArray, IEntity entity) {
        throw new RuntimeException("can't collide itself!");
    }

    @Override public boolean collideDownwards(boolean[] keyArray, IEntity entity) {
        throw new RuntimeException("can't collide itself!");
    }

    @Override public boolean collideLeftwards(boolean[] keyArray, IEntity entity) {
        throw new RuntimeException("can't collide itself!");
    }

    @Override public boolean collideRightwards(boolean[] keyArray, IEntity entity) {
        throw new RuntimeException("can't collide itself!");
    }
}
