package com.example.cinehub.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinehub.Movie.MovieDTO;
import com.example.cinehub.R;

import com.example.cinehub.databinding.ShowMovieCardBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShowMoviesAdapter extends RecyclerView.Adapter<ShowMoviesAdapter.MovieViewHolder> {

    private List<MovieDTO> localDataSet;
    public static OnShowMovieItemClickListener itemClickListener;


    public ShowMoviesAdapter(OnShowMovieItemClickListener listener) {
        itemClickListener = listener;
    }

    public void submitList(List<MovieDTO> dataSet) {
        localDataSet = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShowMoviesAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShowMoviesAdapter.MovieViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
//                R.layout.show_movie_card,
                R.layout.show_movie_card,
                parent,
                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ShowMoviesAdapter.MovieViewHolder holder, int position) {
        holder.bind(localDataSet.get(position));
    }
    @Override
    public int getItemCount() {
        if(localDataSet == null)
            return 0;
        return localDataSet.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ShowMovieCardBinding binding;

        public MovieViewHolder(ShowMovieCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MovieDTO item){
            binding.title.setText(item.getTitle());
            TextView movieTitle = binding.title;
//            movieTitle.setTextSize(SharedBetweenFragments.calculateFontSize(item.getTitle()));
            binding.hallNumber.setText(item.getHallNumber());
            binding.runningDate.setText(item.getRunningDate());
            binding.runningTime.setText(item.getRunningTime());
            Picasso.get().load(item.getPoster()).into(binding.image);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(item);
                }
            });
        }
    }

}