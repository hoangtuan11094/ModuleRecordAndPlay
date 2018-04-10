package com.hoangtuan.modulerecordandplay;

/**
 * Created by atbic on 10/4/2018.
 */

public class WaveModel {
    String name;
    int sampling;

    public WaveModel() {
    }

    public WaveModel(String name, int sampling) {
        this.name = name;
        this.sampling = sampling;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSampling() {
        return sampling;
    }

    public void setSampling(int sampling) {
        this.sampling = sampling;
    }
}
