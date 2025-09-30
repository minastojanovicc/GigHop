import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gighop.R
import com.example.gighop.repository.FirebaseRepo
import com.example.gighop.screens.LeaderboardScreen
import com.example.gighop.screens.LoginScreen
import com.example.gighop.screens.SettingsScreen
import com.example.gighop.screens.MapScreen
import com.example.gighop.screens.ObjectDetailsScreen
import com.example.gighop.screens.SignUpScreen
import com.example.gighop.screens.TableScreen
import com.example.gighop.viewmodel.AuthViewModel
import com.example.gighop.viewmodel.AuthViewModelFactory
import com.example.gighop.viewmodel.ObjectViewModel
import com.example.gighop.viewmodel.ObjectViewModelFactory
import com.example.gighop.viewmodel.UserViewModel
import com.example.gighop.viewmodel.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainApp(hasLocationPermission: Boolean) {

    val navController = rememberNavController()
    val firebaseRepo = FirebaseRepo()
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(firebaseRepo))
    val objectViewModel: ObjectViewModel = viewModel(factory = ObjectViewModelFactory(firebaseRepo))

    val userViewModel: UserViewModel = viewModel(factory =  UserViewModelFactory(firebaseRepo))

    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null


    NavHost(navController = navController,
        if(isUserLoggedIn) Screen.Map.name else Screen.LogIn.name) {
        composable(Screen.SignUp.name) {
            SignUpScreen(navController, viewModel = authViewModel)
        }
        composable(Screen.LogIn.name) {
            LoginScreen(navController, viewModel = authViewModel)
        }
        composable(Screen.Map.name) {
            MapScreen(navController, objectViewModel,hasLocationPermission = hasLocationPermission)
        }
        composable(Screen.Leaderboard.name) {
            LeaderboardScreen(navController, userViewModel)
        }
        composable(Screen.Table.name){
            TableScreen(navController, objectViewModel)
        }
        composable(Screen.Settings.name) {
            SettingsScreen(navController, authViewModel, userViewModel)
        }
        composable(Screen.Object.name + "/{objectId}") { backStackEntry -> //stanje navigacije koje sadrži informacije o trenutnoj ruti, uključujući argumente prosleđene toj ruti.
            val objectId = backStackEntry.arguments?.getString("objectId") ?: return@composable //kao provera, ako se ne dobije objectID kod se prekida ali ne dolazi do greske
            ObjectDetailsScreen(navController, objectViewModel,userViewModel, objectId)
        }
    }
}

enum class Screen(@StringRes val title: Int)
{
    LogIn(title = R.string.log_in),
    SignUp(title = R.string.sign_up),
    Map(title = R.string.app_name),
    Leaderboard(title = R.string.leaderboard),
    Table(title =  R.string.table),
    Settings(title = R.string.settings),
    Object(title = R.string.object_details)
}


