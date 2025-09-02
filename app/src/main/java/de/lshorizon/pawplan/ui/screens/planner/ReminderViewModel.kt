package de.lshorizon.pawplan.ui.screens.planner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.lshorizon.pawplan.data.repo.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReminderItem(val id: Int, val title: String, val time: String)

class ReminderViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ReminderRepository(app.applicationContext)
    private val _items = MutableStateFlow<List<ReminderItem>>(emptyList())
    val items: StateFlow<List<ReminderItem>> = _items

    init {
        viewModelScope.launch {
            repo.observe().collect { list ->
                _items.value = list.map { ReminderItem(it.id, it.title, it.time) }
            }
        }
    }

    fun add(title: String, time: String) {
        viewModelScope.launch { repo.add(title, time) }
    }

    fun delete(id: Int) {
        viewModelScope.launch { repo.delete(id) }
    }
}

