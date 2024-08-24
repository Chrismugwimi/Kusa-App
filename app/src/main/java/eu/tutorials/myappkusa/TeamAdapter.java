package eu.tutorials.myappkusa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private List<String> teamsList;

    public TeamAdapter(List<String> teamsList) {
        this.teamsList = teamsList;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_list_item, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        String teamName = teamsList.get(position);
        holder.bind(teamName, position + 1); // position + 1 to display 1-based index
    }

    @Override
    public int getItemCount() {
        return teamsList.size();
    }

    public class TeamViewHolder extends RecyclerView.ViewHolder {

        TextView teamNameTextView;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teamNameTextView = itemView.findViewById(R.id.teamNameTextView);
        }

        public void bind(String teamName, int position) {
            teamNameTextView.setText(position + ". " + teamName);
        }
    }
}
