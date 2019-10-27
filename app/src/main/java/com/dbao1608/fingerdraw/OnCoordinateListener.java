package com.dbao1608.fingerdraw;

public interface OnCoordinateListener {
    void moving(float x, float y);
    void start(float x, float y);
    void end(float x, float y);
}
