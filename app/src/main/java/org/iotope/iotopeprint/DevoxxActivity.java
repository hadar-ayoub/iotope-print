package org.iotope.iotopeprint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static android.R.id.button1;

public class DevoxxActivity extends Activity {
    Toolbar header;
    ImageButton menuButton;
    RelativeLayout menuLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devoxx);
        header = (Toolbar) findViewById(R.id.toolbar_player);
        menuLayout = (RelativeLayout) findViewById(R.id.menu_layout);

        menuLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(DevoxxActivity.this, menuLayout);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_main, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.initialise) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(
                                    DevoxxActivity.this,
                                    "You Clicked : " + item.getTitle(),
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
    }

}
