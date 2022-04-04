package com.example.instagram.SearchFragment;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class FragmentAdapterSearch extends FragmentPagerAdapter {
    private int pos;

    public FragmentAdapterSearch(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // создаем новые фрагменты по позиции
        switch (position) {
            case 0:
                return new SearchBestFragment();
            case 1:
                pos = position;
                getPosition();
                return new SearchUserFragment();
            case 2:
                return new SearchLabesFragment();
            case 3:
                pos = position;
                return new SearchPlacesFragment();
            default:
                pos = position;
                return new SearchBestFragment();
        }
    }

    public int getPosition() {
        return pos;
    }

    // количетво фрагментов
    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        // изначальное значение
        String title = null;
        if (position == 0) {
            title = "Лучшие";
        }
        if (position == 1) {
            title = "Аккаунты";
        }
        if (position == 2) {
            title = "Метки";
        }

        if (position == 3) {
            title = "Места";
        }

        return title;
    }
}
