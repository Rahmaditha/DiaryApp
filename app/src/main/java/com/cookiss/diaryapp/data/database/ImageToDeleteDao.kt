package com.cookiss.diaryapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cookiss.diaryapp.data.database.entity.ImageToDelete
import com.cookiss.diaryapp.data.database.entity.ImageToUpload

@Dao
interface ImageToDeleteDao {

    @Query("SELECT * FROM images_to_delete ORDER BY id ASC")
    suspend fun getAllImages(): List<ImageToDelete>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImageToDelete(imageToDelete: ImageToDelete)

    @Query("DELETE FROM images_to_delete WHERE id=:imageId")
    suspend fun cleanupImage(imageId: Int)

}