package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application) : AndroidViewModel(application) {

    private var viewModelJod = Job()
    //despachador
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJod)

    //entity
    private var tonight: MutableLiveData<SleepNight?> = MutableLiveData()

    private val nights = database.getAllNights()

    val nightString = Transformations.map(nights){
        formatNights(it, application.resources)
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJod.cancel()
    }

    private fun initializeTonight(){
        uiScope.launch {
            Log.i(TAG,"initializeTonight")
            tonight.value = getTonightFromDataBase()
        }
    }

    private suspend fun getTonightFromDataBase(): SleepNight? {
        return withContext(Dispatchers.IO){
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli){
                Log.i(TAG,"getTonightFromDataBase id ${night?.nightId}")
                night = null
            }
            Log.e(TAG,"getTonightFromDataBase id ${night?.nightId} ")
            night
        }
    }

    fun someWorkNeedsToBeOne(){
        uiScope.launch {
            suspendFunction()
        }
    }

    suspend fun suspendFunction(){
        withContext(Dispatchers.IO){
            //longrunningWork()
        }
    }

    init {
        initializeTonight()
    }

    fun onStartTracking(){
        uiScope.launch {
            val newNight = SleepNight()
            Log.i(TAG,"statTracking")
            insert(newNight)
            tonight.value = getTonightFromDataBase()
        }
    }

    private suspend fun insert(night: SleepNight){
        withContext(Dispatchers.IO){
            Log.e(TAG,"insert")
            database.insert(night)
        }
    }

    companion object{
        const val TAG = "SleepTrackerViewModel"
    }

}