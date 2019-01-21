package com.project.huangchengxi.netdisk;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private ArrayList<FileItem> fileArrayList;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;

    public ListAdapter(ArrayList<FileItem> list){
        this.fileArrayList=list;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private ImageView imageView;
        private TextView fileName;
        private TextView fileSize;
        private LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.file_image);
            fileName=(TextView)itemView.findViewById(R.id.file_name_text_view);
            fileSize=(TextView)itemView.findViewById(R.id.file_size);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.file_item_linearLayout);

            linearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener!=null){
                onClickListener.onItemClick(v,fileArrayList,getAdapterPosition(),ListAdapter.this);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onLongClickListener!=null){
                onLongClickListener.onLongClick(v,fileArrayList,getAdapterPosition(),ListAdapter.this);
                return true;
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return fileArrayList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        FileItem fileItem=fileArrayList.get(i);
        if (fileItem.getType()==FileItem.TYPEDIRECTORY){
            viewHolder.imageView.setImageResource(R.mipmap.icons8_folder_48px);
        }else{
            viewHolder.imageView.setImageResource(R.mipmap.icons8_file_48px);
        }
        viewHolder.fileName.setText(fileItem.getName());
        if (fileItem.getSize()>0){
            viewHolder.fileSize.setText(ToolKits.getUnit(fileItem.getSize()));
        }else{
            viewHolder.fileSize.setText("");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_item_layout,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }
    public interface OnClickListener{
        void onItemClick(View v,ArrayList<FileItem> fileItemArrayList,int position,ListAdapter listAdapter);
    }
    public interface OnLongClickListener{
        void onLongClick(View v,ArrayList<FileItem> fileItemArrayList,int position,ListAdapter listAdapter);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public void setOnLongClickListener(OnLongClickListener onLongClickListener){
        this.onLongClickListener=onLongClickListener;
    }
}
