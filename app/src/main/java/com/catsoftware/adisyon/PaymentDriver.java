package com.catsoftware.adisyon;



import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.catsoftware.adisyon.db.AppDatabase;
import com.catsoftware.adisyon.db.Order;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import static com.catsoftware.adisyon.MainActivity.deleteOldOrders;

public class PaymentDriver extends AppCompatActivity {
    public static final String SORGU_SONUCU_LISTESI="SORGU_SONUCU_LISTESI";
    public static final String SORGULANMIS_SURUCU_NO="SORGULANMIS_SURUCU_NO";
    public static final String KEY_LIST="LIST";

    public static int saatIseBaslama = -1;
    public static int saatIsiBitirme = -1;
    public static int dakikaIseBaslama = -1;
    public static int dakikaIsiBitirme = -1;
    public static String staticDriver;
    Spinner spSurucuNo;
    EditText etSaatlikUcreti;
    Button btGeriDon, btHesapla, btPaketlerinDetaylariniGoster;
    TextView tvTeslimEttigiPaketNakitUcreti, tvTeslimEttigiPaketKartUcreti, tvHakEttigiCalismaUcreti,
            tvIadeEtmesiGerekenTutar, tvIsiBitirmeSaati, tvIseBaslamaSaati, tvSurucununToplamCalismaSaati,
            tvSurucununTeslimEttigiPaketSayisi,tvSurucununToplamSaatlikGeliri,tvSurucuNoSorguSonucu;

