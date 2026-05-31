# Generic Medicine Finder Android App

Native Android Java starter project.

## Features
- Brand/Salt search
- Symptom based search
- Voice search 🎤
- Medicine Read popup
- Expected dose guidance caution
- Diabetes/kidney/liver warning
- Other brand comparison
- PMBI/Jan Aushadhi price comparison

## Setup

1. Android Studio install karein
2. Project folder open karein: `GenericMedicineFinder_Android`
3. Gradle sync hone dein
4. Run button press karein
5. Emulator ya Android phone connect karein

## Performance Tips
- Data ko local SQLite/Room DB me rakhein
- Search ko background thread me chalayein jab data 2000+ records ho
- RecyclerView use karein production me
- API calls ke liye Retrofit + caching use karein
- PMBI sync background WorkManager se daily 10 AM IST ke aas paas karein
