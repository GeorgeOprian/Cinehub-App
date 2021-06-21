package com.example.cinehub.NavigationFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.cinehub.API.OMDBAPIBuilder;
import com.example.cinehub.Movie.MovieModel;
import com.example.cinehub.R;
import com.example.cinehub.SearchMovieAction.MovieResultAdapter;
import com.example.cinehub.SearchMovieAction.OnSearchItemClickListener;
import com.example.cinehub.SearchMovieAction.Search;
import com.example.cinehub.SearchMovieAction.SearchResults;
import com.example.cinehub.SharedBetweenFragments;
import com.example.cinehub.databinding.FragmentSearchMovieBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMovieFragment extends Fragment implements OnSearchItemClickListener {

    private FragmentSearchMovieBinding dataBinding;
    private Button searchButton;
    private EditText searchInput;
    private MovieResultAdapter adapter;

    private SearchResults searchResults;
    private MovieModel movie;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_movie, container, false);

        searchInput = dataBinding.searchMovieInput;
        searchButton = dataBinding.searchMovieButton;
        initAdapter();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movieTitle = searchInput.getText().toString();
                if (movieTitle.equals("")) {
                    showNoMovieTitleEnteredMessage();
                } else {
                    searchMovieByTitle(movieTitle.trim());
                }
            }
        });
        return dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void searchMovieByTitle(String title) {
        Call<SearchResults> call = OMDBAPIBuilder.getInstance().searchMoviesByTitle(OMDBAPIBuilder.API_KEY, title);
        call.enqueue(new Callback<SearchResults>() {
            @Override
            public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                if (response.code() == 200) {
                    if (!response.body().getResponse().equals("False")) {
                        searchResults = response.body();
                        adapter.submitList(searchResults.getSearch());
                    } else {
                        Toast.makeText(getContext(), "Movie not found", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showNoMovieTitleEnteredMessage();
                }
            }

            @Override
            public void onFailure(Call<SearchResults> call, Throwable t) {
                showNoMovieTitleEnteredMessage();
            }
        });
    }

    private void loadMovieByIDMBId(String id) {
        Call<MovieModel> call = OMDBAPIBuilder.getInstance().getMovieByIMDBId(OMDBAPIBuilder.API_KEY, id);
        call.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if (response.code() == 200) {
                    movie = response.body();
                    SharedBetweenFragments.getInstance().setMovieToAddDisplayData(movie);
                    goToRunningDetailsFragment();
                } else  {
                    showNoMovieTitleEnteredMessage();
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                showNoMovieTitleEnteredMessage();
            }
        });
    }
    private void goToRunningDetailsFragment(){
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.runningDetailsFragment);
    }

    @Override
    public void onItemClick(Search search) {
        loadMovieByIDMBId(search.getImdbID());

    }

    private void showNoMovieTitleEnteredMessage() {
        Toast.makeText(getContext(), getString(R.string.errors_executing_query), Toast.LENGTH_LONG).show();
    }

    private void initAdapter(){
        adapter = new MovieResultAdapter(this);
        dataBinding.container.setLayoutManager(new GridLayoutManager(getContext(), 1));
        dataBinding.container.setAdapter(adapter);
    }
}