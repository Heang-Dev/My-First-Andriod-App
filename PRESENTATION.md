# Movie Booking App - Implementation Presentation

## Project Overview

A modern Android movie ticket booking application featuring a cinema-themed dark UI design. The app allows users to browse movie details, select showtimes, and proceed to booking.

**Package:** `chhun.meng.heang.myfirstandriodapp`
**Min SDK:** 24 (Android 7.0)
**Target SDK:** 36
**Language:** Java

---

## Project Structure

```
app/
├── src/main/
│   ├── java/chhun/meng/heang/myfirstandriodapp/
│   │   └── MainActivity.java          # Main activity with all logic
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml      # Main UI layout
│   │   ├── drawable/                   # Custom shapes & icons
│   │   │   ├── gradient_button_bg.xml
│   │   │   ├── date_selected_bg.xml
│   │   │   ├── slot_available_bg.xml
│   │   │   └── ...
│   │   └── values/
│   │       ├── colors.xml             # Theme colors
│   │       └── strings.xml            # All text content
│   └── AndroidManifest.xml
└── build.gradle.kts
```

---

## Key Technologies & Dependencies

| Dependency | Purpose |
|------------|---------|
| `androidx.appcompat` | Backward-compatible Android components |
| `com.google.android.material` | Material Design components (Chips, Buttons) |
| `androidx.activity` | Edge-to-edge display support |
| `androidx.constraintlayout` | Flexible layout system |

---

## UI Implementation

### 1. Layout Structure (activity_main.xml)

The main layout uses a **ScrollView** with nested **LinearLayout** for vertical content organization:

```xml
<ScrollView>
    <LinearLayout orientation="vertical">
        <!-- 1. Movie Thumbnail with Play Button -->
        <!-- 2. Movie Title & Year -->
        <!-- 3. IMDb Rating & Genre Chips -->
        <!-- 4. Synopsis Section -->
        <!-- 5. Date Selection -->
        <!-- 6. Time Slots Grid -->
        <!-- 7. Cinema Location -->
        <!-- 8. Price & Book Button -->
    </LinearLayout>
</ScrollView>
```

### 2. Movie Thumbnail Section

Uses **FrameLayout** to overlay play button on movie poster:

```xml
<FrameLayout>
    <ImageView android:background="@drawable/sample_movie_thumbnail" />
    <LinearLayout android:background="@color/overlay_dark">
        <ImageView android:src="@drawable/ic_play" />
        <TextView android:text="Watch Trailer" />
    </LinearLayout>
</FrameLayout>
```

### 3. Genre Chips (Material Design)

Horizontal scrollable chips using Material Design ChipGroup:

```xml
<HorizontalScrollView>
    <com.google.android.material.chip.ChipGroup>
        <com.google.android.material.chip.Chip
            android:text="Action"
            app:chipBackgroundColor="@color/chip_background"
            app:chipStrokeColor="@color/chip_stroke" />
        <!-- More chips... -->
    </com.google.android.material.chip.ChipGroup>
</HorizontalScrollView>
```

### 4. Date Selection (Horizontal Scroll)

Custom date selector with selected/unselected states:

```xml
<LinearLayout
    android:background="@drawable/date_selected_bg"
    android:orientation="vertical">
    <TextView android:text="Thu" />  <!-- Day -->
    <TextView android:text="26" />   <!-- Date -->
    <TextView android:text="Dec" />  <!-- Month -->
</LinearLayout>
```

### 5. Time Slots Grid

Uses **GridLayout** with 3 columns for time slots:

```xml
<GridLayout
    android:columnCount="3">
    <TextView
        android:layout_columnWeight="1"
        android:background="@drawable/slot_available_bg"
        android:text="10:00 AM" />
    <!-- More slots... -->
</GridLayout>
```

**Slot States:**
- Available (Green)
- Filling Fast (Yellow/Orange)
- Sold Out (Gray)
- Selected (Red)

### 6. Gradient Book Now Button

Material Button with custom gradient background:

```xml
<com.google.android.material.button.MaterialButton
    android:background="@drawable/gradient_button_bg"
    app:backgroundTint="@null"
    android:text="Book Now" />
```

**Key:** `app:backgroundTint="@null"` disables Material's default tint to show custom gradient.

---

## Custom Drawables

### Gradient Button Background (gradient_button_bg.xml)

```xml
<shape android:shape="rectangle">
    <gradient
        android:startColor="#E50914"
        android:centerColor="#FF4757"
        android:endColor="#FF6B81"
        android:angle="0" />
    <corners android:radius="30dp" />
</shape>
```

### Date Selection States

