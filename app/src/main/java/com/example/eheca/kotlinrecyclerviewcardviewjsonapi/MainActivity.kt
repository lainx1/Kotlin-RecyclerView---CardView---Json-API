package com.example.eheca.kotlinrecyclerviewcardviewjsonapi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_movie_layout.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    //Tutorial of https://www.youtube.com/watch?v=Cm587yg3BBw

    private lateinit var movieAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieAdapter = MoviesAdapter()
        movies_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        movies_list.adapter = movieAdapter

        val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://raw.githubusercontent.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val apiMovies = retrofit.create(ApiMovies::class.java)

        apiMovies.getMovies()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    movieAdapter.setMovies(it.data)
                },{
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                    Log.e(this.javaClass.simpleName, it.message)
                })

    }

    inner class MoviesAdapter: RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>(){

        private val movies: MutableList<Movie> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MoviesViewHolder(layoutInflater.inflate(R.layout.item_movie_layout, parent, false))

        override fun getItemCount() = movies.size

        override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
            holder.bindModel(movies[position])
        }

        fun setMovies(data: List<Movie>){
            movies.addAll(data)
            notifyDataSetChanged()
        }

        inner class MoviesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val movieTitleText: TextView = itemView.findViewById(R.id.movieTitle)
            val movieGenreText: TextView = itemView.findViewById(R.id.movieGenre)
            val movieYearText: TextView = itemView.findViewById(R.id.movieYear)
            val moviePoster: ImageView = itemView.findViewById(R.id.movieAvatar)

            fun bindModel(movie: Movie){
                movieTitleText.text = movie.title
                movieGenreText.text = movie.genre
                movieYearText.text = movie.year

                Picasso.get().load(movie.poster).into(moviePoster)
            }
        }
    }
}
