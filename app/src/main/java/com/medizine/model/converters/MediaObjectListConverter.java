package com.medizine.model.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medizine.model.MediaObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class MediaObjectListConverter {
    @TypeConverter
    public static List<MediaObject> fromString(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<MediaObject>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String fromList(List<MediaObject> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
