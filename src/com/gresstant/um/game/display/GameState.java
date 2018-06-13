package com.gresstant.um.game.display;

public enum GameState {
    /**
     * 游戏实例刚刚创建
     */
    CREATED,
    /**
     * 接到指令需要立即退出游戏
     */
    EXITING,
    /**
     * 游戏正在进行
     */
    IN_GAME,
    /**
     * 开始页面
     */
    START_SCREEN,
    /**
     * 设置页面
     */
    CONFIG_SCREEN,
    /**
     * 死太多次导致游戏结束的页面
     */
    GAME_OVER_SCREEN,
    /**
     * 游戏刚刚启动时的 logo 屏
     */
    LOGO_SPLASH,
    /**
     * 游戏刚开始或者死了之后的显示生于命数的那个屏幕
     */
    LIFE_SPLASH,
}
