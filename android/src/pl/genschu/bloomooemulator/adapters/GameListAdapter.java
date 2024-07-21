package pl.genschu.bloomooemulator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.badlogic.gdx.utils.Array;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import pl.genschu.bloomooemulator.GameListActivity;
import pl.genschu.bloomooemulator.R;
import pl.genschu.bloomooemulator.logic.GameEntry;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {

    private Array<GameEntry> games;
    private Context context;

    public GameListAdapter(Context context, Array<GameEntry> games) {
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public GameListAdapter.GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_entry_row, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        GameEntry game = games.get(position);
        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return games.size;
    }

    public class GameViewHolder extends RecyclerView.ViewHolder {

        TextView gameNameTextView;
        TextView detectedGameNameTextView;
        TextView gameVersionTextView;
        ImageButton runButton;
        ImageButton editButton;
        ImageButton deleteButton;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameNameTextView = itemView.findViewById(R.id.gameNameTextView);
            detectedGameNameTextView = itemView.findViewById(R.id.detectedGameNameTextView);
            gameVersionTextView = itemView.findViewById(R.id.gameVersionTextView);
            runButton = itemView.findViewById(R.id.runButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            runButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                GameEntry game = games.get(position);
                // Add your logic for running the game
                Toast.makeText(context, "Running " + game.getName(), Toast.LENGTH_SHORT).show();
            });

            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                GameEntry game = games.get(position);
                ((GameListActivity) context).showGameDialog(game);
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                GameEntry game = games.get(position);
                ((GameListActivity) context).showDeleteDialog(game);
                notifyItemRemoved(position);
            });
        }

        public void bind(GameEntry game) {
            gameNameTextView.setText(game.getName());
            detectedGameNameTextView.setText(game.getGameName());
            gameVersionTextView.setText(game.getVersion());
        }
    }
}
