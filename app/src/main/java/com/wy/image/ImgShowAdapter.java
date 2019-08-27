package com.wy.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wy on 2018/8/8.
 */

public class ImgShowAdapter extends RecyclerView.Adapter<ImgShowAdapter.MyViewHolder> {
    private View view;
    private List<ImageItem> datas;
    private List<File> list = new ArrayList<>();
    private List<Bitmap> bitmap_list = new ArrayList<>();
    private ImgLoader mImagePresenter;

    private OnItemClickListener listener;

    public ImgShowAdapter() {
        mImagePresenter = new PicassoImgLoader();
        datas=new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view=LayoutInflater.from(parent.getContext()).inflate(R.layout.img_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        if (position < bitmap_list.size()) {
            mImagePresenter.onPresentImage(viewHolder.iv_item, list.get(position).getPath(), 365);
//            viewHolder.iv_item.setImageBitmap(bitmap_list.get(position));
            viewHolder.iv_item.setContentDescription(null);
        } else {
            viewHolder.iv_item.setImageResource(R.mipmap.add);
            viewHolder.iv_item.setContentDescription("add");
        }
    }


    @Override
    public int getItemCount() {
        int count = datas == null ? 0 : datas.size() + 1;
        if (count > 9) {
            return datas.size();
        } else {
            return count;
        }
    }

    public void addData(List<ImageItem> list) {
        if (list != null && list.size() > 0) {
            datas.addAll(list);
            changeToBitmap();
        }
    }

    public void clearData() {
        datas.clear();
        list.clear();
        bitmap_list.clear();
        notifyDataSetChanged();
    }

    private void changeToBitmap() {
        Bitmap bitmap = null;
        list = new ArrayList<>();
        for (ImageItem imageItem : datas) {
            list.add(imageItem.getFile());
            bitmap = BitmapFactory.decodeFile(imageItem.getFile().getAbsolutePath());
            bitmap_list.add(bitmap);
        }

//        bitmap_list = ImageUtils.qualityCompress(Bitmap.CompressFormat.JPEG, 50, list, Environment.getExternalStorageDirectory() + "/ww");
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_item;

        public MyViewHolder(View view) {
            super(view);
            iv_item = view.findViewById(R.id.iv_item);
            iv_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v,getPosition());
                }
            });
        }
    }

}
