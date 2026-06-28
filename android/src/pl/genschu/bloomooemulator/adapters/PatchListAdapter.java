package pl.genschu.bloomooemulator.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.genschu.bloomooemulator.R;
import pl.genschu.bloomooemulator.patch.PatchCompatibility;
import pl.genschu.bloomooemulator.patch.PatchRowVM;

/**
 * Renders the per-game patch list. Each row shows the patch name and a one-line
 * status (version / compatibility / install + enable state); the overflow button
 * defers to {@link OnMenu} so the hosting activity can build a context-appropriate
 * {@code PopupMenu} (Install / Enable / Uninstall / reorder).
 */
public class PatchListAdapter extends RecyclerView.Adapter<PatchListAdapter.PatchViewHolder> {

    /** Callback to open the per-row action menu, anchored to the overflow button. */
    public interface OnMenu {
        void show(View anchor, PatchRowVM row);
    }

    private final List<PatchRowVM> rows = new ArrayList<>();
    private final OnMenu onMenu;

    public PatchListAdapter(OnMenu onMenu) {
        this.onMenu = onMenu;
    }

    public void setRows(List<PatchRowVM> newRows) {
        rows.clear();
        if (newRows != null) {
            rows.addAll(newRows);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patch_entry_row, parent, false);
        return new PatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatchViewHolder holder, int position) {
        holder.bind(rows.get(position));
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    private static String compatLabel(PatchCompatibility compat) {
        switch (compat) {
            case EXACT:
                return "dokładna";
            case FAMILY:
                return "rodzina";
            default:
                return "brak";
        }
    }

    /** Builds the secondary line, e.g. "Aidem Media • v1.1 • Zgodność: dokładna • Zainstalowana". */
    private static String statusLine(PatchRowVM row) {
        StringBuilder sb = new StringBuilder();
        if (!row.getAuthor().isEmpty()) {
            sb.append(row.getAuthor()).append(" • ");
        }
        if (!row.getVersion().isEmpty()) {
            sb.append("v").append(row.getVersion()).append(" • ");
        }
        sb.append("Zgodność: ").append(compatLabel(row.getCompat()));
        sb.append(" • ").append(row.isInstalled() ? "Zainstalowana" : "Niezainstalowana");
        if (row.isInstalled()) {
            sb.append(" • ").append(row.isEnabled() ? "Włączona" : "Wyłączona");
        }
        return sb.toString();
    }

    /** Opens {@code url} in the user's browser; toasts on failure. No-op when blank. */
    private static void openReference(Context context, String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(context, R.string.patch_open_source_failed, Toast.LENGTH_SHORT).show();
        }
    }

    class PatchViewHolder extends RecyclerView.ViewHolder {
        final TextView patchName;
        final TextView patchStatus;
        final TextView patchReference;
        final ImageButton patchMenuButton;

        PatchViewHolder(@NonNull View itemView) {
            super(itemView);
            patchName = itemView.findViewById(R.id.patchName);
            patchStatus = itemView.findViewById(R.id.patchStatus);
            patchReference = itemView.findViewById(R.id.patchReference);
            patchMenuButton = itemView.findViewById(R.id.patchMenuButton);
        }

        void bind(PatchRowVM row) {
            patchName.setText(row.getDisplayName());
            patchStatus.setText(statusLine(row));
            patchMenuButton.setOnClickListener(v -> onMenu.show(v, row));

            String reference = row.getReference();
            if (reference != null && !reference.isEmpty()) {
                patchReference.setVisibility(View.VISIBLE);
                patchReference.setOnClickListener(v -> openReference(v.getContext(), reference));
            } else {
                patchReference.setVisibility(View.GONE);
                patchReference.setOnClickListener(null);
            }
        }
    }
}
