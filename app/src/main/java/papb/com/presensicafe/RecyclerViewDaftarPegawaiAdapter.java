package papb.com.presensicafe;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewDaftarPegawaiAdapter extends RecyclerView.Adapter<RecyclerViewDaftarPegawaiAdapter.ViewHolder> {

    private List<User> pegawaiList;
    private Context context;

    public RecyclerViewDaftarPegawaiAdapter(List<User> pegawaiList, Context context) {
        this.pegawaiList = pegawaiList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_adapter_list_nama_pegawai, parent, false);
        RecyclerViewDaftarPegawaiAdapter.ViewHolder viewHolder = new RecyclerViewDaftarPegawaiAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtNamaDaftarPegawai.setText(pegawaiList.get(position).getFullName());
    }

    @Override
    public int getItemCount() {
        return pegawaiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNamaDaftarPegawai;
        public ViewHolder(View itemView) {
            super(itemView);
            txtNamaDaftarPegawai = itemView.findViewById(R.id.txtNamaDaftarPegawai);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pegawaiDetailActivity = new Intent(context, DetailPegawaiActivity.class);
                    context.startActivity(pegawaiDetailActivity);
                }
            });
        }
    }

}
