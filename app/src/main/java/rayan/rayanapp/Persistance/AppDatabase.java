package rayan.rayanapp.Persistance;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Data.LocallyChange;
import rayan.rayanapp.Data.Scenario;
import rayan.rayanapp.Data.UserMembership;
import rayan.rayanapp.Persistance.database.dao.DevicesDAO;
import rayan.rayanapp.Persistance.database.dao.GroupsDAO;
import rayan.rayanapp.Persistance.database.dao.LocallyChangesDAO;
import rayan.rayanapp.Persistance.database.dao.ScenariosDAO;
import rayan.rayanapp.Persistance.database.dao.UserMembershipDAO;
import rayan.rayanapp.Persistance.database.dao.UsersDAO;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Retrofit.Models.Responses.api.User;

@Database(entities = {Device.class, Group.class, User.class,UserMembership.class, LocallyChange.class, Scenario.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "RayanDatabase";
    private static AppDatabase appDatabase;

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return appDatabase;
    }

    public abstract LocallyChangesDAO getLocallyChangesDAO();

    public abstract DevicesDAO getDeviceDAO();

    public abstract GroupsDAO getGroupDAO();

    public abstract ScenariosDAO getScenarioDAO();

    public abstract UsersDAO getUsersDAO();

    public abstract UserMembershipDAO getUserMembershipDAO();
}
