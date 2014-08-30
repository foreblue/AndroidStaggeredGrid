package com.etsy.android.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;

public class StaggeredGridEmptyViewActivity extends Activity implements AbsListView.OnItemClickListener {

    public static final String SAVED_DATA_KEY = "SAVED_DATA";
    private static final int FETCH_DATA_TASK_DURATION = 1000;

    private StaggeredGridView mGridView;
    private SampleAdapter mAdapter;
	private View header;

    private ArrayList<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgv_empy_view);

        setTitle("SGV");
        mGridView = (StaggeredGridView) findViewById(R.id.grid_view);

        LayoutInflater layoutInflater = getLayoutInflater();

        header = layoutInflater.inflate(R.layout.list_item_header_footer, null);
        View footer = layoutInflater.inflate(R.layout.list_item_header_footer, null);
        TextView txtHeaderTitle = (TextView) header.findViewById(R.id.txt_title);
        TextView txtFooterTitle =  (TextView) footer.findViewById(R.id.txt_title);
        txtHeaderTitle.setText("THE HEADER!");
        txtFooterTitle.setText("THE FOOTER!");

        mGridView.addHeaderView(header);
        mGridView.addFooterView(footer);
        mGridView.setEmptyView(findViewById(android.R.id.empty));
        mAdapter = new SampleAdapter(this, R.id.txt_line1);

        // do we have saved data?
        if (savedInstanceState != null) {
            mData = savedInstanceState.getStringArrayList(SAVED_DATA_KEY);
            fillAdapter();
        }

        if (mData == null) {
            mData = SampleData.generateSampleData();
        }

        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        fetchData();


    }

    private void fillAdapter() {
        for (String data : mData) {
            mAdapter.add(data);
        }
    }

    private void fetchData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(FETCH_DATA_TASK_DURATION);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                fillAdapter();
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sgv_empty_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.reload:
				mAdapter.setItemAddListener(new SampleAdapter.ItemAddListener() {
					@Override
					public void onItemAdded() {
						if (mGridView.getDistanceToTop() != 0) {
							return;
						}
						int dist = header.getHeight();
						mGridView.scrollByManual(dist * -1);
						mAdapter.setItemAddListener(null);
					}
				});

				mGridView.resetToTop();
				mAdapter.clear();
				fetchData();
				break;
			case R.id.scroll:
				mGridView.setStopFling();
				while (mGridView.getDistanceToTop() * -1 != header.getHeight()) {
					if (mGridView.getDistanceToTop() * -1 < header.getHeight()) {
						mGridView.scrollByManual(-1);
					} else {
						mGridView.scrollByManual(1);
					}
				}
				break;
		}
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this, "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(SAVED_DATA_KEY, mData);
    }
}
