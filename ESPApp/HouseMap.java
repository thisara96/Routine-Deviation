package com.example.espapp10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HouseMap extends AppCompatActivity  {
    AnimationDrawable planAnimation;

    //dialog box variable initialization
    Dialog custom;
    Dialog outofroom;

    MediaPlayer alert;
    ImageView dialogClose;
    Button dialogOK;
    Handler handler = new Handler();
    int delay = 60000; //milliseconds
    TextView dialogRoomName,dialogOutlierTime,dialogTitle;
    DatabaseReference databaseReference;

    DatabaseReference mRef;
    public ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_house_map);
        setAnimation("Living Room");
        /*final Handler handler = new Handler();
        final int delay = 6000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){

                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                final String strDate = formatter.format(date);
                mRef= FirebaseDatabase.getInstance().getReference();
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("Location/DateFormat")){
                            LocationModel loc=dataSnapshot.child("Location").child("DateFormat").getValue(LocationModel.class);
                            setAnimation(loc.getLocation());
                            Toast.makeText(getApplicationContext(),loc.getLocation()+strDate, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Error in method", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                handler.postDelayed(this, delay);
            }
        }, delay);*/

       /*mRef= FirebaseDatabase.getInstance().getReference();
       mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Location/DateFormat")){
                    LocationModel loc=dataSnapshot.child("Location").child("DateFormat").getValue(LocationModel.class);
                    setAnimation(loc.getLocation());
                }
                else {
                    Toast.makeText(getApplicationContext(),"Error in method", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        ReadPositionFromDatabase();
        mRef= FirebaseDatabase.getInstance().getReference("Outliers");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Temporal Outlier").getValue().toString().toLowerCase().equals("true") || dataSnapshot.child("Transition Outlier").getValue().toString().toLowerCase().equals("true")){
                    custom=new Dialog(HouseMap.this);
                    alert=MediaPlayer.create(HouseMap.this,R.raw.swiftly);
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





    }

    private void checkStatus() {
        custom.dismiss();
        mRef= FirebaseDatabase.getInstance().getReference("Outliers");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Temporal Outlier").getValue().toString().toLowerCase().equals("true") || dataSnapshot.child("Transition Outlier").getValue().toString().toLowerCase().equals("true")){
                    custom=new Dialog(HouseMap.this);
                    alert=MediaPlayer.create(HouseMap.this,R.raw.swiftly);
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


    public void GotoMean(View v){
        Intent intentGoMean = new Intent (HouseMap.this,Mean.class);
        HouseMap.this.startActivity(intentGoMean);
    }
    public  void GotoToday(View v){
        Intent intentGoToday = new Intent (HouseMap.this,Today.class);
        HouseMap.this.startActivity(intentGoToday);

    }
    public void GotoScanner(View v){
        Intent intentGoScanner=new Intent(HouseMap.this,ScannerNetwork.class);
        HouseMap.this.startActivity(intentGoScanner);
    }
    public void GotoTomorrow(View v){
        Intent intentGoTomorrow=new Intent(HouseMap.this,Tomorrow.class);
        HouseMap.this.startActivity(intentGoTomorrow);
    }
    private void ReadPositionFromDatabase() {
        mRef= FirebaseDatabase.getInstance().getReference();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    LocationModel loc=dataSnapshot.child("Location").child("DateFormat").getValue(LocationModel.class);
                    setAnimation(loc.getLocation());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setAnimation(String p){
        p=p.toLowerCase();
        setContentView(R.layout.activity_house_map);
        imageView=(ImageView)findViewById(R.id.planView);
        imageView.setBackgroundResource(R.drawable.animationroom1);//From changing R.drawable.xxxx to animationroomx or animationlivingroom location can be diplayed
        planAnimation=(AnimationDrawable)imageView.getBackground();
        switch (p){
            case "room 01":
            case "room 1":
            case "room1":
            case "room01":
                imageView.setBackgroundResource(R.drawable.animationroom1);
                planAnimation=(AnimationDrawable)imageView.getBackground();
                planAnimation.start();
                break;
            case "room 02":
            case "room02":
            case "room 2":
            case "room2":
                imageView.setBackgroundResource(R.drawable.animationroom2);
                planAnimation=(AnimationDrawable)imageView.getBackground();
                planAnimation.start();
                break;
            case "room 03":
            case "room03":
            case "room 3":
            case "room3":
                imageView.setBackgroundResource(R.drawable.animationroom3);
                planAnimation=(AnimationDrawable)imageView.getBackground();
                planAnimation.start();
                break;
            case "living room":
            case "livingroom":
                imageView.setBackgroundResource(R.drawable.animationlivingroom);
                planAnimation=(AnimationDrawable)imageView.getBackground();
                planAnimation.start();
                break;
            case "washroom":
                imageView.setBackgroundResource(R.drawable.animationbathroom);
                planAnimation=(AnimationDrawable)imageView.getBackground();
                planAnimation.start();
                break;
            case "kitchen":
                imageView.setBackgroundResource(R.drawable.animationkitchen);
                planAnimation=(AnimationDrawable)imageView.getBackground();
                planAnimation.start();
                break;
            case "out":

                imageView.setBackgroundResource(R.drawable.animationempty);
                planAnimation=(AnimationDrawable)imageView.getBackground();
                planAnimation.start();
                outofroom=new Dialog(HouseMap.this);
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

                break;
            default:
                break;
        }

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }


}