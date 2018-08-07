package com.example.imcare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imcare.R;
import com.example.imcare.model.HealthRecordItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_BODY;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_FAT_GLUCOSE;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_HEART_BLOOD;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_SYSTEM;
import static com.example.imcare.etc.Const.HEALTH_RECORD_CATEGORY_UNDEFINED;
import static com.example.imcare.etc.Const.HEALTH_RECORD_VALUE_EMPTY;

public class ProfileHealthRecordListAdapter extends
        RecyclerView.Adapter<ProfileHealthRecordListAdapter.GenericViewHolder> {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int HEADER_ID = -1;

    private final Context mContext;
    private List<HealthRecordItem> mHealthRecordItemWithHeaderList;

    public ProfileHealthRecordListAdapter(Context mContext, List<HealthRecordItem> healthRecordItemList) {
        this.mContext = mContext;
        mHealthRecordItemWithHeaderList = createHealthRecordItemWithHeaderList(healthRecordItemList);
    }

    private List<HealthRecordItem> createHealthRecordItemWithHeaderList(List<HealthRecordItem> healthRecordItemList) {
        List<HealthRecordItem> resultList = new ArrayList<>();

        int previousCategory = HEALTH_RECORD_CATEGORY_UNDEFINED;
        for (HealthRecordItem item : healthRecordItemList) {
            if (previousCategory != item.category) {
                previousCategory = item.category;

                String headerText = "";
                switch (item.category) {
                    case HEALTH_RECORD_CATEGORY_BODY:
                        headerText = "ร่างกาย";
                        break;
                    case HEALTH_RECORD_CATEGORY_HEART_BLOOD:
                        headerText = "หัวใจและเลือด";
                        break;
                    case HEALTH_RECORD_CATEGORY_FAT_GLUCOSE:
                        headerText = "ไขมันและน้ำตาลในเลือด";
                        break;
                    case HEALTH_RECORD_CATEGORY_SYSTEM:
                        headerText = "ระบบการทำงานของร่างกาย";
                        break;
                }
                resultList.add(
                        new HealthRecordItem(
                                HEADER_ID, headerText, null, null,
                                HEALTH_RECORD_VALUE_EMPTY, -1, -1, -1/*, -1*/
                        )
                );
            }
            resultList.add(item);
        }
        return resultList;
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_health_record_normal_read_only, parent, false
                );
                return new NormalViewHolder(view);
            case VIEW_TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_health_record_header, parent, false
                );
                return new HeaderViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        HealthRecordItem item = mHealthRecordItemWithHeaderList.get(position);
        return item.id == HEADER_ID ? VIEW_TYPE_HEADER : VIEW_TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        holder.setDataOnView(position);
    }

    @Override
    public int getItemCount() {
        return mHealthRecordItemWithHeaderList.size();
    }

    public class NormalViewHolder extends GenericViewHolder {
        private final View mRootView;
        private final TextView mTitleTextView;
        private final TextView mValueTextView;
        private final TextView mUnitTextView;
        private final ImageView mBulletImageView;

        NormalViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mTitleTextView = itemView.findViewById(R.id.title_text_view);
            mValueTextView = itemView.findViewById(R.id.value_text_view);
            mUnitTextView = itemView.findViewById(R.id.unit_text_view);
            mBulletImageView = itemView.findViewById(R.id.bullet_image_view);
        }

        @Override
        public void setDataOnView(int position) {
            final HealthRecordItem healthRecordItem = mHealthRecordItemWithHeaderList.get(position);
            mTitleTextView.setText(healthRecordItem.title);
            String valueText = getValueText(healthRecordItem);
            /*if (healthRecordItem.getValue() != HEALTH_RECORD_VALUE_EMPTY) {
                valueText += (healthRecordItem.unit == null ? "" : " " + healthRecordItem.unit);
            }*/
            mValueTextView.setText(valueText);
            mUnitTextView.setText(healthRecordItem.unit == null ? "" : " " + healthRecordItem.unit);

            if (healthRecordItem.getValue() == HEALTH_RECORD_VALUE_EMPTY) {
                mBulletImageView.setImageResource(R.drawable.ic_bullet_off);
            } else {
                mBulletImageView.setImageResource(R.drawable.ic_bullet_on);
            }
        }

        private String getValueText(HealthRecordItem item) {
            if (item.getValue() == HEALTH_RECORD_VALUE_EMPTY) {
                return "";
            } else {
                float value = item.getValue();
                // เช็คว่ามีทศนิยมหรือไม่ ถ้าไม่มี, ให้แสดงเป็นเลขจำนวนเต็ม ไม่ต้องมี .0
                if (value % 1 == 0) { // whole number
                    return String.format(
                            Locale.getDefault(),
                            "%d", Math.round(value) // แสดงเป็นเลขจำนวนเต็ม
                    );
                } else { // has decimal part
                    return String.format(
                            Locale.getDefault(),
                            "%.1f", value // แสดงทศนิยม 1 ตำแหน่ง
                    );
                }
            }
        }
    }

    public class HeaderViewHolder extends GenericViewHolder {
        private final TextView mHeaderTextView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            mHeaderTextView = itemView.findViewById(R.id.header_text_view);
        }

        @Override
        public void setDataOnView(int position) {
            HealthRecordItem item = mHealthRecordItemWithHeaderList.get(position);
            mHeaderTextView.setText(item.title);
        }
    }

    public abstract class GenericViewHolder extends RecyclerView.ViewHolder {
        GenericViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void setDataOnView(int position);
    }

    public interface HealthRecordListAdapterListener {
        void onSaveHealthRecordItem(HealthRecordItem healthRecordItem);
    }
}
