package org.legtux.m_316k.fortune;

import android.content.Context;
import android.os.Environment;
import android.support.v4.BuildConfig;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;
import java.util.TreeSet;
import java.util.function.Predicate;

public class Fortune {
    private static Fortune instance;
    private static Context context;
    private ArrayList<String> entry_list; //here we have all fortunes
    private ArrayList<String> entry_category_list; //here we have the belonging category of entry_list
    private static int fortune_id = Integer.MAX_VALUE; //current fortune
    private static String spinner_category = null; // belonging category of fortune_id
    private Random random = null;
    private Stack<Integer> previous_id_list = null;
    private Stack<Integer> next_id_list = null;
    private int curCatCalled = 0; //debugging
    private boolean scan_called = false;

    public static void setContext(Context context) {
        Log.e("Fortune.setContext","MainA:Fortune.setContext:starting...");
        Fortune.context = context;
        Log.e("Fortune.setContext","MainA:Fortune.setContext:returning...");
    }

    public static Fortune instance() {
        Log.e("Fortune.instance","MainA:Fortune.instance:starting...");
        if(Fortune.instance == null) {
            Log.e("Fortune.instance","MainA:Fortune.instance:instance==null");
            Fortune.instance = new Fortune();
        }
        Log.e("Fortune.instance","MainA:Fortune.instance:returning instance...");
        return Fortune.instance;
    }

    public Fortune() {
        Log.e("Fortune.new","MainA:Fortune.new:starting...");
        this.random = new Random();
        this.entry_list = new ArrayList<>();
        this.entry_category_list = new ArrayList<>();
        this.previous_id_list = new Stack<>();
        this.next_id_list = new Stack<>();
        Log.e("Fortune.new","MainA:Fortune.new:returning...");
    }

    public int countElementsOfCategory(String myCategory) {
        Log.e("Fortune.countElementsOfCategory","MainA:Fortune.countElementsOfCategory:starting...myCategory=["+myCategory+"]");
        ArrayList<String> catList = getCategoryList();
        int found = 0;
        for (String cat : catList) { if(cat.equals(myCategory)) found++; }
        Log.e("Fortune.countElementsOfCategory","MainA:Fortune.countElementsOfCategory:returning...found=["+String.valueOf(found)+"]");
        return found;
    }

    private void logCategoryCounts() { //debug: log category infos
        Log.e("Fortune.logCategoryCount", "MainA:Fortune.logCategoryCount:starting...");
        Log.e("Fortune.logCategoryCount","MainA:Fortune.logCategoryCount:entry_list.size=[" + String.valueOf(this.entry_list.size()) + "]");
        for (int i = 0; i < this.entry_list.size(); i++) {
            Log.e("Fortune.logCategoryCount","MainAFortune.logCategoryCount:entry_list[" + String.valueOf(i) + "]=" + this.entry_list.get(i) + ";");
        }
        Log.e("Fortune.logCategoryCount","MainA:Fortune.logCategoryCount:entry_category_list.size=[" + String.valueOf(this.entry_category_list.size()) + "]");
        for (int i = 0; i < this.entry_category_list.size(); i++) {
            Log.e("Fortune.logCategoryCount","MainA:Fortune.logCategoryCount:entry_category_list[" + String.valueOf(i) + "]=" + this.entry_category_list.get(i) + ";");

        }
        ArrayList<String> catList = getDistinctCategoryList();
        Collections.sort(catList);
        Log.e("Fortune.logCategoryCount","MainA:Fortune.logCategoryCount:distinct:catList.size=[" + String.valueOf(catList.size()) + "]");
        for (String cat : catList) {
            Log.e("Fortune.logCategoryCount",
             "MainA:Fortune.logCategoryCount:distinct:cat["+ cat+"] contains=["+String.valueOf(countElementsOfCategory(cat))+"] elements");
        }
        Log.e("Fortune.logCategoryCount", "MainA:Fortune.logCategoryCount:returning...");
    }

    public String getPrevious() {
        Log.e("Fortune.getPrevious","MainA:Fortune.getPrevious:starting...fortune_id=["+String.valueOf(this.fortune_id)+"]");
        this.next_id_list.push(this.fortune_id);
        this.fortune_id = this.previous_id_list.pop();
        Log.e("Fortune.getPrevious","MainA:Fortune.getPrevious:returning...now fortune_id=[\"+String.valueOf(this.fortune_id)+\"]\"");
        return this.getCurrent();
    }

