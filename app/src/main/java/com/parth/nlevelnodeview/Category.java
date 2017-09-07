package com.parth.nlevelnodeview;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Category implements Parcelable {

    private String string;
    private List<Category> array;
    private String integer;
    private List<String> key;

    public Category() {
    }

    protected Category(Parcel in) {
        string = in.readString();
        array = in.createTypedArrayList(Category.CREATOR);
        integer = in.readString();
        key = in.createStringArrayList();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public void setString(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setArray(List<Category> array) {
        this.array = array;
    }

    public List<Category> getArray() {
        return array;
    }

    public void setInteger(String integer) {
        this.integer = integer;
    }

    public String getInteger() {
        return integer;
    }

    public void setKey(List<String> key) {
        this.key = key;
    }

    public List<String> getKey() {
        return key;
    }

    public int getItemCount() {
        return array == null ? 0 : array.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(string);
        dest.writeTypedList(array);
        dest.writeString(integer);
        dest.writeStringList(key);
    }
}