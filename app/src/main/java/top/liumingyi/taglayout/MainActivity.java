package top.liumingyi.taglayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  String newTag;
  TagLayout tagLayout;
  TangEditText editText;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    tagLayout = findViewById(R.id.tagLayout);
    List<String> data = new ArrayList<>();
    data.add("明年");
    data.add("下周");
    data.add("下个周日");
    data.add("下一个假期");
    data.add("下。");
    data.add("下一个生日");
    data.add("2018年12月30日");
    data.add("2018年12月30日");
    data.add("2018年12月30日");
    data.add("2018年12月30日");
    data.add("2018年12月30日");
    data.add("2018年");
    data.add("12月30日");
    data.add("嘿。");
    tagLayout.setData(data);

    editText = findViewById(R.id.tag_edt);
    editText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        newTag = s.toString();
      }
    });
  }

  public void onSubmit(View view) {
    tagLayout.addData(newTag);
    editText.setText("");
  }
}
