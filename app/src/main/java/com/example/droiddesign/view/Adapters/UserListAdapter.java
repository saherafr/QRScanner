package com.example.droiddesign.view.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.User;

import java.util.HashMap;
import java.util.List;

/**
 * Adapter for displaying a list of users in a RecyclerView.
 * Each user is represented as a card within the list, showing their name, company, and check-in count.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    /**
     * List of users to display in the adapter.
     */
    private List<User> userList;
    /**
     * Map storing the number of check-ins for each user.
     */
    private HashMap<String, Integer> checkInsMap;
    /**
     * Listener for handling clicks on items within the adapter.
     */
    private final OnItemClickListener listener;

    /**
     * Counter for guest users to differentiate between them in the list.
     */
    private int guestUserCount = 0;

    /**
     * Interface for handling item clicks in the RecyclerView.
     */
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    /**
     * Constructs a UserListAdapter with the specified user list, check-ins map, and click listener.
     *
     * @param userList   List of users to be displayed in the adapter.
     * @param checkInsMap Map storing the check-in counts for each user.
     * @param listener  Listener for click events in the adapter.
     */
    public UserListAdapter(List<User> userList, HashMap<String, Integer> checkInsMap, OnItemClickListener listener) {
        this.userList = userList;
        this.checkInsMap = checkInsMap;
        this.listener = listener;
    }

    /**
     * Called when RecyclerView needs a new {@link UserViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new UserViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
        return new UserViewHolder(view);
    }



    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The UserViewHolder which should be updated to represent the
     *                 contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        boolean isRegistered = Boolean.parseBoolean(user.getRegistered());

        if (isRegistered) {
            holder.textUserName.setText(user.getUserName());
            holder.textCompany.setText(user.getCompany());

            // Load the actual profile picture
            if (user.getProfilePic() != null) {
                Glide.with(holder.itemView.getContext())
                        .load(user.getProfilePic())
                        .into(holder.profileImageView);
            }
        } else {
            // Use position + 1 for guest user numbering to maintain consistency
            holder.textUserName.setText("Guest User " + (position + 1));
            holder.textCompany.setText("");

            // Load a generic avatar image
            String avatarUrl = "https://robohash.org/" + user.getUserId();
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .into(holder.profileImageView);
        }


        // Only show check-in data if checkInsMap is not null
        if (checkInsMap != null) {
            Number checkInsCountNumber = checkInsMap.get(user.getUserId());
            Integer checkInsCount = (checkInsCountNumber != null) ? checkInsCountNumber.intValue() : null;
            holder.numCheckIns.setText(checkInsCount == null ? "0" : checkInsCount.toString());
        } else {
            holder.numCheckIns.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(user));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textCompany, numCheckIns;
        ImageView profileImageView;

        public UserViewHolder(View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.text_user_name);
            textCompany = itemView.findViewById(R.id.text_company);
            numCheckIns = itemView.findViewById(R.id.check_in_no);
            profileImageView = itemView.findViewById(R.id.profile_image_view);
        }
    }
}
