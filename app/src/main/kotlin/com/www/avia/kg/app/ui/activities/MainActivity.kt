package com.www.avia.kg.app.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.seismic.ShakeDetector
import com.travelapp.config.AppTabsList
import com.travelapp.debugmenu.DebugMenu
import com.travelapp.sdk.config.AppTabs
import com.travelapp.sdk.internal.core.config.providers.navigation.NavigationBarItem
import com.travelapp.sdk.internal.ui.base.BaseActivity
import com.travelapp.sdk.internal.ui.utils.BottomBarVisibilityHandler
import com.travelapp.sdk.internal.ui.utils.TabSelector
import com.travelapp.sdk.internal.ui.utils.doOnBackStackChanged
import com.travelapp.sdk.internal.ui.utils.dp
import com.travelapp.sdk.internal.ui.utils.setupWithNavController
import com.www.avia.kg.app.BuildConfig
import com.www.avia.kg.app.R
import com.www.avia.kg.app.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import timber.log.Timber
import java.net.URLEncoder


class MainActivity : BaseActivity(R.layout.activity_main), BottomBarVisibilityHandler, TabSelector {

    val phoneNumber = "996777505707"

    private var doubleBackToExitPressedOnce: Boolean = false
    private val TAG0: String = "@@@"

    private val activeTabs by lazy {
        AppTabsList.get().map { tab ->
            NavigationBarItem(
                idRes = tab.idRes,
                destinationRes = tab.graphId,
                iconRes = tab.icon,
                titleRes = tab.title,
            )
        }
    }

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    private var currentNavController: StateFlow<NavController?>? = null

    override val bottomNavViewHeight: Int
        get() = binding.bottomBar.height


    private var shakeDetector: ShakeDetector? = null

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Timber.i("Permission granted = $isGranted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBottomNavigationBar()

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

        initViews()
        initShakeDetector()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        //coreR.mipmap.ta_ic_launcher

    }

