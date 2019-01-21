package com.project.huangchengxi.netdisk;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ViewHolder> {
    private ArrayList<Transfer> downloadThreads;

    public DownloadAdapter(ArrayList<Transfer> downloadThreads){
        this.downloadThreads=downloadThreads;
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        private ProgressBar progressBar;
        private TextView fileName;
        private TextView transferTextDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.transfer_progress_bar);
            fileName=itemView.findViewById(R.id.transfer_item_name);
            transferTextDetail=itemView.findViewById(R.id.transfer_progress_text);
        }
    }

    @Override
    public int getItemCount() {
        return downloadThreads.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Transfer downloadThread = downloadThreads.get(i);
        String downloadDetail="";
        downloadDetail+=ToolKits.getUnit(downloadThread.getTransfered())+"/"+ToolKits.getUnit(downloadThread.getTotal());
        viewHolder.fileName.setText(downloadThread.getName());
        viewHolder.transferTextDetail.setText(downloadDetail);
        viewHolder.progressBar.setProgress(downloadThread.percent(),true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_item_layout,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

}
