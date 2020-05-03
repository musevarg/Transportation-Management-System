package com.aquarian.drivers.ui.receipts;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aquarian.drivers.ui.receipts.receiptsAdd.ReceiptsAdd;
import com.aquarian.drivers.ui.receipts.receiptsList.ReceiptsList;

public class TabAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public TabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ReceiptsList receiptListFragment = new ReceiptsList();
                return receiptListFragment;
            case 1:
                ReceiptsAdd receiptAddFragment = new ReceiptsAdd();
                return receiptAddFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}