package vn.edu.vhu.ltdd.a5intent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** NC1: màn hình thứ 3 – nhập ghi chú, trả kết quả về màn hình 2. */
public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "A5_231A290134";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);
        Log.d(TAG, "C.onCreate (savedInstanceState "
                + (savedInstanceState == null ? "= null" : "!= null") + ")");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        TextView tvChoAi = findViewById(R.id.tvChoAi);
        EditText edtGhiChu = findViewById(R.id.edtGhiChu);
        Button btnLuu = findViewById(R.id.btnLuuGhiChu);
        Button btnHuy = findViewById(R.id.btnHuyGhiChu);

        String tenLienHe = getIntent().getStringExtra(MainActivity.EXTRA_TEN_LIEN_HE);
        tvChoAi.setText(getString(R.string.note_for, tenLienHe == null ? "?" : tenLienHe));

        // Nếu màn hình 2 đã có ghi chú cũ thì điền sẵn để sửa
        String ghiChuCu = getIntent().getStringExtra(MainActivity.EXTRA_GHI_CHU);
        if (ghiChuCu != null) {
            edtGhiChu.setText(ghiChuCu);
        }

        btnLuu.setOnClickListener(v -> {
            String ghiChu = edtGhiChu.getText().toString().trim();
            if (ghiChu.isEmpty()) {
                edtGhiChu.setError(getString(R.string.err_empty));
                return;
            }
            Intent ketQua = new Intent();
            ketQua.putExtra(MainActivity.EXTRA_GHI_CHU, ghiChu);
            setResult(RESULT_OK, ketQua);
            finish();
        });

        btnHuy.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "C.onDestroy");
        super.onDestroy();
    }
}