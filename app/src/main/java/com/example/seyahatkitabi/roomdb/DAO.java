package com.example.seyahatkitabi.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.seyahatkitabi.model.Place;

import java.util.List;


@Dao
interface DAO {

    @Query("SELECT * FROM Place")
    fun getAll(): List<Place>

    @Insert
    fun insert(place : Place)

        @Delete
    fun delete(place : Place)

}