    LinearLayout linlayHesapSonuclari;
    AppDatabase db;
    List<Order> listeSurucununSiparisleri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surucu_hesap_dokumu);


        db = AppDatabase.getDbInstance(this.getApplicationContext());//veritabani baglaniyor

        //layout nesneleri ataniyor
        spSurucuNo = findViewById(R.id.spSurucuNoHesaplik);

        etSaatlikUcreti = findViewById(R.id.etSaatlikUcret);
        btGeriDon = findViewById(R.id.btGeriDon);
        btHesapla = findViewById(R.id.btHesapla);
        btPaketlerinDetaylariniGoster=findViewById(R.id.btPaketlerinDetaylariniGoster);
        linlayHesapSonuclari = findViewById(R.id.linlayHesapSonuclari);
        tvTeslimEttigiPaketNakitUcreti = findViewById(R.id.tvTeslimEttigiPaketNakitUcreti);
        tvTeslimEttigiPaketKartUcreti = findViewById(R.id.tvTeslimEttigiPaketKartUcreti);
        tvHakEttigiCalismaUcreti = findViewById(R.id.tvSurcununCalismaUcreti);
        tvIadeEtmesiGerekenTutar = findViewById(R.id.tvIadeEtmesiGerekenTutar);
        tvIseBaslamaSaati = findViewById(R.id.tvIseBaslamaSaati);
        tvIsiBitirmeSaati = findViewById(R.id.tvIsiBitirmeSaati);
        tvSurucununToplamCalismaSaati = findViewById(R.id.tvSurucununToplamCalismaSaati);
        tvSurucununTeslimEttigiPaketSayisi = findViewById(R.id.tvSurucununTeslimEttigiPaketSayisi);
        tvSurucununToplamSaatlikGeliri=findViewById(R.id.tvSurucununToplamSaatlikGeliri);
        tvSurucuNoSorguSonucu=findViewById(R.id.tvSurucuNoSorguSonucu);




        //spinnera detaylar veriliyor
        ArrayAdapter<CharSequence> adapterSurucuNo = ArrayAdapter.createFromResource(this,// Create an ArrayAdapter using the string array and a default spinner layout
                R.array.surucu_nolari, R.layout.spinner_item);
        spSurucuNo.setAdapter(adapterSurucuNo);

        //butonlara fonksiyon veriliyor
        btGeriDon.setOnClickListener(v -> anaEkranaGit());
        btPaketlerinDetaylariniGoster.setOnClickListener(v -> sorgulananSurucununPaketLeriniGoster());

        btHesapla.setOnClickListener(v -> {

            if (saatIseBaslama == 0 || saatIsiBitirme == 0 || etSaatlikUcreti.getText().toString().equals("")) {//eksik bilgiler var
                Toast.makeText(PaymentDriver.this, "Sorgulama yapilamadi! Lütfen eksik bilgileri giriniz.", Toast.LENGTH_LONG).show();


            } else {//bilgiler tam girilmis



                odemeHesapla(spSurucuNo.getSelectedItem().toString(), saatIseBaslama,
                        dakikaIseBaslama, saatIsiBitirme, dakikaIsiBitirme, Double.
                                parseDouble(etSaatlikUcreti.getText().toString()));

                //klavye gizleniyor
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
        });

    }



    private void odemeHesapla(String surucuNo, int saatIseBaslama, int dakikaIseBaslama, int saatIsiBitirme, int dakikaIsiBitirme, double surucuSaatlikUcret) {//
        deleteOldOrders(PaymentDriver.this);
        staticDriver =surucuNo;
         listeSurucununSiparisleri = db.orderDao().getAllOrdersOfDriver(surucuNo, false);
        int surucununSiparisSayisi = listeSurucununSiparisleri.size();
        System.out.println("" + surucuNo + ".surucunun siparis sayisi : " + surucununSiparisSayisi);
        double surucudeToplananSiparisNakitUcreti = 0.0;
        double surucudeToplananSiparisKartUcreti = 0.0;
        int surucuToplamCalisma10min = sure10MinHesapla(saatIseBaslama, dakikaIseBaslama, saatIsiBitirme, dakikaIsiBitirme);
        int surucununTeslimEttigiPaketSayisi = listeSurucununSiparisleri.size();
        double surucununToplamSaatlikGeliri=((surucuSaatlikUcret / 6.0) * surucuToplamCalisma10min);
        double surucununCalismaUcreti = surucununToplamSaatlikGeliri+ surucununTeslimEttigiPaketSayisi;

        for (Order siparisSatir : listeSurucununSiparisleri) {
            if (siparisSatir.getPaymentMethod().equals("Online")) {
                surucudeToplananSiparisKartUcreti += siparisSatir.getPrice();
            } else if (siparisSatir.getPaymentMethod().equals("Bar")) {
                surucudeToplananSiparisNakitUcreti += siparisSatir.getPrice();
            }

        }
        double surucununIadeEtmesiGerekenTutar = surucudeToplananSiparisNakitUcreti - surucununCalismaUcreti;
        tvTeslimEttigiPaketNakitUcreti.setText(surucudeToplananSiparisNakitUcreti + "€");
        tvTeslimEttigiPaketKartUcreti.setText(surucudeToplananSiparisKartUcreti + "€");
        tvHakEttigiCalismaUcreti.setText(surucununCalismaUcreti + " €");
        tvIadeEtmesiGerekenTutar.setText(surucununIadeEtmesiGerekenTutar + " €");
        tvSurucununToplamCalismaSaati.setText(calismaSaatiHesapla(surucuToplamCalisma10min));
        tvSurucununTeslimEttigiPaketSayisi.setText("" + surucununSiparisSayisi);
        tvSurucununToplamSaatlikGeliri.setText("( "+surucununToplamSaatlikGeliri+"€ )");
        tvSurucuNoSorguSonucu.setText(surucuNo);




        //sonuclar gorunur yapiliyor
        linlayHesapSonuclari.setVisibility(View.VISIBLE);


    }

    private String calismaSaatiHesapla(int toplam10MinAdedi) {
        int toplamSaat = toplam10MinAdedi / 6;
        int toplamDakika = (toplam10MinAdedi % 6) * 10;
        return (toplamSaat + " Stunden " + toplamDakika + " Minuten");

    }

    private int sure10MinHesapla(int saatIseBaslama, int dakikaIseBaslama, int saatIsiBitirme, int dakikaIsiBitirme) {
        return ((60 - dakikaIseBaslama) + ((saatIsiBitirme - saatIseBaslama - 1) * 60) + dakikaIsiBitirme) / 10;
    }

    public void anaEkranaGit() {
        Intent intent = new Intent(PaymentDriver.this, MainActivity.class);
        startActivity(intent);
    }

    public void saatSec(View view) {
        // Get Current Time
        int mHour = simdiSaatKac();
        int mMinute = simdiDakikaKac();

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (timePicker, hourOfDay, minute) -> {
                    if (view.getId() == R.id.btIseBaslamaSaatiSec) {
                        ((TextView) tvIseBaslamaSaati).setText(OrderAdapter.showAsTwoDigits(hourOfDay) + ":" + OrderAdapter.showAsTwoDigits(minute));
                        ((TextView) tvIseBaslamaSaati).setVisibility(View.VISIBLE);
                        saatIseBaslama = hourOfDay;
                        dakikaIseBaslama = minute;
                    } else if (view.getId() == R.id.btIsiBitirmeSaatiSec) {
                        ((TextView) tvIsiBitirmeSaati).setText(OrderAdapter.showAsTwoDigits(hourOfDay) + ":" + OrderAdapter.showAsTwoDigits(minute));
                        ((TextView) tvIsiBitirmeSaati).setVisibility(View.VISIBLE);
                        saatIsiBitirme = hourOfDay;
                        dakikaIsiBitirme = minute;
                    }


                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    private int simdiDakikaKac() {
        final Calendar c = Calendar.getInstance();


        return c.get(Calendar.MINUTE);
    }

    private int simdiSaatKac() {
        final Calendar c = Calendar.getInstance();

        return c.get(Calendar.HOUR_OF_DAY);
    }

    public void sorgulananSurucununPaketLeriniGoster() {
        Intent i = new Intent(this, OrderDetailsOfDriver.class);
        Bundle b = new Bundle();


        b.putSerializable(SORGU_SONUCU_LISTESI, (Serializable) listeSurucununSiparisleri);
        i.putExtra(KEY_LIST,b);
        i.putExtra(SORGULANMIS_SURUCU_NO, staticDriver);
        startActivity(i);
    }
}