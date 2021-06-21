package com.example.cinehub.SearchMovieAction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinehub.R;
import com.example.cinehub.databinding.SearchMovieResultCardBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieResultAdapter extends RecyclerView.Adapter<MovieResultAdapter.MovieViewHolder> {

    private List<Search> localDataSet;
    public static OnSearchItemClickListener itemClickListener;


    public MovieResultAdapter(OnSearchItemClickListener listener) {
        itemClickListener = listener;
    }

    public void submitList(List<Search> dataSet) {
        localDataSet = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.search_movie_result_card,
                parent,
                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        if(localDataSet == null)
            return 0;
        return localDataSet.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        private final SearchMovieResultCardBinding binding;

        public MovieViewHolder(SearchMovieResultCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Search item){
            binding.title.setText(item.getTitle());
            binding.year.setText(item.getYear());
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
