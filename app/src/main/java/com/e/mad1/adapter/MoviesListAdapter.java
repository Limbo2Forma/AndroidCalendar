package com.e.mad1.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.mad1.database.Entity.MovieEntity;
import com.e.mad1.model.Movie;
import com.e.mad1.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.MoviesViewHolder>{
    // similar to EventListAdapter
    private LayoutInflater layoutInflater;
    private List<MovieEntity> movieList;
    private Context context;

    private pickMovie mvPickListener;
    public interface pickMovie {
        void moviePicked(Movie m);
    }

    public MoviesListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setMovieList(List<MovieEntity> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    // set listener from adapter for fragment to listen
    public void setMoviePicked(pickMovie mvPickListener) {
        this.mvPickListener = mvPickListener;
    }

    @NonNull
    @Override
    public MoviesListAdapter.MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.movie_cell, parent, false);
        return new MoviesListAdapter.MoviesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MoviesListAdapter.MoviesViewHolder holder, int position) {
        if (movieList == null) {
            return;
        }

        final Movie movie = movieList.get(position);
        if (movie != null) {

            Resources res = context.getResources(); // get resource
            // get movie poster by name from drawable folder
            // This cause some performance issue by not using bitmap or whatsoever, i have not
            //  research it yet, needs to optimize.
            // The performance issue is just a little bit slow since load heavy .png, using android default icon works fine.
            int resId = res.getIdentifier(movie.getPoster() , "drawable", context.getPackageName());
            holder.poster.setImageDrawable(res.getDrawable(resId,null));

            holder.movieTitle.setText(movie.getTitle());
            holder.movieYear.setText(Integer.toString(movie.getYear()));

            holder.movieView.setOnClickListener(view -> {   // open edit event on click view
                mvPickListener.moviePicked(movie);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (movieList == null) {
            return 0;
        } else {
            return movieList.size();
        }
    }

    public List<MovieEntity> getList() {
        return movieList;
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {
        private TextView movieTitle;
        private TextView movieYear;
        private ImageView poster;
        private View movieView;

        MoviesViewHolder(View view) {
            super(view);
            poster = view.findViewById(R.id.poster);
            movieTitle = view.findViewById(R.id.movieTitle);
            movieYear = view.findViewById(R.id.movieYear);

            movieView = view;
        }
    }
}
