package personal.com.filewordreader;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kuliza on 05-04-2017.
 */

public class FileWordModelAdapter extends RecyclerView.Adapter<FileWordModelAdapter.FileWordViewHolder> {

    private List<FileWordModel> mFileWordModelList;

    public FileWordModelAdapter(List<FileWordModel> fileWordModelList) {
        this.mFileWordModelList = fileWordModelList;
    }

    @Override
    public FileWordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(viewType==0) {
            itemView= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.file_word_item, parent, false);
        }
        else
        {
            itemView=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_header_item, parent, false);
        }
        return new FileWordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileWordViewHolder holder, int position) {

        if (holder != null) {
            if(mFileWordModelList.get(position).getDataType().equalsIgnoreCase("data")) {
                holder.word.setText(mFileWordModelList.get(position).getWord());
                holder.wordCount.setText(String.valueOf(mFileWordModelList.get(position).getCount()));
            }
            else
            {
                holder.groupHeader.setText(mFileWordModelList.get(position).getGroupValue());
            }
            }
    }

    @Override
    public int getItemCount() {
        return mFileWordModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
       if(mFileWordModelList.get(position).getDataType().equalsIgnoreCase("data"))
           return 0;
        else
           return 1;
    }

    public class FileWordViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.word)
         @Nullable
        TextView word;
        @Bind(R.id.count)
        @Nullable
        TextView wordCount;
        @Bind(R.id.group_header)
        @Nullable
        TextView groupHeader;

        public FileWordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
