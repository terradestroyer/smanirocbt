package com.smaniro.smanirocbt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button btnSmaniro;
    private TextInputLayout editURL;
    private AutoCompleteTextView autoURL;
    private SharedPreferences.Editor sprefEdit;
    private final String KEYSET = "kunci";
    private ArrayList<String> urls;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar();

        btnSmaniro = findViewById(R.id.btnSmaniro);
        editURL = findViewById(R.id.editTextURL);
        autoURL = findViewById(R.id.autoURL);
        SwitchCompat switchURL = findViewById(R.id.switchURL);
        SharedPreferences spref = getPreferences(Context.MODE_PRIVATE);
        sprefEdit = spref.edit();

        Set<String> set = spref.getStringSet(KEYSET, Collections.emptySet());
        urls = new ArrayList<>(set);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, urls);

        autoURL.setAdapter(adapter);

        //switch URL
        switchURL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    btnSmaniro.setVisibility(View.GONE);
                    editURL.setVisibility(View.VISIBLE);
                } else {
                    editURL.setVisibility(View.GONE);
                    btnSmaniro.setVisibility(View.VISIBLE);
                }
            }
        });

        //tombol smaniro
        btnSmaniro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("URL", "103.210.35.49:800/portal");
                startActivity(intent);
            }
        });

        //edit text url tombol keyboard
        autoURL.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) cariUrl();
                return true;
            }
        });
        editURL.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cariUrl();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Yakin mau keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_tentang) {
            new AlertDialog.Builder(this)
                    .setTitle("Smaniro CBT")
                    .setMessage("Versi 1.1\n\n-Tim IT SMAN 1 Rongkop")
                    .setPositiveButton("Mengerti", null).create().show();
            return true;
        }else if(id == R.id.menu_hapus_url){
            new AlertDialog.Builder(this)
                    .setMessage("Yakin akan menghapus alamat URL/IP tersimpan?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            urls.clear();
                            adapter.notifyDataSetChanged();
                            Set<String> set = Collections.emptySet();
                            sprefEdit.putStringSet(KEYSET,set);
                            sprefEdit.commit();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //method untuk nyari url berdasarkan edit text
    private void cariUrl() {
        String textUrl = autoURL.getText().toString().trim();
        if (textUrl.equals(""))
            Toast.makeText(MainActivity.this, "URL tidak boleh kosong", Toast.LENGTH_SHORT).show();
        else {
            urls.add(textUrl);
            Set<String> set = new HashSet<>(urls);
            sprefEdit.putStringSet(KEYSET, set);
            sprefEdit.commit();
            adapter.notifyDataSetChanged();
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("URL", textUrl);
            startActivity(intent);
        }
    }
}