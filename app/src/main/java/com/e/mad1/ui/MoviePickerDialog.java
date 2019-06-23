package com.e.mad1.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.e.mad1.adapter.MoviesListAdapter;
import com.e.mad1.R;
import com.e.mad1.viewModel.MoviesListViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MoviePickerDialog extends DialogFragment {
    private Context context;
    private MoviesListAdapter adapter;

    public MoviePickerDialog() {} // empty constructor required for DialogFragment

    private passMoviePick movieListener;
    public interface passMoviePick {
        void addedMovie(long movieId, String movieTitle);   // pass the movie id and it title
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        adapter = new MoviesListAdapter(context);
        this.movieListener = (passMoviePick)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoviesListViewModel movieViewModel = ViewModelProviders.of(this).get(MoviesListViewModel.class);
        movieViewModel.getMovies().observe(this, movies -> adapter.setMovieList(movies));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movie_picker_dialog, container, false);

        ImageButton button = v.findViewById(R.id.closePicker);
        button.setOnClickListener(v1 -> MoviePickerDialog.this.dismiss());

        RecyclerView movieList = v.findViewById(R.id.moviePickerList);
        // set movie List to horizontal scroll
        movieList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        movieList.setAdapter(adapter);
        // set movie list to behave like viewPager
        new PagerSnapHelper().attachToRecyclerView(movieList);

        adapter.setMoviePicked(mv -> {
            // picked a movie, save then dismiss dialog
            movieListener.addedMovie(mv.getId(),mv.getTitle());
            MoviePickerDialog.this.dismiss();
        });
        return v;
    }
}

