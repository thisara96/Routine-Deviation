package com.example.espapp10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.VerticalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Today extends AppCompatActivity {
    DatabaseReference mRef;
    //dialog box variable initialization
    Dialog custom,outofroom;
    MediaPlayer alert;
    ImageView dialogClose;
    Button dialogOK;
    Handler handler = new Handler();
    int delay = 60000; //milliseconds
    TextView dialogRoomName,dialogOutlierTime,dialogTitle;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_today);
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH)+1;
        String strmonth="";
        String strDay="";
        if(dayOfMonth<10){
            strDay+="0";
        }
        if(month<10){
            strmonth+="0";
        }
        strDay+=String.valueOf(dayOfMonth);
        strmonth+=String.valueOf(month);
        String DateFormat=strmonth+"_"+strDay;
        LoadDataFromDataBase(DateFormat);
        mRef= FirebaseDatabase.getInstance().getReference("Outliers");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Temporal Outlier").getValue().toString().toLowerCase().equals("true") || dataSnapshot.child("Transition Outlier").getValue().toString().toLowerCase().equals("true")){
                    custom=new Dialog(Today.this);
                    alert=MediaPlayer.create(Today.this,R.raw.swiftly);
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
                    outofroom=new Dialog(Today.this);
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
                if(dataSnapshot.child("Temporal Outlier").getValue().toString().toLowerCase().equals("true")|| dataSnapshot.child("Transition Outlier").getValue().toString().toLowerCase().equals("true")){
                    custom=new Dialog(Today.this);
                    alert=MediaPlayer.create(Today.this,R.raw.swiftly);
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

    private void LoadDataFromDataBase(final String dateFormat) {

        final ArrayList<TodayDataModel> listoftoday=new ArrayList<>();

        mRef= FirebaseDatabase.getInstance().getReference("Sensor/"+dateFormat);/*dateFormat*///IF YOU WANT TO SET A FIXED DATE DELETE dateFormat variable and add the preferred date
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TodayDataModel previousdata = null;
                    TodayDataModel finaldata=null;
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        TodayDataModel todayData=postSnapshot.getValue(TodayDataModel.class);
                        if(listoftoday.isEmpty()){
                            listoftoday.add(todayData);
                            previousdata=todayData;
                        }
                        else{
                            if(previousdata.getR().equals(todayData.getR())){
                                previousdata=todayData;
                            }
                            else {
                                listoftoday.add(todayData);
                                previousdata=todayData;
                            }
                        }
                        finaldata=todayData;


                    }
                    listoftoday.add(finaldata);
                    VerticalStepView mSetpview0  = (VerticalStepView) findViewById(R.id.step_view);
                    List<String> list0 = new ArrayList<>();
                    for(int i=0;i<listoftoday.size();i++){
                        list0.add(listoftoday.get(i).getD()+"->"+listoftoday.get(i).getR());
                    }




                mSetpview0.setStepsViewIndicatorComplectingPosition(list0.size()-1)
                        .reverseDraw(false)//default is true
                        .setStepViewTexts(list0)
                        .setLinePaddingProportion(0.85f)
                        .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(Today.this, android.R.color.white))
                        .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(Today.this, R.color.uncompleted_text_color))
                        .setStepViewComplectedTextColor(ContextCompat.getColor(Today.this, android.R.color.black))
                        .setStepViewUnComplectedTextColor(ContextCompat.getColor(Today.this, R.color.uncompleted_text_color))
                        .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(Today.this, R.drawable.complted))
                        .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(Today.this, R.drawable.default_icon))
                        .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(Today.this, R.drawable.attention));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public  void ViewRoutine(View v){
        Intent intentGoMap = new Intent (Today.this,HouseMap.class);
        Today.this.startActivity(intentGoMap);
    }
}