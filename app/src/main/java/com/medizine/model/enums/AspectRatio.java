package com.medizine.model.enums;

public enum AspectRatio {
    ONE_BY_ONE((float) 1 / 1, 1280, 1280),
    ONE_BY_TWO((float) 1 / 2, 720, 1440),
    TWO_BY_ONE((float) 2 / 1, 1440, 720),
    THREE_BY_FOUR((float) 3 / 4, 780, 1040),
    FOUR_BY_THREE((float) 4 / 3, 1040, 780),
    NINE_BY_SIXTEEN((float) 9 / 16, 720, 1280),
    SIXTEEN_BY_NINE((float) 16 / 9, 1280, 720);

    float aspectRatio;
    int width;
    int height;

    AspectRatio(float aspectRatio, int width, int height) {
        this.aspectRatio = aspectRatio;
        this.width = width;
        this.height = height;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
