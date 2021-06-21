package com.example.cinehub.NavigationFragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cinehub.R;
import com.example.cinehub.databinding.FragmentManageMoviesBinding;

public class ManageMoviesFragment extends Fragment {

    private FragmentManageMoviesBinding dataBinding;
    private Button searchMovieButton;
    private Button updateMovieButton;
    private Button deleteMovieButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_movies, container, false);


        initNavigationButtons();

        return dataBinding.getRoot();
    }

    private void initNavigationButtons () {
        searchMovieButton = dataBinding.searchButton;
        searchMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.searchMovieFragment);
            }
        });

        updateMovieButton = dataBinding.updateButton;
        updateMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.updateMovieFragment);
            }
        });

        deleteMovieButton = dataBinding.deleteButton;
        deleteMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.deleteMovieFragment);
            }
        });
    }
}