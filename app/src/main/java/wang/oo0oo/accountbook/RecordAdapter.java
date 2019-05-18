package wang.oo0oo.accountbook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import wang.oo0oo.accountbook.pojo.Record;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private static final String TAG = "RecordAdapter";
    private List<Record> mRecordList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final View recordView;
        private final TextView recordCategory;
        private final TextView recordNote;
        private final TextView recordAmount;
        private final TextView recordDate;
        private final ImageButton recordDeleteImageView;

        public ViewHolder(View view) {
            super(view);
            recordView = view;
            recordCategory = view.findViewById(R.id.record_category);
            recordNote = view.findViewById(R.id.record_note);
            recordAmount = view.findViewById(R.id.record_amount);
            recordDate = view.findViewById(R.id.record_date);
            recordDeleteImageView = view.findViewById(R.id.record_delete_image_button);
        }
    }

    public RecordAdapter(List<Record> recordList) {
        mRecordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.recordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Record record = mRecordList.get(position);
                Toast.makeText(v.getContext(), "点击记录没什么用，暂时还不能改", Toast.LENGTH_SHORT).show();
            }
        });
        holder.recordDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Record record = mRecordList.get(position);
                AlertRecord(v, record, position);
            }
        });
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = mRecordList.get(position);
        holder.recordCategory.setText(record.getCategory());
        // TODO: 这里可能要处理 NPE
        holder.recordNote.setText(record.getNote());
        holder.recordAmount.setText(new DecimalFormat("#,##0.00").format(new BigDecimal(record.getAmount())));
        holder.recordDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(record.getDate()));
        if (Objects.equals(record.getType(), "收入")) {
            holder.recordCategory.setTextColor(Color.parseColor("#AA2116"));
            holder.recordAmount.setTextColor(Color.parseColor("#AA2116"));
        } else {
            holder.recordCategory.setTextColor(Color.parseColor("#1D953F"));
            holder.recordAmount.setTextColor(Color.parseColor("#1D953F"));
        }
    }

    @Override
    public int getItemCount() {
        return mRecordList.size();
    }


    void setData(List<Record> newRecordList) {
        this.mRecordList = newRecordList;
    }


    // 删除警告
    private void AlertRecord(final View v, final Record record, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
        dialog.setTitle("确认删除");
        dialog.setMessage("确定要删除该记录吗？");
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 删除记录并刷新 RecyclerView
                mRecordList.remove(position);   // 在数据源删除
                record.delete();    // 在数据库删除
                notifyDataSetChanged();     // 刷新 RecyclerView
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(v.getContext(), new String(Character.toChars(128527)), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}
