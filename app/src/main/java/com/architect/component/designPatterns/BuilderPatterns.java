package com.architect.component.designPatterns;

/**
 * 设计模式设计之初是出于什么目的,个人理解
 * 1.为了代码的简洁性
 * 2.为了代码的可读性
 * <p>
 * 构建者模式
 */
public class BuilderPatterns {

    private void main() {
        //获取一个house对象,定义长宽, 下面两种方式都能获取,
        House house = new House(50, 50);
        House buildHouse = new House().addWidth(50).addHeight(50);

        //如果这个时候需要两个house对象，一个要宽高，一个要宽和颜色的，通过new的方法就无法解决
        //构造者模式在这里解决了这个问题，同时因为addWidth()的方式更有利于代码的可读性

        //需要获取 黄颜色的house里的狗的对象
        //虽然我也可以通过new Dog()的方式来获取一个对象，甚至还可以通过setHouse()的方式来绑定dog跟house的关系
        //但我实际上并不想对外暴露Dog()这个对象，我只希望使用者知道有House()这么一个对象
        Dog dog = new Dog();
        Dog buildDog = new House().addColor(11).build();


        //总结一下使用场景
        //1.目标对象有多个属性，而且多个属性可以随意组合
        //2.目标对象内关联了其他对象，而关联的这个对象不希望对外暴露
    }


    public static class House {
        public int width;
        public int color;
        public int height;
        public Dog dog;

        public House() {
            dog = new Dog();
        }

        public House(int width, int height) {
            this.width = width;
            this.height = height;
            dog = new Dog();
        }

        public House addWidth(int width) {
            this.width = width;
            return this;
        }

        public House addHeight(int height) {
            this.height = height;
            return this;
        }

        public House addColor(int color) {
            this.color = color;
            return this;
        }

        public Dog build() {
            return dog.setHostHouse(this);
        }
    }

    public static class Dog {
        public House house;

        public Dog setHostHouse(House house) {
            this.house = house;
            return this;
        }

        public int getHouseWidth() {
            return house.width;
        }
    }

}
