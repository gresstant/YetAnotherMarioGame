package com.gresstant.um.game.map;

public abstract class MapBase {
    /**
     * 获得地图架构的主要版本
     * 主要版本不同，则不能相互兼容
     */
    abstract byte getStructVerMajor();

    /**
     * 获得地图架构的次要版本
     * 次要版本较高的继承自次要版本较低的
     */
    abstract byte getStructVerMinor();
}
