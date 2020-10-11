package ba.unsa.etf.rma.booksearch.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class Book implements Parcelable {
    private String id = "";
    private VolumeInfo volumeInfo;

    public Book(String id, VolumeInfo volumeInfo) {
        this.id = id;
        this.volumeInfo = volumeInfo;
    }

    protected Book(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readString();
        }
        this.volumeInfo = new Gson().fromJson(in.readString(), VolumeInfo.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }

    public void setVolumeInfo(VolumeInfo volumeInfo) {
        this.volumeInfo = volumeInfo;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(new Gson().toJson(volumeInfo));
    }
}