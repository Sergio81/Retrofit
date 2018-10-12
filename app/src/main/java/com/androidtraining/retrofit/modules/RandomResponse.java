
package com.androidtraining.retrofit.modules;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RandomResponse implements Parcelable
{

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("info")
    @Expose
    private Info info;
    public final static Parcelable.Creator<RandomResponse> CREATOR = new Creator<RandomResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public RandomResponse createFromParcel(Parcel in) {
            return new RandomResponse(in);
        }

        public RandomResponse[] newArray(int size) {
            return (new RandomResponse[size]);
        }

    }
    ;

    protected RandomResponse(Parcel in) {
        in.readList(this.results, (Result.class.getClassLoader()));
        this.info = ((Info) in.readValue((Info.class.getClassLoader())));
    }

    public RandomResponse() {
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(results);
        dest.writeValue(info);
    }

    public int describeContents() {
        return  0;
    }

}
