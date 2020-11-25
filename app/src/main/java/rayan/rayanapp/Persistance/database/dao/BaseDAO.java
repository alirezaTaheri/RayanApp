package rayan.rayanapp.Persistance.database.dao;

import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

public interface BaseDAO<T> {
    @Insert(onConflict = REPLACE)
    void add(T t);

    @Insert(onConflict = REPLACE)
    void addAll(List<T> ts);

}
