package eu.tutorials.myappkusa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<String> scheduleList;

    public ScheduleAdapter(List<String> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        String schedule = scheduleList.get(position);
        holder.scheduleTextView.setText(schedule);
        holder.scheduleTextView.setTextColor(holder.itemView.getResources().getColor(R.color.white));
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView scheduleTextView;

        ScheduleViewHolder(View itemView) {
            super(itemView);
            scheduleTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
