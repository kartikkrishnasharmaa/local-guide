package com.local.guider.repositories

import com.local.guider.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepo : JpaRepository<User, Long> {
    fun findByName(name: String): User?
    fun findByPhone(phone: String): User?

    @Query(
        "SELECT u FROM User u " +
                "WHERE u.pId = :pId"
    )
    fun findByPId(pId: Long): User?

    @Query(
        "SELECT u FROM User u " +
                "WHERE u.gId = :gId"
    )
    fun findByGId(gId: Long): User?

    fun existsByUsername(username: String): Boolean?
    fun existsByPhone(phone: String): Boolean?

    @Query(
        "SELECT u FROM User u " +
                "WHERE (:searchText IS NULL OR " +
                "LOWER(TRIM(u.name)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')) OR " +
                "LOWER(TRIM(u.username)) LIKE LOWER(CONCAT('%', TRIM(:searchText), '%')))"
    )
    fun searchUser(searchText: String? = null, pageable: Pageable): Page<User>?

}