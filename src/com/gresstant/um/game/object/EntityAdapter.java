package com.gresstant.um.game.object;

import java.awt.image.BufferedImage;

public abstract class EntityAdapter implements IEntity {
    /**
     * 根据对其方式所设定的对齐点
     */
    protected double x, y;
    /**
     * 宽度、高度
     */
    protected double width, height;
    /**
     * 横向对齐方式
     */
    protected HorzAlign horzAlign;
    /**
     * 纵向对齐方式
     */
    protected VertAlign vertAlign;

    @Override public double getLeft() {
        switch (horzAlign) {
            case LEFT:
                return x;
            case RIGHT:
                return x - getWidth();
            case CENTER:
                return x - getWidth() / 2.0;
        }
        throw new RuntimeException();
    }

    @Override public void setLeft(double val) {
        switch (horzAlign) {
            case LEFT:
                x = val;
                break;
            case RIGHT:
                x = val + getWidth();
                break;
            case CENTER:
                x = val + getWidth() / 2.0;
                break;
            default:
                throw new RuntimeException();
        }
    }

    @Override public double getVertCenter() {
        switch (horzAlign) {
            case LEFT:
                return x - getWidth() / 2.0;
            case RIGHT:
                return x + getWidth() / 2.0;
            case CENTER:
                return x;
        }
        throw new RuntimeException();
    }

    @Override public void setVertCenter(double val) {
        switch (horzAlign) {
            case LEFT:
                x = val + getWidth() / 2.0;
                break;
            case RIGHT:
                x = val - getWidth() / 2.0;
                break;
            case CENTER:
                x = val;
                break;
            default:
                throw new RuntimeException();
        }
    }

    @Override public double getRight() {
        switch (horzAlign) {
            case LEFT:
                return x + getWidth();
            case RIGHT:
                return x;
            case CENTER:
                return x + getWidth() / 2.0;
        }
        throw new RuntimeException();
    }

    @Override public void setRight(double val) {
        switch (horzAlign) {
            case LEFT:
                x = val - getWidth();
                break;
            case RIGHT:
                x = val;
                break;
            case CENTER:
                x = val - getWidth() / 2.0;
                break;
            default:
                throw new RuntimeException();
        }
    }

    @Override public double getTop() {
        switch (vertAlign) {
            case TOP:
                return y;
            case BOTTOM:
                return y - getHeight();
            case CENTER:
                return y - getHeight() / 2.0;
        }
        throw new RuntimeException();
    }

    @Override public void setTop(double val) {
        switch (vertAlign) {
            case TOP:
                y = val;
                break;
            case BOTTOM:
                y = val + getHeight();
                break;
            case CENTER:
                y = val + getHeight() / 2.0;
                break;
            default:
                throw new RuntimeException();
        }
    }

    @Override public double getHorzCenter() {
        switch (vertAlign) {
            case TOP:
                return y + getHeight() / 2.0;
            case BOTTOM:
                return y - getHeight() / 2.0;
            case CENTER:
                return y;
        }
        throw new RuntimeException();
    }

    @Override public void setHorzCenter(double val) {
        switch (vertAlign) {
            case TOP:
                y = val - getHeight() / 2.0;
                break;
            case BOTTOM:
                y = val + getHeight() / 2.0;
                break;
            case CENTER:
                y = val;
                break;
            default:
                throw new RuntimeException();
        }
    }

    @Override public double getBottom() {
        switch (vertAlign) {
            case TOP:
                return y + getHeight();
            case BOTTOM:
                return y;
            case CENTER:
                return y + getHeight() / 2.0;
        }
        throw new RuntimeException();
    }

    @Override public void setBottom(double val) {
        switch (vertAlign) {
            case TOP:
                y = val - getHeight();
                break;
            case BOTTOM:
                y = val;
                break;
            case CENTER:
                y = val - getHeight() / 2.0;
                break;
            default:
                throw new RuntimeException();
        }
    }

    @Override public double getWidth() {
        return width;
    }

    @Override public double getHeight() {
        return height;
    }

    protected double imgOffsetAdjustX = 0.0;
    protected double getImgOffsetX(int imgWidth) {
        switch (horzAlign) {
            case LEFT:
                return imgOffsetAdjustX;
            case RIGHT:
                return getWidth() - imgWidth + imgOffsetAdjustX;
            case CENTER:
                return (getWidth() - imgWidth) / 2.0 + imgOffsetAdjustX;
        }
        throw new RuntimeException();
    }

    protected double imgOffsetAdjustY = 0.0;
    protected double getImgOffsetY(int imgHeight) {
        switch (vertAlign) {
            case TOP:
                return imgOffsetAdjustY;
            case BOTTOM:
                return getHeight() - imgHeight + imgOffsetAdjustY;
            case CENTER:
                return (getHeight() - imgHeight) / 2.0 + imgOffsetAdjustY;
        }
        throw new RuntimeException();
    }

    /**
     * 默认实现要求实现 getImage()
     * 强烈建议在子类中重写
     */
    @Override public double getImgOffsetX() {
        return getImgOffsetX(getImage().getWidth());
    }

    /**
     * 默认实现要求实现 getImage()
     * 强烈建议在子类中重写
     */
    @Override public double getImgOffsetY() {
        return getImgOffsetY(getImage().getHeight());
    }
}
