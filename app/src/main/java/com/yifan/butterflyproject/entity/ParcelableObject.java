package com.yifan.butterflyproject.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yifan on 17/3/2.
 */

public class ParcelableObject implements Parcelable {

    public String id;

    public ParcelableObject(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
    }

    protected ParcelableObject(Parcel in) {
        this.id = in.readString();
    }

    public static final Creator<ParcelableObject> CREATOR = new Creator<ParcelableObject>() {
        @Override
        public ParcelableObject createFromParcel(Parcel source) {
            return new ParcelableObject(source);
        }

        @Override
        public ParcelableObject[] newArray(int size) {
            return new ParcelableObject[size];
        }
    };
}