    public String getCurrent() {
        Log.e("Fortune.getCurrent","MainA:Fortune.getCurrent:starting...fortune_id=["+String.valueOf(this.fortune_id)+"]");
        if(this.scan_called == false) {
            Log.e("Fortune.getCurrent", "MainA:Fortune.getCurrent:scan_called==false...returning noentry1");
            return "Fortune.getCurrent Kein Eintrag gefunden (1:scancall==false)";
        }
        if(this.spinner_category == null) {
            Log.e("Fortune.getCurrent", "MainA:Fortune.getCurrent:spinner_category==null...returning noentry2");
            return "Fortune.getCurrent Kein Eintrag gefunden (2:forcat==null)";
        }
        if(this.spinner_category.equals(this.context.getString(R.string.choose_category))) {
            Log.e("Fortune.getCurrent", "MainA:Fortune.getCurrent:spinner_category==choose...returning noentry3");
            return "Fortune.getCurrent Kein Eintrag gefunden (3:forcat==choose)";
        }
        if(!this.getCategoryFromCurrentFortuneId().equals(this.spinner_category)) {
            Log.e("Fortune.getCurrent", "MainA:Fortune.getCurrent:spinner_category!=getCategoryFromCurrentFortuneId...returning noentry4");
            return "Fortune.getCurrent Kein Eintrag gefunden (4:forcat!=curcat)";
        }
        if(this.entry_list.size() <= 0) {
            Log.e("Fortune.getCurrent", "MainA:Fortune.getCurrent:size<=0...returning noentry5");
            return "Fortune.getCurrent Kein Eintrag gefunden (5:esize<=0)";
        }
        if(this.fortune_id >= this.entry_list.size()) {
            Log.e("Fortune.getCurrent", "MainA:Fortune.getCurrent:fortune_id>=size...returning noentry6");
            return "Fortune.getCurrent Kein Eintrag gefunden (6:f>=esize)";
        }
        Log.e("Fortune.getCurrent","MainA:Fortune.getCurrent:curCatCalled=["+String.valueOf(this.curCatCalled)+"]");
        this.curCatCalled = 0;
        Log.e("Fortune.getCurrent","MainA:Fortune.getCurrent:resetting: curCatCalled=["+String.valueOf(this.curCatCalled)+"]");
        Log.e("Fortune.getCurrent","MainA:Fortune.getCurrent:returning...fortune_id=["+String.valueOf(this.fortune_id)+"] entry=["+this.entry_list.get(this.fortune_id)+"]");
        return this.entry_list.get(this.fortune_id);
    }

    public String getNext() {
        Log.e("Fortune.getNext","MainA:Fortune.getNext:starting...fortune_id=["+String.valueOf(this.fortune_id)+"]");
        int old_fortune_id = this.fortune_id;
        this.previous_id_list.push(this.fortune_id);
        Log.e("Fortune.getNext","MainA:Fortune.getNext:old_fortune_id=["+String.valueOf(old_fortune_id)+"]");
        Log.e("Fortune.getNext","MainA:Fortune.getNext:old_spinner_category=["+String.valueOf(this.spinner_category)+"]");
        if(!this.next_id_list.empty()) {
            Log.e("Fortune.getNext","MainA:Fortune.getNext:stack!=empty...returning stack");
            this.fortune_id = this.next_id_list.pop();
            return this.getCurrent();
        }
        if(this.spinner_category == null) {
            Log.e("Fortune.getNext","MainA:Fortune.getNext:spinner_category==null...returning current");
            return this.getCurrent();
        }
        if(this.spinner_category.equals(this.context.getString(R.string.choose_category))) {
            Log.e("Fortune.getNext","MainA:Fortune.getNext:spinner_category==choose...returning current");
            return this.getCurrent();
        }
        int startIndex = this.entry_category_list.indexOf(this.spinner_category);
        Log.e("Fortune.getNext","MainA:Fortune.getNext:startIndex=entry_category_list.indexOf(spinner_category)=["+String.valueOf(startIndex)+"]");
        if(startIndex == -1) {
            Log.e("Fortune.getNext","MainA:Fortune.getNext:entry_category_list.indexOf(spinner_category)==-1...returning current");
            return this.getCurrent();
        }
        int numElements = this.countElementsOfCategory(this.spinner_category);
        Log.e("Fortune.getNext","MainA:Fortune.getNext:numElements=countElementsOfCategory(spinner_category)=["+String.valueOf(numElements)+"]");
        if( numElements < 2) {
            Log.e("Fortune.getNext","MainA:Fortune.getNext:numElements<2...returning current");
            return this.getCurrent();
        }
        int loop_count = 0;
        do {
            loop_count += 1;
            this.fortune_id = startIndex + this.random.nextInt(numElements);
            Log.e("Fortune.getNext","MainA:Fortune.getNext:loop_count=["+String.valueOf(loop_count)+"]; <5=["+String.valueOf(loop_count<5)+"]");
            Log.e("Fortune.getNext","MainA:Fortune.getNext:new_fortune_id=["+String.valueOf(this.fortune_id)+"]; old==new["+
                    String.valueOf(old_fortune_id==this.fortune_id)+"]");
            Log.e("Fortune.getNext","MainA:Fortune.getNext:>size?["+String.valueOf(this.fortune_id>=this.entry_list.size())+"]");
            Log.e("Fortune.getNext","MainA:Fortune.getNext:getCurrCat=["+this.getCategoryFromCurrentFortuneId()+"]; spinner_category!=getCurCat()["+
                    String.valueOf(!this.spinner_category.equals(this.getCategoryFromCurrentFortuneId()))+"]");
        } while(
                (loop_count < 5)
             && (old_fortune_id == this.fortune_id)
             && (this.fortune_id <  0)
             && (this.fortune_id >= this.entry_list.size())
             && (!this.spinner_category.equals(this.getCategoryFromCurrentFortuneId()))
        );
        Log.e("Fortune.getNext","MainA:Fortune.getNext:loop_count=["+String.valueOf(loop_count)+"]...returning...via getCurrent:fortune_id=["+String.valueOf(this.fortune_id)+"]");
        return this.getCurrent();
    }