    override fun onResume() {
        super.onResume()
        shakeDetector?.start(
            getSystemService(SENSOR_SERVICE) as SensorManager,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onPause() {
        super.onPause()
        shakeDetector?.stop()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    override fun getBottomBarHeight(): Int {
        return binding.bottomBar.height - binding.bottomBar.paddingBottom
    }

    override fun toggleBottomBar(visible: Boolean) = with(binding) {
        if (bottomBar.isVisible && visible) return
        if (!bottomBar.isVisible && !visible) return

        if (visible) {
            bottomBar.translationY = bottomBar.height.toFloat()
            bottomBar
                .animate()
                .translationYBy(-bottomBar.height.toFloat())
                .setDuration(150)
                .withStartAction {
                    bottomBar.isVisible = true
                    navHostContainer.setPadding(0, 0, 0, 80.dp.toInt())
                }
                .start()
        } else {
            bottomBar
                .animate()
                .translationY(bottomBar.height.toFloat())
                .setDuration(150)
                .withStartAction {
                    navHostContainer.setPadding(0, 0, 0, 0)
                }
                .withEndAction {
                    bottomBar.isVisible = false
                }
                .start()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun initViews() = with(binding) {

    }

    private fun initShakeDetector() {
        if (BuildConfig.DEBUG) {
            shakeDetector = ShakeDetector {
                Timber.w("shake detected")

                openDebugFragment()
            }
        }
    }

    private fun initBottomNavigationBar() = with(binding) {
        activeTabs
            .forEachIndexed { index, item ->
                bottomBar.menu.add(
                    Menu.NONE,
                    item.idRes,
                    index,
                    item.titleRes
                ).apply {
                    this.setIcon(item.iconRes)
                }
            }
    }

    private lateinit var navController: NavController

    private fun setupBottomNavigationBar() = with(binding) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostContainer) as NavHostFragment
        navController = navHostFragment.navController


        // Set up BottomNavigationView listener
        bottomBar.setOnItemSelectedListener { item ->
            val tab = activeTabs.find { it.idRes == item.itemId }
            tab?.let {
                when (item.itemId) {
                    com.travelapp.sdk.R.id.ta_other1 -> {

                        // Handle other tabs or navigation
                        val graph = navController.navInflater.inflate(AppTabs.Info.graphId)

                        // Set the graph dynamically
                        navController.graph = graph
                        navController.navigate(com.travelapp.sdk.R.id.toFavoritesFragment)

                        true
                    }

                    com.travelapp.sdk.R.id.ta_other2 -> {
//                        if (isAppInstalled(ctx, "com.whatsapp.w4b")) {
//                            appPackage = "com.whatsapp.w4b";
//                            //do ...
//                        } else if (isAppInstalled(ctx, "com.whatsapp")) {
//                            appPackage = "com.whatsapp";
//                            //do ...
//                        } else {
//                            Toast.makeText(ctx, "whatsApp is not installed", Toast.LENGTH_LONG).show();
//                        }
                        val intent = Intent(Intent.ACTION_VIEW).apply {

                            val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=" + URLEncoder.encode(
                                    "",
                                    "UTF-8"
                                )
                            setPackage("com.whatsapp")
                            setData(url.toUri())
                        }

                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = "https://wa.me/$phoneNumber".toUri()
                            }
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "WhatsApp не установлен",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        false
                    }
//                    com.travelapp.sdk.R.id.ta_info -> {
//                        // Handle other tabs or navigation
//                        val graph = navController.navInflater.inflate(R.navigation.ta_profile)
//
//                        // Clear the back stack
//                        navController.popBackStack(navController.graph.id, false)
//
//                        // Set the graph dynamically
//                        navController.graph = graph
//                        navController.navigate(R.id.infoFragment)
//
//                        true
//                    }
                    else -> {
                        // Handle other tabs or navigation
                        val graph = navController.navInflater.inflate(it.destinationRes)

                        // Clear the back stack
                        navController.popBackStack(navController.graph.id, false)

                        // Set the graph dynamically
                        navController.graph = graph
                        true
                    }
                }
            } ?: false
        }

        // Optionally set the default graph
        val defaultTab = activeTabs.first()
        navController.setGraph(defaultTab.destinationRes)
    }

    fun makeToaster(res: Int) {
        Snackbar.make(
            this@MainActivity, findViewById(android.R.id.content),
            getString(res), Snackbar.LENGTH_LONG
        ).show()
    }

//    private fun setupBottomNavigationBar() = with(binding) {
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostContainer) as NavHostFragment
//        navController = navHostFragment.navController
//
//        bottomBar.setOnItemSelectedListener { item ->
//            val tab = activeTabs.find { it.idRes == item.itemId }
//            tab?.let {
//                when (it.idRes) {
//                    com.travelapp.sdk.R.id.ta_other1, com.travelapp.sdk.R.id.ta_other2 -> {
//                        // Show a toast for tabs 2 and 3
//                        Toast.makeText(this@MainActivity,
//                            ""+it.destinationRes, Toast.LENGTH_SHORT).show()
//                        true
//                    }
//                    else -> {
//                        // For other tabs, perform navigation
//                        val graph = navController.navInflater.inflate(it.destinationRes)
//
//                        // Clear the back stack
//                        navController.popBackStack(navController.graph.id, false)
//
//                        // Set the graph dynamically
//                        navController.graph = graph
//                        true
//                    }
//                }
//            } ?: false
//        }
//
//        // Optionally set the default graph
//        val defaultTab = activeTabs.first()
//        navController.setGraph(defaultTab.destinationRes)
//    }


    //    private fun setupBottomNavigationBar() = with(binding) {
//        (supportFragmentManager.findFragmentById(R.id.navHostContainer) as NavHostFragment).also {
//            navController = it.navController
//        }
//        //navController.addNavigator(FragmentNavigator(context, supportFragmentManager, R.id.navHostContainer))
////        for (tab in activeTabs) {
////            //navController.setGraph(com.travelapp.sdk.R.navigation.ta_flights)
////            navController.setGraph(tab.destinationRes)
////        }
//        navController.setGraph(activeTabs.get(0).destinationRes)
//
//
//        bottomBar.setupWithNavController(navController)
//
//        bottomBar.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                com.travelapp.sdk.R.id.ta_other1, com.travelapp.sdk.R.id.ta_other2 -> {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Item ${item.title} clicked",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    // Возвращаем false, чтобы предотвратить навигацию
//                    false
//                }
//
//                else -> {
//                    // Для остальных табов выполните навигацию как обычно
//                    navController.navigate(item.itemId)
//                    true
//                }
//            }
//        }
//        // Присваиваем текущий NavController
//        currentNavController = MutableStateFlow(navController)
//    }

