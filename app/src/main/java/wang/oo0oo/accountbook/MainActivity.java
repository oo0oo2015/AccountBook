package wang.oo0oo.accountbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.litepal.LitePalBase;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import wang.oo0oo.accountbook.pojo.Record;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private List<Record> recordList = new ArrayList<>();
    private RecordAdapter recordAdapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // 下拉刷新控件
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecords();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        NavigationView navView = findViewById(R.id.nav_view);
        //navView.setCheckedItem(R.id.nav_set_category);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_set_category:
                        mDrawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this, SetCategoryActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_exit:
                        mDrawerLayout.closeDrawers();
                        finish();
                        break;
                    default:
                }
                return true;
            }
        });

        // 主界面 RecyclerView 逻辑
        loadRecordList();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recordAdapter = new RecordAdapter(recordList);
        recyclerView.setAdapter(recordAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.add_new_record:
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
                break;
            case R.id.test_add_record:
                // 加载一组测试数据并持久化到数据库
                initRecords();
                for (Record r : recordList) {
                    r.save();
                }
                Toast.makeText(MainActivity.this, "成功向数据库添加一组初始数据", Toast.LENGTH_SHORT).show();
                reloadRecordList();
                break;
            case R.id.test_delete_all_record:
                List<Record> recordList1 = DataSupport.findAll(Record.class);
                for (Record r : recordList1) {
                    r.delete();
                }
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        reloadRecordList();
    }

    // 向 recordList 加载一组测试数据
    private void initRecords() {
        Record record1 = new Record("支出", "交通", new Date(), 1200, "飞机票");
        Record record2 = new Record("支出", "餐费", new Date(), 58.52, "吃麻辣烫");
        Record record3 = new Record("收入", "奖金", new Date(), 599.5, "干得不错");
        Record record4 = new Record("支出", "交通", new Date(), 85.0, "打了个车");
        recordList.add(record1);
        recordList.add(record2);
        recordList.add(record3);
        recordList.add(record4);
    }

    // 初次从数据库加载（加载到 recordList ）
    private void loadRecordList() {
        LitePal.getDatabase();
        recordList = DataSupport.findAll(Record.class);
    }

    // 再次从数据库加载（加载到 recordList 并刷新 RecyclerView）
    private void reloadRecordList() {
        recordList = DataSupport.findAll(Record.class);
        recordAdapter.setData(recordList);
        recordAdapter.notifyDataSetChanged();
    }

    // 下拉刷新列表
    private void refreshRecords() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 刷新太快了，给点儿延迟吧
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

}
