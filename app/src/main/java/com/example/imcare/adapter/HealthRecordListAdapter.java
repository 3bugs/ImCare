package com.example.imcare.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class HealthRecordListAdapter extends
        RecyclerView.Adapter<HealthRecordListAdapter.GenericViewHolder> {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int HEADER_ID = -1;

    private final Context mContext;
    private List<HealthRecordItem> mHealthRecordItemList;
    private HealthRecordListAdapterListener mListener;

    public HealthRecordListAdapter(Context context, HealthRecordListAdapterListener listener) {
        mContext = context;
        mListener = listener;
        mHealthRecordItemList = new ArrayList<>();
    }

    public void setHealthRecordItemList(List<HealthRecordItem> healthRecordItemList) {
        mHealthRecordItemList = new ArrayList<>();
        insertHeaderItem(healthRecordItemList);
    }

    private void insertHeaderItem(List<HealthRecordItem> healthRecordItemList) {
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
                mHealthRecordItemList.add(
                        new HealthRecordItem(
                                HEADER_ID, headerText, null, null,
                                HEALTH_RECORD_VALUE_EMPTY, -1, -1, -1/*, -1*/
                        )
                );
            }
            mHealthRecordItemList.add(item);
        }
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_health_record_normal, parent, false
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
        HealthRecordItem item = mHealthRecordItemList.get(position);
        return item.id == HEADER_ID ? VIEW_TYPE_HEADER : VIEW_TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        holder.setDataOnView(position);
    }

    @Override
    public int getItemCount() {
        return mHealthRecordItemList.size();
    }

    public class NormalViewHolder extends GenericViewHolder {
        private final View mRootView;
        private final TextView mTitleTextView;
        private final TextView mValueTextView;
        private final ImageView mBulletImageView;
        private final ImageView mEditImageView;

        NormalViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mTitleTextView = itemView.findViewById(R.id.title_text_view);
            mValueTextView = itemView.findViewById(R.id.value_text_view);
            mBulletImageView = itemView.findViewById(R.id.bullet_image_view);
            mEditImageView = itemView.findViewById(R.id.edit_image_view);
        }

        @Override
        public void setDataOnView(int position) {
            final HealthRecordItem healthRecordItem = mHealthRecordItemList.get(position);
            mTitleTextView.setText(healthRecordItem.title);
            String valueText = getValueText(healthRecordItem);
            if (healthRecordItem.getValue() != HEALTH_RECORD_VALUE_EMPTY) {
                valueText += (healthRecordItem.unit == null ? "" : " " + healthRecordItem.unit);
            }
            mValueTextView.setText(valueText);

            if (healthRecordItem.getValue() == HEALTH_RECORD_VALUE_EMPTY) {
                mBulletImageView.setImageResource(R.drawable.ic_bullet_off);
            } else {
                mBulletImageView.setImageResource(R.drawable.ic_bullet_on);
            }

            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInputDialog(healthRecordItem);
                }
            });
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

        private void showInputDialog(final HealthRecordItem healthRecordItem) {
            View dialogLayout = View.inflate(
                    mContext,
                    R.layout.dialog_input_health_record_value,
                    null
            );

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(dialogLayout);

            final AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT)
                );
            }

            // ข้อความ title
            TextView titleTextView = dialogLayout.findViewById(R.id.title_text_view);
            titleTextView.setText(healthRecordItem.title);
            // ช่องกรอกค่า
            final EditText valueEditText = dialogLayout.findViewById(R.id.value_edit_text);
            valueEditText.setText(getValueText(healthRecordItem));
            // ปุ่ม ok
            Button okButton = dialogLayout.findViewById(R.id.ok_button);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (valueEditText.getText().toString().trim().isEmpty()) {
                        valueEditText.setError("กรุณากรอกค่า");
                        return;
                    } else {
                        try {
                            float newValue = Float.parseFloat(valueEditText.getText().toString());
                            healthRecordItem.setValue(newValue);
                            notifyDataSetChanged();
                            dialog.dismiss();
                            if (mListener != null) {
                                // callback to save data into database
                                mListener.onSaveHealthRecordItem(healthRecordItem);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "กรอกค่าไม่ถูกต้อง", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            });
            // ปุ่ม cancel
            Button cancelButton = dialogLayout.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
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
            HealthRecordItem item = mHealthRecordItemList.get(position);
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
