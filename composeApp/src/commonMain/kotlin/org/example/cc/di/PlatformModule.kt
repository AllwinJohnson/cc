package org.example.cc.di

import org.example.cc.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module
