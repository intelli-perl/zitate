package org.legtux.m_316k.fortune;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collections;
import android.Manifest;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import android.util.Log;

public class MainActivity extends ActionBarActivity {
    private static final int REQUEST_PERMISSION_CODE = 1; //looks I can choose whatever I want here
    private ArrayList<String> spinnerCategoryList; //keeps category list together with number of entries
    private Fortune fortune; //the real magic
    private TextView fortuneDisplayer;
    private Spinner categoryDisplayer;
    private Button prevFortune;
    private Button nextFortune;
    private Button quitButton;
    private ShareActionProvider shareActionProvider;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("MainActivity.onCreate","MainActivity.onCreate:starting...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!hasStoragePermission()) { requestStoragePermission(); return; }

        this.preferences = getApplicationContext().getSharedPreferences("general", Context.MODE_PRIVATE);

        Fortune.setContext(getApplicationContext());
        this.fortune = Fortune.instance();
        //this.fortune.scanFortuneFiles();

        this.fortuneDisplayer = (TextView) findViewById(R.id.fortuneDisplayer);
        ((TextView) findViewById(R.id.fortuneDisplayer)).setText(this.fortune.getCurrent());

        this.prevFortune = (Button) findViewById(R.id.prev);
        prevFortune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MainActivity.prevFortune.onClick","MainActivity.prevFortune.onClick:starting...");
                fortuneDisplayer.setText(fortune.getPrevious()); prevFortune.setEnabled(fortune.previousAvailable());
                Log.e("MainActivity.prevFortune.onClick","MainActivity.prevFortune.onClick:returning...");
            }
        });
        prevFortune.setEnabled(fortune.previousAvailable());

        this.nextFortune = (Button) findViewById(R.id.newFortune);
        nextFortune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MainActivity.nextFortune.onClick","MainActivity.nextFortune.onClick:starting...");
                fortuneDisplayer.setText(fortune.getNext()); prevFortune.setEnabled(true);
                Log.e("MainActivity.nextFortune.onClick","MainActivity.nextFortune.onClick:returning...");
            }
        });

        this.quitButton = (Button) findViewById(R.id.quit_app);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MainActivity.quitButton.onClick","MainActivity.quitButton.onClick:starting...");
                finish(); System.exit(0);
                Log.e("MainActivity.quitButton.onClick","MainActivity.quitButton.onClick:returning...");
            }
        });

        this.categoryDisplayer = (Spinner) findViewById(R.id.categoryDisplayer);
        prepareSpinnerCategoryList(); //CA: preparing list here
        String savedCategory = preferences.getString("selectedCategory", getApplicationContext().getString(R.string.choose_category));
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerCategoryList);
        categoryDisplayer.setAdapter(adapter);
        categoryDisplayer.setSelection(spinnerCategoryList.contains(savedCategory) ? spinnerCategoryList.indexOf(savedCategory) : 1);
        categoryDisplayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:starting...");
                String selected = fortune.getCategoryFromSpinner(parent.getItemAtPosition(position).toString());
                String saved = preferences.getString("selectedCategory", "//no_category_saved_in_preferences//");
                Log.e("MainActivity.categoryDisplayer.onItemSelected",
                 "MainActivity.categoryDisplayer.onItemSelected:saved=["+saved+"]");
                Log.e("MainActivity.categoryDisplayer.onItemSelected",
                        "MainActivity.categoryDisplayer.onItemSelected:selected=["+selected+"]");
                if(selected.equals(getApplicationContext().getString(R.string.reload_files))) {
                    Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:selected==reload...");
                    fortune.scanFortuneFiles();
                    prepareSpinnerCategoryList(); //CA: preparing list here
                    Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:adapter.getCount=["+String.valueOf(adapter.getCount())+"]...");
                    //adapter.clear();
                    //Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:adapter.getCount=["+String.valueOf(adapter.getCount())+"]...addAll()");
                    //adapter.addAll(spinnerCategoryList);
                    //Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:adapter.getCount=["+String.valueOf(adapter.getCount())+"]");
                    categoryDisplayer.setSelection(
                            spinnerCategoryList.contains(saved) ?
                                    spinnerCategoryList.indexOf(saved) :
                                    spinnerCategoryList.indexOf(getApplicationContext().getString(R.string.choose_category)));
                    Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:spinnerCategoryList.contains(saved)=["+String.valueOf(spinnerCategoryList.contains(saved))+"]");
                    Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:indexOf(saved)=["+String.valueOf(spinnerCategoryList.indexOf(saved))+"]");
                    Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:indexOf(choose)=["+String.valueOf(spinnerCategoryList.indexOf(getApplicationContext().getString(R.string.choose_category)))+"]");
                } else {
                    Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:selected==reload=>else...");
                    Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.nextFortune.setEnabled("+String.valueOf(!selected.equals(getApplicationContext().getString(R.string.choose_category)))+")");
                    nextFortune.setEnabled(!selected.equals(getApplicationContext().getString(R.string.choose_category)));
                    if (!fortune.getCategoryFromCurrentFortuneId().equals(selected)) {
                        Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:current!=selected...");
                        fortune.setSpinnerCategory(selected);
                        fortuneDisplayer.setText(fortune.getNext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("selectedCategory", selected);
                        editor.apply();
                    }
                }
                Log.e("MainActivity.categoryDisplayer.onItemSelected","MainActivity.categoryDisplayer.onItemSelected:returning...");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("MainActivity.categoryDisplayer.onNothingSelected","MainActivity.categoryDisplayer.onNothingSelected:starting...");
                nextFortune.setEnabled(false);
                Log.e("MainActivity.categoryDisplayer.onNothingSelected","MainActivity.categoryDisplayer.onNothingSelected:returning...");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem about = (MenuItem) menu.findItem(R.id.action_about);
        about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.e("MainActivity.about.onMenuItemClick","MainActivity.about.onMenuItemClick:starting...");
                showAboutDialog(); //return true;
                Log.e("MainActivity.about.onMenuItemClick","MainActivity.about.onMenuItemClick:returning...");
                return true;
            }
        });

        MenuItem share = (MenuItem) menu.findItem(R.id.menu_item_share);
        this.shareActionProvider = (ShareActionProvider) (MenuItemCompat.getActionProvider(share));
        share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.e("MainActivity.share.onMenuItemClick","MainActivity.share.onMenuItemClick:starting...");
                Intent sendIntent = new Intent()
                        .setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_TEXT, fortune.getCurrent())
                        .setType("text/plain");
                startActivity(sendIntent);
                Log.e("MainActivity.share.onMenuItemClick","MainActivity.share.onMenuItemClick:starting...");
                return true;
            }
        });

        MenuItem quit = (MenuItem) menu.findItem(R.id.action_quit);
        quit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) { finish(); System.exit(0); return true; }
        });

        return true;
    }

    private void showAboutDialog() {
        Log.e("MainActivity.showAboutDialog","MainActivity.showAboutDialog:starting...");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_about)
                .setMessage(R.string.about_text)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                })
                .show();
        Log.e("MainActivity.showAboutDialog","MainActivity.showAboutDialog:returning...");
    }

    private Boolean hasStoragePermission() {
        Log.e("MainActivity.hasStoragePermission","MainActivity.hasStoragePermission:starting...returning ["+
                String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)+"]");
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
         PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        Log.e("MainActivity.requestStoragePermission","MainActivity.requestStoragePermission:starting...");
        String[] permissions;
        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        Log.e("MainActivity.requestStoragePermission","MainActivity.requestStoragePermission:returning...");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e("MainActivity.onRequestPermissionResult","MainActivity.onRequestPermissionResult:starting...requestCode=["+String.valueOf(requestCode)+"]");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_CODE) {
            Log.e("MainActivity.onRequestPermissionResult","MainActivity.onRequestPermissionResult:requestCode==myCode");
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("MainActivity.onRequestPermissionResult","MainActivity.onRequestPermissionResult:grantResults=granted...recreating...");
                recreate();
            } else {
                Log.e("MainActivity.onRequestPermissionResult","MainActivity.onRequestPermissionResult:grantResults=granted=>else");
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
        Log.e("MainActivity.onRequestPermissionResult","MainActivity.onRequestPermissionResult:returning...");
    }

    private void prepareSpinnerCategoryList() {
        Log.e("MainActivity.prepareSpinnerCategoryList","MainActivity.prepareSpinnerCategoryList:starting...");
        if(this.spinnerCategoryList == null) {
            Log.e("MainActivity.prepareSpinnerCategoryList","MainActivity.prepareSpinnerCategoryList:list==null...setting new");
            this.spinnerCategoryList = new ArrayList<String>();
        } else {
            Log.e("MainActivity.prepareSpinnerCategoryList","MainActivity.prepareSpinnerCategoryList:list==null=>else...size=["+String.valueOf(this.spinnerCategoryList.size())+"]");
            this.spinnerCategoryList.clear();
        }
        this.spinnerCategoryList.add(0, getApplicationContext().getString(R.string.choose_category));
        this.spinnerCategoryList.add(0, getApplicationContext().getString(R.string.reload_files));
        ArrayList<String> catArray = fortune.getDistinctCategoryList();
        Collections.sort(catArray);
        for (String cat : catArray) {
            this.spinnerCategoryList.add(fortune.getSpinnerFromCategory(cat));
        }
        Log.e("MainActivity.prepareSpinnerCategoryList","MainActivity.prepareSpinnerCategoryList:spinnerCategoryList.size=[" + String.valueOf(this.spinnerCategoryList.size()) + "]");
        for (int i = 0; i < this.spinnerCategoryList.size(); i++) {
            Log.e("MainActivity.prepareSpinnerCategoryList","MainActivity.prepareSpinnerCategoryList:spinnerCategoryList[" + String.valueOf(i) + "]=" + this.spinnerCategoryList.get(i) + ";");
        }
        Log.e("MainActivity.prepareSpinnerCategoryList","MainActivity.prepareSpinnerCategoryList:returning...");
    }
}
/* //
scanFortuneFiles() läuft, füllt die variablen auch korrekt
Aber wenn ich dann eine Kategorie aussuche friert er ein.
a) immer loggen, was ich tue, also jeden function call start+ende loggen
b)
*/ //