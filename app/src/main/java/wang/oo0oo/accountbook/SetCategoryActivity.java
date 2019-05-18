package wang.oo0oo.accountbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;

//TODO: 设置分类以后再做吧，先用着
public class SetCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_category);
        Toolbar toolbar = findViewById(R.id.toolbar_set_category);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_set_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add_new_category:
                Toast.makeText(SetCategoryActivity.this, "添加新分类", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.submit_add_category:
                Toast.makeText(SetCategoryActivity.this, "提交", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
        }
        return true;
    }
}
