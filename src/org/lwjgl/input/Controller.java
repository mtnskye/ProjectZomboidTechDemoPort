package org.lwjgl.input;
public interface Controller {
    int getButtonCount(); boolean isButtonPressed(int i); int getAxisCount(); String getAxisName(int i); float getAxisValue(int i);
}
