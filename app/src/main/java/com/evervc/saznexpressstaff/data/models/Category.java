package com.evervc.saznexpressstaff.data.models;

import java.io.Serializable;
import java.util.Objects;

public class Category implements Serializable {
    private String name;
    private int image;

    public Category(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public Category() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return image == category.image && Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, image);
    }
}
