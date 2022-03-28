package `in`.kit.college_management_system.facultySection.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FilterClassesChip::class], version = 1, exportSchema = false)
abstract class DatabaseClient : RoomDatabase() {

    abstract fun filterClassChipDao(): FilterClassChipDao

    companion object {

        private var mInstance: DatabaseClient? = null

        @Synchronized
        fun getInstance(mContext: Context): DatabaseClient? {
            if (mInstance == null) {
                mInstance =
                    Room.databaseBuilder(
                        mContext.applicationContext,
                        DatabaseClient::class.java,
                        "databaseClient"
                    ).build()
            }
            return mInstance
        }
    }

}