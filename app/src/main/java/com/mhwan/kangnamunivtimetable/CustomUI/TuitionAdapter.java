package com.mhwan.kangnamunivtimetable.CustomUI;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuTuition;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TuitionAdapter extends RecyclerView.Adapter<TuitionAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<KnuTuition.TuitionItem> tuitionList;
    private Context context;
    private int expandedPosition = -1;
    public TuitionAdapter(Context context) {
        this.context = context;
        tuitionList = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.ui_item_tuition, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(this);
        viewHolder.itemView.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        KnuTuition.TuitionItem item = tuitionList.get(i);
        if (item.isPay) {
            viewHolder.napbuCircle.setCircleBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            viewHolder.napbuText.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            viewHolder.napbuText.setText(item.pay_gubn);
            viewHolder.napbuAmount.setText(new DecimalFormat("###,###").format(Integer.parseInt(item.actual_sum))+"원");
            viewHolder.napbuDate.setText(item.bank_name +" / "+item.schl_date);
            viewHolder.date_title.setText("수납은행/날짜 : ");

            StringBuilder builder = new StringBuilder();
            if (item.pay_amount != null && !item.pay_amount.equals("0"))
                builder.append("수업료 : "+new DecimalFormat("###,###").format(Integer.parseInt(item.pay_amount))+"원\n");
            if (item.lecture_scholar != null && !item.lecture_scholar.equals("0"))
                builder.append("수업료장학금 : "+new DecimalFormat("###,###").format(Integer.parseInt(item.lecture_scholar))+"원\n");
            if (item.pay_dongmun_amount != null && !item.pay_dongmun_amount.equals("0"))
                builder.append("동문회비 : "+new DecimalFormat("###,###").format(Integer.parseInt(item.pay_dongmun_amount))+"원\n");
            if (item.pay_iphak != null && !item.pay_iphak.equals("0"))
                builder.append("입학금 : "+new DecimalFormat("###,###").format(Integer.parseInt(item.pay_iphak))+"원\n");
            if (item.pay_ot != null && !item.pay_ot.equals("0"))
                builder.append("OT비 : "+new DecimalFormat("###,###").format(Integer.parseInt(item.pay_ot))+"원\n");

            String a = builder.toString();

            if (!a.isEmpty() && a.length() >2)
                a = a.substring(0, a.length()-2);

            //Log.d("builder", a);
            viewHolder.extraText.setText(a);

            if (i == expandedPosition) {
                viewHolder.extra_Frame.setVisibility(View.VISIBLE);
                viewHolder.extraText.setVisibility(View.VISIBLE);
                viewHolder.rotate_view.setRotation(180);
            } else {
                viewHolder.extra_Frame.setVisibility(View.GONE);
                viewHolder.extraText.setVisibility(View.GONE);
                viewHolder.rotate_view.setRotation(0);
            }

        } else {
            viewHolder.napbuCircle.setCircleBackgroundColor(ContextCompat.getColor(context, R.color.colorLightPink));
            viewHolder.napbuText.setTextColor(ContextCompat.getColor(context, R.color.colorLightPink));
            viewHolder.napbuText.setText(item.pay_gubn);
            viewHolder.napbuAmount.setText(new DecimalFormat("###,###").format(Integer.parseInt(item.actual_sum))+"원");
            viewHolder.napbuDate.setText(item.pay_term);
            viewHolder.date_title.setText("기간 : ");
            if (item.bank_numb !=null) {
                viewHolder.extra_Frame.setVisibility(View.VISIBLE);
                viewHolder.extraText.setVisibility(View.VISIBLE);
                viewHolder.rotate_view.setVisibility(View.GONE);

                StringBuilder builder = new StringBuilder("납부 계좌 : ");
                if (item.bank_numb.contains("<br>"))
                    item.bank_numb = item.bank_numb.replaceAll("<br>", "\\\n\\\t");
                builder.append(item.bank_numb);
                viewHolder.extraText.setText(builder.toString());
            }
        }


    }

    @Override
    public int getItemCount() {
        return tuitionList.size();
    }

    public void updateList(ArrayList<KnuTuition.TuitionItem> list) {
        this.tuitionList.clear();
        this.tuitionList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();

        if (tuitionList.get(holder.getAdapterPosition()).isPay == true) {
            if (expandedPosition == holder.getAdapterPosition()) {
                expandedPosition = -1;
                notifyItemChanged(holder.getAdapterPosition());
                return;
            }
            // Check for an expanded view, collapse if you find one
            else if (expandedPosition >= 0) {
                int prev = expandedPosition;
                notifyItemChanged(prev);
            }
            // Set the current position to "expanded"
            expandedPosition = holder.getAdapterPosition();
            notifyItemChanged(expandedPosition);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleView napbuCircle;
        TextView napbuText, napbuDate, napbuAmount, date_title, extraText;
        LinearLayout extra_Frame;
        ImageView rotate_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date_title = (TextView) itemView.findViewById(R.id.date_title);
            napbuCircle = (CircleView) itemView.findViewById(R.id.napbu_circle);
            napbuText = (TextView) itemView.findViewById(R.id.napbu_text);
            napbuDate = (TextView) itemView.findViewById(R.id.napbu_date);
            napbuAmount = (TextView) itemView.findViewById(R.id.napbu_amount);
            extraText = (TextView) itemView.findViewById(R.id.extra_textview);
            extra_Frame = (LinearLayout) itemView.findViewById(R.id.more_value_frame);
            rotate_view = (ImageView) itemView.findViewById(R.id.rotate_view);
        }
    }
}
