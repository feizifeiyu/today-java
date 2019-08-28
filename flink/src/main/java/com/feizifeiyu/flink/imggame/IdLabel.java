package com.feizifeiyu.flink.imggame;

import java.io.Serializable;

/**
 * @author: 非子非鱼
 * @date: 2019/8/28 下午3:42
 * @description:
 */
public class IdLabel implements Serializable {

    private String id;
    private String label;

    public IdLabel(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public IdLabel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "IdLabel{" +
                "id='" + id + '\'' +
                ", label=" + label +
                '}';
    }

}
