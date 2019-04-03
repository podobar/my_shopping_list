package com.example.my_shopping_list;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    static boolean sorted=false;
    final static ArrayList<String> shoppingList = new ArrayList<>();

    RecyclerView rv;
    FloatingActionButton fab;
    Toolbar toolbar;
    ShoppingListAdapter adapter;
    LinearLayoutManager layoutManager;
    FrameLayout gl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        rv=findViewById(R.id.recycler_view);
        gl =findViewById(R.id.settings_holder);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_OnClickListener();
            }
        });
        adapter = new ShoppingListAdapter(shoppingList);
        layoutManager = new LinearLayoutManager(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReadFile();
            }
        }).start();

        setSupportActionBar(toolbar);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settings_holder, new SettingsPreferenceFragment());
        ft.addToBackStack(null);
        ft.commit();
        PreferenceManager.setDefaultValues(this, R.xml.settings_preference_fragment, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sorted = sp.getBoolean("sort_selection", false);

        EnableSwipeLeftToDelete();
        EnableSwipeRightToEdit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {

        fab.show();
        rv.setVisibility(View.VISIBLE);
        gl.setVisibility(View.INVISIBLE);
        (getSupportActionBar()).setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if(sorted){
            Collections.sort(shoppingList);
        }
        adapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SaveFile();
            }
        }).start();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            rv.setVisibility(View.INVISIBLE);
            fab.hide();
            gl.setVisibility(View.VISIBLE);
            (getSupportActionBar()).setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void EnableSwipeLeftToDelete(){
        ItemTouchHelper itemTouchHelper_Delete = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                shoppingList.remove(position);
                adapter.notifyDataSetChanged();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SaveFile();
                    }
                }).start();
            }
        });
        itemTouchHelper_Delete.attachToRecyclerView(rv);
    }
    public void EnableSwipeRightToEdit(){
        final Context context=this;
        ItemTouchHelper itemTouchHelper_Edit = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final EditText input = new EditText(context);
                final int position = viewHolder.getAdapterPosition();
                final String item = shoppingList.get(position);
                shoppingList.remove(position);
                builder.setTitle("Edit Item");
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shoppingList.add(position,input.getText().toString());
                        if(sorted){
                            Collections.sort(shoppingList);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shoppingList.add(position,item);
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });
                builder.show();
                adapter.notifyDataSetChanged();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SaveFile();
                    }
                }).start();
            }
        });
        itemTouchHelper_Edit.attachToRecyclerView(rv);
    }
    private void Add_OnClickListener() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shoppingList.add(input.getText().toString());
                if(sorted){
                    Collections.sort(shoppingList);
                }
                adapter.notifyDataSetChanged();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SaveFile();
                    }
                }).start();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    public void SaveFile(){
        try{
            //TODO: Do przemyślenia - lock na plik, bo w przypadku dużych można zamknąć i zapisywać, otworzyć w trakcie zapisu i może się popsuć
            FileOutputStream out = openFileOutput("saved_list", Context.MODE_PRIVATE);
            for(String item : shoppingList){
                out.write((item+'\n').getBytes());
            }
            out.close();
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public void ReadFile(){
        try {
            FileInputStream in = openFileInput("saved_list");
            int token;
            StringBuilder single_line = new StringBuilder();
            shoppingList.clear();

            while ((token = in.read()) != -1) {
                if ((char) token != '\n') {
                    single_line.append(Character.toString((char) token));
                } else {
                    shoppingList.add(single_line.toString());
                    single_line = new StringBuilder();
                }
            }
            in.close();
        }
        catch (IOException e){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(e.getMessage());
        }
    }


}

