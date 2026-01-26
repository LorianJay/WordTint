package com.github.lorenj.wordtint.ui.fragment.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.lorenj.wordtint.R;


/**
 * @author sukidayo
 * @date Saturday, February 04, 2023
 */
public class TriplePartInfoShareFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_triple_part_info_share, container, false);
    }
}