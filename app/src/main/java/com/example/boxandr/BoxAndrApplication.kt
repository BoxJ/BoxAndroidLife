package com.bbox.boxlife

import android.app.Application
import com.bbox.boxlife.data.AppDatabase
import com.bbox.boxlife.data.ExpenseRepository

class BoxAndrApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ExpenseRepository(database.expenseDao()) }
}
