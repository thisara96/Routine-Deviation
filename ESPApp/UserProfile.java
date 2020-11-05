package com.example.espapp10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {
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

    TextView fullname,email,phoneNo,password,username;
    TextView fullNameLable,usernameLable,fullNameLable1,LocationStatus;
    DatabaseReference mRef,mRef2;
    Button goBtn;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);

//        hooks

        fullname = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_profile);
        username = findViewById(R.id.username_profile);
      /*  password = findViewById(R.id.password_profile);*/
        fullNameLable = findViewById(R.id.profile_name_profile);
        usernameLable = findViewById(R.id.profile_details_profile);
        fullNameLable1 = findViewById(R.id.profile_name_profile1);
        LocationStatus=findViewById(R.id.booking_desc);

        //        show all data
        mRef2= FirebaseDatabase.getInstance().getReference("Location/DateFormat");
        mRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    LocationModel loc=dataSnapshot.getValue(LocationModel.class);
                    LocationStatus.setText(loc.getLocation());
                    if(loc.getLocation().equals("Out")){
                        outofroom=new Dialog(UserProfile.this);
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




        mRef= FirebaseDatabase.getInstance().getReference("Outliers");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Temporal Outlier").getValue().toString().toLowerCase().equals("true") || dataSnapshot.child("Transition Outlier").getValue().toString().toLowerCase().equals("true")){
                    custom=new Dialog(UserProfile.this);
                    alert=MediaPlayer.create(UserProfile.this,R.raw.swiftly);
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
                            custom.cancel();
                        }
                    });
                    dialogOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            custom.dismiss();
                            custom.cancel();
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
        showAllUserData();

        Intent intent=getIntent();
        final String user_email = intent.getStringExtra("email");
        final String user_name = intent.getStringExtra("name");
        final String user_phone = intent.getStringExtra("phoneNo");
        final String user_username = intent.getStringExtra("username");



        goBtn = findViewById(R.id.go_btn);

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handler.removeCallbacksAndMessages(null);
                Intent intent = new Intent (UserProfile.this,ScannerNetwork.class);
                intent.putExtra("name",user_name);
                intent.putExtra("username",user_username);
                intent.putExtra("email",user_email);
                intent.putExtra("phoneNo",user_phone);
                UserProfile.this.startActivity(intent);

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
                    custom=new Dialog(UserProfile.this);
                    alert=MediaPlayer.create(UserProfile.this,R.raw.swiftly);
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
                            //custom.dismiss();
                            custom.cancel();
                        }
                    });
                    dialogOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //custom.dismiss();
                            custom.cancel();
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

    private void showAllUserData() {

        Intent intent = getIntent();
        String user_email = intent.getStringExtra("email");
        String user_name = intent.getStringExtra("name");

        String user_phone = intent.getStringExtra("phoneNo");
        String user_username = intent.getStringExtra("username");

        fullNameLable.setText(user_name);
        fullNameLable1.setText(user_name);
        usernameLable.setText(user_username);
        fullname.setText(user_name);
        email.setText(user_email);
        phoneNo.setText(user_phone);
        username.setText(user_username);
     /*   password.setText(user_password);*/

    }



}