package in.tagteen.tagteen.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.Adapters.placements.PlacementsAdapter;
import in.tagteen.tagteen.IdeaBoxInfoActivity;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.placements.ApplyPlacementResponse;
import in.tagteen.tagteen.Model.placements.Placements;
import in.tagteen.tagteen.PlacementDetailsActivity;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.SearchPlacementActivity;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.base.BaseFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacementFragment extends BaseFragment {
  private boolean mScreenLoadedOnce = false;
  private RecyclerView recyclerViewPlacements;
  private LinearLayout layoutIdeabox;

  private int placementType = 1;
  private String userId;
  private int pageNumber = 1;
  private boolean apiCallBlocker = false;
  private boolean isDataLoading = false;
  private List<Placements.Placement> placementList = new ArrayList<>();
  private PlacementsAdapter placementsAdapter;
  private TextView lblNoJobs;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_placement, container, false);

    this.initWidgets(view);
    this.bindEvents();

    return view;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser && !mScreenLoadedOnce) {
      loadData();
      mScreenLoadedOnce = true;
    }
    // do the rest of the code here.

  }

  private void initWidgets(View view) {
    SharedPreferenceSingleton sharedPref = SharedPreferenceSingleton.getInstance();
    this.userId = sharedPref.getStringPreference(RegistrationConstants.USER_ID);
    this.recyclerViewPlacements = view.findViewById(R.id.recyclerViewPlacements);
    this.layoutIdeabox = view.findViewById(R.id.layoutIdeabox);
    this.lblNoJobs = view.findViewById(R.id.lblNoJobs);

    this.recyclerViewPlacements.setHasFixedSize(true);
    LinearLayoutManager layoutManager = new LinearLayoutManager(
        getActivity(), LinearLayoutManager.VERTICAL, false);
    this.recyclerViewPlacements.setLayoutManager(layoutManager);
  }

  private void setGreetings() {
    String firstName = SharedPreferenceSingleton.getInstance()
        .getStringPreference(RegistrationConstants.FIRST_NAME);
    String currentlyStudying = SharedPreferenceSingleton.getInstance()
        .getStringPreference(RegistrationConstants.CURRENTLY_STUDYING);

    StringBuilder sb = new StringBuilder("<b>Hi ");
    if (firstName != null) {
      sb.append(firstName);
    }
    sb.append("</b><br><br>");
    sb.append("You're currently studying");
    sb.append("\n");
    if (currentlyStudying != null) {
      if (currentlyStudying.equals(Constants.SCHOOL)) {
        String stantard = SharedPreferenceSingleton.getInstance()
            .getStringPreference(RegistrationConstants.STANDARD_NAME);
        if (stantard != null) {
          sb.append(stantard);
        }
      } else {
        String degree = SharedPreferenceSingleton.getInstance()
            .getStringPreference(RegistrationConstants.DEGREE_NAME);
        if (degree != null) {
          sb.append(degree);
          sb.append(" in ");
          String course = SharedPreferenceSingleton.getInstance()
              .getStringPreference(RegistrationConstants.COURSE_NAME);
          if (course != null) {
            sb.append(course);
            String year = SharedPreferenceSingleton.getInstance()
                .getStringPreference(RegistrationConstants.YEAR_NAME);
            if (year != null) {
              sb.append("(" + year + ")");
            }
          }
        }
      }
    }
  }

  private void bindEvents() {
    this.layoutIdeabox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        moveToIdeaBox();
      }
    });
  }

  private void loadData() {
    loadPlacements();
  }

  private void moveToSearchPlacements() {
    Intent intent = new Intent(getActivity(), SearchPlacementActivity.class);
    startActivity(intent);
  }

  private void moveToIdeaBox() {
    Intent intent = new Intent(getActivity(), IdeaBoxInfoActivity.class);
    startActivity(intent);
  }

  private void loadPlacements() {
    Apimethods apiMethods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
    Call<Placements> call = apiMethods.getPlacements(this.userId);
    showProgressDialog();
    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<Placements>() {
      @Override
      public void onResponse(@NotNull Call<Placements> call,
          @NotNull Response<Placements> response) {
        dismissProgressDialog();
        int statusCode = response.code();
        apiCallBlocker = true;
        if (pageNumber > 1 && placementList != null && placementList.size() > 0) {
          placementList.remove(placementList.size() - 1);
          placementsAdapter.notifyItemRemoved(placementList.size());
        }
        if (pageNumber == 1) {
          placementList.clear();
        }

        if (statusCode == 200) {
          int addedPosition = placementList.size() - 1;
          List<Placements.Placement> dataList = response.body().getPlacementList();
          if (dataList.isEmpty()) {
            apiCallBlocker = false;
          } else {
            placementList.addAll(dataList);
          }

          if (placementsAdapter == null) {
            placementsAdapter =
                new PlacementsAdapter(getActivity(), placementList, recyclerViewPlacements);
            recyclerViewPlacements.setAdapter(placementsAdapter);
            placementsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
              @Override
              public void onLoadMore() {
                // TODO: uncomment this when paging is done
                                /*if (apiCallBlocker && isDataLoading == false) {
                                    placementList.add(null);
                                    placementsAdapter.notifyItemInserted(placementList.size() - 1);
                                    apiCallBlocker = false;
                                    pageNumber++;
                                    loadPlacements();
                                }*/
              }
            });
            placementsAdapter.setOnItemClickListener(new PlacementsAdapter.OnItemClickListener() {
              @Override
              public void onItemClicked(int position, Placements.Placement placement) {
                moveToPlacementDetails(position, placement);
              }

              @Override
              public void apply(int position, Placements.Placement placement) {
                applyForPlacement(position, placement);
              }
            });
          } else {
            if (pageNumber == 1) {
              placementsAdapter.notifyDataSetChanged();
            } else {
              if (addedPosition < 0) {
                addedPosition = 0;
              }
              if (dataList.size() > 0) {
                placementsAdapter.notifyItemRangeChanged(addedPosition, dataList.size());
              }
            }
            placementsAdapter.setLoaded();
          }
          isDataLoading = false;

          if (placementList.isEmpty()) {
            lblNoJobs.setVisibility(View.VISIBLE);
            recyclerViewPlacements.setVisibility(View.GONE);
          } else {
            lblNoJobs.setVisibility(View.GONE);
            recyclerViewPlacements.setVisibility(View.VISIBLE);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<Placements> call, @NotNull Throwable t) {
        dismissProgressDialog();
      }
    });
  }

  private void moveToPlacementDetails(int position, Placements.Placement placement) {
    Intent intent = new Intent(getContext(), PlacementDetailsActivity.class);
    intent.putExtra(Constants.PLACEMENT, placement);
    getContext().startActivity(intent);
  }

  private void applyForPlacement(final int position, Placements.Placement placement) {
    if (placement == null) {
      return;
    }

    Apimethods apiMethods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
    showProgressDialog();
    Call<ApplyPlacementResponse> call = apiMethods.applyPlacement(
        placement.getPlacementId(), this.userId, placement.getMediaLink(),
        placement.getThumbnail());

    call.enqueue(new Callback<ApplyPlacementResponse>() {
      @Override
      public void onResponse(@NotNull Call<ApplyPlacementResponse> call,
          @NotNull Response<ApplyPlacementResponse> response) {
        dismissProgressDialog();
        if (response.code() == 200) {
          if (response.body().isSuccess()) {
            Utils.showShortToast(getActivity(), "Successfully applied.");
            placement.setIsApplied("1");
            placementsAdapter.notifyItemChanged(position);
          }
        }
      }

      @Override
      public void onFailure(@NotNull Call<ApplyPlacementResponse> call, @NotNull Throwable t) {
        dismissProgressDialog();
        Utils.showShortToast(getActivity(), "Failed to apply.");
      }
    });
  }
}