    private fun isAppInstalled(ctx: Context, packageName: String): Boolean {
        val pm: PackageManager = ctx.getPackageManager()
        var app_installed: Boolean
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        }
        return app_installed
    }

    private fun setupBottomNavigationBar00() = with(binding) {

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomBar
            .setupWithNavController(
                tabs = activeTabs,
                fragmentManager = supportFragmentManager,
                containerId = R.id.navHostContainer,
                intent = intent
            )


        //New ver
//        controller.value?.addOnDestinationChangedListener { _, destination, _ ->
//
//            Toast.makeText(this@MainActivity, "Tab ${destination.label} selected", Toast.LENGTH_SHORT).show()
//
//
//            when (destination.id) {
//                com.travelapp.sdk.R.navigation.ta_other1, com.travelapp.sdk.R.navigation.ta_other2 -> {
//                    // Показываем тост вместо навигации для 2 и 3 табов
//                    Toast.makeText(this@MainActivity, "Tab ${destination.label} selected", Toast.LENGTH_SHORT).show()
//                    // Прерываем навигацию
//                    return@addOnDestinationChangedListener
//                }
//            }
//        }

        // Whenever the selected controller changes, setup the action bar.
        currentNavController = controller
    }

    /**
     * Select tab in bottom nav view and navigate to destination inside navController of selected tab
     */
    override fun selectTab(tab: Int, dest: Int, args: Bundle?) {

//        if (dest == com.travelapp.sdk.R.navigation.ta_other1 || dest == com.travelapp.sdk.R.navigation.ta_other2) {
//            // Показываем тост для 2-го и 3-го табов
//
//            // Прерываем выполнение метода для этих табов
//            return
//        }
        //Toast.makeText(this, "Selected tab: $tab $dest", Toast.LENGTH_SHORT).show()

        val selectedNavController = currentNavController ?: return
        selectedNavController.value?.addOnDestinationChangedListener { _, destination, _ ->

            Log.d(TAG0, "Tab ${destination.label} selected")


            when (destination.id) {
                com.travelapp.sdk.R.navigation.ta_other1, com.travelapp.sdk.R.navigation.ta_other2 -> {
                    // Показываем тост вместо навигации для 2 и 3 табов
                    Toast.makeText(
                        this@MainActivity,
                        "Tab ${destination.label} selected",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Прерываем навигацию
                    return@addOnDestinationChangedListener
                }
            }
        }
        selectedNavController
            .drop(1)
            .filterNotNull()
            .take(1)
            .onEach {
                doOnBackStackChanged(supportFragmentManager) {
                    Log.d("@@@@", "selectTab: ")
                    Timber.w("navController: ${it.findDestination(dest)}")
                    it.navigate(dest, args)
                }
            }
            .launchIn(lifecycleScope)

        binding.bottomBar.selectedItemId = tab
    }

    private fun openDebugFragment() {
        DebugMenu.open(supportFragmentManager, R.id.container)
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(count > 0)
        }
        if (count > 0) {
            supportFragmentManager.popBackStack(
                supportFragmentManager.getBackStackEntryAt(0).id,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        } else { //count == 0


//                Dialog
//                new AlertDialog.Builder(this)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle("Leaving this App?")
//                        .setMessage("Are you sure you want to close this application?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
            //super.onBackPressed();


            if (isFirstPage()) {
                if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;

                    // Move the task containing the MActivity to the back of the activity stack, instead of
                    // destroying it. Therefore, MActivity will be shown when the user switches back to the app.

                    moveTaskToBack(true)
                    return
                }

                this.doubleBackToExitPressedOnce = true

                makeToaster(R.string.press_again_to_exit)

                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 1500)
            } else {
                binding.bottomBar.selectedItemId =
                    AppTabs.Flights.idRes // Replace with the actual ID of the first tab

            }
        }
    }

    private fun isFirstPage(): Boolean {
        // Get the currently selected item's ID
        val currentPosition = binding.bottomBar.selectedItemId
        // Compare it with the ID of the first tab (replace with the actual first tab ID)
        return currentPosition == AppTabs.Flights.idRes // Replace 'first_tab_id' with the actual ID of your first tab
    }

}