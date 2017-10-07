package com.example.android.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Zsolt on 10/2/2017.
 */

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    private List<Books> books;
    private Context context;
    private String image = "";

    /**
     * Create a new {@link BooksAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param books   is the fragment_items_list of {@link Books}s to be displayed.
     */
    public BooksAdapter(Context context, List<Books> books) {
        this.context = context;
        this.books = books;
    }

    // Create the ViewHolder class for references
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titleView;
        private TextView authorView;
        private ImageView urlImageView;
        private View parentView;

        // Add a public constructor, instantiate all of the references to the private variables
        public ViewHolder(View view) {
            super(view);
            this.parentView = view;
            this.titleView = (TextView) view.findViewById(R.id.textview_title);
            this.authorView = (TextView) view.findViewById(R.id.textview_author);
            this.urlImageView = (ImageView) view.findViewById(R.id.imageview_image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the {@link Books} object located at this position in list_item layout
        final Books currentBook = books.get(position);

        // Get the title from the current Books object and set this text on the title TextView.
        holder.titleView.setText(currentBook.getTitleBook());

        // Get the author from the current Books object and set this text on the title TextView.
        List<String> authorList = currentBook.getAuthorsBook();
        StringBuilder authors = new StringBuilder();
        if (authorList.isEmpty()){
            authors.append("No author");
        } else {
            authors.append(authorList.get(0));
            for (int i = 1; i < authorList.size(); i++){
                authors.append(", ").append(authorList.get(i));
            }
        }
        holder.authorView.setText(authors);

        // Set Image if available
        image = currentBook.getUrlImage();
        if (image != null || image.length() > 0) {
            Picasso.with(context).load(currentBook.getUrlImage()).into(holder.urlImageView);
        } else {
            Picasso.with(context).load(R.drawable.no_cover_thumb).into(holder.urlImageView);
        }

        // Set Image if available
        Picasso.with(context).load(currentBook.getUrlImage()).into(holder.urlImageView);

//        // Get the image resource URL from the current Books object and set this on the
//        // image ImageView
//        holder.urlImageView.setImageURI(Uri.parse(currentBook.getUrlImage()));

        // Attach an OnClickListener to open a current Book specific URL
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrlBook());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                if (websiteIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(websiteIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.books.size();
    }

    public void setBooksList(List<Books> books){
        this.books = books;
        this.notifyDataSetChanged();
    }
}
