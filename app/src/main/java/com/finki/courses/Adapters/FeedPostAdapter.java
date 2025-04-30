package com.finki.courses.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finki.courses.Activities.ActivityHelpers.MainActivityHelper;
import com.finki.courses.Fragments.FragmentFeed;
import com.finki.courses.Helper.Implementations.Toaster;
import com.finki.courses.Model.FeedPost;
import com.finki.courses.R;
import com.finki.courses.Repositories.Implementations.FeedPostRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedPostAdapter extends RecyclerView.Adapter<FeedPostAdapter.MyViewHolder> {

    private FirebaseAuth firebaseAuth;
    private Context context;
    private List<FeedPost> feedPosts;
    private FeedPostRepository feedPostRepository;
    private MainActivityHelper mainActivityHelper;
    private Toaster toaster;
    private String loggedInUserEmail;

    public FeedPostAdapter(Context context, List<FeedPost> feedPosts, FeedPostRepository feedPostRepository, MainActivityHelper mainActivityHelper) {
        this.firebaseAuth = FirebaseAuth.getInstance();

        this.context = context;
        this.feedPosts = feedPosts;
        this.feedPostRepository = feedPostRepository;
        this.mainActivityHelper = mainActivityHelper;

        this.toaster = new Toaster(context);
        this.loggedInUserEmail = firebaseAuth.getCurrentUser().getEmail();
    }

    @NonNull
    @Override
    public FeedPostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.single_feed_post, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        myViewHolder.imageViewDelete.setOnClickListener(v -> {
            FeedPost feedPost = feedPosts.get(myViewHolder.getAdapterPosition());

            if (loggedInUserEmail.equals(feedPost.getEmail())) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                builder.setTitle("Delete post?")
                        .setIcon(R.drawable.ic_close_red)
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                feedPostRepository.delete(feedPost);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                toaster.text("That's what I thought");
                            }
                        })
                        .setCancelable(true)
                        .show();
            } else {
                toaster.text("Authorization not granted");
            }
        });

        myViewHolder.imageViewDownload.setOnClickListener(v -> {
            FeedPost feedPost = feedPosts.get(myViewHolder.getAdapterPosition());
            downloadImage(feedPost);
        });

        return myViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FeedPostAdapter.MyViewHolder holder, int position) {
        FeedPost feedPost = feedPosts.get(position);

        String postEmail = feedPost.getEmail();

        if (postEmail.equals(loggedInUserEmail)) {
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewDelete.setVisibility(View.GONE);
        }

        holder.textViewEmail.setText(feedPost.getEmail());
        holder.textViewTotalLikes.setText(feedPost.totalLikes() + " likes");
        holder.textViewTotalComments.setText(feedPost.totalComments() + " comments");
        Glide.with(context)
                .load(feedPost.getImageUrl())
                .into(holder.touchImageViewPost);
    }

    @Override
    public int getItemCount() {
        return feedPosts.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        protected TextView textViewEmail;
        protected TextView textViewTotalLikes;
        protected TextView textViewTotalComments;
        protected TouchImageView touchImageViewPost;
        protected ImageView imageViewDelete;
        protected ImageView imageViewLike;
        protected ImageView imageViewDownload;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.textViewEmail = itemView.findViewById(R.id.textViewOwnerEmail);
            this.textViewTotalLikes = itemView.findViewById(R.id.textViewLikesFeed);
            this.textViewTotalComments = itemView.findViewById(R.id.textViewCommentsFeed);
            this.touchImageViewPost = itemView.findViewById(R.id.touchImageViewFeedPost);
            this.imageViewDelete = itemView.findViewById(R.id.imageViewIconDeleteFeedPost);
            this.imageViewLike = itemView.findViewById(R.id.imageViewLikeFeed);
            this.imageViewDownload = itemView.findViewById(R.id.imageViewDownload);
        }
    }

    // Make this a service, or with a notification
    public void downloadImage(FeedPost feedPost) {
        toaster.text("Download started");
        new Thread(() -> {

            String imageUrl = feedPost.getImageUrl();

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();
                File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Camera");

                if (!storageDir.exists())
                    storageDir.mkdir();

                String fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(storageDir, fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                fileOutputStream.close();
                inputStream.close();

                Log.d("Tag", "Download successful");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

}






