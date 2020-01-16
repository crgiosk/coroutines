package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application) : AndroidViewModel(application) {

    private var viewModelJod = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJod)
    private var tonight: MutableLiveData<SleepNight?> = MutableLiveData()


    override fun onCleared() {
        super.onCleared()
        viewModelJod.cancel()
    }

    private fun initializeTonight(){
        uiScope.launch {
            tonight.value = getTonightFromDataBase()
        }
    }

    private suspend fun getTonightFromDataBase(): SleepNight? {
        return withContext(Dispatchers.IO){
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli){
                night = null
            }
            night
        }
    }

    init {
        initializeTonight()
    }




}