    public String getCategoryFromCurrentFortuneId() { //liefert category of fortune_id als string oder choose
        Log.e("Fortune.getCategoryFromCurrentFortuneId","MainA:Fortune.getCategoryFromCurrentFortuneId:starting...fortune_id=["+String.valueOf(this.fortune_id)+"] curCatCalled=["+String.valueOf(this.curCatCalled)+"]");
        if(this.curCatCalled >20) {
            Log.e("Fortune.getCategoryFromCurrentFortuneId","MainA:Fortune.getCategoryFromCurrentFortuneId:curCatCalled>20...returning choose");
            return this.context.getString(R.string.choose_category);
        } //debugging
        this.curCatCalled += 1; //debugging
        if ((this.entry_category_list.size() > 0) && (this.fortune_id < this.entry_category_list.size())) {
            Log.e("Fortune.getCategoryFromCurrentFortuneId","MainA:Fortune.getCategoryFromCurrentFortuneId:inside entry_category_list, returning...entry_category_list()=["+this.entry_category_list.get(this.fortune_id)+"]");
            return this.entry_category_list.get(this.fortune_id);
        } else {
            Log.e("Fortune.getCategoryFromCurrentFortuneId","MainA:Fortune.getCategoryFromCurrentFortuneId:inside entry_category_list=>else, returning...choose");
            return this.context.getString(R.string.choose_category);
        }
    }
    public Boolean previousAvailable() {
        Log.e("Fortune.previousAvailable","MainA:Fortune.previousAvailable:starting...returning..."+String.valueOf(!this.previous_id_list.empty()));
        return !this.previous_id_list.empty();
    }

    public ArrayList<String> getCategoryList() {
        Log.e("Fortune.getCategoryList","MainA:Fortune.getCategoryList:starting...returning...entry_category_list.size="+String.valueOf(entry_category_list.size()));
        return entry_category_list; }
    public ArrayList<String> getDistinctCategoryList() {
        Log.e("Fortune.getDistinctCategoryList","MainA:Fortune.getDistinctCategoryList:starting...returning...list");
        return new ArrayList(new TreeSet<>(entry_category_list)); }
    public static String getSpinnerCategory() {  //getFortuneCategory getSpinnerCategory
        Log.e("Fortune.getSpinnerCategory","MainA:Fortune.getSpinnerCategory:starting...returning...spinner_category"+String.valueOf(spinner_category));
        return spinner_category; }
    public static void setSpinnerCategory(String myCategory) {
        Log.e("Fortune.setSpinnerCategory","MainA:Fortune.setSpinnerCategory:starting...myCategory=["+myCategory+"]");
        spinner_category = myCategory;
        Log.e("Fortune.setSpinnerCategory","MainA:Fortune.setSpinnerCategory:returning...(spinner_category=["+spinner_category+"])");
    }

    public void scanFortuneFiles() {
        Log.e("Fortune.scanFortuneFiles","MainA:Fortune.scanFortuneFiles:starting...");
        this.entry_list.clear();
        this.entry_category_list.clear();
        this.previous_id_list.clear();
        this.next_id_list.clear();
        this.fortune_id = Integer.MAX_VALUE;
        this.spinner_category = null;
        int inserted = 0;

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                context.getString(R.string.fortune_files_directory));

