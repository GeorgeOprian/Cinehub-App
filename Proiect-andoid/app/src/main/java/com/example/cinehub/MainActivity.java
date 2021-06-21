package com.example.cinehub;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;


import com.example.cinehub.NavigationFragments.RunningInTheatersFragment;
import com.example.cinehub.connectivitymanager.NetworkChangeReceiver;
import com.example.cinehub.connectivitymanager.NetworkListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NetworkListener {


    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sharedPreferences;
    private FirebaseUser user;
    private boolean loggedInState;

    private NetworkChangeReceiver receiver;
    private IntentFilter filter;
    private TextView connectivityTextView;
    private boolean wasOffline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLoginData();
        if (loggedInState == SignInActivity.USER_LOGED_OUT || user == null) {
            goToSignInActivity();
        }

        SharedBetweenFragments.getInstance().setUser(user);
        initNavigationComponent();
        decideEditPermisions();
        addUserInfoToMenuHeader(user);

        connectivityTextView = findViewById(R.id.connectivity_text_view);
        initNetworkManager();
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }


    @Override
    protected void onStart() {
        super.onStart();
        initNetworkManager();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void networkChange() {
        if (receiver.isOnline(this)){
            connectivityTextView.setVisibility(View.GONE);
            if (wasOffline) {
                Toast.makeText(this, "Back online", Toast.LENGTH_LONG).show();
                Log.v("Br", "back online");
                wasOffline = false;
            }
        } else {
            wasOffline = true;
            connectivityTextView.setVisibility(View.VISIBLE);
        }
    }



    private void initNetworkManager(){
        receiver = new NetworkChangeReceiver();
        receiver.setupListener(this);
        filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        wasOffline = false;
    }

    private void initLoginData (){
        sharedPreferences = this.getSharedPreferences(SignInActivity.LOGIN_SHARED_PREF, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        loggedInState = sharedPreferences.getBoolean(SignInActivity.LOGIN_STATE, false);
    }

    private void addUserInfoToMenuHeader (FirebaseUser user) {
        View headerView = navigationView.getHeaderView(0);
        ImageView profileImage = headerView.findViewById(R.id.profile_image);
        TextView accountName = headerView.findViewById(R.id.account_name);
        TextView email = headerView.findViewById(R.id.email);

        Picasso.get().load(user.getPhotoUrl()).into(profileImage);
        accountName.setText(user.getDisplayName());
        email.setText(user.getEmail());
    }


    private void initNavigationComponent() {
        drawer = findViewById((R.id.drawer_layout));
        navigationView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void hideManageMoviesButton() {

        Menu menu = navigationView.getMenu();
        MenuItem manageMoviesItem = menu.findItem(R.id.nav_manage_movies);
        manageMoviesItem.setVisible(false);

    }

    private void decideEditPermisions() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("UsersAllowedToEdit");
        Query query = reference.orderByChild("identifier");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean userCanEdit = false;
                    for (DataSnapshot snapshot1: snapshot.getChildren()) {
                        UserAllowedToEdit userToCheck = snapshot1.getValue(UserAllowedToEdit.class);
                        if (userToCheck.getIdentifier().equals(user.getEmail())) {
                            userCanEdit = true;
                            break;
                        }
                    }
                    if (!userCanEdit) {
                        hideManageMoviesButton();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); ///close the menu bar if the drawer is opened
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            Fragment firstChild = currentFragment.getChildFragmentManager().getFragments().get(0);

            if (firstChild.getClass() == RunningInTheatersFragment.class) {
                moveTaskToBack(true);
            }else {
                super.onBackPressed(); //close the activity on back pressed
            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) { //navigare intre fragmente
            case R.id.nav_running_in_theaters:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.runningInTheatersFragment);
                break;
            case R.id.nav_bookings:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.bookingsFragment);
                break;

            case R.id.nav_manage_movies:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.manageMoviesFragment);

                break;
            case R.id.nav_log_out:
                signOut();
                break;


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void signOut(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        saveLogOutState();
                        goToSignInActivity();
                    }
                });
    }

    private void saveLogOutState(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SignInActivity.LOGIN_STATE, SignInActivity.USER_LOGED_OUT);
        editor.apply();
    }
}
