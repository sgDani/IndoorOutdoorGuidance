package com.indooratlas.android.sdk.examples.Fragments;

/**
 * Created by Daniel on 03/05/2016.
 */
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.indooratlas.android.sdk.examples.R;

import com.indooratlas.android.sdk.examples.Adapter.*;


import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements SearchView.OnQueryTextListener {

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public static final String[] MOVIES = new String[]{
            "3Amigos",
            "Aleksi 13",
            "Arnold",
            "Belizia",
            "BikBok",
            "BR lelut",
            "BURGER KING",
            "Caffi",
            "Cubus",
            "DNA",
            "Dressmann",
            "Du Pareil",
            "Elisa",
            "Emotion",
            "Expresso House",
            "Expert",
            "FeelVegas",
            "Fiilinki",
            "Finlayson",
            "Fonum",
            "GameStop",
            "Gelato",
            "Gina Tricot",
            "H&M",
            "Halonen",
            "Hanko Sushi",
            "Hesburger",
            "Iittala",
            "Indiska",
            "Instrumentarium",
            "Jack & Jones",
            "Jesper Junior",
            "Jungle Juice Bar",
            "Kaivokukka",
            "Karkkitori",
            "Kukkakauppa Sitomo",
            "Kung Food Panda",
            "Life",
            "Makuuni",
            "Mango",
            "Martins",
            "Ninja",
            "Nissen",
            "OLearys",
            "Oliva",
            "Pentik",
            "Punainen Rusetti",
            "R-Kioski",
            "Rajala",
            "Ravintola Base",
            "Robert's Coffee",
            "Sonera",
            "Subway",
            "Teknikmagasinet",
            "The Body Shop",
            "Zip"
    };

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private List<ExampleModel> mModels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        return view;

    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mModels = new ArrayList<>();

        for (String movie : MOVIES) {
            mModels.add(new ExampleModel(movie));
        }

        mAdapter = new ExampleAdapter(getActivity(), mModels);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    public List<ExampleModel> filteredModelList=new ArrayList<>();
    @Override
    public boolean onQueryTextChange(String query) {
        filter(mModels, query);
        mAdapter.animateTo(filteredModelList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }



    private void filter(List<ExampleModel> models, String query) {
        query = query.toLowerCase();

        filteredModelList.clear();
        for (ExampleModel model : models) {
            final String text = model.getText().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
                int a=1;
            }
        }
        //return filteredModelList;
    }
}
