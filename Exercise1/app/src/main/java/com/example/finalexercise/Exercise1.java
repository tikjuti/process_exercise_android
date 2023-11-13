package com.example.finalexercise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

class Exercise1 extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private View column_one_one, column_one_two, column_two_one, column_two_two, column_two_three;
    private final int layout, menu;
    private final String activeMessage;

    Exercise1(int layout, int menu, String activeMessage) {
        this.layout = layout;
        this.menu = menu;
        this.activeMessage = activeMessage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        column_one_one = (View) findViewById(R.id.column_one_one);
        column_one_two = (View) findViewById(R.id.column_one_two);
        column_two_one = (View) findViewById(R.id.column_two_one);
        column_two_two = (View) findViewById(R.id.column_two_two);
        column_two_three = (View) findViewById(R.id.column_two_three);
        this.<SeekBar>findViewById(R.id.seek_bar).setOnSeekBarChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == menu) {  // Use the specific menu item ID for the Toast
            Toast.makeText(this, activeMessage, Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.menu_more_information) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setView(R.layout.dialog_content);
            dialog.show();
        } else {
            Intent intent;
            if (id == R.id.linearLayout) {
                intent = new Intent(this, LinearLayout.class);
            } else if (id == R.id.relativeLayout) {
                intent = new Intent(this, RelativeLayout.class);
            } else if (id == R.id.tableLayout) {
                intent = new Intent(this, TableLayout.class);
            } else {
                return false;
            }
            startActivity(intent);
        }
        return true;
    }
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (menu == R.id.linearLayout) {
            column_one_one.setBackgroundColor(Color.parseColor("#9943" + i));
            column_one_two.setBackgroundColor(Color.parseColor("#1200" + i));
            column_two_one.setBackgroundColor(Color.parseColor("#1535" + i));
            column_two_two.setBackgroundColor(Color.parseColor("#5572" + i));
            column_two_three.setBackgroundColor(Color.parseColor("#6824" + i));
        } else {
            if (menu == R.id.relativeLayout) {
                column_one_one.setBackgroundColor(Color.parseColor("#3345" + i));
                column_one_two.setBackgroundColor(Color.parseColor("#8800" + i));
                column_two_one.setBackgroundColor(Color.parseColor("#9999" + i));
                column_two_two.setBackgroundColor(Color.parseColor("#1234" + i));
                column_two_three.setBackgroundColor(Color.parseColor("#1388" + i));
            } else {
                if (menu == R.id.tableLayout) {
                    column_one_one.setBackgroundColor(Color.parseColor("#5678" + i));
                    column_one_two.setBackgroundColor(Color.parseColor("#8765" + i));
                    column_two_one.setBackgroundColor(Color.parseColor("#6543" + i));
                    column_two_two.setBackgroundColor(Color.parseColor("#1597" + i));
                    column_two_three.setBackgroundColor(Color.parseColor("#9531" + i));
                }
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
