package com.gresstant.um.game.display;

public interface IResource<T> {
    void setStyle(String style);
    String getStyle();
    T[] getResource(String key);
}
