package com.example.cristian.myapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristian.myapp.Fragment1.OnFragmentInteractionListener;
import com.example.cristian.myapp.models.MovieModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnFragmentInteractionListener {

    private TextView tvJsonText;
    private ListView lvMovies;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create default options which will be used for every
//  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        lvMovies = (ListView) findViewById(R.id.lvMovies);
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading, Please wait...");
//        new JSONTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesDemoList.txt");

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class JSONTask extends AsyncTask<String,String,List<MovieModel>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {

                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream =  connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                String line = "";
                StringBuffer buffer = new StringBuffer();
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONObject jsonObject = new JSONObject(finalJson);
                JSONArray jsonArray = jsonObject.getJSONArray("movies");
                List<MovieModel> modelList =  new ArrayList<>();
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++){// iterando en el ombejoto movie y llenando el modelo
                    JSONObject FinaljsonObject = jsonArray.getJSONObject(i);
                    MovieModel model =  gson.fromJson(FinaljsonObject.toString(),MovieModel.class);
//                    model.setMovie(FinaljsonObject.getString("movie"));
//                    model.setYear(FinaljsonObject.getInt("year"));
//                    model.setRating((float) FinaljsonObject.getDouble("rating"));
//                    model.setDuration(FinaljsonObject.getString("duration"));
//                    model.setDirector(FinaljsonObject.getString("director"));
//
//                    model.setTagline(FinaljsonObject.getString("tagline"));
//                    model.setImage(FinaljsonObject.getString("image"));
//                    model.setStory(FinaljsonObject.getString("story"));
//
//                    List<MovieModel.Cast> castList =  new ArrayList<>();
//                    for (int j =0;j < FinaljsonObject.getJSONArray("cast").length();j++){//Recorriendo el otro array
//                        MovieModel.Cast cast = new MovieModel.Cast();
//                        cast.setName(FinaljsonObject.getJSONArray("cast").getJSONObject(j).getString("name"));
//                        castList.add(cast);
//                    }
//                    model.setCastList(castList);
                    modelList.add(model);
                }

                return modelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){connection.disconnect();}
                try {
                    if(reader != null){reader.close();}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieModel> s) {
            super.onPostExecute(s);
            dialog.dismiss();
            MovieAdapter movieAdapter = new MovieAdapter(getApplicationContext(),R.layout.row,s);
            lvMovies.setAdapter(movieAdapter);

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Boolean aux = false;

        if (id == R.id.nav_camara) {
            // Handle the camera action
            fragment = new Fragment1();
            aux = true;
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        if(aux) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            getSupportActionBar().setTitle(item.getTitle());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Menu de opciones
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.opmRefresh) {
            Toast.makeText(this,"refresh",Toast.LENGTH_LONG).show();
            new JSONTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesData.txt");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MovieAdapter extends ArrayAdapter{

        private List<MovieModel> movieModelList;
        private int resource;

        public MovieAdapter(Context context, int resource, List<MovieModel> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.resource = resource;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(resource,parent,false);
            }

            ImageView ivMovieIcon;
            TextView tvMovie;
            TextView tvTagline;
            TextView tvYear;
            TextView tvDuration;
            TextView tvDirector;
            RatingBar rbMovieRating;
            TextView tvCast;
            TextView tvStory;

            ivMovieIcon = (ImageView) v.findViewById(R.id.ivIcon);
            tvMovie = (TextView) v.findViewById(R.id.tvMovie);
            tvTagline = (TextView) v.findViewById(R.id.tvTagline);
            tvYear = (TextView) v.findViewById(R.id.tvYear);
            tvDuration = (TextView) v.findViewById(R.id.tvDuration);
            tvDirector = (TextView) v.findViewById(R.id.tvDirector);
            rbMovieRating = (RatingBar) v.findViewById(R.id.rbMovie);
            tvCast = (TextView) v.findViewById(R.id.tvCast);
            tvStory = (TextView) v.findViewById(R.id.tvStory);
            ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), ivMovieIcon);
            String nombre = movieModelList.get(position).getMovie();
            tvMovie.setText(nombre);
            tvTagline.setText(movieModelList.get(position).getTagline());
            tvYear.setText("Year: "+ movieModelList.get(position).getYear());
            tvDuration.setText("Duration: "+movieModelList.get(position).getDuration());
            tvDirector.setText("Director: "+movieModelList.get(position).getDirector());

            //Rating bar
            rbMovieRating.setRating(movieModelList.get(position).getRating()/2);

            StringBuffer buffer = new StringBuffer();
            for (MovieModel.Cast cast : movieModelList.get(position).getCastList()){
                buffer.append(cast.getName()+ ", ");
            }
            tvCast.setText(buffer.toString());
            tvStory.setText(movieModelList.get(position).getStory());



            return v;
        }
    }
}
