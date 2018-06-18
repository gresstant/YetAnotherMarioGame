package com.gresstant.um.game.map;

import com.gresstant.um.game.object.IBlock;
import com.gresstant.um.game.object.IEnemy;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Map_0_0 extends MapBase {
    @Override byte getStructVerMajor() {
        return 0;
    }

    @Override byte getStructVerMinor() {
        return 0;
    }

    Map_0_0() {} // package private

    protected int width;
    public int marioX, marioY;
    public Color bgColor;

    public int winX;

    // IEntity 只有两个子接口
    List<IEnemy> enemies = new LinkedList<>();
    List<IBlock> blocks = new LinkedList<>();

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<IEnemy> getEnemies() {
        return enemies;
    }

    public List<IBlock> getBlocks() {
        return blocks;
    }
}
