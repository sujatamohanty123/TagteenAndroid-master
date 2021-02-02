package in.tagteen.tagteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import in.tagteen.tagteen.Model.placements.ApplyPlacementResponse;
import in.tagteen.tagteen.Model.placements.Placements;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacementDetailsActivity extends AppCompatActivity {
    private AppCompatImageView imgPlacementImage;
    private AppCompatImageView imgPlacementThumbnail;

    private TextView lblPostedAgo;
    private TextView lblPlacementTitle;
    private TextView lblPlacementCity;
    private TextView lblPlacementPay;
    private TextView lblPlacementDescription;
    private TextView lblApply;

    private Placements.Placement placement;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_details);

        this.initWidgets();
        this.bindEvents();
        this.bindDetails();
    }

    private void initWidgets() {
        if (getIntent().getSerializableExtra(Constants.PLACEMENT) != null) {
            this.placement = (Placements.Placement) getIntent().getSerializableExtra(Constants.PLACEMENT);
        }
        this.imgPlacementImage = findViewById(R.id.imgPlacementImage);
        this.imgPlacementThumbnail = findViewById(R.id.imgThumbnail);

        this.lblPostedAgo = findViewById(R.id.lblPostedAgo);
        this.lblPlacementTitle = findViewById(R.id.lblPlacementTitle);
        this.lblPlacementCity = findViewById(R.id.lblPlacementCity);
        this.lblPlacementPay = findViewById(R.id.lblPlacementPay);
        this.lblPlacementDescription = findViewById(R.id.lblPlacementDescription);
        this.lblApply = findViewById(R.id.lblApply);

        if (this.placement == null) {
            return;
        }
    }

    private void bindEvents() {
        this.imgPlacementImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewVideo();
            }
        });
        this.lblApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placement == null) {
                    return;
                }
                if (placement.getIsApplied().equals("1")) {
                    return;
                }
                applyForPlacement();
            }
        });
    }

    private void bindDetails() {
        if (this.placement == null) {
            return;
        }

        if (this.placement.getThumbnail() != null) {
            Utils.loadImageUsingGlide(this, this.imgPlacementThumbnail, this.placement.getThumbnail());
        }

        if (this.placement.getMediaLink() != null) {
            // set view image
            Utils.loadVideoThumnailUsingGlide(this, this.imgPlacementImage, this.placement.getMediaLink());
        } else {
            Utils.loadImageUsingGlide(this, this.imgPlacementImage, this.placement.getThumbnail());
        }

        if (this.placement.getPlacementTitle() != null) {
            this.lblPlacementTitle.setText("Placement : " + this.placement.getPlacementTitle());
        }
        if (this.placement.getCity() != null) {
            this.lblPlacementCity.setText("City : " + this.placement.getCity());
        }
        if (this.placement.getPayScale() != null) {
            this.lblPlacementPay.setText("Pay : " + this.placement.getPayScale());
        }
        if (this.placement.getDescription() != null) {
            this.lblPlacementDescription.setText(this.placement.getDescription());
        }
    }

    private void previewVideo() {
        if (this.placement.getMediaLink() == null) {
            return;
        }
        Intent intent = new Intent(PlacementDetailsActivity.this, VideoPreviewActivity.class);
        intent.putExtra(Constants.VIDEO_URL, this.placement.getMediaLink());
        startActivity(intent);
    }

    private void applyForPlacement() {
        if (this.placement == null) {
            return;
        }

        Apimethods apiMethods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        Call<ApplyPlacementResponse> call = apiMethods.applyPlacement(
                placement.getPlacementId(), this.userId, placement.getMediaLink(), placement.getThumbnail());

        call.enqueue(new Callback<ApplyPlacementResponse>() {
            @Override
            public void onResponse(Call<ApplyPlacementResponse> call, Response<ApplyPlacementResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isSuccess()) {
                        lblApply.setText("Applied");
                        Utils.showShortToast(PlacementDetailsActivity.this, "Successfully applied.");
                        placement.setIsApplied("1");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApplyPlacementResponse> call, Throwable t) {
                Utils.showShortToast(PlacementDetailsActivity.this, "Failed to apply.");
            }
        });
    }
}
