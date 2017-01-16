package suxin.dribble.view.attachment_list;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import suxin.dribble.R;
import suxin.dribble.dribble.Dribbble;
import suxin.dribble.dribble.DribbbleException;
import suxin.dribble.model.Attachment;
import suxin.dribble.model.Comment;
import suxin.dribble.view.base.DribbbleTask;
import suxin.dribble.view.base.InfiniteAdapter;
import suxin.dribble.view.base.SpaceItemDecoration;
import suxin.dribble.view.comment_list.ShotCommentViewAdapter;
import suxin.dribble.view.comment_list.ShotListCommentFragment;

/**
 * Created by suxin on 10/30/16.
 */

public class AttachmentListFragment extends Fragment {

    public static final String SHOTID = "shotid";
    private String shotid;
    ProgressDialog mProgressDialog;

    @BindView(R.id.attachment_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.attachment_swipe_refresh_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private AttachmentListViewAdapter attachmentListViewAdapter;

    private InfiniteAdapter.LoadMoreListener onLoadMore = new InfiniteAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (Dribbble.isLoggedIn()) {
                AsyncTaskCompat.executeParallel(new AttachmentListFragment.LoadShotsTask(false));
            }
        }
    };

    public static AttachmentListFragment newInstance(String shotid) {
        Bundle args = new Bundle();
        args.putString(SHOTID, shotid);
        AttachmentListFragment fragment = new AttachmentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attachments_swipe_recyclerview, container, false);
        ButterKnife.bind(this, view);

        shotid = getArguments().getString(SHOTID);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new AttachmentListFragment.LoadShotsTask(true));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));
        attachmentListViewAdapter = new AttachmentListViewAdapter(this, new ArrayList<Attachment>(), onLoadMore, shotid);
        recyclerView.setAdapter(attachmentListViewAdapter);
    }

    private class LoadShotsTask extends DribbbleTask<Void, Void, List<Attachment>> {

        private boolean refresh;

        private LoadShotsTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Attachment> doJob(Void... params) throws DribbbleException {
            int page = refresh ? 1 : attachmentListViewAdapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            return Dribbble.getAttachments(page, shotid);
        }

        @Override
        protected void onSuccess(List<Attachment> attachments) {
            attachmentListViewAdapter.setShowLoading(attachments.size() >= Dribbble.COUNT_PER_LOAD);

            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                attachmentListViewAdapter.setData(attachments);
            } else {
                swipeRefreshLayout.setEnabled(true);
                attachmentListViewAdapter.append(attachments);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Download Image Tutorial");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            // Close progressdialog
            mProgressDialog.dismiss();
        }
    }

}
