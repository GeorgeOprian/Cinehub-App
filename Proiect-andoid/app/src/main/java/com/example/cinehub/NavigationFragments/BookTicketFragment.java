package com.example.cinehub.NavigationFragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cinehub.API.ServerAPIBuilder;
import com.example.cinehub.MainActivity;
import com.example.cinehub.Movie.BookingDTO;
import com.example.cinehub.Movie.MovieDTO;
import com.example.cinehub.Movie.MovieModel;
import com.example.cinehub.R;
import com.example.cinehub.SharedBetweenFragments;
import com.example.cinehub.databinding.FragmentBookTicketBinding;
import com.example.cinehub.databinding.FragmentBookingsBinding;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.*;
import java.util.function.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BookTicketFragment extends Fragment {

    private FragmentBookTicketBinding dataBinding;
    private Button bookTicketButton;
    private EditText listOfSeatsInput;
    private MovieDTO movie;
    private boolean inputFailed;



    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_ticket, container, false);
        movie = SharedBetweenFragments.getInstance().getMovieToPassForDetailsDisplay();

        initLayout();
        return dataBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initLayout() {
        dataBinding.movieTitle.setText(movie.getTitle());
        dataBinding.runningDate.setText(movie.getRunningDate());
        dataBinding.hallNumber.setText(movie.getHallNumber());
        {
            List<Integer> listOfSeats =  movie.getOccupiedSeats();
            Collections.sort(listOfSeats);
            char[] listOfSeatsString = listOfSeats.toString().toCharArray();
            listOfSeatsString[0] = ' ';
            listOfSeatsString[listOfSeats.toString().length() - 1] = ' ';

            dataBinding.listOfSeats.setText(String.valueOf(listOfSeatsString));
        }
        listOfSeatsInput = dataBinding.listOfSeatsInput;
        initBookingsButton();
    }

    private void initBookingsButton() {
        bookTicketButton = dataBinding.bookButton;
        bookTicketButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                inputFailed = false;
                if (listOfSeatsInput.getText().toString().trim().length() > 0) {
                    BookingDTO booking = createBooking();
                    if (booking != null) {
                        sendBookingToServer(booking);

                    }
                 }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private BookingDTO createBooking() {
        BookingDTO bookingDTO = new BookingDTO();
        String userId = SharedBetweenFragments.getInstance().getUser().getUid();
        bookingDTO.setUserId(userId);
        bookingDTO.setRunningId(movie.getRunningId());
        bookingDTO.setHallNumber(movie.getHallNumber());
        List<Integer> listOfSeats = createListOfSeats();
        if (listOfSeats != null) {
//            if (!areTheSeatsReserverd(listOfSeats)) {
                bookingDTO.setListOfReservedSeats(listOfSeats);
//            } else {
//                Toast.makeText(getContext(), "You have to select a seat that was not already reserved", Toast.LENGTH_LONG).show();
//                return null;
//            }
        } else {
            if (!inputFailed) {
                Toast.makeText(getContext(), "You have to select at least a seat", Toast.LENGTH_LONG).show();
            }
            return null;
        }
        return bookingDTO;
    }

    private boolean areTheSeatsReserverd(List<Integer> listOfSeats) {
        List<Integer> reservedSeats = movie.getOccupiedSeats();
        for (Integer seat:listOfSeats) {
            if (reservedSeats.contains(seat)) {
                return true;
            }
        }
        return false;
    }

    private void sendBookingToServer(BookingDTO booking) {
        Call<BookingDTO> call = ServerAPIBuilder.getInstance().addBooking(booking);
        call.enqueue(new Callback<BookingDTO>() {
            @Override
            public void onResponse(Call<BookingDTO> call, Response<BookingDTO> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == SharedBetweenFragments.COULD_NOT_INSERT_IN_DB) {
                        Toast.makeText(getContext(), "The seats that you want to reserve were already booked", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.bookTicketFragment);
                    }
                    else {
                        Toast.makeText(getContext(), "Response Code: " + response.code(), Toast.LENGTH_LONG).show();
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.bookTicketFragment);
                    }
                    return;
                }
                Toast.makeText(getContext(), "Booking was successfully sent to the server.", Toast.LENGTH_LONG).show();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.runningInTheatersFragment);
            }

            @Override
            public void onFailure(Call<BookingDTO> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Integer> createListOfSeats() {
        String inputText = listOfSeatsInput.getText().toString();
        if (containsOnlyDigutsAndSeparator(inputText)) {
            List<Integer> seats =  convertListOfSeatsFromStringToInteger(inputText);
            for (Integer seat: seats) {
                if (seat > SharedBetweenFragments.MAX_NUMBER_OF_SEATS_IN_HALL) {
                    Toast.makeText(getContext(), "Seat number has to be in the interval 1 - " + SharedBetweenFragments.MAX_NUMBER_OF_SEATS_IN_HALL, Toast.LENGTH_LONG).show();
                    inputFailed = true;
                    return null;
                }
            }
            return seats;
        }
        return null;
    }

    private boolean containsOnlyDigutsAndSeparator(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                if (text.charAt(i) != ' ') {
                    Toast.makeText(getContext(), "You have to enter only space separated numbers", Toast.LENGTH_LONG).show();
                    inputFailed = true;
                    return false;
                }
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Integer> convertListOfSeatsFromStringToInteger(String listOfSeatsInput) {
        String[] seatsValues = listOfSeatsInput.split(" ");
        List<String> listOfSeatsString = Arrays.asList(seatsValues);
        List<Integer> listOfSeats = convertStringListToIntList(listOfSeatsString,Integer::parseInt);

        return listOfSeats;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private  <T, U> List<U>  convertStringListToIntList(List<T> listOfString, Function<T, U> function) {
        return listOfString.stream().map(function).collect(Collectors.toList());
    }



}