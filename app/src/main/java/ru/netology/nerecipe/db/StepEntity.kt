package ru.netology.nerecipe.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "steps",
    foreignKeys = [ForeignKey(
        entity = RecipeEntity::class,
        parentColumns = ["id"],
        childColumns = ["idRecipe"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
class StepEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idStep")
    val idStep: Long,
    @ColumnInfo(name = "idRecipe")
    val idRecipe: Long,
    @ColumnInfo(name = "stepText")
    val stepText: String,
    @ColumnInfo(name = "picture")
    val picture: String
)