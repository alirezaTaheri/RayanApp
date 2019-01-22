package rayan.rayanapp.Persistance.database.dao;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

public interface BaseDAO<T> {
    @Insert(onConflict = REPLACE)
    void add(T t);

    @Insert(onConflict = REPLACE)
    void addAll(List<T> ts);

}
