package com.evervc.saznexpressstaff.ui.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.evervc.saznexpressstaff.R;
import com.evervc.saznexpressstaff.data.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> lstCategories;
    private Context context;
    private OnSelectCategory onSelectCategory;
    public interface OnSelectCategory {
        void selectCategory(Category category);
    }
    public CategoryAdapter(List<Category> lstCategories, Context context, OnSelectCategory onSelectCategory) {
        this.lstCategories = lstCategories;
        this.context = context;
        this.onSelectCategory = onSelectCategory;
    }
    public CategoryAdapter(List<Category> lstCategories, Context context) {
        this.lstCategories = lstCategories;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewList = LayoutInflater.from(context).inflate(R.layout.category_list, parent, false);
        return new CategoryViewHolder(viewList);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = lstCategories.get(position);
        holder.imgCategoryImage.setImageResource(category.getImage());
        holder.tvCategoryName.setText(category.getName());
        holder.linearLayoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectCategory.selectCategory(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstCategories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgCategoryImage;
        private TextView tvCategoryName;
        private CardView linearLayoutCategory;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategoryImage = itemView.findViewById(R.id.imgvCategoryImage);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            linearLayoutCategory = itemView.findViewById(R.id.linearLayoutCategory);
        }
    }
}
