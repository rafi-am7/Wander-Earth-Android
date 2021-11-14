package com.example.wanderearth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;

public class ConsumerActivity extends AppCompatActivity {

    //private SharedPreferences sharedPreferences;
    private Button logOutButton, seeRoomsButton;
    private Spinner selectHotelSpinner;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private TextView hotelName, roomHeadingConsumer, hotelEmailConsumer;
    private ArrayList<String> hotelIdArray, hotelNameArray;
    private ArrayList<String> ratingsList;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<DailyBookingDT> roomListArray;
    private RoomRvAdapter mRoomRvAdapter;
    private ProgressBar progressBar;
    private AlertDialog mAlertDialog;
    private MaterialCalendarView materialCalendarView;
    private String formattedBookingDate;
    private DailyBookingDT dailyBookingDT;
    private RoomDataType roomDataType, ratingRoomPropDT;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);
        hotelName = findViewById(R.id.hotelNameConsumer);
        materialCalendarView = findViewById(R.id.consumerCalendarView);
        roomHeadingConsumer = findViewById(R.id.roomHeadingConsumer);
        hotelEmailConsumer = findViewById(R.id.emailConsumerHotel);
        progressBar = findViewById(R.id.marker_progressbar_consumer);
        logOutButton = findViewById(R.id.logOutButtonConsumer);
        seeRoomsButton = findViewById(R.id.seeRoomsButton);
        selectHotelSpinner = findViewById(R.id.select_hotel_spinner);
        recyclerView = findViewById(R.id.rvHotelRoomsConsumer);
        hotelIdArray = new ArrayList<String>();
        hotelIdArray.add("selectHotelId");
        hotelNameArray = new ArrayList<>();
        hotelNameArray.add("Select Hotel");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_signup_list_item, R.id.spinner_optinos_textview, hotelNameArray);
        selectHotelSpinner.setAdapter(adapter);
        gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        roomListArray = new ArrayList<>();
        ratingsList = new ArrayList<>();

        mRoomRvAdapter = new RoomRvAdapter(this, ContextCompat.getColor(this, R.color.colorBooked), ContextCompat.getColor(this, R.color.colorYellow), roomListArray, ratingsList);
        recyclerView.setLayoutManager(gridLayoutManager);
        mAuth = FirebaseAuth.getInstance();

       /* sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();*
        */
        Intent paymentIntent = getIntent();
        if (paymentIntent.hasExtra("transaction")) {
            String trans = paymentIntent.getStringExtra("transaction");
            if (trans.equals("success")) {
                snackbar = Snackbar.make(findViewById(R.id.consumerScrollView), "Transaction Successful!", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.white));
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.success));
                snackbar.show();
            } else if (trans.equals("failed")) {
                snackbar = Snackbar.make(findViewById(R.id.consumerScrollView), "Transaction Failed!", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.white));
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.error));
                snackbar.show();
            }
        }

