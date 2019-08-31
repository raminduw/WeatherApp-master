package com.ramindu.weeraman.weather.views.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ramindu.weeraman.weather.R;
import com.ramindu.weeraman.weather.models.ListItemView;
import com.ramindu.weeraman.weather.utils.FontProvider;
import com.ramindu.weeraman.weather.utils.IconProvider;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CityWeatherAdapter extends RecyclerView.Adapter<CityWeatherAdapter.ViewHolder> {
    private List<ListItemView> cities;
    private int layoutReference;
    private OnItemClickListener onItemClickListener;
    private Activity activity;
    private View parentView;

    public CityWeatherAdapter(List<ListItemView> cities, int layoutReference, Activity activity, OnItemClickListener onItemClickListener) {
        this.cities = cities;
        this.layoutReference = layoutReference;
        this.activity = activity;
        this.onItemClickListener = onItemClickListener;

    }

    @Override
    public CityWeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentView = parent;
        View view = LayoutInflater.from(activity).inflate(layoutReference, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CityWeatherAdapter.ViewHolder holder, int position) {
        holder.bind(cities.get(position), position, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public void addItem(int position, ListItemView city) {
        cities.add(position, city);
        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements Target {
        @BindView(R.id.textViewCardCityName)
        TextView textViewCityName;
        @BindView(R.id.textViewCardWeatherDescription)
        TextView textViewWeatherDescription;
        @BindView(R.id.textViewCardCurrentTemp)
        TextView textViewCurrentTemp;
        @BindView(R.id.imageViewCardWeatherIcon)
        ImageView imageViewWeatherIcon;
        @BindView(R.id.cardViewWeatherCard)
        CardView cardViewWeather;
        @BindView(R.id.bgLayout)
        RelativeLayout relativeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final ListItemView listItemView, int position, final OnItemClickListener onItemClickListener) {
            relativeLayout.setForeground(activity.getDrawable(getOverlayImage(position)));
            textViewCityName.setText(listItemView.getName());
            Picasso.with(activity).load(listItemView.getImageUrl()).networkPolicy(NetworkPolicy.NO_CACHE).into(this);
            Picasso.with(activity).load(IconProvider.getImageIcon(listItemView.getDescription())).into(imageViewWeatherIcon);
            textViewWeatherDescription.setText(listItemView.getDescription());
            textViewCurrentTemp.setText((int) listItemView.getTemp()+"°");

            textViewCityName.setTypeface(FontProvider.getRobotoFont(activity));
            textViewWeatherDescription.setTypeface(FontProvider.getRobotoFont(activity));
            textViewCurrentTemp.setTypeface(FontProvider.getRobotoFont(activity));
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            relativeLayout.setBackground(new BitmapDrawable(activity.getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ListItemView cityWeather, int position, View view);
    }


    private int getOverlayImage(int index) {
        switch (index % 4) {
            case 0:
                return R.drawable.overlay_1;
            case 1:
                return R.drawable.overlay_2;
            case 2:
                return R.drawable.overlay_3;
            case 3:
                return R.drawable.overlay_4;
        }
        return R.drawable.overlay_1;
    }


}
