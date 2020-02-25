package com.yanzhenxing.smzdmau.spider;

/**
 * @author Jason Yan
 * @date 20/04/2019
 */
public class Product {

    private String brand;
    private String category;
    private String name;
    private String image;
    private Float price = 0f;
    private Float save = 0f;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getSave() {
        return save;
    }

    public void setSave(Float save) {
        this.save = save;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", save=" + save +
                '}';
    }
}
