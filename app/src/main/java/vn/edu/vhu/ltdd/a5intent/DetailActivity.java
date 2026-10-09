package vn.edu.vhu.ltdd.a5intent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "A5_231A290134";
    private static final String KEY_GHI_CHU = "key_ghi_chu";

    private Contact contact;
    private EditText edtHoTenMoi;
    private TextView tvGhiChu;
    private String ghiChu; // NC1: ghi chú nhận từ màn hình 3

    // NC1: nhận kết quả từ màn hình 3 (NoteActivity)
    private final ActivityResultLauncher<Intent> ghiChuLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String moi = result.getData().getStringExtra(MainActivity.EXTRA_GHI_CHU);
                    if (moi != null) {
                        ghiChu = moi;
                        hienGhiChu();
                        Log.d(TAG, "B nhận ghi chú từ C: " + moi);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        Log.d(TAG, "B.onCreate (savedInstanceState "
                + (savedInstanceState == null ? "= null" : "!= null") + ")");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        TextView tvThongTin = findViewById(R.id.tvThongTin);
        TextView tvNguoiGui = findViewById(R.id.tvNguoiGui);
        tvGhiChu = findViewById(R.id.tvGhiChu);
        edtHoTenMoi = findViewById(R.id.edtHoTenMoi);
        Button btnLuu = findViewById(R.id.btnLuu);
        Button btnHuy = findViewById(R.id.btnHuy);
        Button btnThemGhiChu = findViewById(R.id.btnThemGhiChu);

        // NC1: khôi phục ghi chú sau khi xoay màn hình (biến tự khai báo không tự được lưu)
        if (savedInstanceState != null) {
            ghiChu = savedInstanceState.getString(KEY_GHI_CHU);
        }
        hienGhiChu();

        contact = IntentCompat.getParcelableExtra(getIntent(),
                MainActivity.EXTRA_CONTACT, Contact.class);
        String nguoiGui = getIntent().getStringExtra(MainActivity.EXTRA_NGUOI_GUI);

        if (contact == null) {
            tvThongTin.setText(R.string.no_data);
            Log.w(TAG, "Không nhận được Contact từ Intent");
            return;
        }

        tvThongTin.setText(getString(R.string.detail_format,
                contact.getHoTen(), contact.getDienThoai(), contact.getEmail()));
        tvNguoiGui.setText(getString(R.string.sent_by, nguoiGui));
        edtHoTenMoi.setText(contact.getHoTen());

        btnLuu.setOnClickListener(v -> luuVaQuayLai());
        btnHuy.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        btnThemGhiChu.setOnClickListener(v -> moManHinhGhiChu());
    }

    private void hienGhiChu() {
        tvGhiChu.setText(ghiChu == null ? "" : getString(R.string.note_label, ghiChu));
    }

    // NC1: B (màn hình 2) mở C (màn hình 3), truyền tên liên hệ và ghi chú cũ sang
    private void moManHinhGhiChu() {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(MainActivity.EXTRA_TEN_LIEN_HE, contact.getHoTen());
        if (ghiChu != null) {
            intent.putExtra(MainActivity.EXTRA_GHI_CHU, ghiChu);
        }
        ghiChuLauncher.launch(intent, ActivityOptionsCompat.makeCustomAnimation(
                this, R.anim.slide_in_right, R.anim.slide_out_left));
    }

    private void luuVaQuayLai() {
        String hoTenMoi = edtHoTenMoi.getText().toString().trim();
        if (hoTenMoi.isEmpty()) {
            edtHoTenMoi.setError(getString(R.string.err_empty));
            return;
        }
        contact.setHoTen(hoTenMoi);

        Intent ketQua = new Intent();
        ketQua.putExtra(MainActivity.EXTRA_CONTACT, contact);
        if (ghiChu != null) {
            ketQua.putExtra(MainActivity.EXTRA_GHI_CHU, ghiChu); // NC1: chuyển tiếp về A
        }
        setResult(RESULT_OK, ketQua);
        Log.d(TAG, "B trả kết quả về A: " + hoTenMoi + " | ghi chú: " + ghiChu);
        finish();
    }

    // NC3: hiệu ứng trượt khi ĐÓNG màn hình (cả nút Lưu/Hủy lẫn nút Back đều gọi finish())
    @SuppressWarnings("deprecation")
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // ============ LOG VÒNG ĐỜI (Phần 6) ============

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "B.onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "B.onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "B.onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "B.onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "B.onRestart");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "B.onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_GHI_CHU, ghiChu);
        Log.d(TAG, "B.onSaveInstanceState");
    }
}