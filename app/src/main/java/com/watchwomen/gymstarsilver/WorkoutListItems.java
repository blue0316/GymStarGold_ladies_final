package com.watchwomen.gymstarsilver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;



public class WorkoutListItems extends AppCompatActivity {
    ListView lv;
    String mode;
    ArrayList<String> listItems;
    TextView headertext;
    ImageView backarr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_workout_list_items);
        lv=(ListView)findViewById(R.id.list);
        headertext=(TextView)findViewById(R.id.headertext);
        /*backarr=(ImageView)findViewById(R.id.backarr);
        backarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        mode=getIntent().getStringExtra("mode");
        if(mode!=null && !mode.equals("")){

            listItems = new ArrayList<String>();

            if(mode.equals("arms")){
                listItems.add("Biceps");
                listItems.add("Triceps");
                headertext.setText("ARM");
            }
            else if(mode.equals("chest")){
                listItems.add("Chest lower");
                listItems.add("Chest upper");
                headertext.setText("CHEST");
            }
            else if(mode.equals("shoulder")){
                listItems.add("Deltoid front");
                listItems.add("Deltoid back");

                headertext.setText("SHOULDERS");
            }
            else if(mode.equals("ulegs")){
                listItems.add("Legs Front");
                listItems.add("Legs back");
                listItems.add("Gluteus Maxim(butt)");

                headertext.setText("LEGS/Upper");
            }

        }
        lv.setAdapter(new MyAdapter());
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i=new Intent(WorkoutListItems.this,Calculateresult.class);
//
//                i.putExtra("item",listItems.get(position).toString());
//                startActivity(i);
//            }
//        });

    }
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.row_simple_item_layout, parent, false);
                TextView t=(TextView)convertView.findViewById(R.id.textView);
                t.setText(listItems.get(position));
                /*convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(WorkoutListItems.this,Calculateresult.class);

                        i.putExtra("item",listItems.get(position).toString());
                        startActivity(i);
                    }
                });*/
            }
            else {
                convertView=null;
            }

                return convertView;
        }
    }
}
