package com.pv.m_eager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author p-v
 */
public class Meaning implements Parcelable{

    private String type;
    private String meaning;

    public Meaning(String type, String meaning){
        this.type = type;
        this.meaning = meaning;
    }

    protected Meaning(Parcel in) {
        type = in.readString();
        meaning = in.readString();
    }

    public static final Creator<Meaning> CREATOR = new Creator<Meaning>() {
        @Override
        public Meaning createFromParcel(Parcel in) {
            return new Meaning(in);
        }

        @Override
        public Meaning[] newArray(int size) {
            return new Meaning[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(meaning);
    }

    public String getMeaning(){
        return meaning;
    }
}
