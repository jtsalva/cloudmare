package dev.jtsalva.cloudmare.api

import android.os.Parcel

inline fun Parcel.readStringOrBlank() = readString() ?: ""

inline fun Parcel.createStringArrayListOrBlank() = createStringArrayList() ?: emptyList<String>()