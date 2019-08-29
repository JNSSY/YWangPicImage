package com.wy.image;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

public class ImageGridFragment extends Fragment implements OnImagesLoadedListener {

    private GridView mGridView;
    private ImageGridAdapter mAdapter;
    private Context context;
    private AndroidImagePicker androidImagePicker;
    private int imageGridSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_gride, null);
        mGridView = view.findViewById(R.id.gridview);

        context = getActivity();
        DataSource dataSource = new LocalDataSource(context);
        dataSource.provideMediaItems(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidImagePicker = AndroidImagePicker.getInstance();
        imageGridSize = (getActivity().getWindowManager().getDefaultDisplay().getWidth() - Util.dp2px(getActivity(), 2) * 2) / 3;
    }


    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {

        mAdapter = new ImageGridAdapter(context, imageSetList.get(0).imageItems, this, imageGridSize);
        mGridView.setAdapter(mAdapter);
    }
}
