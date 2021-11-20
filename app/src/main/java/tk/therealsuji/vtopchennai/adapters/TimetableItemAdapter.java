package tk.therealsuji.vtopchennai.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tk.therealsuji.vtopchennai.models.Timetable;
import tk.therealsuji.vtopchennai.widgets.TimetableItem;

public class TimetableItemAdapter extends RecyclerView.Adapter<TimetableItemAdapter.ViewHolder> {
    private final List<Timetable.AllData> timetable;
    private final int status;
    private float pixelDensity;

    public TimetableItemAdapter(List<Timetable.AllData> timetable, int status) {
        this.timetable = timetable;
        this.status = status;
    }

    @NonNull
    @Override
    public TimetableItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.pixelDensity = context.getResources().getDisplayMetrics().density;
        TimetableItem timetableItem = new TimetableItem(context);
        timetableItem.setStatus(this.status);

        return new ViewHolder(timetableItem);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableItemAdapter.ViewHolder holder, int position) {
        holder.setTimetableItem(timetable.get(position));

        int left = (int) (30 * pixelDensity);
        int top = (int) (10 * pixelDensity);
        int right = (int) (30 * pixelDensity);
        int bottom = (int) (0 * pixelDensity);

        if (position == this.timetable.size() - 1) {
            bottom = (int) (20 * pixelDensity);
        }

        holder.setPadding(left, top, right, bottom);
    }

    @Override
    public int getItemCount() {
        return timetable.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TimetableItem timetableItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.timetableItem = (TimetableItem) itemView;
        }

        public void setTimetableItem(Timetable.AllData timetableItem) {
            this.timetableItem.setTimetableItem(timetableItem);
        }

        public void setPadding(int left, int top, int right, int bottom) {
            this.timetableItem.setPadding(left, top, right, bottom);
        }
    }
}
