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

    // Parcelling part
    public ParcelableObject(Parcel in) {
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelableObject createFromParcel(Parcel in) {
            return new ParcelableObject(in);
        }

        public ParcelableObject[] newArray(int size) {
            return new ParcelableObject[size];
        }
    };

}
