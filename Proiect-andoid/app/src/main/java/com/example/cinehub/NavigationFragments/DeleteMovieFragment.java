package com.example.cinehub.NavigationFragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cinehub.API.OMDBAPIBuilder;
import com.example.cinehub.API.ServerAPIBuilder;
import com.example.cinehub.Adapters.OnShowMovieItemClickListener;
import com.example.cinehub.Adapters.ShowMoviesAdapter;
import com.example.cinehub.Movie.GetMoviesDTO;
import com.example.cinehub.Movie.MovieDTO;
import com.example.cinehub.R;
import com.example.cinehub.SearchMovieAction.SearchResults;
import com.example.cinehub.SharedBetweenFragments;
import com.example.cinehub.databinding.FragmentDeleteMovieBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteMovieFragment extends Fragment implements OnShowMovieItemClickListener {

    private FragmentDeleteMovieBinding dataBinding;
    private Button searchButton;
    private EditText searchInput;
    private ShowMoviesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_delete_movie, container, false);

        searchInput = dataBinding.searchMovieInput;
        initAdapter();
        initSearchButton();
        return dataBinding.getRoot();

    }

    private void initAdapter(){
        adapter = new ShowMoviesAdapter(this);
        dataBinding.container.setLayoutManager(new GridLayoutManager(getContext(), 1));
        dataBinding.container.setAdapter(adapter);
    }

    private void initSearchButton() {
        searchButton = dataBinding.searchMovieButton;
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movieTitle = searchInput.getText().toString();
                if (movieTitle.equals("")) {
                    showNoMovieTitleEnteredMessage();
                } else {
                    searchMovieByTitle(movieTitle);
                }
            }

        });
    }

    private void showNoMovieTitleEnteredMessage() {
        Toast.makeText(getContext(), getString(R.string.errors_executing_query), Toast.LENGTH_LONG).show();
    }

    private void deleteMovieRequest(String imdbId) {
        Call<Void> call = ServerAPIBuilder.getInstance().deleteMovie(imdbId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == SharedBetweenFragments.BOOKINGS_LINKED_TO_MOVIE) {
                        Toast.makeText(getContext(), "You can't delete this movie because it has bookings linked to it.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Response Code: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                if (response.code() == SharedBetweenFragments.RESOURCE_NOT_FOUND) {
                    Toast.makeText(getContext(), "Movie was not found in the database", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getContext(), "Movie was deleted successfully", Toast.LENGTH_LONG).show();
                    List<MovieDTO> emptyList = new ArrayList<>();
                    adapter.submitList(emptyList);
                    searchInput.setText("");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchMovieByTitle(String title) {
        Call<GetMoviesDTO> call = ServerAPIBuilder.getInstance().getMoviesByTitle(title); //just for tests
        call.enqueue(new Callback<GetMoviesDTO>() {
            @Override
            public void onResponse(Call<GetMoviesDTO> call, Response<GetMoviesDTO> response) {
                if (response.code() == 200) {
                    adapter.submitList(response.body().getMoviesList());
                } else if (response.code() == SharedBetweenFragments.RESOURCE_NOT_FOUND){
                    List<MovieDTO> emptyList = new ArrayList<>();
                    adapter.submitList(emptyList);
                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                } else {
                    List<MovieDTO> emptyList = new ArrayList<>();
                    adapter.submitList(emptyList);
                    Toast.makeText(getContext(), "There where some problems with the query", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetMoviesDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Server request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(MovieDTO movie) {
        deleteMovieRequest(movie.getImdbID());
    }
}