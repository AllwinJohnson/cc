package org.example.cc.di

import org.example.cc.database.AndroidDatabaseDriverFactory
import org.example.cc.database.DatabaseDriverFactory
import org.example.cc.hardware.AndroidCardScannerEngine
import org.example.cc.hardware.AndroidHardwareSensorEngine
import org.example.cc.hardware.CardScannerEngine
import org.example.cc.hardware.HardwareSensorEngine
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(get()) }
    single<CardScannerEngine> { AndroidCardScannerEngine() }
    single<HardwareSensorEngine> { AndroidHardwareSensorEngine(get()) }
}
