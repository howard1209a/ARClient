package com.narc.arclient.entity;

import com.google.mediapipe.tasks.components.containers.Category;

public class RenderData {
    private Rectangle rectangle;
    private Category category;

    public RenderData(Rectangle rectangle, Category category) {
        this.rectangle = rectangle;
        this.category = category;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Category getCategory() {
        return category;
    }
}
