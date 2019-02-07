package rayan.rayanapp.Persistance;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Persistance.database.dao.DevicesDAO;
import rayan.rayanapp.Persistance.database.dao.GroupsDAO;
import rayan.rayanapp.Retrofit.Models.Responses.Group;

@Database(entities = {Device.class, Group.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "RayanDatabase";
    private static AppDatabase appDatabase;

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries().build();
        }
        return appDatabase;
    }

    public abstract DevicesDAO getDeviceDAO();

    public abstract GroupsDAO getGroupDAO();

}
