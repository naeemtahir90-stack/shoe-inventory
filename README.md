# SoleStock - Offline Shoe Store Inventory & Profit Tracker

A modern, fast, and completely offline native Android application designed specifically for shoe store owners. It tracks incoming inventory batches, shoe sizes (Men, Ladies, Kids), costs, retail selling prices, profits, and physical shelf locations with zero internet required.

## Key Features

1. **100% Offline with Room (SQLite)**: All inventory, prices, and sales data are stored securely on the local device.
2. **Product Photos**: Attach 1 clear photo per shoe using the camera or gallery.
3. **Department Size Presets**:
   - **Men**: Auto-loads standard sizes (EU 39–45 / UK 6–11)
   - **Ladies**: Auto-loads women's sizes (EU 35–41 / UK 3–8)
   - **Kids**: Auto-loads children's sizes (EU 24–34)
   - **Carton Quick-Add**: Tap "+1 to All Sizes" to add wholesale carton distributions instantly.
4. **Live Financial Dashboard**:
   - Total Stock (pairs in store)
   - Total Invested Cost (wholesale money tied up in inventory)
   - Expected Retail Value
   - Total Potential Profit & Margin %
5. **Quick Sale Logger (-1 Pair)**:
   - Tap any shoe size to record a sale.
   - Enter negotiated/bargained price with minimum-price protection warning.
   - Automatically decrements inventory and logs net profit.
6. **Privacy Mode (Hide Cost PIN)**:
   - Toggle eye icon in the top bar to mask wholesale cost prices and profits when staff or customers are viewing the screen.
7. **Daily Sales Closing Report**:
   - End-of-day summary of total pairs sold, cash collected, and actual profit earned.

## How to Build & Run in Android Studio

1. **Extract the ZIP file** to your computer.
2. Open **Android Studio** (Hedgehog, Iguana, Jellyfish, or newer).
3. Click **File -> Open...** and select the `ShoeStoreInventory` folder.
4. Allow Gradle to sync dependencies (it uses Gradle 8.3+ and Android SDK 34).
5. Connect your Android phone via USB (with Developer Options / USB Debugging enabled) or start an Android Emulator (API 29+ / Android 10+).
6. Click the green **Run (▶)** button to install and run the app on your phone!

## Generate Standalone APK to Install on Any Phone

1. In Android Studio, click **Build -> Build Bundle(s) / APK(s) -> Build APK(s)**.
2. Once finished, click **locate** in the popup.
3. Copy the `app-debug.apk` file to your phone via USB or WhatsApp, tap the file, and install it directly!
