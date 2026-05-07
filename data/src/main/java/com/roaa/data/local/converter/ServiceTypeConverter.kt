package com.roaa.data.local.converter

import androidx.room.TypeConverter
import com.roaa.domain.model.ServiceType


class ServiceTypeConverter {
    @TypeConverter
    fun fromServiceType(type: ServiceType): String = type.name

    @TypeConverter
    fun toServiceType(value: String): ServiceType = ServiceType.valueOf(value)
}