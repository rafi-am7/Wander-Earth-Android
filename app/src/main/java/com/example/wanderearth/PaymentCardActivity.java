package com.example.wanderearth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentCardActivity extends AppCompatActivity {
    private TextView hotelFeeAmounttv, gatewayAmounttv, vatAmounttv, totalAmounttv;
    private TextView successButton, failureButton;
    private TextView hotelRoomDestv, bookingDatetv;
    private String hotelId, roomNo, hotelName, bookingDate;
    private int hotelFee;
    private int paymentGatewayFee, vat, totalFee;
    private DatabaseReference reference, bookRoomRef, hotelBalRef;
    private RoomDataType mRoomDataType;
    private HotelDataType mHotelDataType;
    private double hotelBal;
    private DailyBookingDT mDailyBookingDT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_card);
        hotelFeeAmounttv = findViewById(R.id.tvHfeeAmountPaycard);
        gatewayAmounttv = findViewById(R.id.tvGateFeeAmountPaycard);
        vatAmounttv = findViewById(R.id.tvVatAmountPaycard);
        totalAmounttv = findViewById(R.id.tvTotalAmountPaycard);
        successButton = findViewById(R.id.tvSuccessPaycard);
        failureButton = findViewById(R.id.tvFailedPaycard);
        hotelRoomDestv = findViewById(R.id.tvHotelandRoomPaycard);
        bookingDatetv = findViewById(R.id.tvDatePaycard);

        Intent intent = getIntent();
        hotelId = intent.getStringExtra("hotel_id");
        roomNo = intent.getStringExtra("room_no");
        hotelName = intent.getStringExtra("hotel_name");
        bookingDate = intent.getStringExtra("booking_date");

        hotelRoomDestv.setText(hotelName);
        bookingDatetv.setText("Room no " + roomNo + " for " + bookingDate);

        bookRoomRef = FirebaseDatabase.getInstance().getReference("DailyBooking").child(hotelId).child(bookingDate).child(roomNo);
        hotelBalRef = FirebaseDatabase.getInstance().getReference("Hotels");
        reference = FirebaseDatabase.getInstance().getReference().child("RoomProperties").child(hotelId).child(roomNo);
        //dailyBookRef = FirebaseDatabase.getInstance().getReference("DailyBooking").child(hotelId).child(bookingDate).child(roomNo);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mRoomDataType = snapshot.getValue(RoomDataType.class);
                if (mRoomDataType != null) {
                    hotelFee = (int) Math.ceil(mRoomDataType.getRoomPrize());
                    vat = (int) Math.ceil(hotelFee * 0.05);
                    paymentGatewayFee = 27;
                    totalFee = hotelFee + vat + paymentGatewayFee;


                    hotelFeeAmounttv.setText(hotelFee + ".0");
                    vatAmounttv.setText(vat + ".0");
                    gatewayAmounttv.setText(paymentGatewayFee + ".0");
                    totalAmounttv.setText(totalFee + ".0");
                }

                successButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        bookRoomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                mDailyBookingDT = snapshot.getValue(DailyBookingDT.class);
                                if (mDailyBookingDT != null) {
                                    if (!mDailyBookingDT.getIsBooked()) {
                                        //is booked
                                        bookRoomRef.child("isBooked").setValue(true);
                                        bookRoomRef.child("bookUserId").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        //updating hotel balance;
                                        hotelBalRef.child(hotelId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                mHotelDataType = snapshot.getValue(HotelDataType.class);
                                                if (mHotelDataType != null) {
                                                    hotelBal = mHotelDataType.getBalance();
                                                    //hotelBal+=totalFee;
                                                    hotelBal += hotelFee;
                                                    hotelBalRef.child(hotelId).child("balance").setValue(hotelBal);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        Intent intent2 = new Intent(PaymentCardActivity.this, ConsumerActivity.class);
                                        intent2.putExtra("transaction", "success");
                                        startActivity(intent2);
                                        finish();
                                    } else {
                                        Toast.makeText(PaymentCardActivity.this, "Sorry, this room has booked recently!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });


                //failed
                failureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent2 = new Intent(PaymentCardActivity.this, ConsumerActivity.class);
                        intent2.putExtra("transaction", "failed");
                        startActivity(intent2);
                        finish();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}