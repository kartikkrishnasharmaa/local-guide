package com.local.guider.repositories

import com.local.guider.entities.Settings
import org.springframework.data.jpa.repository.JpaRepository

interface SettingsRepository :  JpaRepository<Settings, Long> {

}
