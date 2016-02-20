package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

        sideList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch(position){
                            case 0:
                                Intent intent = new Intent(HomePageActivity.this, ProfileSetup.class);
                                startActivity(intent);
                            case 1:

                            case 2:

                            case 3:

                            case 4:
                        }

                    }
                }
        );
    }
}
