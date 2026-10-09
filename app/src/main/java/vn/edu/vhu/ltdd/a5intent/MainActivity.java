package vn.edu.vhu.ltdd.a5intent;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.os.BundleCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A5_231A290134";

    // Khóa extras: dùng hằng số để gõ sai là báo lỗi ngay lúc biên dịch
    public static final String EXTRA_CONTACT = "extra_contact";
    public static final String EXTRA_NGUOI_GUI = "extra_nguoi_gui";
    public static final String EXTRA_GHI_CHU = "extra_ghi_chu";          // NC1
    public static final String EXTRA_TEN_LIEN_HE = "extra_ten_lien_he";  // NC1
    public static final String EXTRA_DANH_SACH = "extra_danh_sach";      // NC2

    private static final String KEY_DANH_SACH = "key_danh_sach";

    private EditText edtHoTen, edtDienThoai, edtEmail;
    private TextView tvKetQuaTraVe;

    // NC2: danh sách liên hệ giữ trong bộ nhớ
    private final ArrayList<Contact> danhSach = new ArrayList<>();

    private final ActivityResultLauncher<Intent> chiTietLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Contact daSua = IntentCompat.getParcelableExtra(
                            result.getData(), EXTRA_CONTACT, Contact.class);
                    String ghiChu = result.getData().getStringExtra(EXTRA_GHI_CHU); // NC1
                    if (daSua != null) {
                        edtHoTen.setText(daSua.getHoTen());
                        if (ghiChu != null && !ghiChu.isEmpty()) {
                            tvKetQuaTraVe.setText(getString(
                                    R.string.returned_with_note, daSua.getHoTen(), ghiChu));
                        } else {
                            tvKetQuaTraVe.setText(getString(R.string.returned, daSua.getHoTen()));
                        }
                        Log.d(TAG, "A nhận kết quả: " + daSua.getHoTen() + " | ghi chú: " + ghiChu);
                    }
                } else {
                    tvKetQuaTraVe.setText(R.string.returned_cancel);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "A.onCreate (savedInstanceState "
                + (savedInstanceState == null ? "= null" : "!= null") + ")");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        edtHoTen = findViewById(R.id.edtHoTen);
        edtDienThoai = findViewById(R.id.edtDienThoai);
        edtEmail = findViewById(R.id.edtEmail);
        tvKetQuaTraVe = findViewById(R.id.tvKetQuaTraVe);

        Button btnChiTiet = findViewById(R.id.btnChiTiet);
        Button btnThemDs = findViewById(R.id.btnThemDs);
        Button btnXemDs = findViewById(R.id.btnXemDs);
        Button btnGoi = findViewById(R.id.btnGoi);
        Button btnWeb = findViewById(R.id.btnWeb);
        Button btnChiaSe = findViewById(R.id.btnChiaSe);

        btnChiTiet.setOnClickListener(v -> moManHinhChiTiet());
        btnThemDs.setOnClickListener(v -> themVaoDanhSach());
        btnXemDs.setOnClickListener(v -> xemDanhSach());
        btnGoi.setOnClickListener(v -> goiDien());
        btnWeb.setOnClickListener(v -> moTrangWeb());
        btnChiaSe.setOnClickListener(v -> chiaSe());

        // NC2: khôi phục danh sách sau khi xoay màn hình
        if (savedInstanceState != null) {
            ArrayList<Contact> daLuu = BundleCompat.getParcelableArrayList(
                    savedInstanceState, KEY_DANH_SACH, Contact.class);
            if (daLuu != null) {
                danhSach.addAll(daLuu);
            }
        }

        // NC4: nhận văn bản do ứng dụng khác chia sẻ tới
        xuLyChiaSeDen(savedInstanceState);
    }

    // NC3: hiệu ứng trượt khi MỞ màn hình mới
    private ActivityOptionsCompat hieuUngMo() {
        return ActivityOptionsCompat.makeCustomAnimation(
                this, R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // ============ INTENT TƯỜNG MINH (explicit) ============

    private void moManHinhChiTiet() {
        String hoTen = edtHoTen.getText().toString().trim();
        if (hoTen.isEmpty()) {
            edtHoTen.setError(getString(R.string.err_empty));
            return;
        }
        Contact contact = new Contact(hoTen,
                edtDienThoai.getText().toString().trim(),
                edtEmail.getText().toString().trim());

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_CONTACT, contact);
        intent.putExtra(EXTRA_NGUOI_GUI, TAG);
        chiTietLauncher.launch(intent, hieuUngMo());
    }

    // ============ NC2: TRUYỀN DANH SÁCH ĐỐI TƯỢNG ============

    private void themVaoDanhSach() {
        String hoTen = edtHoTen.getText().toString().trim();
        if (hoTen.isEmpty()) {
            edtHoTen.setError(getString(R.string.err_empty));
            return;
        }
        danhSach.add(new Contact(hoTen,
                edtDienThoai.getText().toString().trim(),
                edtEmail.getText().toString().trim()));
        Toast.makeText(this, getString(R.string.added_to_list, danhSach.size()),
                Toast.LENGTH_SHORT).show();
    }

    private void xemDanhSach() {
        Intent intent = new Intent(this, DanhSachActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_DANH_SACH, danhSach);
        startActivity(intent, hieuUngMo().toBundle());
    }

    // ============ NC4: NHẬN CHIA SẺ TỪ ỨNG DỤNG KHÁC ============

    private void xuLyChiaSeDen(Bundle savedInstanceState) {
        // Chỉ xử lý lần đầu; nếu không, mỗi lần xoay màn hình sẽ ghi đè lại ô họ tên
        if (savedInstanceState != null) {
            return;
        }
        Intent intent = getIntent();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(intent.getAction())
                && type != null && type.startsWith("text/")) {
            String vanBan = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (vanBan != null) {
                edtHoTen.setText(vanBan);
                Toast.makeText(this, R.string.shared_received, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "A nhận chia sẻ: " + vanBan);
            }
        }
    }

    // ============ INTENT NGẦM ĐỊNH (implicit) ============

    private void goiDien() {
        String sdt = edtDienThoai.getText().toString().trim();
        if (sdt.isEmpty()) {
            edtDienThoai.setError(getString(R.string.err_empty));
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + sdt));
        moAnToan(intent);
    }

    private void moTrangWeb() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.school_url)));
        moAnToan(intent);
    }

    private void chiaSe() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text,
                edtHoTen.getText().toString(), edtDienThoai.getText().toString()));
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }

    private void moAnToan(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.err_no_app, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Không có ứng dụng nào xử lý: " + intent.getAction(), e);
        }
    }

    // ============ LOG VÒNG ĐỜI (Phần 6) ============

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "A.onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "A.onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "A.onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "A.onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "A.onRestart");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "A.onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_DANH_SACH, danhSach); // NC2
        Log.d(TAG, "A.onSaveInstanceState");
    }
}