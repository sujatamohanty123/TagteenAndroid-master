package in.tagteen.tagteen;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.tagteen.tagteen.util.Utils;

public class TermsOfRewardsActivity extends AppCompatActivity {
    private TextView lblTerms;
    private TextView lblRockstarPriceDetails;
    private ImageView imgBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_terms_of_rewards);

        this.initWidgets();
        this.bindEvents();
    }

    private void initWidgets() {
        this.lblTerms = findViewById(R.id.lblTerms);
        this.lblRockstarPriceDetails = findViewById(R.id.lblRockstarPriceDetails);
        this.imgBack = findViewById(R.id.imgBack);

        //this.lblTerms.setText(Utils.getRewardsTermsAndConditions());
        this.lblRockstarPriceDetails.setText(Html.fromHtml(Utils.getRockstarPriceDetails()));
    }

    private void bindEvents() {
        this.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
