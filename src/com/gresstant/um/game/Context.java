package com.gresstant.um.game;

import com.gresstant.um.game.display.Resource;

import java.awt.image.BufferedImage;
import java.util.concurrent.Future;

public class Context {
    /**
     * 每帧预期停留时间，target time per frame。
     * 默认50帧/秒
     */
    public final int TARGET_TPF = 1000 / 50;

    /**
     * 重力加速度。
     * 单位为 px/s² (像素每平方秒)
     */
    public final double GRAVITY = 0.5;

    public Runnable exitCallback;

    /**
     * 获取图像资源库的 Future 对象。
     */
    public Future<Resource<BufferedImage>> imgResFuture;

    /**
     * 图像资源库。在游戏加载完毕后，值不应当为 null
     */
    public Resource<BufferedImage> imgRes = null;
}
