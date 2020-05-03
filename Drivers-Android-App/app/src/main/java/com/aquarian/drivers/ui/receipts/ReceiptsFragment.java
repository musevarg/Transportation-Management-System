package com.aquarian.drivers.ui.receipts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aquarian.drivers.R;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class ReceiptsFragment extends Fragment {

    public static ReceiptsFragment newInstance() {
        return new ReceiptsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_receipts, null);

        TabLayout tabLayout = (TabLayout) fragmentView.findViewById(R.id.receiptsTabLayout); // get the reference of TabLayout
        CustomViewPager viewPager = (CustomViewPager) fragmentView.findViewById(R.id.receiptsViewPager);
        viewPager.setPagingEnabled(false);
        TabLayout.Tab firstTab = tabLayout.newTab(); // Create a new Tab names
        firstTab.setText("Today's Receipts"); // set the Text for the first Tab
        tabLayout.addTab(firstTab,true);
        TabLayout.Tab secondTab = tabLayout.newTab(); // Create a new Tab names
        secondTab.setText("Upload Receipt"); // set the Text for the first Tab
        tabLayout.addTab(secondTab,false);

        final TabAdapter adapter = new TabAdapter(this.getContext(), getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==0){ viewPager.setAdapter(adapter);}
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return fragmentView;
    }
}
