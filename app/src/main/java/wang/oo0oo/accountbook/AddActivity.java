package wang.oo0oo.accountbook;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import wang.oo0oo.accountbook.pojo.Record;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";

    private EditText editTextDate;

    private Record record;
    private Spinner spinnerType;
    private Spinner spinnerCategory;
    private EditText editTextAmount;
    private EditText editTextNote;
    private List<String> expendCategoryList;
    private List<String> incomeCategoryList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        if (record == null) {
            record = new Record();
        }

        Toolbar toolbar = findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // 控件安排上
        spinnerType = findViewById(R.id.spinner_type);
        spinnerCategory = findViewById(R.id.spinner_category);
        editTextAmount = findViewById(R.id.edit_text_amount);
        editTextNote = findViewById(R.id.edit_text_note);

        // 绑定适配器和事件监听器
        // TODO: 显示指定，之后要做到从数据库中取
        expendCategoryList = new ArrayList<String>() {{
            add("交通");
            add("购物");
            add("餐费");
        }};
        incomeCategoryList = new ArrayList<String>() {{
            add("奖金");
            add("工资");
        }};
        bindSpinnerCategoryData(true);

        // 为 spinnerType 绑定事件监听器
        spinnerType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = spinnerType.getSelectedItem().toString();
                if (Objects.equals("收入", selectedType)) {
                    bindSpinnerCategoryData(false);
                } else {
                    bindSpinnerCategoryData(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 为 spinnerCategory 绑定事件监听器
        spinnerCategory.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = spinnerCategory.getSelectedItem().toString();

                if (Objects.equals("添加新分类...", selectedCategory)) {
                    Intent intent = new Intent(AddActivity.this, SetCategoryActivity.class);
                    startActivity(intent);
                    // 当设置分类页面关闭后
                    //TODO: 重新加载“分类” List（expendCategoryList 和 incomeCategoryList）
                    //首先肯定要把“分类”信息持久化啊

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 设置 add 活动的日期选择器相关逻辑
        editTextDate = findViewById(R.id.edit_text_date);
        editTextDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDatePickDialog();
                    return true;
                }
                return false;
            }
        });
        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickDialog();
                }
            }
        });
    }

    // 给 Toolbar 设置菜单按钮（绑定 R.menu.toolbar_add 菜单）
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_add, menu);
        return true;
    }

    // 设置 Toolbar 上 menu 各按钮的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.submit_add:
                // 处理添加逻辑
                if (SetRecord()) {
                    // 写数据库
                    saveToDatabase();
                    finish();
                } else {
                    return true;
                }
                break;
            default:
        }
        return true;
    }

    //  add 活动的日期选择器相关逻辑（包括展示）
    private void showDatePickDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                AddActivity.this.editTextDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                //TODO: 把逻辑放到SetRecord()去
                // 算了算了，放不放也无所谓了(๑¯∀¯๑)
                record.setDate(new Date(year - 1900, month, dayOfMonth));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }


    private boolean SetRecord() {

        record.setType(spinnerType.getSelectedItem().toString());
        record.setCategory(spinnerCategory.getSelectedItem().toString());

        String amountString = editTextAmount.getText().toString();
        if (amountString.isEmpty()) {
            Toast.makeText(AddActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
            return false;
        }
        double amount = Double.parseDouble(amountString);
        if (amount <= 0) {
            Toast.makeText(AddActivity.this, "金额输入有误，请重新输入！", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (amount >= 100000000) {
            Toast.makeText(AddActivity.this, "金额超过范围", Toast.LENGTH_SHORT).show();
            return false;
        }
        record.setAmount(amount);

        String note = editTextNote.getText().toString();
        if (!note.isEmpty()) {
            record.setNote(note);
        }

        //TODO: 把日期处理逻辑移到这儿来（使用字符串组日期）
        // 算了算了，移不移也无所谓了(๑¯∀¯๑)
        if (record.getDate() == null) {
            Toast.makeText(AddActivity.this, "请选择日期", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveToDatabase() {
        if (record.save()) {
            Toast.makeText(AddActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddActivity.this, "添加失败。。。", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 将数据绑定到 SpinnerCategory 上（初始化及改变“类型”时被调用）
     * @param isExpend 是否是“支出”
     */
    private void bindSpinnerCategoryData(boolean isExpend) {
        ArrayAdapter<String> arrayAdapter;
        List<String> targetList = isExpend ? expendCategoryList : incomeCategoryList;
        List<String> list = new ArrayList<String>(targetList);
        list.add("添加新分类...");
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 为 spinnerCategory 绑定新的数据适配器
        spinnerCategory.setAdapter(arrayAdapter);
    }

}
