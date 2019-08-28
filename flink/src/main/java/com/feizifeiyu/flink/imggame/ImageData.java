package com.feizifeiyu.flink.imggame;

import java.io.Serializable;

/**
 * @author: 非子非鱼
 * @date: 2019/8/28 下午3:42
 * @description:
 */
public class ImageData implements Serializable {

    private String id;
    private byte[] image;

    public ImageData(String id, byte[] image) {
        this.id = id;
        this.image = image;
    }

    public ImageData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
