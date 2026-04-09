package org.example.cc.di

import org.example.cc.database.DatabaseDriverFactory
import org.example.cc.database.WalletDatabase
import org.example.cc.domain.WalletRepository
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val sharedModule = module {
    single { get<DatabaseDriverFactory>().createDriver() }
    single { WalletDatabase(get()) }
    single { WalletRepository(get()) }
}

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        config?.invoke(this)
        modules(platformModule(), sharedModule)
    }
}
