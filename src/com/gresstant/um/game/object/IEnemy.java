package com.gresstant.um.game.object;

public interface IEnemy extends IEntity {
    void barrierBottom();
    void barrierTop();
    void barrierLeft();
    void barrierRight();
    double getSpeedX();
    double getSpeedY();
}
