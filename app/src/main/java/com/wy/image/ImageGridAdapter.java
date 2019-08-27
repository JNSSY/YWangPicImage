package com.wy.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

@SuppressLint("StringFormatMatches")
public class ImageGridAdapter extends BaseAdapter {

    private static final int ITEM_TYPE_CAMERA = 0;//the first Item may be Camera
    private static final int ITEM_TYPE_NORMAL = 1;
    private List<ImageItem> images;
    private Context context;
    private AndroidImagePicker androidImagePicker;
    private Fragment fragment;
    private int imageGridSize;
    private ImgLoader mImagePresenter;

    public ImageGridAdapter(Context context, List<ImageItem> images, Fragment fragment, int imageGridSize) {
        this.images = images;
        this.context = context;
        this.fragment = fragment;
        androidImagePicker = AndroidImagePicker.getInstance();
        this.imageGridSize = imageGridSize;
        mImagePresenter = new PicassoImgLoader();
    }

    private boolean shouldShowCamera() {
        return androidImagePicker.isShouldShowCamera();
    }

    private boolean shouldSelectMulti() {
        return androidImagePicker.getSelectMode() == AndroidImagePicker.Select_Mode.MODE_MULTI;
    }

    @Override
    public int getCount() {
        return shouldShowCamera() ? images.size() + 1 : images.size();
    }

    @Override
    public ImageItem getItem(int position) {
        if (shouldShowCamera()) {
            if (position == 0) {
                return null;
            }
            return images.get(position - 1);
        } else {
            return images.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (shouldShowCamera()) {
            return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == ITEM_TYPE_CAMERA) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_camera, parent, false);
            convertView.setTag(null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        androidImagePicker.takePicture(fragment, AndroidImagePicker.REQ_CAMERA);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.image_grid_item, null);
                holder = new ViewHolder();
                holder.ivPic = convertView.findViewById(R.id.iv_thumb);
                holder.cbSelected = convertView.findViewById(R.id.iv_thumb_check);
                holder.cbPanel = convertView.findViewById(R.id.thumb_check_panel);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (shouldSelectMulti()) {//Multi Select mode will show a CheckBox at the Top Right corner
                holder.cbSelected.setVisibility(View.VISIBLE);
            } else {
                holder.cbSelected.setVisibility(View.GONE);
            }

            final ImageItem item = getItem(position);

            holder.cbSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (androidImagePicker.getSelectImageCount() > androidImagePicker.getSelectLimit()) {
                        if (holder.cbSelected.isChecked()) {
                            //had better use ImageView instead of CheckBox
                            holder.cbSelected.toggle();//do this because CheckBox will auto toggle when clicking,must inverse
                            String toast = context.getResources().getString(R.string.you_have_a_select_limit, androidImagePicker.getMaxSelect());
                            Toast.makeText(context,toast, Toast.LENGTH_SHORT).show();
                        } else {
                            //
                        }
                    } else {
                        //
                    }
                }
            });

            holder.cbSelected.setOnCheckedChangeListener(null);//first set null or will have a bug when Recycling the view
            if (androidImagePicker.isSelect(position, item)) {
                holder.cbSelected.setChecked(true);
                holder.ivPic.setSelected(true);
            } else {
                holder.cbSelected.setChecked(false);
            }

            ViewGroup.LayoutParams params = holder.ivPic.getLayoutParams();
            params.width = params.height = imageGridSize;

            final View imageItemView = convertView.findViewById(R.id.iv_thumb);
            imageItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mOnItemClickListener.onItemClick(mGridView, imageItemView, position, position);
                }
            });


            holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        androidImagePicker.addSelectedImageItem(position, item);
                    } else {
                        androidImagePicker.deleteSelectedImageItem(position, item);
                    }
                }

            });

            //load the image to ImageView
            mImagePresenter.onPresentImage(holder.ivPic, getItem(position).path, imageGridSize);

        }

        return convertView;
    }

    class ViewHolder {
        ImageView ivPic;
        SuperCheckBox cbSelected;
        View cbPanel;
    }
}
