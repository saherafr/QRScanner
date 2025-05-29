package com.example.droiddesign.view.Adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.ImageItem;
import com.example.droiddesign.view.Admin.BrowseImagesActivity;

/**
 * Adapter class for populating the RecyclerView with images.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

	private final List<ImageItem> imageItemList;
	private Context context;

	/**
	 * Constructor for the ImagesAdapter.
	 *
	 * @param imageItemList List of ImageItem objects to be displayed.
	 */
	public ImagesAdapter(List<ImageItem> imageItemList) {
		this.imageItemList = imageItemList;
	}

	/**
	 * Inflates the layout for each image card.
	 *
	 * @param parent   The parent view group.
	 * @param viewType The type of view to be inflated.
	 * @return A ViewHolder object representing the inflated view.
	 */
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		context = parent.getContext();
		View view = LayoutInflater.from(context).inflate(R.layout.card_image_item, parent, false);
		return new ViewHolder(view);
	}

	/**
	 * Binds the image URL to the ImageView in the ViewHolder.
	 *
	 * @param holder   The ViewHolder object.
	 * @param position The position of the item in the RecyclerView.
	 */
	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		ImageItem imageItem = imageItemList.get(position);
		Glide.with(context)
				.load(imageItem.getImageUrl())
				.into(holder.imageView);

		holder.imageView.setOnClickListener(v -> {
			if (context instanceof BrowseImagesActivity) {
				((BrowseImagesActivity) context).showImageDialog(imageItem);
			}
		});
	}

	/**
	 * Returns the number of items in the RecyclerView.
	 *
	 * @return The number of items in the RecyclerView.
	 */
	@Override
	public int getItemCount() {
		return imageItemList.size();
	}

	/**
	 * ViewHolder class for the ImagesAdapter.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView imageView;

		/**
		 * Constructor for the ViewHolder.
		 *
		 * @param itemView The view to be held by the ViewHolder.
		 */
		ViewHolder(View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.imageView);
		}
	}
}

