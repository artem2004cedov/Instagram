package com.example.instagram.SearchFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.instagram.R;
import com.google.android.material.tabs.TabLayout;

public class SearchMainFragment extends Fragment {
    private ViewPager viewPagerSearch;
    private TabLayout tableLayot;
    private FragmentAdapterSearch fragmentAdapterSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_main, container, false);

        viewPagerSearch = view.findViewById(R.id.viewPagerSearch);
        tableLayot = view.findViewById(R.id.tableLayot);

        fragmentAdapterSearch = new FragmentAdapterSearch(getActivity().getSupportFragmentManager());
        viewPagerSearch.setAdapter(fragmentAdapterSearch);
        tableLayot.setupWithViewPager(viewPagerSearch);

//        Toast.makeText(getContext(), String.valueOf(tableLayot.getSelectedTabPosition()), Toast.LENGTH_SHORT).show();

//        Toast.makeText(getContext(), String.valueOf(f.getCurrentItem()), Toast.LENGTH_SHORT).show();

        return view;
    }

}