package com.example.wanderearth;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Calendar;

public class HotelActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    public static final String filename = "login", usernameSp = "user", memTypSp = "", passwordSp = "pass";
    private Button logOutButton, saveButton, openBookingButton;
    private TextView roomListHeading;
    private EditText roomNoEditText, floorNoEditText, openBookingToDateET;
    private RecyclerView recycler_view;
    private FirebaseAuth mAuth;
    private GridLayoutManager gridLayoutManager;
    private int roomNo, floorNo;
    private double roomPrize;
    private ProgressBar progressBar;
    private ArrayList<DailyBookingDT> roomListArray;
    private ArrayList<String> ratingsList;
    private RoomRvAdapter mRoomRvAdapter;
    private DailyBookingDT dailyBookingDT;
    private AlertDialog mAlertDialog;
    private boolean isBooked;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private MaterialCalendarView materialCalendarView;
    private String formattedBookingDate;
    private HotelDataType mHotelDataType;
    private EditText avgRoomPrizeET;
    private Calendar calendar;
    private RoomDataType roomDataType;
    private int yrstr, mnthstr, daystr;

    // public static final String fileName = "login", userName = "userName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        materialCalendarView = findViewById(R.id.hotelCalendarView);
        //materialCalendarView.state().edit().setMinimumDate(CalendarDay.today()).commit();
        openBookingToDateET = findViewById(R.id.etToDateHotel);
        openBookingButton = findViewById(R.id.openBookingButtonHotel);
        logOutButton = findViewById(R.id.logOutButton);
        roomListHeading = findViewById(R.id.recyclerHeading);
        saveButton = findViewById(R.id.saveButton);
        roomNoEditText = findViewById(R.id.roomNoEditText);
        floorNoEditText = findViewById(R.id.floorNoEditText);
        avgRoomPrizeET = findViewById(R.id.roomAvgPricHotel);
        progressBar = findViewById(R.id.marker_progress_hotel);
        mAuth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        roomListArray = new ArrayList<>();
        ratingsList = new ArrayList<>();
        recycler_view = findViewById(R.id.rvHotelRooms);
        gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRoomRvAdapter = new RoomRvAdapter(this, ContextCompat.getColor(this, R.color.colorBooked), ContextCompat.getColor(this, R.color.colorYellow), roomListArray, ratingsList);

        recycler_view.setAdapter(mRoomRvAdapter);
        recycler_view.setLayoutManager(gridLayoutManager);

        sharedPreferences = getSharedPreferences("logintable", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //mRoomAdapter.notifyDataSetChanged();

/*
        if(sharedPreferences.contains(usernameSp))
        {
          //  setText in a text view
        }*/
        //room list calender
        //adaptRecycler();
       /* materialCalendarView.setCurrentDate(CalendarDay.today());
        materialCalendarView.setSelectedDate(CalendarDay.today());*/
        formattedBookingDate = getFormattedDate(CalendarDay.today());
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                progressBar.setVisibility(View.VISIBLE);
                roomListArray.clear();
                ratingsList.clear();
                mRoomRvAdapter.notifyDataSetChanged();
                formattedBookingDate = getFormattedDate(date);
                //roomListArray.clear();
                if (roomListArray.size() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference().child("DailyBooking").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(formattedBookingDate).addValueEventListener(new ValueEventListener() {
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
                    FirebaseDatabase.getInstance().getReference().child("RoomProperties").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
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
                } else {
                    Toast.makeText(HotelActivity.this, "Room List already created!", Toast.LENGTH_SHORT).show();
                }
                // mRoomAdapter.notifyDataSetChanged();
                //mRoomRvAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);


            }
        });

        //Calender opening in editText
        openBookingToDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(HotelActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        //showing in edittext after selecting date
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                String inputToString = year + "-" + (month + 1) + "-" + dayOfMonth;
                openBookingToDateET.setText(inputToString);

                openBookingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //getting today date
                        CalendarDay calendarDay = CalendarDay.today();
                        calendar.set(calendarDay.getYear(), calendarDay.getMonth() - 1, calendarDay.getDay());
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                .child("DailyBooking").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mnthstr = calendar.get(Calendar.MONTH) + 1;
                        yrstr = calendar.get(Calendar.YEAR);
                        daystr = calendar.get(Calendar.DAY_OF_MONTH);
                        String todayDate = yrstr + "-" + mnthstr + "-" + daystr;

                        //checking validity of date
                        if (year < yrstr) {
                            Toast.makeText(HotelActivity.this, "Invalid Date!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (year == yrstr && (month + 1) < mnthstr) {
                            Toast.makeText(HotelActivity.this, "Invalid Date!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (year == yrstr && (month + 1) == mnthstr && dayOfMonth < daystr) {
                            Toast.makeText(HotelActivity.this, "Invalid Date!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseDatabase.getInstance().getReference()
                                .child("DailyBooking").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.hasChild(todayDate)) {
                                    FirebaseDatabase.getInstance().getReference().child("Hotels")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.hasChild("roomNo")) {

                                                        mHotelDataType = snapshot.getValue(HotelDataType.class);
                                                        roomNo = mHotelDataType.getRoomNo();
                                                        Toast.makeText(HotelActivity.this, "Open booking successful", Toast.LENGTH_LONG).show();

                                                        while (true) {
                                                            mnthstr = calendar.get(Calendar.MONTH) + 1;
                                                            yrstr = calendar.get(Calendar.YEAR);
                                                            daystr = calendar.get(Calendar.DAY_OF_MONTH);
                                                            for (int i = 1; i <= roomNo; i++) {
                                                                dailyBookingDT = new DailyBookingDT(false, "None", i);
                                                                reference.child(yrstr + "-" + mnthstr + "-" + daystr).child("" + i).setValue(dailyBookingDT);
                                                            }
                                                            //Toast.makeText(HotelActivity.this, yrstr + " " + mnthstr + " " + daystr, Toast.LENGTH_SHORT).show();
                                                            if ((mnthstr == (month + 1)) && (yrstr == year) && (daystr == dayOfMonth)) {
                                                                break;
                                                            }
                                                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                                                        }
                                                    } else {
                                                        roomNoEditText.setError("Enter num of rooms");
                                                        roomNoEditText.requestFocus();
                                                        return;
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                    // Toast.makeText(HotelActivity.this,""+calendar.get(Calendar.MONTH),Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(HotelActivity.this, "Booking already updated!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });

            }
        };


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                createRoomList();
                progressBar.setVisibility(View.GONE);
            }
        });

        /*logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HotelActivity.this,"befr "+sharedPreferences.getString("username","not found"),Toast.LENGTH_SHORT).show();
                editor.clear();
                editor.apply();
                Toast.makeText(HotelActivity.this,sharedPreferences.getString("username","not found"),Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                startActivity(new Intent(HotelActivity.this, LogInActivity.class));
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
                progressBar.setVisibility(View.VISIBLE);
                DatabaseReference bookRoomRef = FirebaseDatabase.getInstance().getReference("DailyBooking")
                        .child(mAuth.getCurrentUser().getUid()).child(formattedBookingDate)
                        .child(String.valueOf(position + 1));
                bookRoomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DailyBookingDT dailyBookingDT = snapshot.getValue(DailyBookingDT.class);
                        if (dailyBookingDT.getIsBooked()) {
                            if (dailyBookingDT.getBookUserId().equals(mAuth.getCurrentUser().getUid())) {
                                progressBar.setVisibility(View.GONE);
                                mAlertDialog = new AlertDialog.Builder(HotelActivity.this)
                                        .setTitle("Canceled booking in offline ?")
                                        .setMessage("room no " + String.valueOf(position + 1) + " will available for booking.")
                                        .setPositiveButton("Yes", null)
                                        .setNegativeButton("No", null)
                                        .show();
                                Button posButton, negButton;
                                posButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                negButton = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                                posButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bookRoomRef.child("isBooked").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(HotelActivity.this, "Room no " + String.valueOf(position + 1) + " marked as unbooked", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(HotelActivity.this, "Failed!", Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });
                                        bookRoomRef.child("bookUserId").setValue("None");

                                        //Toast.makeText(ConsumerActivity.this,"room no " + String.valueOf(position+1), Toast.LENGTH_SHORT).show();
                                        mAlertDialog.dismiss();

                                    }
                                });

                            }
                            // flag[0]=1;
                            else
                                Toast.makeText(HotelActivity.this, "Room no " + String.valueOf(position + 1) + " alreaday booked", Toast.LENGTH_LONG).show();

                        } else {

                            mAlertDialog = new AlertDialog.Builder(HotelActivity.this)
                                    .setTitle("Booked in offline ?")
                                    .setMessage("room no " + String.valueOf(position + 1) + " will be marked as booked.")
                                    .setPositiveButton("Yes", null)
                                    .setNegativeButton("No", null)
                                    .show();
                            Button posButton, negButton;
                            posButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            negButton = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                            posButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bookRoomRef.child("isBooked").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(HotelActivity.this, "Room no " + String.valueOf(position + 1) + " marked as booked", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HotelActivity.this, "Failed!", Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    });
                                    bookRoomRef.child("bookUserId").setValue(mAuth.getCurrentUser().getUid());

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
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void customOnLongClick(int position, View v) {

            }
        });

    }

    public void createRoomList() {
        String roomNoString, floorNoString, avgRoomPrizeString;
        roomNoString = roomNoEditText.getText().toString();
        floorNoString = floorNoEditText.getText().toString();
        avgRoomPrizeString = avgRoomPrizeET.getText().toString();
        if (roomNoString.isEmpty()) {
            roomNoEditText.setError("Enter num of rooms");
            roomNoEditText.requestFocus();
            return;
        }
        if (floorNoString.isEmpty()) {
            floorNoEditText.setError("Enter num of rooms");
            floorNoEditText.requestFocus();
            return;
        }
        if (avgRoomPrizeString.isEmpty()) {
            avgRoomPrizeET.setError("Enter prize!");
            avgRoomPrizeET.requestFocus();
            return;
        }
        roomNo = Integer.parseInt(roomNoString);
        floorNo = Integer.parseInt(floorNoString);
        roomPrize = Double.parseDouble(avgRoomPrizeString);
        if (roomNo < 1) {
            roomNoEditText.setError("Enter valid num of rooms");
            roomNoEditText.requestFocus();
            return;
        }
        if (roomPrize <= 0.0) {
            avgRoomPrizeET.setError("Enter a valid prize!");
            avgRoomPrizeET.requestFocus();
            return;
        }
        if (floorNo < 1) {
            floorNoEditText.setError("Enter valid num of floors");
            floorNoEditText.requestFocus();
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Hotels");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().getUid())) {
                    Toast.makeText(HotelActivity.this, "Already Entered!", Toast.LENGTH_SHORT).show();
                    //mRoomAdapter.notifyDataSetChanged();
                    return;
                } else {
                    mHotelDataType = new HotelDataType(roomNo, floorNo, 0.0, roomPrize, 5.0);
                    //hotel default things
                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(mHotelDataType).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int i = 1; i <= roomNo; i++) {
                                    String htlId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    roomDataType = new RoomDataType(Double.parseDouble(avgRoomPrizeString), 0, 5.0);
                                    //roomListArray.clear();
                                    FirebaseDatabase.getInstance().getReference("RoomProperties").child(htlId).child(String.valueOf(i)).setValue(roomDataType);
                                }
                                Toast.makeText(HotelActivity.this, "Properties saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HotelActivity.this, "Operation failed!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //mRoomAdapter.notifyDataSetChanged();


    }


    @Override
    protected void onResume() {
        /*Toast.makeText(this,"onstart",Toast.LENGTH_SHORT).show();
        getRoomList();
        mRoomAdapter.notifyDataSetChanged();*/
        super.onResume();
        //adaptRecycler();


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            //log out
            case R.id.logOutItemMenu:
                PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
                mAuth.signOut();
                startActivity(new Intent(HotelActivity.this, LogInActivity.class));
                finish();
                return true;
            case R.id.changePassItemMenu:
                startActivity(new Intent(HotelActivity.this, HotelChangePasswordActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_out_menu, menu);
        return true;
    }

    private static String getFormattedDate(CalendarDay calendarDay) {
        return calendarDay.getYear() + "-" + calendarDay.getMonth() + "-" + calendarDay.getDay();
    }

}