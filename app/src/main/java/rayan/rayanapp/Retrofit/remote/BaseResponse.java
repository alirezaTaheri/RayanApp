package rayan.rayanapp.Retrofit.remote;

import java.util.List;

public class BaseResponse<T> {
    int count;
    List<T> items;

    public int getCount() {
        return count;
    }
}
