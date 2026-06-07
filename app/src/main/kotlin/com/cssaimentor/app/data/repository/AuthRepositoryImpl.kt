package com.cssaimentor.app.data.repository

import com.cssaimentor.app.BuildConfig
import com.cssaimentor.app.data.model.UserDto
import com.cssaimentor.app.domain.model.UserProfile
import com.cssaimentor.app.domain.repository.AuthRepository
import com.cssaimentor.app.utils.AppResult
import com.cssaimentor.app.utils.Constants
import com.cssaimentor.app.utils.awaitTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val _currentUser = MutableStateFlow(auth.currentUser?.let {
        UserProfile(
            uid = it.uid,
            name = it.displayName ?: "CSS Aspirant",
            email = it.email.orEmpty(),
            photoUrl = it.photoUrl?.toString(),
            streak = 7,
            progress = 0.42f
        )
    })

    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            scope.launch {
                _currentUser.value = user?.let {
                    fetchUserProfile(it.uid).getOrNull() ?: UserProfile(
                        uid = it.uid,
                        name = it.displayName ?: "CSS Aspirant",
                        email = it.email.orEmpty(),
                        photoUrl = it.photoUrl?.toString(),
                        streak = 7,
                        progress = 0.42f
                    )
                }
            }
        }
    }

    override suspend fun login(email: String, password: String): AppResult<UserProfile> = withContext(ioDispatcher) {
        runCatching {
            val result = auth.signInWithEmailAndPassword(email, password).awaitTask()
            val firebaseUser = result.user ?: error("Unable to load Firebase user")
            fetchUserProfile(firebaseUser.uid).getOrNull() ?: firebaseUser.toProfile()
        }.fold(
            onSuccess = {
                _currentUser.value = it
                AppResult.Success(it)
            },
            onFailure = {
                if (BuildConfig.ALLOW_DEMO_FALLBACK) {
                    val demo = demoProfile(email)
                    _currentUser.value = demo
                    AppResult.Success(demo)
                } else {
                    AppResult.Error("Login failed. Check credentials and Firebase Auth setup.", it)
                }
            }
        )
    }

    override suspend fun signup(name: String, email: String, password: String): AppResult<UserProfile> = withContext(ioDispatcher) {
        runCatching {
            val result = auth.createUserWithEmailAndPassword(email, password).awaitTask()
            val firebaseUser = result.user ?: error("Unable to create Firebase user")
            val profile = UserProfile(uid = firebaseUser.uid, name = name, email = email, streak = 1, progress = 0.08f)
            firestore.collection(Constants.FIRESTORE_USERS)
                .document(profile.uid)
                .set(profile.toDto())
                .awaitTask()
            profile
        }.fold(
            onSuccess = {
                _currentUser.value = it
                AppResult.Success(it)
            },
            onFailure = {
                if (BuildConfig.ALLOW_DEMO_FALLBACK) {
                    val demo = demoProfile(email, name)
                    _currentUser.value = demo
                    AppResult.Success(demo)
                } else {
                    AppResult.Error("Signup failed. Check Firebase Auth and Firestore rules.", it)
                }
            }
        )
    }

    override suspend fun sendPasswordReset(email: String): AppResult<Unit> = withContext(ioDispatcher) {
        runCatching {
            auth.sendPasswordResetEmail(email).awaitTask()
        }.fold(
            onSuccess = { AppResult.Success(Unit) },
            onFailure = { AppResult.Error("Unable to send reset email.", it) }
        )
    }

    override suspend fun signInWithGoogle(idToken: String): AppResult<UserProfile> = withContext(ioDispatcher) {
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).awaitTask()
            val firebaseUser = result.user ?: error("Unable to sign in with Google")
            val profile = firebaseUser.toProfile()
            firestore.collection(Constants.FIRESTORE_USERS)
                .document(profile.uid)
                .set(profile.toDto())
                .awaitTask()
            profile
        }.fold(
            onSuccess = {
                _currentUser.value = it
                AppResult.Success(it)
            },
            onFailure = { AppResult.Error("Google sign-in needs a real Firebase OAuth client.", it) }
        )
    }

    override suspend fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    private suspend fun fetchUserProfile(uid: String): Result<UserProfile> = runCatching {
        val snap = firestore.collection(Constants.FIRESTORE_USERS).document(uid).get().awaitTask()
        snap.toObject(UserDto::class.java)?.toDomain() ?: error("Profile not found")
    }

    private fun com.google.firebase.auth.FirebaseUser.toProfile() = UserProfile(
        uid = uid,
        name = displayName ?: "CSS Aspirant",
        email = email.orEmpty(),
        photoUrl = photoUrl?.toString(),
        streak = 7,
        progress = 0.42f,
        completedQuizzes = 3,
        totalStudyMinutes = 480
    )

    private fun UserProfile.toDto() = UserDto(
        uid = uid,
        name = name,
        email = email,
        photoUrl = photoUrl,
        streak = streak,
        progress = progress,
        completedQuizzes = completedQuizzes,
        totalStudyMinutes = totalStudyMinutes
    )

    private fun demoProfile(email: String, name: String = "CSS Aspirant") = UserProfile(
        uid = "demo-user",
        name = name.ifBlank { email.substringBefore("@").replaceFirstChar { it.uppercase() } },
        email = email,
        streak = 7,
        progress = 0.42f,
        completedQuizzes = 3,
        totalStudyMinutes = 480
    )
}