/*        materialCalendarView.setCurrentDate(CalendarDay.today());
        materialCalendarView.setSelectedDate(CalendarDay.today());*/
        formattedBookingDate = getFormattedDate(CalendarDay.today());


        progressBar.setVisibility(View.VISIBLE);
        //retriving data from firebase for spinner
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DailyBooking");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (hotelIdArray.size() > 1) {
                    return;
                }
                if (snapshot != null) {
                    for (DataSnapshot i : snapshot.getChildren()) {
                        hotelIdArray.add(i.getKey());
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users").child(i.getKey());
                        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserDataType userDataType = snapshot.getValue(UserDataType.class);
                                hotelNameArray.add(userDataType.getFullName());
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        progressBar.setVisibility(View.GONE);


        //see room
        seeRoomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackbar != null) snackbar.dismiss();
                Toast.makeText(ConsumerActivity.this, "Please select date", Toast.LENGTH_LONG).show();
                roomListArray.clear();
                ratingsList.clear();
                mRoomRvAdapter.notifyDataSetChanged();
                materialCalendarView.clearSelection();

                if (selectHotelSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(ConsumerActivity.this, "Please select a hotel", Toast.LENGTH_SHORT).show();
                    return;
                }
                //materialCalendarView.setSelectedDate(CalendarDay.today());
                progressBar.setVisibility(View.VISIBLE);
                String sitem = hotelIdArray.get(selectHotelSpinner.getSelectedItemPosition());
                //Toast.makeText(ConsumerActivity.this, sitem, Toast.LENGTH_SHORT).show();
                //retriving hotel name
                FirebaseDatabase.getInstance().getReference("Users").child(sitem).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDataType userDataType = snapshot.getValue(UserDataType.class);
                        hotelName.setText(userDataType.getFullName());
                        hotelName.setVisibility(View.VISIBLE);
                        hotelEmailConsumer.setText(userDataType.getEmail());
                        hotelEmailConsumer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                roomHeadingConsumer.setVisibility(View.VISIBLE);

                //calender date change
                materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                        progressBar.setVisibility(View.VISIBLE);
                        roomListArray.clear();
                        ratingsList.clear();
                        mRoomRvAdapter.notifyDataSetChanged();
                        formattedBookingDate = getFormattedDate(date);
                        //roomListArray.clear();
                        progressBar.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference().child("DailyBooking").child(hotelIdArray.get(selectHotelSpinner.getSelectedItemPosition())).child(formattedBookingDate).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                roomListArray.clear();
                                for (DataSnapshot i : snapshot.getChildren()) {
                                    if (i.exists()) {
                                        dailyBookingDT = i.getValue(DailyBookingDT.class);
                                        roomListArray.add(dailyBookingDT);
                                        mRoomRvAdapter.notifyDataSetChanged();
                                        //Toast.makeText(HotelActivity.this,i.getValue().toString(),Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        FirebaseDatabase.getInstance().getReference().child("RoomProperties").child(hotelIdArray.get(selectHotelSpinner.getSelectedItemPosition())).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ratingsList.clear();
                                for (DataSnapshot i : snapshot.getChildren()) {
                                    if (i.exists()) {
                                        roomDataType = i.getValue(RoomDataType.class);
                                        String ratingFullStr = "" + roomDataType.getRating();
                                        String formattedRating = ratingFullStr.substring(0, 3);
                                        ratingsList.add(formattedRating);
                                        mRoomRvAdapter.notifyDataSetChanged();
                                        //Toast.makeText(HotelActivity.this,i.getValue().toString(),Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        // progressBar.setVisibility(View.GONE);

                        // mRoomAdapter.notifyDataSetChanged();
                        //mRoomRvAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);


                    }
                });

                // mRoomAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(mRoomRvAdapter);
                progressBar.setVisibility(View.GONE);

            }
        });


        //logout
