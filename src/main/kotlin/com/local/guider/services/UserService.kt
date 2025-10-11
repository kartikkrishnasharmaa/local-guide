package com.local.guider.services

import com.local.guider.entities.User
import com.local.guider.repositories.UserRepo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepo: UserRepo
) {
    fun findById(id: Long): User? {
        return userRepo.findByIdOrNull(id)
    }

    fun count(): Long {
        return userRepo.count()
    }
    fun findAll(): List<User> {
        return userRepo.findAll()
    }
    fun findByPage(pageable: Pageable): Page<User>? {
        return userRepo.findAll(pageable)
    }
    fun searchUser(searchText: String?, pageable: Pageable): Page<User>? {
        println("searchText>>>>>>>>> $searchText")
        return userRepo.searchUser(if (searchText.isNullOrEmpty()) null else searchText, pageable)
    }
    fun findByPhone(phone: String): User? {
        return userRepo.findByPhone(phone)
    }

    fun findByPId(pId: Long) = userRepo.findByPId(pId)
    fun findByGId(gId: Long) = userRepo.findByGId(gId)

    fun existsByPhone(phone: String): Boolean {
        return userRepo.existsByPhone(phone) ?: false
    }

    fun existsByUsername(userName: String): Boolean {
        return userRepo.existsByUsername(userName) ?: false
    }

    fun existsById(id: Long): Boolean {
        return userRepo.existsById(id) ?: false
    }

    fun save(user: User): User {
        return userRepo.save(user)
    }
    fun deleteUser(userId: Long) {
        return userRepo.deleteById(userId)
    }
}