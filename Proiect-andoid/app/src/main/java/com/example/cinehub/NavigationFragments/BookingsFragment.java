package com.example.cinehub.NavigationFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.cinehub.API.ServerAPIBuilder;
import com.example.cinehub.Adapters.OnShowBookingItemClickListener;
import com.example.cinehub.Adapters.ShowBookingsAdapter;
import com.example.cinehub.Movie.BookingDTO;
import com.example.cinehub.Movie.GetBookingsDTO;
import com.example.cinehub.Movie.MovieDTO;
import com.example.cinehub.R;
import com.example.cinehub.SharedBetweenFragments;
import com.example.cinehub.databinding.FragmentBookingsBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingsFragment extends Fragment implements OnShowBookingItemClickListener {

    private FragmentBookingsBinding dataBinding;
    private ShowBookingsAdapter adapter;
    private List<BookingDTO> bookings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBookingsFromDB();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookings, container, false);
        initAdapter();
        return  dataBinding.getRoot();
    }
    @Override
    public void onPause() {
        super.onPause();
        SharedBetweenFragments.getInstance().setListOfBookings(bookings);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<BookingDTO> list = SharedBetweenFragments.getInstance().getListOfBookings();
        adapter.submitList(list);
    }
    private void initAdapter(){
        adapter = new ShowBookingsAdapter(this);
        dataBinding.container.setLayoutManager(new GridLayoutManager(getContext(), 1));
        dataBinding.container.setAdapter(adapter);
    }

    private void getBookingsFromDB() {
        String userId = SharedBetweenFragments.getInstance().getUser().getUid();
        Call<GetBookingsDTO> call = ServerAPIBuilder.getInstance().getBookingsForUser(userId); //just for tests

        call.enqueue(new Callback<GetBookingsDTO>() {
            @Override
            public void onResponse(Call<GetBookingsDTO> call, Response<GetBookingsDTO> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Response Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.body().getBookingsList().size() == 0) {
                    Toast.makeText(getContext(), "You don't have any bookings", Toast.LENGTH_LONG).show();
                }
                bookings = response.body().getBookingsList();
                adapter.submitList(bookings);
            }

            @Override
            public void onFailure(Call<GetBookingsDTO> call, Throwable t) {
                Log.v("ServerReqFailed", t.getMessage());
            }
        });
    }
    @Override
    public void onItemClick(BookingDTO booking) {
        SharedBetweenFragments.getInstance().setBookingToBeDisplayed(booking);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.displayBookingFragment);
    }
}
