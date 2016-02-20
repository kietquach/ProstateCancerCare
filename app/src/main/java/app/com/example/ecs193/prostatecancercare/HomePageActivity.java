package app.com.example.ecs193.prostatecancercare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HomePageActivity extends AppCompatActivity {

    private ListView sideList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        sideList = (ListView) findViewById(R.id.sideList);
        String[] menuList = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, menuList);
        sideList.setAdapter(adapter);
    }
}
