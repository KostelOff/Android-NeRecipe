package ru.netology.nerecipe.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY indexNumber DESC")
    fun getAll(): LiveData<List<RecipeWithSteps>>

    @Insert
    fun insert(recipe: RecipeEntity)

    @Query(
        "UPDATE recipes SET " +
                "indexNumber = indexNumber + 1 " +
                "WHERE id = :fromId"
    )
    fun updateListItemMoveUpFirst(fromId: Long)

    @Query(
        "UPDATE recipes SET " +
                "indexNumber = indexNumber - 1 " +
                "WHERE id = :toId"
    )
    fun updateListItemMoveUpSecond(toId: Long)

    @Transaction
    fun updateItemMoveUp(fromId: Long, toId: Long) {
        updateListItemMoveUpFirst(fromId)
        updateListItemMoveUpSecond(toId)
    }

    @Query(
        "UPDATE recipes SET " +
                "indexNumber = indexNumber - 1 " +
                "WHERE id = :fromId"
    )
    fun updateListItemMoveDownFirst(fromId: Long)

    @Query(
        "UPDATE recipes SET " +
                "indexNumber = indexNumber + 1 " +
                "WHERE id = :toId"
    )
    fun updateListItemMoveDownSecond(toId: Long)

    @Transaction
    fun updateItemMoveDown(fromId: Long, toId: Long) {
        updateListItemMoveDownFirst(fromId)
        updateListItemMoveDownSecond(toId)
    }

    @Insert
    fun insertStep(step: StepEntity)

    @Query(
        "UPDATE recipes SET " +
                "author = :author, " +
                "title = :title, " +
                "category = :category " +
                "WHERE id = :id"
    )
    fun update(
        id: Long,
        author: String,
        title: String,
        category: String
    )

    @Query(
        "UPDATE steps SET " +
                "stepText = :stepText, " +
                "picture = :picture, " +
                "idRecipe = :idRecipe " +
                "WHERE idStep = :idStep"
    )
    fun updateStep(
        idStep: Long,
        idRecipe: Long,
        stepText: String,
        picture: String
    )

    @Query(
        """
        UPDATE recipes SET
        isFavorite = CASE WHEN isFavorite THEN 0 ELSE 1 END
        WHERE id = :id
    """
    )
    fun addFavorite(id: Long)

    @Query("DELETE FROM recipes WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM steps WHERE idStep = :idStep")
    fun deleteStep(idStep: Long)

    @Query("SELECT MAX(indexNumber) FROM recipes")
    fun getMaxNumber(): Int
}