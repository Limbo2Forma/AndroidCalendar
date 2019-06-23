package com.e.mad1.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.e.mad1.adapter.MoviesListAdapter;
import com.e.mad1.database.Entity.MovieEntity;
import com.e.mad1.R;
import com.e.mad1.viewModel.MoviesListViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MoviesFragment extends Fragment {
    private Context context;
    private MoviesListAdapter adapter;
    private MoviesListViewModel moviesListViewModel;
    private RecyclerView movieList;

    private void subscribeUi(LiveData<List<MovieEntity>> liveData) {
        // update the list when the data changes
        liveData.observe(this, movies -> adapter.setMovieList(movies));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        adapter = new MoviesListAdapter(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moviesListViewModel = ViewModelProviders.of(this).get(MoviesListViewModel.class);
        subscribeUi(moviesListViewModel.getMovies());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movie_fragment, container, false);

        Spinner sort = v.findViewById(R.id.spinnerMovie);
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if (position == 0){
                    subscribeUi(moviesListViewModel.getMovies());
                    movieList.smoothScrollToPosition(0);
                }
                if (position == 1){
                    subscribeUi(moviesListViewModel.getMoviesNew());
                    movieList.smoothScrollToPosition(0);
                }
                if (position == 2){
                    subscribeUi(moviesListViewModel.getMoviesOld());
                    movieList.smoothScrollToPosition(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        movieList = v.findViewById(R.id.movieView);
        // set recyclerView to be horizontal scroll
        movieList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        movieList.setAdapter(adapter);
        // makes recyclerView behave like viewPager
        new PagerSnapHelper().attachToRecyclerView(movieList);

        return v;
    }
}
