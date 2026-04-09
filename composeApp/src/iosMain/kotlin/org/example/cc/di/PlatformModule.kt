package org.example.cc.di

import org.example.cc.database.DatabaseDriverFactory
import org.example.cc.database.IosDatabaseDriverFactory
import org.example.cc.hardware.CardScannerEngine
import org.example.cc.hardware.HardwareSensorEngine
import org.example.cc.hardware.IosCardScannerEngine
import org.example.cc.hardware.IosHardwareSensorEngine
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<DatabaseDriverFactory> { IosDatabaseDriverFactory() }
    single<CardScannerEngine> { IosCardScannerEngine() }
    single<HardwareSensorEngine> { IosHardwareSensorEngine() }
}
