package com.example.gighop.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gighop.repository.FirebaseRepo
import com.example.gighop.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class UserViewModel(private val firebaseRepo: FirebaseRepo) : ViewModel() {

    var userState by mutableStateOf<UserState>(UserState.Idle)
        private set

    var currentUser by mutableStateOf<User?>(null)

    fun getCurrentUser() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            firebaseRepo.getUserById(currentUserId) { user ->
                currentUser = user
            }
        }
    }

    fun updateOwnerPoints(ownerId: String, pointsToAdd: Int) {
        viewModelScope.launch {
            firebaseRepo.updateOwnerPoints(ownerId, pointsToAdd)
        }
    }

    fun fetchAllUsers() {
        userState = UserState.Loading
        firebaseRepo.getAllUsers { users, error ->
            if (error == null) {
                userState = UserState.UsersFetched(users)
            } else {
                userState = UserState.Error(error)
            }
        }
    }

    fun getUserRatingForObject(objectId: String, onResult: (Int?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            firebaseRepo.getUserRatingForObject(objectId, currentUser.uid) { rating ->
                onResult(rating)
            }
        } else {
            onResult(null)
        }
    }
}

// Definisanje stanja za korisničke radnje
sealed class UserState {
    object Idle : UserState()
    object Loading : UserState()
    data class UsersFetched(val users: List<User>) : UserState()
    data class Error(val message: String) : UserState()
}

class UserViewModelFactory (private val firebaseRepo: FirebaseRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(firebaseRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
