package com.gometro.kmpapp.screens.detail

import androidx.lifecycle.ViewModel
import com.gometro.kmpapp.data.MuseumObject
import com.gometro.kmpapp.data.MuseumRepository
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
