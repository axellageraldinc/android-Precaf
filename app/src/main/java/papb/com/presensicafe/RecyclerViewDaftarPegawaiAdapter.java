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
import java.util.concurrent.TimeUnit;

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
        User user = pegawaiList.get(position);
        holder.txtPegawaiId.setText(user.getId());
        holder.txtNamaDaftarPegawai.setText(user.getFullName());
        holder.txtJamJaga.setText("Durasi bulan ini : " + String.format("%02d jam %02d menit %02d detik",
                TimeUnit.MILLISECONDS.toHours(user.getJamJagaBulanIni()),
                TimeUnit.MILLISECONDS.toMinutes(user.getJamJagaBulanIni()),
                TimeUnit.MILLISECONDS.toSeconds(user.getJamJagaBulanIni()) -
                        TimeUnit.MINUTES.toHours(TimeUnit.MILLISECONDS.toMinutes(TimeUnit.MILLISECONDS.toSeconds(user.getJamJagaBulanIni())))));
    }

    @Override
    public int getItemCount() {
        return pegawaiList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNamaDaftarPegawai;
        private TextView txtPegawaiId;
        private TextView txtJamJaga;
        public ViewHolder(View itemView) {
            super(itemView);
            txtPegawaiId = itemView.findViewById(R.id.txtPegawaiId);
            txtNamaDaftarPegawai = itemView.findViewById(R.id.txtNamaDaftarPegawai);
            txtJamJaga = itemView.findViewById(R.id.txtJamJaga);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pegawaiDetailActivity = new Intent(context, DetailPegawaiActivity.class);
                    pegawaiDetailActivity.putExtra("pegawaiId", txtPegawaiId.getText().toString());
                    context.startActivity(pegawaiDetailActivity);
                }
            });
        }
    }

}
