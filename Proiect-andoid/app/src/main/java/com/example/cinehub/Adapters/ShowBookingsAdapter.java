package com.example.cinehub.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinehub.Movie.BookingDTO;
import com.example.cinehub.Movie.BookingDTO;
import com.example.cinehub.R;
import com.example.cinehub.SharedBetweenFragments;
import com.example.cinehub.databinding.ShowBookingCardBinding;
import com.example.cinehub.databinding.ShowMovieCardBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShowBookingsAdapter extends RecyclerView.Adapter<ShowBookingsAdapter.BookingViewHolder>{

    private List<BookingDTO> localDataSet;
    public static OnShowBookingItemClickListener itemClickListener;
    
    public ShowBookingsAdapter(OnShowBookingItemClickListener listener) {
        itemClickListener = listener;
    }

    public void submitList(List<BookingDTO> dataSet) {
        localDataSet = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShowBookingsAdapter.BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShowBookingsAdapter.BookingViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.show_booking_card,
                parent,
                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ShowBookingsAdapter.BookingViewHolder holder, int position) {
        holder.bind(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        if(localDataSet == null)
            return 0;
        return localDataSet.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {

        private final ShowBookingCardBinding binding;

        public BookingViewHolder(ShowBookingCardBinding binding) { //de schimbat layoutul
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BookingDTO item){
            Picasso.get().load(item.getPoster()).into(binding.image);
            binding.title.setText(item.getMovieTitle());
            TextView movieTitle = binding.title;
//            movieTitle.setTextSize(SharedBetweenFragments.calculateFontSize(item.getMovieTitle()));
            binding.hallNumber.setText(item.getHallNumber());
            binding.runningDate.setText(item.getRunningDate());
            binding.runningTime.setText(item.getRunningTime());
            binding.listOfReserverdSeats.setText(item.getListOfReservedSeats().toString());

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(item);
                }
            });
        }

    }
}
