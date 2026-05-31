
package com.example.genericmedicinefinder;

import android.Manifest;
import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.view.*;
import android.widget.*;
import android.graphics.Color;
import java.util.*;

public class MainActivity extends Activity {

    EditText brandSearch, symptomSearch;
    LinearLayout resultBox;
    static final int VOICE_BRAND = 101;
    static final int VOICE_SYMPTOM = 102;

    static class Medicine {
        String brand, composition, salt, company, price, pmbiProduct, pmbiPrice, use, caution;
        Medicine(String brand, String composition, String salt, String company, String price,
                 String pmbiProduct, String pmbiPrice, String use, String caution) {
            this.brand = brand; this.composition = composition; this.salt = salt; this.company = company;
            this.price = price; this.pmbiProduct = pmbiProduct; this.pmbiPrice = pmbiPrice;
            this.use = use; this.caution = caution;
        }
    }

    ArrayList<Medicine> medicines = new ArrayList<>();
    HashMap<String, String> symptomSalt = new HashMap<>();

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        loadDemoData();
        buildUi();
    }

    void loadDemoData() {
        medicines.add(new Medicine("Telma 40", "Telmisartan 40 mg", "Telmisartan", "Glenmark", "130",
                "Telmisartan Tablets 40 mg", "22", "High BP ke liye doctor prescription par use hoti hai.",
                "Pregnancy, kidney disease, high potassium, low BP me doctor advice zaroori."));
        medicines.add(new Medicine("Telmikind 40", "Telmisartan 40 mg", "Telmisartan", "Mankind", "75",
                "Telmisartan Tablets 40 mg", "22", "High BP management.", "Kidney/diabetes patients me monitoring zaroori."));
        medicines.add(new Medicine("Crocin Advance", "Paracetamol 500 mg", "Paracetamol", "GSK", "30",
                "Paracetamol Tablets 500 mg", "10.5", "Fever aur mild pain me use hoti hai.",
                "Liver disease, alcohol use, overdose me dangerous ho sakti hai."));
        medicines.add(new Medicine("Calpol 500", "Paracetamol 500 mg", "Paracetamol", "GSK", "20.5",
                "Paracetamol Tablets 500 mg", "10.5", "Fever/pain.", "Child dose weight-based hoti hai."));
        medicines.add(new Medicine("Glycomet 500", "Metformin 500 mg", "Metformin", "USV", "38",
                "Metformin Tablets 500 mg", "12", "Diabetes medicine.", "Kidney/liver disease me doctor advice zaroori."));

        symptomSalt.put("fever", "Paracetamol");
        symptomSalt.put("bukhar", "Paracetamol");
        symptomSalt.put("body pain", "Paracetamol");
        symptomSalt.put("allergy", "Cetirizine");
        symptomSalt.put("sneezing", "Cetirizine");
        symptomSalt.put("high bp", "Telmisartan");
        symptomSalt.put("blood pressure", "Telmisartan");
        symptomSalt.put("diabetes", "Metformin");
        symptomSalt.put("sugar", "Metformin");
    }

    void buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 28, 28, 28);
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("Generic Medicine Finder");
        title.setTextSize(26);
        title.setTextColor(Color.rgb(7,55,99));
        title.setPadding(0,0,0,18);
        root.addView(title);

        TextView sync = new TextView(this);
        sync.setText("PMBI Sync: Daily 10:00 AM IST | Demo local data");
        sync.setTextColor(Color.DKGRAY);
        root.addView(sync);

        brandSearch = addSearchRow(root, "Brand/Salt Search", "Telma 40, Crocin, Metformin", VOICE_BRAND);
        Button brandBtn = button("Search Medicine");
        brandBtn.setOnClickListener(v -> searchBrand());
        root.addView(brandBtn);

        symptomSearch = addSearchRow(root, "Symptoms Search", "Bukhar, fever, body pain, high BP", VOICE_SYMPTOM);
        Button symptomBtn = button("Find by Symptoms");
        symptomBtn.setOnClickListener(v -> searchSymptoms());
        root.addView(symptomBtn);

        resultBox = new LinearLayout(this);
        resultBox.setOrientation(LinearLayout.VERTICAL);
        resultBox.setPadding(0,24,0,0);
        root.addView(resultBox);

        setContentView(scroll);
    }

    EditText addSearchRow(LinearLayout root, String label, String hint, int requestCode) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(18);
        tv.setPadding(0,22,0,8);
        root.addView(tv);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        row.addView(input, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        Button mic = button("🎤");
        mic.setOnClickListener(v -> startVoice(requestCode));
        row.addView(mic);

        root.addView(row);
        return input;
    }

    Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        return b;
    }

    void searchBrand() {
        String q = brandSearch.getText().toString().toLowerCase().trim();
        resultBox.removeAllViews();
        for (Medicine m : medicines) {
            if (m.brand.toLowerCase().contains(q) || m.composition.toLowerCase().contains(q) || m.salt.toLowerCase().contains(q)) {
                addMedicineCard(m);
            }
        }
        if (resultBox.getChildCount() == 0) addText("No medicine found");
    }

    void searchSymptoms() {
        String q = symptomSearch.getText().toString().toLowerCase().trim();
        resultBox.removeAllViews();
        String salt = null;
        for (String key : symptomSalt.keySet()) {
            if (q.contains(key)) { salt = symptomSalt.get(key); break; }
        }
        if (salt == null) {
            addText("Symptom match nahi mila. Severe symptoms me doctor se consult karein.");
            return;
        }
        addText("Possible salt: " + salt + " | Ye diagnosis nahi hai, sirf possible match hai.");
        for (Medicine m : medicines) {
            if (m.salt.equalsIgnoreCase(salt)) addMedicineCard(m);
        }
    }

    void addMedicineCard(Medicine m) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20,20,20,20);
        card.setBackgroundColor(Color.rgb(245,248,250));

        TextView name = new TextView(this);
        name.setText(m.brand + " | " + m.composition);
        name.setTextSize(18);
        name.setTextColor(Color.rgb(7,55,99));
        card.addView(name);

        TextView price = new TextView(this);
        price.setText("Brand Price ₹" + m.price + " | PMBI ₹" + m.pmbiPrice);
        card.addView(price);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button read = button("📖 Read");
        read.setOnClickListener(v -> showInfo(m));
        actions.addView(read);

        Button compare = button("₹ Compare");
        compare.setOnClickListener(v -> showCompare(m));
        actions.addView(compare);

        card.addView(actions);
        resultBox.addView(card);
        addSpace();
    }

    void showInfo(Medicine m) {
        String info =
                "Medicine: " + m.brand + "\n" +
                "Composition: " + m.composition + "\n\n" +
                "Use:\n" + m.use + "\n\n" +
                "Expected dose guidance:\n" +
                "- Adult/child dose age, weight, disease aur doctor advice par depend karti hai.\n" +
                "- Child dose often weight-based hoti hai.\n" +
                "- Exact dose doctor/pharmacist se confirm karein.\n\n" +
                "Avoid/Caution:\n" + m.caution + "\n\n" +
                "Diabetes/Kidney/Liver patients:\n" +
                "Chronic disease me medicine start/change karne se pehle doctor se poochna zaroori hai.\n\n" +
                "Disclaimer: Ye prescription nahi hai.";
        popup(m.brand, info);
    }

    void showCompare(Medicine m) {
        StringBuilder sb = new StringBuilder();
        sb.append("Selected: ").append(m.brand).append("\n");
        sb.append("Composition: ").append(m.composition).append("\n");
        sb.append("Brand Price: ₹").append(m.price).append("\n\n");
        sb.append("Same composition brands:\n");
        for (Medicine x : medicines) {
            if (x.composition.equalsIgnoreCase(m.composition)) {
                sb.append("- ").append(x.brand).append(" ₹").append(x.price).append("\n");
            }
        }
        sb.append("\nJan Aushadhi / PMBI:\n");
        sb.append(m.pmbiProduct).append(" ₹").append(m.pmbiPrice).append("\n");
        try {
            float saving = Float.parseFloat(m.price) - Float.parseFloat(m.pmbiPrice);
            if (saving > 0) sb.append("\nEstimated saving: ₹").append(saving);
        } catch(Exception ignored){}
        popup("Price Comparison", sb.toString());
    }

    void popup(String title, String msg) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    void addText(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        resultBox.addView(tv);
    }

    void addSpace() {
        Space s = new Space(this);
        resultBox.addView(s, new LinearLayout.LayoutParams(1, 18));
    }

    void startVoice(int requestCode) {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 55);
            return;
        }
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Bolkar search karein");
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && result.size() > 0) {
                if (requestCode == VOICE_BRAND) brandSearch.setText(result.get(0));
                if (requestCode == VOICE_SYMPTOM) symptomSearch.setText(result.get(0));
            }
        }
    }
}
