package ch.hackzurich.migrozept.fragment;

import android.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// TODO: Fragments http://developer.android.com/training/basics/fragments/fragment-ui.html
public class RecipeFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.list_content, container, false);
    }

	
}
