package net.oschina.gitapp.photoBrowse;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kymjs.core.bitmap.client.BitmapCore;
import com.kymjs.rxvolley.client.HttpCallback;

import net.oschina.gitapp.R;
import net.oschina.gitapp.common.UIHelper;


import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by 火蚁 on 15/4/29.
 */
public class PhotoFragment extends Fragment {

    @InjectView(R.id.image)
    PhotoView image;
    @InjectView(R.id.loading)
    ProgressBar loading;
    private String imageUrl;

    private PhotoViewAttacher attacher;

    public static PhotoFragment newInstance(String imageUrl) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.photo_item, container, false);
        ButterKnife.inject(this, root);
        loadImage();
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            imageUrl = args.getString("imageUrl");
        }
    }

    private void loadImage() {
        if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
            new BitmapCore.Builder().url(imageUrl).view(image).callback(new HttpCallback() {
                @Override
                public void onPreStart() {
                    if (loading != null) {
                        loading.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    if (loading != null) {
                        loading.setVisibility(View.GONE);
                    }
                    UIHelper.toastMessage(getContext(), "加载图片失败");
                }

                @Override
                public void onSuccess(Map<String, String> headers, Bitmap bitmap) {
                    super.onSuccess(headers, bitmap);
                    if (loading != null) {
                        loading.setVisibility(View.GONE);
                    }
                    if (image != null) {
                        attacher = new PhotoViewAttacher(image);
                        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                            @Override
                            public void onPhotoTap(View view, float v, float v2) {
                                getActivity().finish();
                            }
                        });
                        attacher.update();
                    }
                }
            }).doTask();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
