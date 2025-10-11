package com.local.guider.repositories

import com.local.guider.entities.Notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface NotificationRepository : JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE (n.photographerId = :photographerId OR n.forAll = true OR n.forPhotographers = true) AND n.createdOn > :userCreatedDate")
    fun findByPhotographerId(photographerId: Long, userCreatedDate: Date?, pageable: Pageable): Page<Notification>

    @Query("SELECT n FROM Notification n WHERE (n.guiderId = :guiderId OR n.forAll = true OR n.forGuiders = true) AND n.createdOn > :userCreatedDate")
    fun findByGuiderId(guiderId: Long, userCreatedDate: Date?, pageable: Pageable): Page<Notification>

    @Query("SELECT n FROM Notification n WHERE (n.userId = :userId OR n.forAll = true OR n.forUsers = true) AND n.createdOn > :userCreatedDate")
    fun findByUserId(userId: Long, userCreatedDate: Date?, pageable: Pageable): Page<Notification>

    @Query("SELECT n FROM Notification n WHERE n.fromAdmin = true")
    fun findAllByAdmin(pageable: Pageable): Page<Notification>

    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.photographerId = :photographerId OR n.forAll = true OR n.forPhotographers = true) AND (n.markAsRead IS NULL OR n.markAsRead != true) AND n.createdOn > :userCreatedDate")
    fun countUnreadByPhotographerId(photographerId: Long, userCreatedDate: Date): Long

    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.guiderId = :guiderId OR n.forAll = true OR n.forGuiders = true) AND (n.markAsRead IS NULL OR n.markAsRead != true) AND n.createdOn > :userCreatedDate")
    fun countUnreadByGuiderId(guiderId: Long, userCreatedDate: Date): Long

    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.userId = :userId OR n.forAll = true OR n.forUsers = true) AND (n.markAsRead IS NULL OR n.markAsRead != true) AND n.createdOn > :userCreatedDate")
    fun countUnreadByUserId(userId: Long, userCreatedDate: Date): Long

}