        try {
            if(dir.exists()) {
                String[] customFiles = dir.list();
                for (String name : customFiles) {
                    try {
                        String category_name = (name.endsWith(".txt") ? name.replace(".txt", "") : name)
                         .replace('_',' ');
                        Log.e("Fortune.scanFortuneFiles","MainA:Fortune.scanFortuneFiles:scanning dir=["+dir.getAbsolutePath().toString()+"], name=["+name+"], category_name=["+category_name+"]");
                        File f = new File(dir.getAbsolutePath(), name);
                        StringBuilder content = new StringBuilder();
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        String line;
                        while ((line = br.readLine()) != null) { content.append(line).append('\n'); }
                        br.close();
                        for (String entry : content.toString().split("\r?\n%\r?\n")) {
                            try {
                                String entry_trimmed = entry.trim();
                                if (entry_trimmed.length() > 0) {
                                    Log.e("Fortune.scanFortuneFiles","MainA:Fortune.scanFortuneFiles:entry_trimmed>0");
                                    int idx = this.entry_list.indexOf(entry_trimmed);
                                    Log.e("Fortune.scanFortuneFiles","MainA:Fortune.scanFortuneFiles:idx=["+String.valueOf(idx)+"], entry_category_list.size=["+this.entry_category_list.size()+"]");
                                    if((idx > -1) && (idx < this.entry_category_list.size()) &&
                                            this.entry_category_list.get(idx).equals(category_name)) {
                                        //skip adding
                                        Log.e("Fortune.scanFortuneFiles",
                                                "MainA:Fortune.scanFortuneFiles:skipped adding e["+ String.valueOf(idx)+"]="+this.entry_list.get(idx)+"; cat="+this.entry_category_list.get(idx));
                                    } else {
                                        if(inserted <= (Integer.MAX_VALUE - 10)) {
                                            Log.e("Fortune.scanFortuneFiles", "MainA:Fortune.scanFortuneFiles:adding");
                                            this.entry_list.add(entry_trimmed);
                                            this.entry_category_list.add(category_name);
                                            inserted += 1;
                                        } else {
                                            Toast.makeText(context, R.string.entry_limit_reached, Toast.LENGTH_LONG).show();
                                            Log.e("Fortune.scanFortuneFiles", "MainA:Fortune.scanFortuneFiles:limit reached...skipping");
                                        }
                                    }
                                }
                            } catch (Exception e3) {
                                Log.e("Fortune.scanFortuneFiles",
                                        "MainA:Fortune.scanFortuneFiles:for entry try+catch e2=["+e3.getMessage()+"]");
                            }
                        }
                    } catch (Exception e2) {
                        Log.e("Fortune.scanFortuneFiles",
                                "MainA:Fortune.scanFortuneFiles:for customFile try+catch e2=["+e2.getMessage()+"]");
                    }
                }
                Log.e("Fortune.scanFortuneFiles","MainA:Fortune.scanFortuneFiles:for checking debugging: build_type=" + BuildConfig.BUILD_TYPE);
                //if(BuildConfig.BUILD_TYPE == "debug") {
                    logCategoryCounts();
                //}
            }
        } catch (Exception e) {
            Log.e("Fortune.scanFortuneFiles",
                    "MainA:Fortune.scanFortuneFiles:try+catch e=["+e.getMessage()+"]");
        }
        this.scan_called = (inserted > 0 ?  true : false);
        Log.e("Fortune.scanFortuneFiles","MainA:Fortune.scanFortuneFiles:inserted=["+String.valueOf(inserted)+"] setting scan_called=["+String.valueOf(inserted > 0 ?  true : false)+"]");
        Log.e("Fortune.scanFortuneFiles","MainA:Fortune.scanFortuneFiles:returning...");
    }

    public String getCategoryFromSpinner(String cat) {
        Log.e("Fortune.getCategoryFromSpinner","MainA:Fortune.getCategoryFromSpinner:starting...cat=["+cat+"]");
        int pos = cat.lastIndexOf(" (");
        Log.e("Fortune.getCategoryFromSpinner","MainA:Fortune.getCategoryFromSpinner:pos=["+String.valueOf(pos)+"]");
        if(pos > -1) {
            Log.e("Fortune.getCategoryFromSpinner","MainA:Fortune.getCategoryFromSpinner:pos>-1;returning...["+cat.substring(0, pos)+"]");
            return cat.substring(0, pos);
        }
        Log.e("Fortune.getCategoryFromSpinner","MainA:Fortune.getCategoryFromSpinner:returning...["+cat+"]");
        return cat;
    }

    public String getSpinnerFromCategory(String cat) {
        Log.e("Fortune.getSpinnerFromCategory","MainA:Fortune.getSpinnerFromCategory:starting...cat=["+cat+"]");
        int found = this.countElementsOfCategory(cat);
        Log.e("Fortune.getSpinnerFromCategory","MainA:Fortune.getSpinnerFromCategory:found=["+String.valueOf(found)+"]");
        Log.e("Fortune.getSpinnerFromCategory","MainA:Fortune.getSpinnerFromCategory:returning...["+cat+" ("+String.valueOf(found)+")"+"]");
        return cat + " (" + String.valueOf(found) + ")";
    }

}
