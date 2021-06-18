package com.catsoftware.adisyon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.catsoftware.adisyon.db.AppDatabase;
import com.catsoftware.adisyon.db.AppDatabase_Impl;
import com.catsoftware.adisyon.db.SiparisSatiri;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SiparisAdapter extends RecyclerView.Adapter<SiparisAdapter.MyViewHolder> {
    List<SiparisSatiri> mDataList;
    LayoutInflater layoutInflater;
    AppDatabase db;

    public SiparisAdapter(Context context, List<SiparisSatiri> siparisList){
        layoutInflater=LayoutInflater.from(context);
        this.mDataList=siparisList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        db=AppDatabase.getDbInstance(parent.getContext());
        View v=layoutInflater.inflate(R.layout.list_item,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder( SiparisAdapter.MyViewHolder holder, int position) {

        SiparisSatiri tiklanilanSiparis=mDataList.get(position);
        holder.setData(tiklanilanSiparis,position);

    }

    @Override
    public int getItemCount() {
        return this.mDataList.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvUcret,tvOdemeYontemi,tvSurucuNo,tvSaatDakika;
        ImageView ivDuzenle, ivSil;
        int siparisId=-1;
        int tiklanilanPosition=-1;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvUcret=itemView.findViewById(R.id.tvUcret);
            tvOdemeYontemi=itemView.findViewById(R.id.tvOdemeYontemi);
            tvSurucuNo=itemView.findViewById(R.id.tvSurucuNo);
            tvSaatDakika=itemView.findViewById(R.id.tvSaatDakika);
            ivDuzenle=itemView.findViewById(R.id.ivDuzenle);
            ivSil=itemView.findViewById(R.id.ivSil);

            ivSil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //veri silme islemi yapilacak
                    siparisSil(siparisId);
                }
            });

            ivDuzenle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //veri duzenleme islemi yapilacak
                }
            });

        }
        private void  siparisSil(int siparisId){//satirin silindiMi degeri true yapiliyor
            db.siparisDao().silindiIsaretle(siparisId,true);
            mDataList=db.siparisDao().siparisleriGetir(false);//liste silinmisten ayiklanarak güncellendi
            notifyItemRemoved(tiklanilanPosition); //TODO:aktif olmali mi



        }
        public void setData(SiparisSatiri tiklanilanSiparis, int position) {
            this.tvUcret.setText(tiklanilanSiparis.getUcret().toString()+" €");
            this.tvOdemeYontemi.setText(tiklanilanSiparis.getOdemeYontemi());
            this.tvSurucuNo.setText(tiklanilanSiparis.getSurucu()+" nolu sürücü");
            this.tvSaatDakika.setText(tiklanilanSiparis.getSaat()+":"+tiklanilanSiparis.getDakika());
            siparisId=tiklanilanSiparis.getsId();
            tiklanilanPosition=position;
            System.out.println("Silindi mi:"+tiklanilanSiparis.getSilindiMi());//TODO: SIL

        }
    }
}
