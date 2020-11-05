package com.example.espapp10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.number.Precision;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.VerticalStepView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import java.lang.Math;

public class Mean extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //dialog box variable initialization
    Dialog custom,outofroom;
    MediaPlayer alert;
    ImageView dialogClose;
    Button dialogOK;
    Handler handler = new Handler();
    int delay = 60000; //milliseconds
    TextView dialogRoomName,dialogOutlierTime,dialogTitle;
    DatabaseReference databaseReference;

    DatabaseReference mRef;
    String[][] spaceProbes;
    TableLayout mTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mean);


        Spinner spinner=(Spinner) findViewById(R.id.roomselect);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.roomList,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        mRef= FirebaseDatabase.getInstance().getReference("Outliers");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Temporal Outlier").getValue().toString().equals("true") || dataSnapshot.child("Transition Outlier").getValue().toString().equals("true")){
                    custom=new Dialog(Mean.this);
                    alert=MediaPlayer.create(Mean.this,R.raw.swiftly);
                    custom.setContentView(R.layout.popup);
                    dialogClose=(ImageView)custom.findViewById(R.id.closePopUp);
                    dialogOK=(Button)custom.findViewById(R.id.btnAccept);
                    //add for set Text

                    dialogRoomName=(TextView)custom.findViewById(R.id.roomName);
                    dialogOutlierTime=(TextView)custom.findViewById(R.id.outlierTime);
                    /////////////////////////////////////////
                    //reading the last query
                    databaseReference = FirebaseDatabase.getInstance().getReference("Outliers/Temporal Outlier Time");
                    databaseReference.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            OutlierDataModel message=dataSnapshot.getValue(OutlierDataModel.class);
                            dialogRoomName.setText(message.getRoom());
                            dialogOutlierTime.setText(message.getTime());
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    /////////////////////////////////////////
                    dialogClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            custom.dismiss();

                        }
                    });
                    dialogOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            custom.dismiss();

                        }
                    });
                    custom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    custom.show();
                    alert.start();
                    //Assigns the function to check in every minute
                    handler.postDelayed(new Runnable(){
                        public void run(){
                            checkStatus();
                            handler.postDelayed(this, delay);
                        }
                    }, delay);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        CheckOutOfTheHouse();


    }
    private void CheckOutOfTheHouse() {
        mRef= FirebaseDatabase.getInstance().getReference("Location/DateFormat");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LocationModel loc=dataSnapshot.getValue(LocationModel.class);
                if(loc.getLocation().equals("Out")){
                    outofroom=new Dialog(Mean.this);
                    outofroom.setContentView(R.layout.popup);
                    dialogClose=(ImageView)outofroom.findViewById(R.id.closePopUp);
                    dialogOK=(Button)outofroom.findViewById(R.id.btnAccept);
                    dialogRoomName=(TextView)outofroom.findViewById(R.id.roomName);
                    dialogOutlierTime=(TextView)outofroom.findViewById(R.id.outlierTime);
                    dialogTitle=(TextView)outofroom.findViewById(R.id.popup_Title);
                    dialogOutlierTime.setVisibility(View.INVISIBLE);
                    dialogTitle.setVisibility(View.INVISIBLE);
                    dialogRoomName.setText("Patient is Out of the House");
                    dialogClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            outofroom.dismiss();

                        }
                    });
                    dialogOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            outofroom.dismiss();

                        }
                    });
                    outofroom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    outofroom.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkStatus() {
        custom.dismiss();
        mRef= FirebaseDatabase.getInstance().getReference("Outliers");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Temporal Outlier").getValue().toString().toLowerCase().equals("true") || dataSnapshot.child("Transition Outlier").getValue().toString().toLowerCase().equals("true")){
                    custom=new Dialog(Mean.this);
                    alert=MediaPlayer.create(Mean.this,R.raw.swiftly);
                    custom.setContentView(R.layout.popup);
                    dialogClose=(ImageView)custom.findViewById(R.id.closePopUp);
                    dialogOK=(Button)custom.findViewById(R.id.btnAccept);

                    //add for set Text

                    dialogRoomName=(TextView)custom.findViewById(R.id.roomName);
                    dialogOutlierTime=(TextView)custom.findViewById(R.id.outlierTime);
                    /////////////////////////////////////////
                    //reading the last query
                    databaseReference = FirebaseDatabase.getInstance().getReference("Outliers/Temporal Outlier Time");
                    databaseReference.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            OutlierDataModel message=dataSnapshot.getValue(OutlierDataModel.class);
                            dialogRoomName.setText(message.getRoom());
                            dialogOutlierTime.setText(message.getTime());
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    /////////////////////////////////////////
                    dialogClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            custom.dismiss();
                        }
                    });
                    dialogOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            custom.dismiss();
                        }
                    });
                    custom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    custom.show();
                    alert.start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public  void ViewRoutine(View v){
        Intent intentGoMap = new Intent (Mean.this,HouseMap.class);
        Mean.this.startActivity(intentGoMap);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedRoom=adapterView.getItemAtPosition(i).toString();
        final ArrayList<Entry>yValues1=new ArrayList<>();
        final ArrayList<Entry>Ubound1=new ArrayList<>();
        final ArrayList<Entry>Lbound1=new ArrayList<>();
        final ArrayList<ILineDataSet> datasets=new ArrayList<>();

        final ArrayList<Entry>Ubound2=new ArrayList<>();
        final ArrayList<Entry>Lbound2=new ArrayList<>();
        final ArrayList<Entry>yValues2=new ArrayList<>();
        final ArrayList<ILineDataSet> datasets2=new ArrayList<>();

        final ArrayList<Entry>yValues3=new ArrayList<>();
        final LineChart chart1=(LineChart) findViewById(R.id.linechart);
        final LineChart chart2=(LineChart) findViewById(R.id.linechart2);



        mRef= FirebaseDatabase.getInstance().getReference("Model 01/"+selectedRoom+"/mean");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    //Toast.makeText(Mean.this, postSnapshot.getKey()+postSnapshot.getValue().toString(),Toast.LENGTH_SHORT).show();
                    yValues1.add(new Entry(Integer.parseInt(postSnapshot.getKey()),Float.parseFloat(postSnapshot.getValue().toString())));
                }
                showChart(yValues1,chart1);

            }

            private void showChart(ArrayList<Entry> yValues,LineChart mchart) {

                mchart.setDragEnabled(true);
                mchart.setScaleEnabled(false);
                LineDataSet set1=new LineDataSet(yValues,"DataSet 1");
                set1.setDrawCircles(false);
                set1.setFillAlpha(110);
                set1.setLineWidth(5f);
                datasets.add(set1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef= FirebaseDatabase.getInstance().getReference("Model 01/"+selectedRoom+"/lower_bound");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    //Toast.makeText(Mean.this, postSnapshot.getKey()+postSnapshot.getValue().toString(),Toast.LENGTH_SHORT).show();
                    Lbound1.add(new Entry(Integer.parseInt(postSnapshot.getKey()),Float.parseFloat(postSnapshot.getValue().toString())));
                }
                showChart(Lbound1,chart1);

            }

            private void showChart(ArrayList<Entry> yValues,LineChart mchart) {

                mchart.setDragEnabled(true);
                mchart.setScaleEnabled(false);
                LineDataSet set1=new LineDataSet(yValues,"Lower Bound");
                set1.setDrawCircles(false);
                set1.setFillAlpha(110);
                set1.setLineWidth(1f);
                set1.setColor(Color.BLACK);
                datasets.add(set1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef= FirebaseDatabase.getInstance().getReference("Model 01/"+selectedRoom+"/upper_bound");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    //Toast.makeText(Mean.this, postSnapshot.getKey()+postSnapshot.getValue().toString(),Toast.LENGTH_SHORT).show();
                    Ubound1.add(new Entry(Integer.parseInt(postSnapshot.getKey()),Float.parseFloat(postSnapshot.getValue().toString())));
                }
                showChart(Ubound1,chart1);

            }

            private void showChart(ArrayList<Entry> yValues,LineChart mchart) {

                mchart.setDragEnabled(true);
                mchart.setScaleEnabled(false);
                LineDataSet set1=new LineDataSet(yValues,"Upper Bound");
                set1.setDrawCircles(false);
                set1.setFillAlpha(110);
                set1.setLineWidth(1f);
                set1.setColor(Color.GRAY);
                datasets.add(set1);

                LineData data=new LineData(datasets);
                mchart.setData(data);
                mchart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef= FirebaseDatabase.getInstance().getReference("Model 02/"+selectedRoom+"/mean");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    yValues2.add(new Entry(Integer.parseInt(postSnapshot.getKey()),Float.parseFloat(postSnapshot.getValue().toString())));
                }
                showChart(yValues2,chart2);

            }

            private void showChart(ArrayList<Entry> yValues,LineChart mchart) {

                mchart.setDragEnabled(true);
                mchart.setScaleEnabled(false);
                LineDataSet set2=new LineDataSet(yValues,"DataSet 2");
                set2.setDrawCircles(false);
                set2.setFillAlpha(110);
                set2.setLineWidth(4f);
                set2.setColor(Color.RED);
                datasets2.add(set2);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef= FirebaseDatabase.getInstance().getReference("Model 02/"+selectedRoom+"/lower_bound");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Lbound2.add(new Entry(Integer.parseInt(postSnapshot.getKey()),Float.parseFloat(postSnapshot.getValue().toString())));
                }
                showChart(Lbound2,chart2);

            }

            private void showChart(ArrayList<Entry> yValues,LineChart mchart) {

                mchart.setDragEnabled(true);
                mchart.setScaleEnabled(false);
                LineDataSet set2=new LineDataSet(yValues,"Lower Bound");
                set2.setDrawCircles(false);
                set2.setFillAlpha(110);
                set2.setLineWidth(2f);
                set2.setColor(Color.BLACK);
                datasets2.add(set2);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef= FirebaseDatabase.getInstance().getReference("Model 02/"+selectedRoom+"/upper_bound");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Ubound2.add(new Entry(Integer.parseInt(postSnapshot.getKey()),Float.parseFloat(postSnapshot.getValue().toString())));
                }
                showChart(Ubound2,chart2);

            }

            private void showChart(ArrayList<Entry> yValues,LineChart mchart) {

                mchart.setDragEnabled(true);
                mchart.setScaleEnabled(false);
                LineDataSet set2=new LineDataSet(yValues,"Upper Bound");
                set2.setDrawCircles(false);
                set2.setFillAlpha(110);
                set2.setLineWidth(2f);
                set2.setColor(Color.GRAY);
                datasets2.add(set2);

                LineData data=new LineData(datasets2);
                mchart.setData(data);
                mchart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final String [] tablecolnames={"Room","Probability"};





        final ArrayList<String> Room = new ArrayList<>();
        final ArrayList<String> Prob = new ArrayList<>();
        mRef= FirebaseDatabase.getInstance().getReference("Model 03/"+selectedRoom);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    //Toast.makeText(Mean.this, postSnapshot.getKey()+postSnapshot.getValue().toString(),Toast.LENGTH_SHORT).show();
                    if(!(postSnapshot.getValue().toString()).equals("0.0")){
                        Room.add(postSnapshot.getKey());
                        Prob.add(postSnapshot.getValue().toString());

                    }


                }
                spaceProbes=new String[Room.size()][2];
                for(int c=0;c<Room.size();c++){
                    spaceProbes[c][0]=Room.get(c);
                    spaceProbes[c][1]=Prob.get(c);
                }
                LinearLayout lLayout = (LinearLayout) findViewById(R.id.linearlayout2); // Root ViewGroup in which you want to add textviews
                lLayout.removeAllViews();
                for(int c=0;c<Room.size();c++){
                    //Toast.makeText(Mean.this, spaceProbes[c][0]+"**>>**"+spaceProbes[c][1],Toast.LENGTH_SHORT).show();
                    TextView tv = new TextView(Mean.this); // Prepare textview object programmatically
                    float value=Float.parseFloat(spaceProbes[c][1]);
                    String printtxt="\t\t\t\t\t"+spaceProbes[c][0];
                    printtxt+=getWhiteSpace(30-spaceProbes[c][0].length());
                    printtxt+="-\t\t\t\t"+Double.toString((float)Math.round(value * 1000.0) / 1000.0);
                    tv.setText(printtxt);
                    tv.setId(c + 5);
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTextSize(20f);
                    lLayout.addView(tv); // Add to your ViewGroup using this method
                }










            }

            private String getWhiteSpace(int size) {
                String r="";
                for (int i = 0; i < size; i++) {
                    r+=" ";
                }
                return r;
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });










    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}