package vn.edu.vhu.ltdd.a5intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

/** NC2: nhận một danh sách đối tượng Contact qua Intent và hiển thị. */
public class DanhSachActivity extends AppCompatActivity {

    private static final String TAG = "A5_231A290134";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_sach);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        TextView tvTieuDe = findViewById(R.id.tvTieuDeDs);
        ListView lvDanhSach = findViewById(R.id.lvDanhSach);
        Button btnDong = findViewById(R.id.btnDong);

        ArrayList<Contact> ds = IntentCompat.getParcelableArrayListExtra(
                getIntent(), MainActivity.EXTRA_DANH_SACH, Contact.class);

        if (ds == null || ds.isEmpty()) {
            tvTieuDe.setText(R.string.list_empty);
            Log.d(TAG, "D: danh sách rỗng");
        } else {
            tvTieuDe.setText(getString(R.string.list_count, ds.size()));
            ArrayList<String> dong = new ArrayList<>();
            for (int i = 0; i < ds.size(); i++) {
                Contact c = ds.get(i);
                dong.add((i + 1) + ". " + c.getHoTen() + " – " + c.getDienThoai());
            }
            lvDanhSach.setAdapter(new ArrayAdapter<>(
                    this, android.R.layout.simple_list_item_1, dong));
            Log.d(TAG, "D nhận " + ds.size() + " liên hệ");
        }

        btnDong.setOnClickListener(v -> finish());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}