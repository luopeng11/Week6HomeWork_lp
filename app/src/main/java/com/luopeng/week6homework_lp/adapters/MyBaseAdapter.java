package com.luopeng.week6homework_lp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luopeng.week6homework_lp.R;
import com.luopeng.week6homework_lp.beans.News;
import com.luopeng.week6homework_lp.cache.MyLruCache;
import com.luopeng.week6homework_lp.utils.HttpsUtils;
import com.luopeng.week6homework_lp.utils.NetWorkUtils;
import com.luopeng.week6homework_lp.utils.SdCardUtils;

import java.io.File;
import java.util.List;

/**
 * Created by my on 2016/11/11.
 */
public class MyBaseAdapter extends BaseAdapter {
    private List<News.DataBean> data;
    private Context context;
    private MyLruCache mLruCache;
    public MyBaseAdapter(List<News.DataBean> data, Context context) {
        this.data=data;
        this.context=context;
        int maxsize= (int) (Runtime.getRuntime().maxMemory()/16);
        mLruCache = new MyLruCache(maxsize);
    }

    @Override
    public int getCount() {
        return data!=null?data.size():0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret=null;
        ViewHolder holder=null;
        if (convertView != null) {
            ret=convertView;
            holder= (ViewHolder) ret.getTag();
        }else {
            ret= LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
            holder=new ViewHolder();

            holder.wap_thumb= (ImageView) ret.findViewById(R.id.wap_thumb);
            holder.create_time= (TextView) ret.findViewById(R.id.create_time);
            holder.description= (TextView) ret.findViewById(R.id.description);
            holder.nickname= (TextView) ret.findViewById(R.id.nickname);
            holder.source= (TextView) ret.findViewById(R.id.source);
            holder.title= (TextView) ret.findViewById(R.id.title);

            ret.setTag(holder);
        }
        holder.title.setText(data.get(position).getTitle());
        holder.source.setText(data.get(position).getSource());

        String description = data.get(position).getDescription();
        holder.description.setText(description);

        holder.nickname.setText(data.get(position).getNickname());
        holder.create_time.setText(data.get(position).getCreate_time());

        holder.wap_thumb.setTag(position);
        String path = data.get(position).getWap_thumb();
        if (path.length()>1){
            final ViewHolder finalHolder = holder;
            final int currentPosition=position;

            Bitmap bitmap = loadImage(path);//从一二级缓存获取数据

            if (bitmap != null) {
                finalHolder.wap_thumb.setImageBitmap(bitmap);
            }else {//有网络下载  无网络不设置
                if (NetWorkUtils.isConnected(context)){
                    HttpsUtils.loadBytes(context,path,position,new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            switch (msg.what){
                                case 110:
                                    int arg1 = msg.arg1;
                                    byte[]bytes= (byte[]) msg.obj;
                                    if (bytes != null) {
                                        if (arg1==currentPosition){
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            finalHolder.wap_thumb.setImageBitmap(bitmap);
                                        }
                                    }
                            }
                        }
                    });
                }
            }
        }
        return ret;
    }

    private Bitmap loadImage(String path) {
        String[] split1 = path.split("/");
        String fileName=split1[split1.length-1];
        Bitmap bitmap = mLruCache.get(fileName);

        if (bitmap==null){//一级缓存没有，二级缓存取
            byte[] data1 = SdCardUtils.getData(context.getExternalCacheDir().getAbsolutePath() +
                    File.separator + fileName);
            if (data1 != null) {//二级缓存取到数据--存进一级缓存
                bitmap = BitmapFactory.decodeByteArray(data1, 0, data1.length);

                mLruCache.put(fileName,bitmap);
            }
        }
        return bitmap;
    }

    public void remove(int position){
        if (position<data.size()){
            data.remove(position);
        }
        notifyDataSetChanged();
    }
    private static class ViewHolder{
        ImageView wap_thumb;
        TextView source,description,create_time,nickname,title;
    }
}
