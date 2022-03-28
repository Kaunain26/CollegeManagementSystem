package `in`.kit.college_management_system.facultySection.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FilterClassChipDao {

    @Insert
    suspend fun insert(filterClassChip: FilterClassesChip)

    @Query("Delete from filter_class_chip")
    suspend fun deleteAll()

    @Query("Delete from filter_class_chip where chip = :chip")
    suspend fun deleteOneChip(chip: String)

    @Query("SELECT * from filter_class_chip")
    fun getAllChip(): List<FilterClassesChip>

}