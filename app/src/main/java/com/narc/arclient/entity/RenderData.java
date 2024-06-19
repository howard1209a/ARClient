package com.narc.arclient.entity;

import com.google.mediapipe.tasks.components.containers.Category;

public class RenderData {
    private Rectangle rectangle;
    private String category;

    public RenderData(Rectangle rectangle, String category) {
        this.rectangle = rectangle;
        this.category = category;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public String getCategory() {
        return category;
    }
}