/*
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("checkusername","hellw");
                Log.v("checkusername",sharedPreferences.getString("username","not found"));
                editor.clear();
                editor.apply();
                Log.e("checkusername2",sharedPreferences.getString("username","not found"));
                Toast.makeText(ConsumerActivity.this,"logout "+sharedPreferences.getString("username","not found"),Toast.LENGTH_LONG).show();
                mAuth.signOut();
                startActivity(new Intent(ConsumerActivity.this, LogInActivity.class));
                finish();
            }
        });
*/


        //recycler itemclick
        //final int[] flag = new int[1];
        mRoomRvAdapter.setCustomClickListener(new RoomRvAdapter.CustomClickListener() {
            @Override
            public void customOnClick(int position, View v) {
                //flag[0] = 0;
                DatabaseReference bookRoomRef = FirebaseDatabase.getInstance().getReference("DailyBooking")
                        .child(hotelIdArray.get(selectHotelSpinner.getSelectedItemPosition())).child(formattedBookingDate)
                        .child(String.valueOf(position + 1));
                DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("RoomProperties")
                        .child(hotelIdArray.get(selectHotelSpinner.getSelectedItemPosition()))
                        .child(String.valueOf(position + 1));
                bookRoomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DailyBookingDT dailyBookingDT = snapshot.getValue(DailyBookingDT.class);

                        //if room is booked
                        if (dailyBookingDT.getIsBooked()) {
                            // flag[0]=1;


                            //rating part
                            if (dailyBookingDT.getBookUserId().equals(mAuth.getCurrentUser().getUid())) {
                                if (dailyBookingDT.getIsRated()) {
                                    Toast.makeText(ConsumerActivity.this, "Already rated!", Toast.LENGTH_SHORT).show();
                                } else {
                                    final AlertDialog.Builder popDialog = new AlertDialog.Builder(ConsumerActivity.this); //alertbox creation

                                    LinearLayout linearLayout = new LinearLayout(ConsumerActivity.this);
                                    final RatingBar rating = new RatingBar(ConsumerActivity.this);


                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    lp.leftMargin = 100;
                                    lp.topMargin = 50;

                                    //Setting parameters of rating bar
                                    rating.setLayoutParams(lp);
                                    rating.setNumStars(5);
                                    rating.setStepSize(1);

                                    //add ratingBar to linearLayout
                                    linearLayout.addView(rating);
                                    //popDialog.setIcon(android.R.drawable.btn_star_big_on);
                                    popDialog.setTitle("Add rating for room no " + (position + 1));
                                    //add linearLayout to dailog
                                    popDialog.setView(linearLayout);

/*
                                rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                    @Override
                                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                                    }
                                });*/

                                    popDialog.setPositiveButton("Proceed",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Toast.makeText(ConsumerActivity.this, "" + rating.getRating(), Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(ConsumerActivity.this, "Rating given successfully!", Toast.LENGTH_SHORT).show();


                                                    //rating main part

                                                    ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            ratingRoomPropDT = snapshot.getValue(RoomDataType.class);
                                                            double curRating;
                                                            int numOfRating;
                                                            curRating = ratingRoomPropDT.getRating();
                                                            numOfRating = ratingRoomPropDT.getNoOfRatings();

                                                            //rating calculation
                                                            curRating = (curRating * numOfRating + rating.getRating()) / (numOfRating + 1);
                                                            numOfRating += 1;

                                                            ratingRef.child("noOfRatings").setValue(numOfRating);
                                                            ratingRef.child("rating").setValue(curRating);
                                                            mRoomRvAdapter.notifyDataSetChanged();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    bookRoomRef.child("isRated").setValue(true);
                                                    mRoomRvAdapter.notifyDataSetChanged();


                                                    // textView.setText(String.valueOf(rating.getProgress()));
                                                    dialog.dismiss();
                                                }

                                            });
                                    // Button Cancel
                                    popDialog.setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });


                                    popDialog.create();
                                    popDialog.show();
                                }

                            }
                            // flag[0]=1;
                            else
                                Toast.makeText(ConsumerActivity.this, "Room no " + String.valueOf(position + 1) + " alreaday booked", Toast.LENGTH_LONG).show();
                            //else Toast.makeText(ConsumerActivity.this, roomDataType.getBookUserId()+" "+mAuth.getCurrentUser().getUid(), Toast.LENGTH_LONG).show();

                        } else {

                            mAlertDialog = new AlertDialog.Builder(ConsumerActivity.this)
                                    .setTitle("Want to book ?")
                                    .setMessage("room no " + String.valueOf(position + 1) + " will be booked.")
                                    .setPositiveButton("Proceed", null)
                                    .setNegativeButton("Cancel", null)
                                    .show();
                            Button posButton, negButton;
                            posButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            negButton = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                            posButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(ConsumerActivity.this, PaymentCardActivity.class);
                                    intent.putExtra("hotel_id", hotelIdArray.get(selectHotelSpinner.getSelectedItemPosition()));
                                    intent.putExtra("hotel_name", hotelNameArray.get(selectHotelSpinner.getSelectedItemPosition()));
                                    intent.putExtra("room_no", String.valueOf(position + 1));
                                    intent.putExtra("booking_date", formattedBookingDate);
                                    startActivity(intent);
                                   /*
                                    bookRoomRef.child("isBooked").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ConsumerActivity.this, "Room no " + String.valueOf(position + 1) + " booking successful!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ConsumerActivity.this, "Failed!", Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    });
                                    bookRoomRef.child("bookUserId").setValue(mAuth.getCurrentUser().getUid());*/

                                    //Toast.makeText(ConsumerActivity.this,"room no " + String.valueOf(position+1), Toast.LENGTH_SHORT).show();
                                    mAlertDialog.dismiss();

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Toast.makeText(ConsumerActivity.this, String.valueOf(flag[0]), Toast.LENGTH_LONG).show();

                // if (flag[0] == 1) return;


            }


            //long click
            @Override
            public void customOnLongClick(int position, View v) {
                //ShowRatingDialog();

            }
        });

    }


    //menu onclick
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            //log out
            case R.id.logOutItemMenu:
                PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
                mAuth.signOut();
                startActivity(new Intent(ConsumerActivity.this, LogInActivity.class));
                finish();
                return true;
            case R.id.changePassItemMenu:
                startActivity(new Intent(ConsumerActivity.this, ConsumerChangePasswordActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    //creating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_out_menu, menu);
        return true;
    }

    private static String getFormattedDate(CalendarDay calendarDay) {
        return calendarDay.getYear() + "-" + calendarDay.getMonth() + "-" + calendarDay.getDay();
    }

    @Override
    protected void onPostResume() {
        // roomListArray.clear();
        // ratingsList.clear();
        super.onPostResume();
    }
}