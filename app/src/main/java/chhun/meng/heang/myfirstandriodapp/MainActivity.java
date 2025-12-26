package chhun.meng.heang.myfirstandriodapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView synopsisText;
    private TextView readMoreBtn;
    private boolean isSynopsisExpanded = false;

    private LinearLayout[] dateItems;
    private int selectedDateIndex = 0;

    private TextView[] timeSlots;
    private int selectedSlotIndex = 3; // Default to 7:00 PM (index 3)

    private TextView totalPrice;
    private Button bookNowBtn;
    private ImageView playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        // Synopsis
        synopsisText = findViewById(R.id.synopsisText);
        readMoreBtn = findViewById(R.id.readMoreBtn);

        // Date items
        dateItems = new LinearLayout[]{
                findViewById(R.id.date1),
                findViewById(R.id.date2),
                findViewById(R.id.date3),
                findViewById(R.id.date4),
                findViewById(R.id.date5),
                findViewById(R.id.date6)
        };

        // Time slots
        timeSlots = new TextView[]{
                findViewById(R.id.slot1),
                findViewById(R.id.slot2),
                findViewById(R.id.slot3),
                findViewById(R.id.slot4),
                findViewById(R.id.slot5)
        };

        // Other views
        totalPrice = findViewById(R.id.totalPrice);
        bookNowBtn = findViewById(R.id.bookNowBtn);
        playButton = findViewById(R.id.playButton);
    }

    private void setupClickListeners() {
        // Read More / Read Less toggle
        readMoreBtn.setOnClickListener(v -> toggleSynopsis());

        // Play button click
        playButton.setOnClickListener(v -> {
            Toast.makeText(this, "Playing trailer...", Toast.LENGTH_SHORT).show();
        });

        // Date selection
        for (int i = 0; i < dateItems.length; i++) {
            final int index = i;
            dateItems[i].setOnClickListener(v -> selectDate(index));
        }

        // Time slot selection (only for available slots)
        // Slot 0 (10:00 AM) - Available
        timeSlots[0].setOnClickListener(v -> selectTimeSlot(0));
        // Slot 1 (1:30 PM) - Filling Fast
        timeSlots[1].setOnClickListener(v -> selectTimeSlot(1));
        // Slot 2 (4:00 PM) - Sold Out - No click
        timeSlots[2].setOnClickListener(v -> {
            Toast.makeText(this, "This showtime is sold out", Toast.LENGTH_SHORT).show();
        });
        // Slot 3 (7:00 PM) - Selected/Available
        timeSlots[3].setOnClickListener(v -> selectTimeSlot(3));
        // Slot 4 (10:30 PM) - Available
        timeSlots[4].setOnClickListener(v -> selectTimeSlot(4));

        // Book Now button
        bookNowBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Proceeding to seat selection...", Toast.LENGTH_SHORT).show();
        });
    }

    private void toggleSynopsis() {
        if (isSynopsisExpanded) {
            synopsisText.setMaxLines(3);
            readMoreBtn.setText(R.string.read_more);
        } else {
            synopsisText.setMaxLines(Integer.MAX_VALUE);
            readMoreBtn.setText(R.string.read_less);
        }
        isSynopsisExpanded = !isSynopsisExpanded;
    }

    private void selectDate(int index) {
        // Reset previous selection
        dateItems[selectedDateIndex].setBackgroundResource(R.drawable.date_unselected_bg);
        updateDateTextColors(selectedDateIndex, false);

        // Set new selection
        selectedDateIndex = index;
        dateItems[selectedDateIndex].setBackgroundResource(R.drawable.date_selected_bg);
        updateDateTextColors(selectedDateIndex, true);

        Toast.makeText(this, "Date selected", Toast.LENGTH_SHORT).show();
    }

    private void updateDateTextColors(int index, boolean isSelected) {
        LinearLayout dateItem = dateItems[index];
        int textColor = isSelected ?
                getColor(R.color.date_text_selected) :
                getColor(R.color.date_text_unselected);

        for (int i = 0; i < dateItem.getChildCount(); i++) {
            View child = dateItem.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextColor(textColor);
            }
        }
    }

    private void selectTimeSlot(int index) {
        // Don't allow selection of sold out slot
        if (index == 2) return;

        // Reset previous selection to its original state
        resetSlotAppearance(selectedSlotIndex);

        // Set new selection
        selectedSlotIndex = index;
        timeSlots[selectedSlotIndex].setBackgroundResource(R.drawable.slot_selected_bg);
        timeSlots[selectedSlotIndex].setTextColor(getColor(R.color.slot_selected));

        // Update price based on selection
        updatePrice();
    }

    private void resetSlotAppearance(int index) {
        switch (index) {
            case 0:
            case 4:
                // Available slots
                timeSlots[index].setBackgroundResource(R.drawable.slot_available_bg);
                timeSlots[index].setTextColor(getColor(R.color.slot_available));
                break;
            case 1:
                // Filling fast
                timeSlots[index].setBackgroundResource(R.drawable.slot_filling_fast_bg);
                timeSlots[index].setTextColor(getColor(R.color.slot_filling_fast));
                break;
            case 2:
                // Sold out
                timeSlots[index].setBackgroundResource(R.drawable.slot_sold_out_bg);
                timeSlots[index].setTextColor(getColor(R.color.slot_sold_out));
                break;
            case 3:
                // Was selected, now available
                timeSlots[index].setBackgroundResource(R.drawable.slot_available_bg);
                timeSlots[index].setTextColor(getColor(R.color.slot_available));
                break;
        }
    }

    private void updatePrice() {
        // Different prices based on time slot
        String price;
        switch (selectedSlotIndex) {
            case 0: // Morning
                price = "$8.50";
                break;
            case 1: // Afternoon (Filling Fast - premium)
                price = "$15.00";
                break;
            case 3: // Evening
                price = "$12.50";
                break;
            case 4: // Late night
                price = "$10.00";
                break;
            default:
                price = "$12.50";
        }
        totalPrice.setText(price);
    }
}
