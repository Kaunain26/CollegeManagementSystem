package `in`.kit.college_management_system.facultySection.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_class_chip")
data class FilterClassesChip(
    @PrimaryKey(autoGenerate = false)
    var key: String,
    var chip: String
) {

    constructor() : this("", "")

}