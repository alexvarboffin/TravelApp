//package com.travel.travelapp.ui.activities;
//
//import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
//
//import android.Manifest;
//import android.hardware.SensorManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.Menu;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.navigation.NavController;
//
//import com.squareup.seismic.ShakeDetector;
//import com.travel.travelapp.BuildConfig;
//import com.travel.travelapp.R;
//import com.travel.travelapp.databinding.ActivityMainBinding;
//import com.travelapp.sdk.internal.core.config.providers.navigation.NavigationBarItem;
//import com.travelapp.sdk.internal.ui.base.BaseActivity;
//import com.travelapp.sdk.internal.ui.utils.BottomBarVisibilityHandler;
//import com.travelapp.sdk.internal.ui.utils.TabSelector;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import kotlinx.coroutines.flow.StateFlow;
//import timber.log.Timber;
//
//public class Ma extends BaseActivity implements BottomBarVisibilityHandler, TabSelector {
//
//    private List<NavigationBarItem> activeTabs;
//    private ActivityMainBinding binding;
//    private StateFlow<NavController> currentNavController = null;
//    private ShakeDetector shakeDetector;
//
//    private ActivityResultLauncher<String> permissionRequestLauncher = registerForActivityResult(
//            new ActivityResultContracts.RequestPermission(),
//            isGranted -> Timber.i("Permission granted = " + isGranted)
//    );
//
////    public MainActivity() {
////        super(R.layout.activity_main);
////    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityMainBinding.bind(findViewById(R.id.container));
//        setContentView(binding.getRoot());
//
//        initBottomNavigationBar();
//
//        if (savedInstanceState == null) {
//            setupBottomNavigationBar();
//        }
//
//        initViews();
//        initShakeDetector();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            permissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (shakeDetector != null) {
//            shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE), SensorManager.SENSOR_DELAY_UI);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (shakeDetector != null) {
//            shakeDetector.stop();
//        }
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        setupBottomNavigationBar();
//    }
//
//    @Override
//    public int getBottomBarHeight() {
//        return binding.bottomBar.getHeight() - binding.bottomBar.getPaddingBottom();
//    }
//
//    @Override
//    public void toggleBottomBar(boolean visible) {
//        if (binding.bottomBar.isVisible() == visible) return;
//
//        if (visible) {
//            binding.bottomBar.setTranslationY(binding.bottomBar.getHeight());
//            binding.bottomBar.animate()
//                    .translationYBy(-binding.bottomBar.getHeight())
//                    .setDuration(150)
//                    .withStartAction(() -> {
//                        binding.bottomBar.setVisible(true);
//                        binding.navHostContainer.setPadding(0, 0, 0, 80);
//                    }).start();
//        } else {
//            binding.bottomBar.animate()
//                    .translationY(binding.bottomBar.getHeight())
//                    .setDuration(150)
//                    .withStartAction(() -> binding.navHostContainer.setPadding(0, 0, 0, 0))
//                    .withEndAction(() -> binding.bottomBar.setVisible(false)).start();
//        }
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        return currentNavController.getValue() != null && currentNavController.getValue().navigateUp();
//    }
//
//    private void initViews() {
//        // Initialize views here if needed
//    }
//
//    private void initShakeDetector() {
//        if (BuildConfig.DEBUG) {
//            shakeDetector = new ShakeDetector(() -> {
//                Timber.w("shake detected");
//                openDebugFragment();
//            });
//        }
//    }
//
//    private void initBottomNavigationBar() {
//        activeTabs = AppTabsList.get().stream()
//                .map(tab -> new NavigationBarItem(tab.idRes, tab.graphId, tab.icon, tab.title))
//                .collect(Collectors.toList());
//
//        for (int index = 0; index < activeTabs.size(); index++) {
//            NavigationBarItem item = activeTabs.get(index);
//            binding.bottomBar.getMenu().add(Menu.NONE, item.idRes, index, item.titleRes)
//                    .setIcon(item.iconRes);
//        }
//    }
//
//    private void setupBottomNavigationBar() {
//        StateFlow<NavController> controller = binding.bottomBar.setupWithNavController(
//                activeTabs, getSupportFragmentManager(), R.id.navHostContainer, getIntent());
//
//        controller.getValue().addOnDestinationChangedListener((controller1, destination, arguments) -> {
//            Toast.makeText(MainActivity.this, "Tab " + destination.getLabel() + " selected", Toast.LENGTH_SHORT).show();
//
//            if (destination.getId() == com.travelapp.sdk.R.navigation.ta_other1 ||
//                    destination.getId() == com.travelapp.sdk.R.navigation.ta_other2) {
//                // Show toast for tab 2 and 3 and stop further navigation
//                return;
//            }
//        });
//
//        currentNavController = controller;
//    }
//
//    @Override
//    public void selectTab(int tab, int dest, Bundle args) {
//        Toast.makeText(this, "Selected tab: " + tab + " " + dest, Toast.LENGTH_SHORT).show();
//
//        StateFlow<NavController> selectedNavController = currentNavController;
//        if (selectedNavController == null) return;
//
//        selectedNavController.getValue()
//                .getBackStack()
//                .drop(1)
//                .filter(Objects::nonNull)
//                .findFirst()
//                .ifPresent(navController -> {
//                    doOnBackStackChanged(getSupportFragmentManager(), () -> {
//                        Timber.w("navController: " + navController.findDestination(dest));
//                        navController.navigate(dest, args);
//                    });
//                });
//
//        binding.bottomBar.setSelectedItemId(tab);
//    }
//
//    private void openDebugFragment() {
//        DebugMenu.open(getSupportFragmentManager(), R.id.container);
//    }
//}