**Selected (date_selected_bg.xml):**
- Red background (#E50914)
- Rounded corners (12dp)

**Unselected (date_unselected_bg.xml):**
- Dark gray background (#2D2D2D)
- Subtle border

---

## Java Implementation (MainActivity.java)

### 1. View Initialization

```java
private void initViews() {
    // Synopsis views
    synopsisText = findViewById(R.id.synopsisText);
    readMoreBtn = findViewById(R.id.readMoreBtn);

    // Date items array
    dateItems = new LinearLayout[]{
        findViewById(R.id.date1),
        findViewById(R.id.date2),
        // ... 6 date items
    };

    // Time slots array
    timeSlots = new TextView[]{
        findViewById(R.id.slot1),
        // ... 5 time slots
    };
}
```

### 2. Synopsis Expand/Collapse

```java
private void toggleSynopsis() {
    if (isSynopsisExpanded) {
        synopsisText.setMaxLines(3);           // Collapse
        readMoreBtn.setText(R.string.read_more);
    } else {
        synopsisText.setMaxLines(Integer.MAX_VALUE);  // Expand
        readMoreBtn.setText(R.string.read_less);
    }
    isSynopsisExpanded = !isSynopsisExpanded;
}
```

### 3. Date Selection Logic

```java
private void selectDate(int index) {
    // Reset previous selection
    dateItems[selectedDateIndex].setBackgroundResource(R.drawable.date_unselected_bg);
    updateDateTextColors(selectedDateIndex, false);

    // Set new selection
    selectedDateIndex = index;
    dateItems[selectedDateIndex].setBackgroundResource(R.drawable.date_selected_bg);
    updateDateTextColors(selectedDateIndex, true);
}

private void updateDateTextColors(int index, boolean isSelected) {
    int textColor = isSelected ?
        getColor(R.color.date_text_selected) :
        getColor(R.color.date_text_unselected);

    // Update all TextViews inside the date item
    for (int i = 0; i < dateItem.getChildCount(); i++) {
        View child = dateItem.getChildAt(i);
        if (child instanceof TextView) {
            ((TextView) child).setTextColor(textColor);
        }
    }
}
```

### 4. Time Slot Selection with State Management

```java
private void selectTimeSlot(int index) {
    if (index == 2) return;  // Sold out - no selection

    resetSlotAppearance(selectedSlotIndex);  // Reset previous

    selectedSlotIndex = index;
    timeSlots[selectedSlotIndex].setBackgroundResource(R.drawable.slot_selected_bg);
    timeSlots[selectedSlotIndex].setTextColor(getColor(R.color.slot_selected));

    updatePrice();  // Dynamic pricing
}

private void resetSlotAppearance(int index) {
    switch (index) {
        case 0: case 4:  // Available
            timeSlots[index].setBackgroundResource(R.drawable.slot_available_bg);
            timeSlots[index].setTextColor(getColor(R.color.slot_available));
            break;
        case 1:  // Filling fast
            timeSlots[index].setBackgroundResource(R.drawable.slot_filling_fast_bg);
            break;
        // ... more cases
    }
}
```

### 5. Dynamic Pricing

```java
private void updatePrice() {
    String price;
    switch (selectedSlotIndex) {
        case 0: price = "$8.50";  break;  // Morning discount
        case 1: price = "$15.00"; break;  // Peak time premium
        case 3: price = "$12.50"; break;  // Evening standard
        case 4: price = "$10.00"; break;  // Late night
        default: price = "$12.50";
    }
    totalPrice.setText(price);
}
```

---

## Color Theme

The app uses a **Cinema Dark Theme** with red accents:

| Color | Hex | Usage |
|-------|-----|-------|
| Background | `#0D0D0D` | Main background |
| Primary | `#E50914` | Selected states, CTA buttons |
| Gold | `#FFD700` | Premium accents |
| Text Primary | `#FFFFFF` | Headings |
| Text Secondary | `#B3B3B3` | Body text |
| Available | `#00C853` | Time slot status |
| Filling Fast | `#FFB300` | Warning status |

---

## Edge-to-Edge Display

The app uses modern edge-to-edge display for immersive experience:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);  // Enable edge-to-edge
    setContentView(R.layout.activity_main);

    // Handle system bar insets
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });
}
```

---

## Key Features Summary

1. **YouTube-style Trailer Preview** - Thumbnail with play button overlay
2. **IMDb Rating Display** - Badge with star rating
3. **Genre Chips** - Horizontal scrollable Material Design chips
4. **Expandable Synopsis** - Read More/Less functionality
5. **Date Selector** - Horizontal scroll with visual selection feedback
6. **Time Slot Grid** - Color-coded availability status
7. **Dynamic Pricing** - Price changes based on selected showtime
8. **Gradient CTA Button** - Custom gradient background
9. **Cinema Location Card** - Location info with map link
10. **Edge-to-Edge Design** - Modern immersive display

---

## Future Improvements

- Seat selection screen
- Payment integration
- User authentication
- Movie search functionality
- Booking history
- Push notifications for booking reminders

---

## Demo Flow

1. Launch app → Movie details screen
2. Tap "Watch Trailer" → Toast notification
3. Expand/collapse synopsis → Read More/Less
4. Select different dates → Visual feedback
5. Select time slots → Price updates dynamically
6. Tap "Book Now" → Proceed to booking (Toast)
