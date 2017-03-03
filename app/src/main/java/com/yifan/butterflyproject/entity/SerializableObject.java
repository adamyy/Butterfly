package com.yifan.butterflyproject.entity;

import java.io.Serializable;

/**
 * Created by yifan on 17/3/2.
 */

public class SerializableObject implements Serializable {

    public String id;

    public SerializableObject(String id) {
        this.id = id;
    }
}
