package pl.genschu.bloomooemulator.adapters;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;
import pl.genschu.bloomooemulator.GameListActivity;
import pl.genschu.bloomooemulator.logic.GameEntry;

public class GameListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public GameListAdapter(GameListActivity gameListActivity, Array<GameEntry> games) {
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
