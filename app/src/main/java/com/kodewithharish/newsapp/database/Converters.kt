package com.kodewithharish.newsapp.database

import androidx.room.TypeConverter
import com.kodewithharish.newsapp.models.Source

class Converters {


    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}