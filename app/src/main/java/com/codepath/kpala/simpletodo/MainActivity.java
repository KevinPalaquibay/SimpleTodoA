package com.codepath.kpala.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//Using Pixel 3 API 28 virtual machine to run the code

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items;

    Button butn_Add;
    EditText etitem;
    RecyclerView RVitems;
    ItemsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        butn_Add = findViewById(R.id.butn_Add);
        etitem = findViewById(R.id.etitem);
        RVitems = findViewById(R.id.RVitems);

        loadItems();

        //items.add("go to gym");
        //items.add("have some fun!");


        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {

            @Override
            public void onItemLongClicked(int position) {
                //Delete the item from the model
                items.remove(position);
                //Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("Main Activity","Single click at position " + position);
                //Create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //pass the data being edited
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //display the activity
                startActivityForResult(i,EDIT_TEXT_CODE);

            }
        };
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        RVitems.setAdapter(itemsAdapter);
        RVitems.setLayoutManager(new LinearLayoutManager(this));

        butn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etitem.getText().toString();
                //Add item to the model
                items.add(todoItem);
                //Modify adapter that an item is inserted
                itemsAdapter.notifyItemInserted(items.size()-1);
                etitem.setText("");
                Toast.makeText(getApplicationContext(),"Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    // handle the result of the edit activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            //Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            //update the model at the right position with new item text
            items.set(position, itemText);
            //notify the adapter
            itemsAdapter.notifyItemChanged(position);
            //persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(),"items updated successfully!",Toast.LENGTH_SHORT).show();
        } else{
            Log.w("MainActivity","Unknown call to onActivityResult");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }
    //This function will load items by reading every line of the data file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity","Error reading items", e);
            items = new ArrayList<>();
        }
    }
    //This function saves items by writing them into the data file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity","Error writing items", e);

        }
    }
}