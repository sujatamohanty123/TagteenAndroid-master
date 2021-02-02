package in.tagteen.tagteen;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import in.tagteen.tagteen.Adapters.CalcAdapter;

public class SendTagActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    CalcAdapter calcAdapter;
    Toolbar toolbar3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.tagteen.tagteen.R.layout.activity_senda_tag);

        recyclerView = (RecyclerView) findViewById(in.tagteen.tagteen.R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(SendTagActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        calcAdapter = new CalcAdapter(SendTagActivity.this);
        recyclerView.setAdapter(calcAdapter);
        calcAdapter.notifyDataSetChanged();
    }



    @Override
    public void onResume()
    {
        super.onResume();
    }


}
