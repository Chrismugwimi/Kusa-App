package eu.tutorials.myappkusa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewName.setText(event.getName());
        holder.textViewDate.setText(event.getDate());
        holder.textViewDisciplines.setText(event.getDisciplines());
        holder.textViewVenue.setText(event.getVenue());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName, textViewDate, textViewDisciplines, textViewVenue;

        public EventViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.textViewName);
            textViewDate = view.findViewById(R.id.textViewDate);
            textViewDisciplines = view.findViewById(R.id.textViewDisciplines);
            textViewVenue = view.findViewById(R.id.textViewVenue);
        }
    }
}
