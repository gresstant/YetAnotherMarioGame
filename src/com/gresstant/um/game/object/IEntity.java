package com.gresstant.um.game.object;

import com.sun.istack.internal.Nullable;

import java.awt.image.BufferedImage;

public interface IEntity {
    double getLeft();
    double getVertCenter();
    double getRight();
    double getTop();
    double getHorzCenter();
    double getBottom();
    void setLeft(double val);
    void setVertCenter(double val);
    void setRight(double val);
    void setTop(double val);
    void setHorzCenter(double val);
    void setBottom(double val);
    double getWidth();
    double getHeight();
    EntityState getState();
    void setState(EntityState state);
    void activate();
    void dispose();
    void die(long timestamp, @Nullable Runnable callback);

    /**
     * 在检测到玩家从下方向上方碰撞时被调用
     * @return 返回值指示是否阻挡玩家前进
     */
    boolean collideUpwards(boolean[] keyArray, Mario player);
    /**
     * 在检测到玩家从上方向下方碰撞时被调用
     * @return 返回值指示是否阻挡玩家前进
     */
    boolean collideDownwards(boolean[] keyArray, Mario player);
    /**
     * 在检测到玩家从右方向左方碰撞时被调用
     * @return 返回值指示是否阻挡玩家前进
     */
    boolean collideLeftwards(boolean[] keyArray, Mario player);
    /**
     * 在检测到玩家从左方向右方碰撞时被调用
     * @return 返回值指示是否阻挡玩家前进
     */
    boolean collideRightwards(boolean[] keyArray, Mario player);

    BufferedImage getImage();

    /**
     * 绘制图像时，左上角的横坐标为 getLeft() + getImgOffsetX()
     */
    double getImgOffsetX();

    /**
     * 绘制图像时，左上角的纵坐标为 getTop() + getImgOffsetY()
     */
    double getImgOffsetY();

    /**
     * 程序通知实体跳帧
     * 实体状态为 FROZEN 或 DISPOSED 时，或游戏暂停时，此函数不会被调用
     * @param ms
     */
    void tick(int ms);
}
